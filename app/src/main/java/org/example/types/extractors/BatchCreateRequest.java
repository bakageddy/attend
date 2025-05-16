package org.example.types.extractors;

import java.util.Map;
import java.util.Optional;

import org.example.types.Err;
import org.example.types.ErrKind;
import org.example.util.Extractor;
import org.example.util.Parser;
import org.example.util.Result;

public class BatchCreateRequest implements Extractor<BatchCreateRequest> {
	private Optional<Long> teacherid;
	private String name; 
	public static Result<BatchCreateRequest, Err> extract(Map<String, String[]> map) {
		if (map == null) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Parameter map is null"
			));
		}
		BatchCreateRequest request = new BatchCreateRequest();
		String[] teacherid = map.get("teacherid");
		String[] name = map.get("name");
		if (teacherid == null || name == null) {
			return Result.err(new Err(
				ErrKind.IllegalArgument, 
				"Cannot create batch without `teacherid` | `name`"
			));
		}


		if (teacherid.length != 1 || name.length != 1) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Request parameters `teacherid`, `name` must be singular"
			));
		}
		request.setName(name[0]);

		Result<Long, Err> result = Parser.parse_long(teacherid[0]);
		if (result.isErr()) {
			return Result.err(result.err_msg());
		}
		request.setTeacherid(result.asOption());


		return Result.ok(request);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
