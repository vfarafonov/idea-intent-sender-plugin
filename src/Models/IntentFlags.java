package Models;

/**
 * Created by vfarafonov on 04.09.2015.
 */
public enum IntentFlags {
	NONE(""),
	FLAG_GRANT_READ_URI_PERMISSION(" --grant-read-uri-permission"),
	FLAG_GRANT_WRITE_URI_PERMISSION(" --grant-write-uri-permission"),
	FLAG_DEBUG_LOG_RESOLUTION(" --debug-log-resolution"),
	FLAG_EXCLUDE_STOPPED_PACKAGES(" --exclude-stopped-packages"),
	FLAG_INCLUDE_STOPPED_PACKAGES(" --include-stopped-packages"),
	FLAG_ACTIVITY_BROUGHT_TO_FRONT(" --activity-brought-to-front"),
	FLAG_ACTIVITY_CLEAR_TOP(" --activity-clear-top"),
	FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET(" --activity-clear-when-task-reset"),
	FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS(" --activity-exclude-from-recents"),
	FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY("--activity-launched-from-history"),
	FLAG_ACTIVITY_MULTIPLE_TASK(" --activity-multiple-task"),
	FLAG_ACTIVITY_NO_ANIMATION(" --activity-no-animation"),
	FLAG_ACTIVITY_NO_HISTORY(" --activity-no-history"),
	FLAG_ACTIVITY_NO_USER_ACTION(" --activity-no-user-action"),
	FLAG_ACTIVITY_PREVIOUS_IS_TOP(" --activity-previous-is-top"),
	FLAG_ACTIVITY_REORDER_TO_FRONT(" --activity-reorder-to-front"),
	FLAG_ACTIVITY_RESET_TASK_IF_NEEDED(" --activity-reset-task-if-needed"),
	FLAG_ACTIVITY_SINGLE_TOP(" --activity-single-top"),
	FLAG_ACTIVITY_CLEAR_TASK(" --activity-clear-task"),
	FLAG_ACTIVITY_TASK_ON_HOME(" --activity-task-on-home"),
	FLAG_RECEIVER_REGISTERED_ONLY(" --receiver-registered-only"),
	FLAG_RECEIVER_REPLACE_PENDING(" --receiver-replace-pending");

	private final String command_;

	IntentFlags(String command) {
		command_ = command;
	}

	public String getCommand() {
		return command_;
	}
}
