package org.example.types.extractors;

import java.util.Map;
import java.util.Optional;

import org.example.types.Err;
import org.example.types.ErrKind;
import org.example.util.Extractor;
import org.example.util.Parser;
import org.example.util.Result;

public class BatchDataFetchRequest implements Extractor<BatchDataFetchRequest> {
	public static Result<BatchDataFetchRequest, Err> extract(Map<String, String[]> map) {
		if (map == null) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Parameter map is empty"
			));
		}

		BatchDataFetchRequest request = new BatchDataFetchRequest();

		String[] batchid = map.get("batchid");
		if (batchid == null) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Parameter `batchid` must be set"
			));
		}

		if (batchid.length != 1) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Parameter `batchid` must be singular"
			));
		}

		Result<Long, Err> result = Parser.parse_long(batchid[0]);
		if (result.isErr()) {
			return Result.err(result.err_msg());
		}
		return Result.ok(request);
	}
	private Optional<Long> batchid = Optional.empty();

	public Optional<Long> getBatchid() {
		return batchid;
	}

	public void setBatchid(Optional<Long> batchid) {
		this.batchid = batchid;
	}

	public void setBatchid(long batchid) {
		this.batchid = Optional.of(batchid);
	}
}
