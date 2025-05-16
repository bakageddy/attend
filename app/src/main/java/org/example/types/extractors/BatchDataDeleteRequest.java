package org.example.types.extractors;

import java.util.Map;
import java.util.Optional;

import org.example.types.Err;
import org.example.types.ErrKind;
import org.example.util.Extractor;
import org.example.util.Parser;
import org.example.util.Result;

/**
 * BatchDataDeleteRequest
 */
public class BatchDataDeleteRequest implements Extractor<BatchDataDeleteRequest> {
	public static Result<BatchDataDeleteRequest, Err> extract(Map<String, String[]> map) {
		if (map == null || map.size() == 1) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Parameter map is empty"
			));
		}
		BatchDataDeleteRequest request = new BatchDataDeleteRequest();
		String[] batchid = map.get("batchid");
		String[] rollno = map.get("rollno");
		String[] rollnos = map.get("rollno[]");
		if (batchid == null || (rollno == null && rollnos == null)) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Cannot delete from batch without either `batchid` | (`rollno` | `rollnos`)"
			));
		}
		if (batchid != null) {
			if (batchid.length != 1) {
				return Result.err(new Err(
					ErrKind.IllegalArgument,
					"Parameter 'batchid' must be singular"
				));
			}
			Result<Long, Err> parsed_batchid = Parser.parse_long(batchid[0]);
			if (parsed_batchid.isErr()) {
				return Result.err(parsed_batchid.err_msg());
			}

			request.setBatchid(parsed_batchid.asOption());
		}
		if (rollnos != null) {
			if (rollnos.length != 1) {
				return Result.err(new Err(
					ErrKind.IllegalArgument,
					"Request parameters `rollnos` must be singular"
				));
			}
			// TODO: Better validation of rollnos format
			Result<long[], Err> result = Parser.parse_long(rollnos[0].split(","));
			if (result.isErr()) {
				return Result.err(result.err_msg());
			}
			request.setRollnos(result.asOption());
		} else {
			if (rollno.length != 1) {
				return Result.err(new Err(
					ErrKind.IllegalArgument,
					"Request parameters `rollno` must be singular"
				));
			}
			Result<Long, Err> result = Parser.parse_long(rollno[0]);
			if (result.isErr()) {
				return Result.err(result.err_msg());
			}
			request.setRollno(result.asOption());
		}
		return Result.ok(request);

	}

	private Optional<Long> batchid = Optional.empty();
	private Optional<Long> rollno = Optional.empty();
	private Optional<long[]> rollnos = Optional.empty();

	public Optional<long[]> getRollnos() {
		return rollnos;
	}

	public void setRollnos(Optional<long[]> rollnos) {
		this.rollnos = rollnos;
	}

	public void setRollnos(long[] rollnos) {
		this.rollnos = Optional.ofNullable(rollnos);
	}
	public Optional<Long> getBatchid() {
		return batchid;
	}
	public void setBatchid(Optional<Long> batchid) {
		this.batchid = batchid;
	}
	public void setBatchid(long batchid) {
		this.batchid = Optional.ofNullable(batchid);
	}

	public Optional<Long> getRollno() {
		return rollno;
	}
	public void setRollno(Optional<Long> rollno) {
		this.rollno = rollno;
	}
	public void setRollno(long rollno) {
		this.rollno = Optional.ofNullable(rollno);
	}

}
