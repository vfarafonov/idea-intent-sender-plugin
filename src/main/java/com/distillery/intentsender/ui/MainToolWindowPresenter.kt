package com.distillery.intentsender.ui

import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener
import com.android.ddmlib.IDevice
import com.intellij.facet.FacetManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiClass
import com.distillery.intentsender.domain.command.Command
import com.distillery.intentsender.models.ExtraField
import com.distillery.intentsender.models.IntentFlags
import com.distillery.intentsender.adb.AdbHelper
import com.distillery.intentsender.domain.command.CommandParamsValidator
import com.distillery.intentsender.domain.command.CommandParamsValidator.*
import com.distillery.intentsender.data.history.HistoryUtils
import com.distillery.intentsender.utils.exhaustive
import org.jetbrains.android.dom.manifest.Manifest
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.android.facet.AndroidRootUtil
import org.jetbrains.android.util.AndroidUtils
import java.awt.Desktop
import java.io.File
import java.net.URI
import javax.swing.SwingWorker

private const val ERROR_UNKNOWN = "Unknown error"

class MainToolWindowPresenter(
    private val view: MainToolWindowContract.View,
    private val project: Project,
    private val commandParamsValidator: CommandParamsValidator,
) : MainToolWindowContract.Presenter {

    private val devicesListener: IDeviceChangeListener = DevicesListener()
    private var selectedDevice: IDevice? = null
    private var lastCommandOutput: String? = null
    var selectedFlags = listOf(IntentFlags.NONE)

    override fun onViewStart() {
        val adbLocation = AdbHelper.getAdbLocation()
        if (adbLocation == null) {
            view.setLocateAdbButtonVisible(true)
            view.setIntentCreationLayoutVisible(false)
        } else {
            view.setLocateAdbButtonVisible(false)
            view.setIntentCreationLayoutVisible(true)

            startAdbAndSwitchUI(adbLocation)
        }
        view.displaySelectedFlags(selectedFlags)
    }

    override fun onAdbLocationPicked(adbFile: File) {
        AdbHelper.saveAdbLocation(adbFile.absolutePath)
        startAdbAndSwitchUI(adbFile.absolutePath)
    }

    /**
     * Checks if adb is connected and switches UI accordingly
     */
    private fun startAdbAndSwitchUI(adbPath: String) {
        val adbHelper = AdbHelper.getInstance()
        if (adbHelper.initAdb(project, adbPath, devicesListener)) {
            if (!adbHelper.isConnected) {
                view.setLocateAdbButtonVisible(false)
                view.setAdbInitProgressIndicatorVisible(true)

                RestartAdbWorker(adbHelper).execute()
            } else {
                view.setLocateAdbButtonVisible(false)
                view.setIntentCreationLayoutVisible(true)

                updateDevicesList()
            }
        } else {
            view.setLocateAdbButtonVisible(false)
            view.setIntentCreationLayoutVisible(true)
        }
    }

    override fun onLocateAdbClicked() {
        view.pickAdbLocation()
    }

    /**
     * Updates devices list keeping selected device if it is still connected
     */
    override fun onUpdateDevicesClicked() {
        updateDevicesList()
    }

    private fun updateDevicesList() {
        val helper = AdbHelper.getInstance()

        val devices = helper.devices
        if (devices.isEmpty()) {
            view.showNoDevicesConnected()
            view.enableStartButtons(false)
            selectedDevice = null
        } else {
            // Find previously selected or use the first device
            selectedDevice = devices.firstOrNull { it.serialNumber == selectedDevice?.serialNumber }
                ?: devices[0]
            view.showAvailableDevices(devices, selectedDevice)
            view.enableStartButtons(true)
        }
    }

    override fun onDeviceSelected(device: IDevice) {
        selectedDevice = device
    }

    override fun onShowHistoryClicked() {
        val commandsHistory = HistoryUtils.getCommandsFromHistory()
        view.showCommandsFromHistoryChooser(commandsHistory)
    }

    override fun onCommandSelectedFromHistory(command: Command) {
        selectedFlags = command.flags
        view.updateUiFromCommand(command)
    }

    override fun onSendFeedbackClicked() {
        if (!Desktop.isDesktopSupported()) {
            return
        }
        with(Desktop.getDesktop()) {
            if (isSupported(Desktop.Action.BROWSE)) {
                try {
                    browse(URI(MainToolWindow.ISSUES_LINK))
                } catch (exc: Exception) {
                    exc.printStackTrace()
                }
            }
        }
    }

    override fun onShowTerminalOutputClicked() {
        view.showTerminalOutput(lastCommandOutput)
    }

    override fun onSendCommandClicked(
        action: String,
        data: String,
        category: String,
        mimeType: String,
        component: String,
        user: String?,
        extras: List<ExtraField>,
        type: AdbHelper.CommandType,
        applicationId: String?,
    ) {
        // Check if device is selected
        if (selectedDevice == null) {
            return
        }

        val command = Command(
            action,
            data,
            category,
            mimeType,
            component,
            user,
            extras,
            selectedFlags,
            type,
            applicationId,
        )
        val validationResult = commandParamsValidator.validate(command)

        when (validationResult) {
            ValidationResult.Valid -> sendCommand(command)
            is ValidationResult.Invalid -> handleCommandParamsErrors(validationResult)
        }.exhaustive
    }

    private fun handleCommandParamsErrors(validationResult: ValidationResult.Invalid) {
        view.displayParamsErrors(validationResult.errors)
    }

    private fun sendCommand(command: Command) {
        view.enableStartButtons(false)

        SendAdbCommandWorker(command).execute()
    }

    override fun onPickComponentClicked() {
        view.showClassPicker(project)
    }

    override fun onComponentSelected(selectedClass: PsiClass?) {
        if (selectedClass != null) {
            val androidPackage = getAndroidPackage(selectedClass)
            var fullComponentName = selectedClass.qualifiedName
            if (!androidPackage.isNullOrEmpty()
                && fullComponentName != null
            ) {
                val packageIndex = fullComponentName.indexOf(androidPackage)
                if (packageIndex != -1) {
                    val builder = StringBuilder(fullComponentName)
                    fullComponentName = builder.insert(packageIndex + androidPackage.length, "/").toString()
                }
                view.setUser(androidPackage)
            }
            view.setComponent(fullComponentName)
        }
    }

    /**
     * Gets android app package from selected class
     *
     * @return Package or null if package cannot be parsed from sources
     */
    private fun getAndroidPackage(selectedClass: PsiClass): String? {
        val projectRootManager = ProjectRootManager.getInstance(selectedClass.project)
        val selectedClassVirtualFile = selectedClass.containingFile.virtualFile
        val module = projectRootManager.fileIndex.getModuleForFile(selectedClassVirtualFile)
            ?: return null
        val facetManager = FacetManager.getInstance(module)
        val facet = facetManager.getFacetByType(AndroidFacet.ID) ?: return null
        val manifestFile = AndroidRootUtil.getPrimaryManifestFile(facet) ?: return null
        val manifest = AndroidUtils.loadDomElement(facet.module, manifestFile, Manifest::class.java)
            ?: return null
        val rootPackage = manifest.getPackage()
        // TODO(vfarafonov, 1/3/21): use application id instead of package from module's manifest.
        return rootPackage.stringValue
    }

    override fun onFlagsClicked() {
        view.showFlagsSelection(listOf(*IntentFlags.values()), selectedFlags)
    }

    override fun onFlagsSelected(newFlags: List<IntentFlags>) {
        if (newFlags.isEmpty()) {
            selectedFlags = listOf(IntentFlags.NONE)
        }

        val flags = mutableListOf<IntentFlags>()
        flags.addAll(newFlags)
        // Remove NONE if it was added
        if (flags.size > 1
            && flags.contains(IntentFlags.NONE)
        ) {
            flags.remove(IntentFlags.NONE)
        }
        selectedFlags = flags

        view.displaySelectedFlags(selectedFlags)
    }

    override fun onApplicationIdChanged(appId: String) {
        view.setUser(appId)
    }

    private inner class SendAdbCommandWorker(
        private val command: Command
    ) : SwingWorker<String?, String?>() {
        override fun doInBackground(): String? {
            var error: String? = null
            lastCommandOutput = null
            try {
                AdbHelper.getInstance().setOutputListener { output ->
                    lastCommandOutput = output
                }
                error = AdbHelper.getInstance().sendCommand(command, selectedDevice)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return error
        }

        override fun done() {
            val error: String? = try {
                get()
            } catch (e: Exception) {
                if (e.message != null) e.message else ERROR_UNKNOWN
            }
            if (error == null) {
                handleCommandExecutionSuccess(command)
            } else {
                handleCommandExecutionError(error, command)
            }
            view.enableStartButtons(true)
        }

        private fun handleCommandExecutionError(error: String, command: Command) {
            println("Sending command FAILED: $error")
            view.showCommandExecutionError(error)
        }

        private fun handleCommandExecutionSuccess(command: Command) {
            println("SUCCESS sending command")
            HistoryUtils.saveCommand(command)
            view.showCommandSentSuccessfully()
        }
    }

    private inner class RestartAdbWorker(
        private val adbHelper: AdbHelper
    ) : SwingWorker<Void?, Void?>() {

        override fun doInBackground(): Void? {
            adbHelper.restartAdb()
            return null
        }

        override fun done() {
            // TODO(vfarafonov, 12/23/20): move Done actions to constructor as lambda.
            view.setAdbInitProgressIndicatorVisible(false)
            view.setLocateAdbButtonVisible(false)
            view.setIntentCreationLayoutVisible(true)

            updateDevicesList()
        }
    }

    private inner class DevicesListener : IDeviceChangeListener {

        override fun deviceConnected(iDevice: IDevice) {
            updateDevicesList()
        }

        override fun deviceDisconnected(iDevice: IDevice) {
            updateDevicesList()
        }

        override fun deviceChanged(iDevice: IDevice, i: Int) {
            updateDevicesList()
        }
    }

    companion object Factory {

        fun create(view: MainToolWindowContract.View, project: Project): MainToolWindowPresenter {
            return MainToolWindowPresenter(view, project, CommandParamsValidator.create())
        }
    }
}
