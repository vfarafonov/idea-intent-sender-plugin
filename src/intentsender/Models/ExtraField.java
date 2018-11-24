package intentsender.Models;

/**
 * Created by vfarafonov on 31.08.2015.
 */
public class ExtraField {
	public enum ExtrasTypes{
		STRING(" -e "), STRING_ARRAY(" --esa "), INT(" --ei "), BOOLEAN(" --ez "), NULL_EXTRA(" --esn "), LONG(" --el "),
		FLOAT(" --ef "), URI(" --eu "), INT_ARRAY(" --eia "), LONG_ARRAY(" --ela "), FLOAT_ARRAY(" --efa ");

		private final String prefix_;

		ExtrasTypes(String prefix) {
			prefix_ = prefix;
		}

		public String getPrefix() {
			return prefix_;
		}
	}

	private ExtrasTypes type_;
	private String key_;
	private String value_;

	public ExtraField(ExtrasTypes type, String key, String value) {
		this.type_ = type;
		this.key_ = key;
		this.value_ = value;
	}

	public ExtrasTypes getType() {
		return type_;
	}

	public void setType(ExtrasTypes type) {
		this.type_ = type;
	}

	public String getKey() {
		return key_;
	}

	public void setKey(String key) {
		this.key_ = key;
	}

	public String getValue() {
		return value_;
	}

	public void setValue(String value) {
		this.value_ = value;
	}
}
