package org.example.types.extractors;

import java.util.Optional;

// TODO: Documentation
public class BatchSearchRequest {
	private Optional<Long> batchid; //id
	private Optional<Long> teacherid; //owner
	private String batchname; //pattern

	public Optional<Long> getBatchid() {
		return batchid;
	}

	public void setBatchid(long batchid) {
		this.batchid = Optional.of(batchid);
	}

	public void setBatchid(Optional<Long> batchid) {
		this.batchid = batchid;
	}

	public Optional<Long> getTeacherid() {
		return teacherid;
	}
	public void setTeacherid(long teacherid) {
		this.teacherid = Optional.of(teacherid);
	}
	public void setTeacherid(Optional<Long> teacherid) {
		this.teacherid = teacherid;
	}

	public String getBatchname() {
		return batchname;
	}
	public void setBatchname(String batchname) {
		this.batchname = batchname;
	}
}
