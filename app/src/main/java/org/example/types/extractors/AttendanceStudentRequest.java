package org.example.types.extractors;

import java.util.Map;
import java.util.Optional;

import org.example.types.Err;
import org.example.types.ErrKind;
import org.example.util.Extractor;
import org.example.util.Parser;
import org.example.util.Result;
import org.example.util.Validator;

public class AttendanceStudentRequest implements Extractor<AttendanceStudentRequest> {
	private Optional<Long> rollno = Optional.empty();
	private Optional<Long> subjectid = Optional.empty();
	private Optional<Long> teacherid = Optional.empty();
	private String period;
	private String date;

	public static Result<AttendanceStudentRequest, Err> extract(
		Map<String, String[]> map
	) {
		if (map == null) {
			return Result.err(new Err(
				ErrKind.IllegalArgument, 
				"Parameter map is null"
			));
		}

		AttendanceStudentRequest request = new AttendanceStudentRequest();

		String[] rollno = map.get("rollno");
		if (rollno == null) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Request parameter `rollno` is empty"
			));
		}

		if (rollno.length != 1) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Request parameter `rollno` must be singular"
			));
		}

		Result<Long, Err> parsed_rollno = Parser.parse_long(rollno[0]);
		if (parsed_rollno.isErr()) {
			return Result.err(parsed_rollno.err_msg());
		}
		request.setRollno(parsed_rollno.asOption());

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

		Result<Long, Err> parsed_teacherid = Parser.parse_long(teacherid[0]);
		if (parsed_teacherid.isErr()) {
			return Result.err(parsed_teacherid.err_msg());
		}
		request.setTeacherid(parsed_teacherid.asOption());

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

		Result<Long, Err> parsed_subjectid = Parser.parse_long(subjectid[0]);
		if (parsed_subjectid.isErr()) {
			return Result.err(parsed_subjectid.err_msg());
		}
		request.setSubjectid(parsed_subjectid.asOption());

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

	public Optional<Long> getRollno() {
		return rollno;
	}

	public void setRollno(Optional<Long> rollno) {
		this.rollno = rollno;
	}

	public void setRollno(long rollno) {
		this.rollno = Optional.of(rollno);
	}

	public Optional<Long> getSubjectid() {
		return subjectid;
	}

	public void setSubjectid(Optional<Long> subjectid) {
		this.subjectid = subjectid;
	}

	public void setSubjectid(long subjectid) {
		this.subjectid = Optional.of(subjectid);
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
