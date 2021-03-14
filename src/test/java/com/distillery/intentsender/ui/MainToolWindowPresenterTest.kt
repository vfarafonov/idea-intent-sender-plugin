package com.distillery.intentsender.ui

import com.distillery.intentsender.adb.AdbHelper
import com.distillery.intentsender.domain.command.CommandParamsValidator
import com.distillery.intentsender.domain.command.CommandParamsValidator.ValidationResult
import com.distillery.intentsender.domain.command.CommandParamsValidator.ValidationResult.Invalid.Error
import com.distillery.intentsender.testutils.stubMock
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.nhaarman.mockitokotlin2.*
import org.junit.Test

class MainToolWindowPresenterTest {

    private val view = mock<MainToolWindowContract.View>()
    private val project = mock<Project>()
    private val commandParamsValidator = mock<CommandParamsValidator>()

    private val presenter = MainToolWindowPresenter(view, project, commandParamsValidator)

    @Test
    fun `user set to view when application id changes`() {
        val appIdStub = "stub"
        presenter.onApplicationIdChanged(appIdStub)

        verify(view).setUser(appIdStub)
    }

    @Test
    fun `presenter tells view to display errors if command is invalid`() {
        val errors = listOf(Error.APPLICATION_ID_MISSING)
        whenever(commandParamsValidator.validate(any()))
            .thenReturn(ValidationResult.Invalid(errors))

        callSendCommandClickedWithStubs()

        verify(view).displayParamsErrors(errors)
    }

    @Test
    fun `start buttons disabled when command params valid and command is being executed`() {
        whenever(commandParamsValidator.validate(any()))
            .thenReturn(ValidationResult.Valid)

        callSendCommandClickedWithStubs()

        verify(view).enableStartButtons(false)
        verify(view, never()).displayParamsErrors(any())
    }

    @Test
    fun `component in view updated after it was selected`() {
        val qualifiedNameStub = "stub"
        val component: PsiClass = mock() {
            on { qualifiedName } doReturn qualifiedNameStub
        }

        presenter.onComponentSelected(component)

        verify(view).setComponent(qualifiedNameStub)
    }

    private fun callSendCommandClickedWithStubs() {
        presenter.onDeviceSelected(stubMock())
        presenter.onSendCommandClicked("", "", "", "", "", "", emptyList(), AdbHelper.CommandType.START_ACTIVITY, "")
    }
}
