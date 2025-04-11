package org.example;

/**
 * Student
 */
public class Student {
	public Long rollNo;
	public String name;

	private Student() {
	}

	public Student(Long rollNo, String name) {
		this.rollNo = rollNo;
		this.name = name;
	}
}
