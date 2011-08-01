package org.cloudfoundry.services;

public class Person {

	private String id;
	private String firstName;
	private int age;

	public Person() {
	}

	public Person(String firstName, int age) {
		super();
		this.firstName = firstName;
		this.age = age;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "Person [id=" + id + ", firstName=" + firstName + ", age=" + age + "]";
	}

}
