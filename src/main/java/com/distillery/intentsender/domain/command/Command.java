package com.distillery.intentsender.domain.command;

import com.distillery.intentsender.adb.AdbHelper;
import com.distillery.intentsender.models.ExtraField;
import com.distillery.intentsender.models.IntentFlags;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Command {
	private final String action_;
	private final String data_;
	private final String category_;
	private final String mimeType_;
	private final String component_;
	private final List<IntentFlags> flags_;
	private final List<ExtraField> extras_;
	@NotNull
	private final AdbHelper.CommandType type_;
	private final String user_;
	private final String applicationId_;

	public Command(String action_, String data_, String category_, String mimeType_, String component_, String user_,
				   List<ExtraField> extras_, List<IntentFlags> flags_, @NotNull AdbHelper.CommandType type_,
				   String applicationId_) {
		this.action_ = action_;
		this.data_ = data_;
		this.category_ = category_;
		this.mimeType_ = mimeType_;
		this.component_ = component_;
		this.user_ = user_;
		this.flags_ = flags_;
		this.extras_ = extras_;
		this.type_ = type_;
		this.applicationId_ = applicationId_;
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

	@NotNull
	public AdbHelper.CommandType getType() {
		return type_;
	}

	public String getUser() {
		return user_;
	}

	public String getApplicationId() {
		return applicationId_;
	}
}
