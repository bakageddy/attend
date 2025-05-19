package org.example.types.extractors;

import java.util.Map;
import java.util.Optional;

import org.example.types.Err;
import org.example.types.ErrKind;
import org.example.util.Extractor;
import org.example.util.Parser;
import org.example.util.Result;
import org.example.util.Validator;

public class AttendanceBatchRequest implements Extractor<AttendanceBatchRequest> {
	public static Result<AttendanceBatchRequest, Err> extract(
		Map<String, String[]> map
	) {
		if (map == null) {
			return Result.err(new Err(
				ErrKind.IllegalArgument, 
				"Parameter map is null"
			));
		}

		AttendanceBatchRequest request = new AttendanceBatchRequest();
		String[] batchid = map.get("batchid");
		if (batchid == null) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Request parameter `batchid` is empty"
			));
		}

		if (batchid.length != 1) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Request parameter `batchid` must be singular"
			));
		}

		Result<Long, Err> result = Parser.parse_long(batchid[0]);
		if (result.isErr()) {
			return Result.err(result.err_msg());
		}
		request.setBatchid(result.asOption());

		String[] teacherid = map.get("teacherid");
		if (teacherid == null) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Request parameter `teacherid` is empty"
			));
		}

		if (teacherid.length != 1) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Request parameter `teacherid` must be singular"
			));
		}

		result = Parser.parse_long(teacherid[0]);
		if (result.isErr()) {
			return Result.err(result.err_msg());
		}
		request.setTeacherid(result.asOption());

		String[] subjectid = map.get("subjectid");
		if (subjectid == null) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Request parameter `subjectid` is empty"
			));
		}

		if (subjectid.length != 1) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Request parameter `subjectid` must be singular"
			));
		}

		result = Parser.parse_long(subjectid[0]);
		if (result.isErr()) {
			return Result.err(result.err_msg());
		}
		request.setSubjectid(result.asOption());

		String[] period = map.get("period");
		if (period == null) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Request parameter `period` is empty"
			));
		}

		if (period.length != 1) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Request parameter `period` must be singular"
			));
		}

		if (!Validator.validate_period(period[0])) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Request parameter `period` must be upper-case roman-numeral (I-VIII)"
			));
		}
		request.setPeriod(period[0]);

		String[] date = map.get("date");
		if (date == null) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Request parameter `date` must be singular"
			));
		}

		if (date.length != 1) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Request parameter `date` must be singular"
			));
		}

		if (Validator.validate_date(date[0]).isEmpty()) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Request Parameter `date` must be in the format YYYY-MM-DD"
			));
		}
		request.setDate(date[0]);

		return Result.ok(request);


	}

	private Optional<Long> batchid = Optional.empty();
	private Optional<Long> subjectid = Optional.empty();
	private Optional<Long> teacherid = Optional.empty();
	private String period;
	private String date;

	public Optional<Long> getBatchid() {
		return batchid;
	}

	public void setBatchid(Optional<Long> batchid) {
		this.batchid = batchid;
	}

	public void setBatchid(long batchid) {
		this.batchid = Optional.of(batchid);
	}

	public Optional<Long> getSubjectid() {
		return subjectid;
	}

	public void setSubjectid(long subjectid) {
		this.subjectid = Optional.of(subjectid);
	}

	public void setSubjectid(Optional<Long> subjectid) {
		this.subjectid = subjectid;
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

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
