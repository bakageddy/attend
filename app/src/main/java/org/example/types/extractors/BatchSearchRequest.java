package org.example.types.extractors;

import java.util.Map;
import java.util.Optional;

import org.example.types.Err;
import org.example.types.ErrKind;
import org.example.util.Extractor;
import org.example.util.Parser;
import org.example.util.Result;

// TODO: Documentation
public class BatchSearchRequest implements Extractor<BatchSearchRequest> {
	private Optional<Long> batchid = Optional.empty(); //id
	private Optional<Long> teacherid = Optional.empty(); //owner
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

	public static Result<BatchSearchRequest, Err> extract(Map<String, String[]> map) {
		BatchSearchRequest request = new BatchSearchRequest();
		String[] patterns = map.get("pattern");
		if (patterns != null) {
			if (patterns.length != 1) {
				return Result.err(new Err(
					ErrKind.IllegalArgument,
					"pattern must be singular"
				));
			}
			request.setBatchname(patterns[0]);
		}

		String[] teacherid = map.get("teacherid");
		if (teacherid != null) {
			if (teacherid.length != 1) {
				return Result.err(new Err(
					ErrKind.IllegalArgument,
					"teacherid must be singular"
				));
			}
			Result<Long, Err> parsed_teacherid = Parser.parse_long(teacherid[0]);
			if (parsed_teacherid.isErr()) {
				return Result.err(parsed_teacherid.err_msg());
			}
			request.setTeacherid(parsed_teacherid.asOption());
		}

		String[] batchid = map.get("batchid");
		if (batchid != null) {
			if (batchid.length != 1) {
				return Result.err(new Err(
					ErrKind.IllegalArgument,
					"batchid must be singular"
				));
			}
			Result<Long, Err> parsed_batchid = Parser.parse_long(batchid[0]);
			if (parsed_batchid.isErr()) {
				return Result.err(parsed_batchid.err_msg());
			}
			request.setBatchid(parsed_batchid.asOption());
		}
		if (patterns == null && teacherid == null && batchid == null) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Search parameters: `<pattern>` || `teacherid` || `batchid` unfulfilled"
			));
		}
		return Result.ok(request);
	}
}
