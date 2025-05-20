package org.example.types.extractors;

import java.util.Map;

import org.example.types.Err;
import org.example.types.ErrKind;
import org.example.util.Extractor;
import org.example.util.Parser;
import org.example.util.Result;

public class StudentSearchRequest implements Extractor<StudentSearchRequest> {
	long rollno;
	String pattern;

	public long getRollno() {
		return rollno;
	}

	public void setRollno(long rollno) {
		this.rollno = rollno;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public static Result<StudentSearchRequest, Err> extract(Map<String, String[]> map) {
		StudentSearchRequest out = new StudentSearchRequest();
		String[] patterns = map.get("pattern");
		if (patterns != null) {
			if (patterns.length != 1) {
				return Result.err(new Err(
					ErrKind.IllegalArgument, 
					"Pattern must be singular"
				));
			}
			out.setPattern(patterns[0]);
		}

		String[] rollnos = map.get("rollno");
		if (rollnos != null) {
			if (rollnos.length != 1) {
				return Result.err(new Err(
					ErrKind.IllegalArgument,
					"Number must be singular"
				));
			}
			Result<Long, Err> parsed_elem = Parser.parse_long(rollnos[0]);
			if (parsed_elem.isErr()) {
				return Result.err(parsed_elem.err_msg());
			}
			out.setRollno(parsed_elem.unwrap());
		}

		if (patterns == null && rollnos == null) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Search Parameters unfulfilled"
			));
		}
		// System.out.printf("ID: %d, Pattern: %s", out.getRollno(), out.getPattern());

		return Result.ok(out);
	}

	public StudentSearchRequest() {
		rollno = 0;
		pattern = null;
	}
}
