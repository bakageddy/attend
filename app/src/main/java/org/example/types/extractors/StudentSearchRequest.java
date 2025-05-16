package org.example.types.extractors;

/**
 * StudentSearchRequest
 */
public class StudentSearchRequest {
	long rollno;
	String pattern;

	public long getRollno() {
		return rollno;
	}

	public void setRollno(long rollno) {
		this.rollno = rollno;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public StudentSearchRequest() {
		rollno = 0;
		pattern = null;
	}
}
