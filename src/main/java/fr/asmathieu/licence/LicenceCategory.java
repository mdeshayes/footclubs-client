package fr.asmathieu.licence;

public enum LicenceCategory {

	LIBRE_SENIOR("Libre / Senior"), LIBRE_VETERAN("Libre / Vétéran"), LIBRE_U19("Libre / U19 (- 19 ans)"), LOISIRS("Loisirs"), DIRIGEANT(
			"Dirigeant"), DIRIGEANTE("Dirigeant / Dirigeante");

	private String value;

	LicenceCategory(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static LicenceCategory getEnum(String value) {
		for (LicenceCategory v : values()) {
			if (v.getValue()
					.equalsIgnoreCase(value))
				return v;
		}
		throw new IllegalArgumentException("Category '" + value + "' doesn't exist");
	}

}
