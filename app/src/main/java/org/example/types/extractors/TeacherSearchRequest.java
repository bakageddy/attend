package org.example.types.extractors;

public class TeacherSearchRequest {
	long teacherid;
	String pattern;
	public long getTeacherid() {
		return teacherid;
	}
	public void setTeacherid(long teacherid) {
		this.teacherid = teacherid;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
}
