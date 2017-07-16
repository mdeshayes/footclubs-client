package fr.asmathieu.licence;

import java.time.LocalDate;
import java.util.Date;

public class Licence {

	private String firstName;
	private String lastName;
	private String id;
	private LocalDate birthDate;
	private LocalDate registerDate;
	private LicenceCategory category;
	private LicenceState state;

	public Licence(String firstName, String lastName, String id, LocalDate birthDate, LocalDate registerDate, LicenceCategory category, LicenceState state) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.id = id;
		this.birthDate = birthDate;
		this.registerDate = registerDate;
		this.category = category;
		this.state = state;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public LocalDate getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(LocalDate registerDate) {
		this.registerDate = registerDate;
	}

	public LicenceCategory getCategory() {
		return category;
	}

	public void setCategory(LicenceCategory category) {
		this.category = category;
	}

	public LicenceState getState() {
		return state;
	}

	public void setState(LicenceState state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "Licence [firstName=" + firstName + ", lastName=" + lastName + ", id=" + id + ", birthDate=" + birthDate + ", registerDate=" + registerDate
				+ ", category=" + category + ", state=" + state + "]";
	}

	// @Override
	// public String toString() {
	// return "Category = " + category + ", Id = " + id;
	// }

}
