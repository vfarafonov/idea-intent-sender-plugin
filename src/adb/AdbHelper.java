package adb;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import Models.Command;
import Models.ExtraField;
import Models.IntentFlags;

/**
 * Created by vfarafonov on 26.08.2015.
 */
public class AdbHelper {
	private static final String COMMAND_SEND_BROADCAST_BASE = "am broadcast";
	private static final String COMMAND_START_ACTIVITY_BASE = "am start";
	private static final String COMMAND_START_SERVICE_BASE = "am startservice";
	private final static Object lock = new Object();
	private static AdbHelper adbHelper_;
	private AndroidDebugBridge adb_;
	private String errorString_ = null;

	public AdbHelper() {
		AndroidDebugBridge.init(false);
	}

	public static AdbHelper getInstance() {
		AdbHelper instance = adbHelper_;
		if (instance == null) {
			synchronized (lock) {    // While we were waiting for the lock, another
				instance = adbHelper_;        // thread may have instantiated the object.
				if (instance == null) {
					instance = new AdbHelper();
					adbHelper_ = instance;
				}
			}
		}
		return instance;
	}

	/**
	 * Picks up adb location from ANDROID_HOME environment variable
	 */
	public static String getAdbLocation() {
		String android_home = System.getenv("ANDROID_HOME");
		if (android_home != null) {
			return new File(android_home, "platform-tools/adb").getAbsolutePath();
		} else {
			return null;
		}
	}

	public boolean initAdb(String adbLocation) {
		if (adbLocation != null && !adbLocation.isEmpty()) {
			try {
				adb_ = AndroidDebugBridge.createBridge(adbLocation, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return adb_ != null;
	}

	/**
	 * Waiting for connection to adb being established
	 */
	private void waitForDevices() {
		int count = 0;
		while (!adb_.hasInitialDeviceList()) {
			try {
				Thread.sleep(100);
				count++;
			} catch (final InterruptedException e) {
				// pass
			}
			// let's not wait more than 10 sec.
			if (count > 100) {
				return;
			}
		}
	}

	public IDevice[] getDevices() {
		return adb_.getDevices();
	}

	/**
	 * Sends command to device.
	 *
	 * @return Error message if something went wrong, null if it is no errors
	 */
	public String sendCommand(CommandType type, IDevice device, String action, String data,
							  String category, String mime, String component, List<ExtraField> extras, List<IntentFlags> flags)
			throws TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException,
			IOException, IllegalArgumentException {
		if (device == null) {
			throw new IllegalArgumentException("Device cannot be null");
		}
		errorString_ = null;
		String fullCommand = getFullCommand(type, action, data, category, mime, component, extras, flags);
		device.executeShellCommand(fullCommand, new IShellOutputReceiver() {

			@Override
			public void addOutput(byte[] bytes, int i, int i1) {
				try {
					String output = new String(bytes, "UTF-8");
					int index = output.indexOf("Error:");
					if (index == 0) {
						int lineEndingIndex = output.indexOf("\n");
						errorString_ = lineEndingIndex > -1 ? output.substring(0, lineEndingIndex) : output;
					} else if (index > 0) {
						Character beforeSymb = output.charAt(index - 1);
						if (beforeSymb == '\n') {
							errorString_ = output.substring(index);
							int lineEndingIndex = errorString_.indexOf("\n");
							if (lineEndingIndex > -1) {
								errorString_ = errorString_.substring(0, lineEndingIndex);
							}
						}
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void flush() {
			}

			@Override
			public boolean isCancelled() {
				return false;
			}
		});
		return errorString_;
	}

	private String getFullCommand(CommandType type, String action, String data, String category, String mime, String component, List<ExtraField> extras, List<IntentFlags> flags) {
		StringBuilder builder = new StringBuilder();
		switch (type) {
			case BROADCAST:
				builder.append(COMMAND_SEND_BROADCAST_BASE);
				break;
			case START_ACTIVITY:
				builder.append(COMMAND_START_ACTIVITY_BASE);
				break;
			case START_SERVICE:
				builder.append(COMMAND_START_SERVICE_BASE);
				break;
		}
		if (action != null && action.length() > 0) {
			builder.append(" -a " + action);
		}
		if (data != null && data.length() > 0) {
			builder.append(" -d " + data);
		}
		if (category != null && category.length() > 0) {
			builder.append(" -c " + category);
		}
		if (mime != null && mime.length() > 0) {
			builder.append(" -t " + mime);
		}
		if (component != null && component.length() > 0) {
			builder.append(" -n " + component);
		}
		if (extras != null && extras.size() > 0) {
			for (ExtraField extra : extras) {
				builder.append(extra.getType().getPrefix() + extra.getKey() + " " + extra.getValue());
			}
		}
		if (flags != null && flags.size() > 0) {
			for (IntentFlags flag : flags) {
				builder.append(flag.getCommand());
			}
		}
		System.out.println("Command: " + builder.toString());
		return builder.toString();
	}

	public boolean isConnected() {
		return adb_.isConnected();
	}

	public void restartAdb() {
		adb_.restart();
	}

	/**
	 * Sends command to device.
	 *
	 * @return Error message if something went wrong, null if it is no errors
	 */
	public String sendCommand(Command command, IDevice device) throws TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
		return sendCommand(command.getType(), device, command.getAction(), command.getData(), command.getCategory(), command.getMimeType(), command.getComponent(), command.getExtras(), command.getFlags());
	}

	public enum CommandType {
		BROADCAST, START_SERVICE, START_ACTIVITY
	}
}
