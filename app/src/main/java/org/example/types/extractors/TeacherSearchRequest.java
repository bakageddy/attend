package org.example.types.extractors;

import java.util.Map;

import org.example.types.Err;
import org.example.types.ErrKind;
import org.example.util.Extractor;
import org.example.util.Parser;
import org.example.util.Result;

public class TeacherSearchRequest implements Extractor<TeacherSearchRequest> {
	long teacherid;
	String pattern;
	public long getTeacherid() {
		return teacherid;
	}
	public void setTeacherid(long teacherid) {
		this.teacherid = teacherid;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public static Result<TeacherSearchRequest, Err> extract(Map<String, String[]> map) {
		TeacherSearchRequest out = new TeacherSearchRequest();
		String[] patterns = map.get("pattern");
		if (patterns != null) {
			if (patterns.length != 1) {
				return Result.err(new Err(
					ErrKind.IllegalArgument,
					"pattern must be singular"
				));
			}
			out.setPattern(patterns[0]);
		}

		String[] ids = map.get("id");
		if (ids != null) {
			if (patterns.length != 1) {
				return Result.err(new Err(
					ErrKind.IllegalArgument,
					"rollno must be singular"
				));
			}
			Result<Long, Err> parsed_id = Parser.parse_long(ids[0]);
			if (parsed_id.isErr()) {
				return Result.err(parsed_id.err_msg());
			}
			out.setTeacherid(parsed_id.unwrap());
		}

		if (patterns == null && ids == null) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Search Parameters unfulfilled"
			));
		
		}
		return Result.ok(out);
	}
}
