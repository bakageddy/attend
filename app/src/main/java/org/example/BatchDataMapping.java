package org.example;

import java.util.ArrayList;

public class BatchDataMapping {
	Long batchid;
	Long teacherid;
	ArrayList<Student> students;
	public BatchDataMapping(Long batchid, Long teacherid, ArrayList<Student> students) {
		this.batchid = batchid;
		this.teacherid = teacherid;
		this.students = students;
	}
}
