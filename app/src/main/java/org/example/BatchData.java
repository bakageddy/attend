package org.example;

import java.util.ArrayList;

public class BatchData {
	Long batchid;
	Long teacherid;
	ArrayList<Student> students;
	public BatchData(Long batchid, Long teacherid, ArrayList<Student> students) {
		this.batchid = batchid;
		this.teacherid = teacherid;
		this.students = students;
	}
}
