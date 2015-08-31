package adb;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import Models.ExtraField;

/**
 * Created by vfarafonov on 26.08.2015.
 */
public class AdbHelper {
	private static final String COMMAND_SEND_BROADCAST_BASE = "am broadcast";
	private static final String COMMAND_START_BASE = "am start";
	private final static Object lock = new Object();
	private static AdbHelper adbHelper_;
	private AndroidDebugBridge adb_;
	public AdbHelper() {
		AndroidDebugBridge.init(false);
		String adbLocation = getAdbLocation();
		if (adbLocation != null) {
			adb_ = AndroidDebugBridge.createBridge(adbLocation, true);
		}
		if (adb_ == null) {
			return;
		}
		// TODO: move waiting to another thread
		waitForDevices();

		if (!adb_.isConnected()) {
			adb_.restart();
			waitForDevices();
		}
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

	/**
	 * Picks up adb location from ANDROID_HOME environment variable
	 */
	public String getAdbLocation() {
		String android_home = System.getenv("ANDROID_HOME");
		if (android_home != null) {
			return new File(android_home, "platform-tools/adb").getAbsolutePath();
		} else {
			return null;
		}
	}

	public IDevice[] getDevices() {
		return adb_.getDevices();
	}

	public void sendCommand(CommandType type, IDevice device, String action, String data,
							String category, String mime, String component, List<ExtraField> extras)
			throws TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException,
			IOException, IllegalArgumentException {
		// TODO: implement sending data in separate thread
		if (device == null) {
			throw new IllegalArgumentException("Device cannot be null");
		}
		String fullCommand = getFullCommand(type, action, data, category, mime, component, extras);
		device.executeShellCommand(fullCommand, new IShellOutputReceiver() {
			@Override
			public void addOutput(byte[] bytes, int i, int i1) {
			}

			@Override
			public void flush() {
			}

			@Override
			public boolean isCancelled() {
				return false;
			}
		});
	}

	private String getFullCommand(CommandType type, String action, String data, String category, String mime, String component, List<ExtraField> extras) {
		StringBuilder builder = new StringBuilder();
		switch (type) {
			case BROADCAST:
				builder.append(COMMAND_SEND_BROADCAST_BASE);
				break;
			case START:
				builder.append(COMMAND_START_BASE);
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
		// TODO: validate data
		if (extras != null && extras.size() > 0) {
			for (ExtraField extra : extras) {
				builder.append(extra.getType().getPrefix() + extra.getKey() + " " + extra.getValue());
			}
		}
		System.err.println("Command: " + builder.toString());
		return builder.toString();
	}

	public static enum CommandType {
		BROADCAST, START;
	}
}
