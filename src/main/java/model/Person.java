package model;

public class Person {
	int persId;
	String profession;
	String name;
	public static final String PROFESSION_F = "profession";
	public static final String NAME_F = "name";

	public Person(int persId, String name, String profession) {
		super();
		this.persId = persId;
		this.profession = profession;
		this.name = name;
	}

	public Person() { // Default construct will help the jackson to create the
						// Person object
		// from the result of elsastic search

	}

	public int getPersId() {
		return persId;
	}

	public void setPersId(int persId) {
		this.persId = persId;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String title) {
		this.profession = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "id:" + persId + ",Name:" + name + ", Profession:" + profession;

	}
}
