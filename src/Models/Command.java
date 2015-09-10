package Models;

import java.util.List;

import adb.AdbHelper;

/**
 * Created by vfarafonov on 08.09.2015.
 */
public class Command {
	private String action_;
	private String data_;
	private String category_;
	private String mimeType_;
	private String component_;
	private List<IntentFlags> flags_;
	private List<ExtraField> extras_;
	private AdbHelper.CommandType type_;
	private String user_;

	public Command(String action_, String data_, String category_, String mimeType_, String component_, String user_, List<ExtraField> extras_, List<IntentFlags> flags_, AdbHelper.CommandType type_) {
		this.action_ = action_;
		this.data_ = data_;
		this.category_ = category_;
		this.mimeType_ = mimeType_;
		this.component_ = component_;
		this.user_ = user_;
		this.flags_ = flags_;
		this.extras_ = extras_;
		this.type_ = type_;
	}

	public String getAction() {
		return action_;
	}

	public String getData() {
		return data_;
	}

	public String getCategory() {
		return category_;
	}

	public String getMimeType() {
		return mimeType_;
	}

	public String getComponent() {
		return component_;
	}

	public List<IntentFlags> getFlags() {
		return flags_;
	}

	public List<ExtraField> getExtras() {
		return extras_;
	}

	public AdbHelper.CommandType getType() {
		return type_;
	}

	public String getUser() {
		return user_;
	}
}
