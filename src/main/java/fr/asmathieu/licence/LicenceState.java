package fr.asmathieu.licence;

public enum LicenceState {

	VALIDATED("Valid�e"), INCOMPLETE("Incompl�te"), NOT_VALIDATED("Non valid�e");

	private String value;

	LicenceState(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static LicenceState getEnum(String value) {
		for (LicenceState v : values()) {
			if (v.getValue()
					.equalsIgnoreCase(value))
				return v;
		}
		throw new IllegalArgumentException("Category '" + value + "' doesn't exist");
	}

}
