package adb;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;

import java.io.File;

/**
 * Created by vfarafonov on 26.08.2015.
 */
public class AdbHelper {
	private AndroidDebugBridge adb_;
	private static AdbHelper adbHelper_;
	private final static Object lock = new Object();

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
}
