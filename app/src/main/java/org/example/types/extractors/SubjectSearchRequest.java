package org.example.types.extractors;

// TODO: Documentation
public class SubjectSearchRequest {
	private long subjectid;
	private String subjectcode; // pattern
	private String subjectname; // pattern
	public long getSubjectid() {
		return subjectid;
	}
	public void setSubjectid(final long subjectid) {
		this.subjectid = subjectid;
	}
	public String getSubjectcode() {
		return subjectcode;
	}
	public void setSubjectcode(final String subjectcode) {
		this.subjectcode = subjectcode;
	}
	public String getSubjectname() {
		return subjectname;
	}
	public void setSubjectname(final String subjectname) {
		this.subjectname = subjectname;
	}
	public SubjectSearchRequest() {
		this.subjectid = 0;
		this.subjectcode = null;
		this.subjectname = null;
	}
}
