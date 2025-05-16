package org.example.types.extractors;

import java.util.Map;
import java.util.Optional;

import org.example.types.Err;
import org.example.types.ErrKind;
import org.example.util.Extractor;
import org.example.util.Parser;
import org.example.util.Result;

public class BatchDeleteRequest implements Extractor<BatchDeleteRequest> {
	public static Result<BatchDeleteRequest, Err> extract(Map<String, String[]> map) {
		if (map == null) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Parameter map is empty"
			));
		}
		BatchDeleteRequest request = new BatchDeleteRequest();
		String[] batchid = map.get("batchid");
		String[] teacherid = map.get("teacherid");
		if (batchid == null || teacherid == null) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Request parameters `batchid` && `teacherid` not set"
			));
		}

		if (batchid.length != 1 || teacherid.length != 1) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Request parameters `batchid`, `teacherid` must be singular"
			));
		}

		Result<Long, Err> parsed_batchid = Parser.parse_long(batchid[0]);
		if (parsed_batchid.isErr()) {
			return Result.err(parsed_batchid.err_msg());
		}
		request.setBatchid(parsed_batchid.unwrap());

		Result<Long, Err> parsed_teacherid = Parser.parse_long(teacherid[0]);
		if (parsed_teacherid.isErr()) {
			return Result.err(parsed_teacherid.err_msg());
		}
		request.setTeacherid(parsed_teacherid.unwrap());

		return Result.ok(request);
	}

	private Optional<Long> batchid = Optional.empty();
	private Optional<Long> teacherid = Optional.empty();

	public Optional<Long> getBatchid() {
		return batchid;
	}
	public void setBatchid(Optional<Long> batchid) {
		this.batchid = batchid;
	}
	public void setBatchid(long batchid) {
		this.batchid = Optional.of(batchid);
	}

	public Optional<Long> getTeacherid() {
		return teacherid;
	}
	public void setTeacherid(Optional<Long> teacherid) {
		this.teacherid = teacherid;
	}
	public void setTeacherid(long teacherid) {
		this.teacherid = Optional.of(teacherid);
	}



}
