package org.example.types.extractors;

import java.util.Map;

import org.example.types.Err;
import org.example.types.ErrKind;
import org.example.util.Extractor;
import org.example.util.Parser;
import org.example.util.Result;

// TODO: Documentation
public class SubjectSearchRequest implements Extractor<SubjectSearchRequest> {
	private long subjectid;
	private String subjectcode; // pattern
	private String subjectname; // pattern
	public long getSubjectid() {
		return subjectid;
	}
	public void setSubjectid(final long subjectid) {
		this.subjectid = subjectid;
	}
	public String getSubjectcode() {
		return subjectcode;
	}
	public void setSubjectcode(final String subjectcode) {
		this.subjectcode = subjectcode;
	}
	public String getSubjectname() {
		return subjectname;
	}
	public void setSubjectname(final String subjectname) {
		this.subjectname = subjectname;
	}

	public static Result<SubjectSearchRequest, Err> extract(Map<String, String[]> map) {
		SubjectSearchRequest request = new SubjectSearchRequest();
		String[] patterns = map.get("pattern");
		if (patterns != null) {
			if (patterns.length != 1) {
				return Result.err(new Err(
					ErrKind.IllegalArgument,
					"`pattern` must be singular"
				));
			}
			request.setSubjectname(patterns[0]);
		}
		String[] codes = map.get("code");
		if (codes != null) {
			if (codes.length != 1) {
				return Result.err(new Err(
					ErrKind.IllegalArgument,
					"`code` must be singular"
				));
			}
			request.setSubjectcode(codes[0]);
		}
		String[] id = map.get("id");
		if (id != null) {
			if (id.length != 1) {
				return Result.err(new Err(
					ErrKind.IllegalArgument,
					"`code` must be singular"
				));
			}
			Result<Long, Err> parsed_id = Parser.parse_long(id[0]);
			if (parsed_id.isErr()) {
				return Result.err(parsed_id.err_msg());
			}
			request.setSubjectid(parsed_id.unwrap());
		}
		if (patterns == null && codes == null && id == null) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"Search parameters unfulfilled"
			));
		}

		return Result.ok(request);
	}

	public SubjectSearchRequest() {
		this.subjectid = 0;
		this.subjectcode = null;
		this.subjectname = null;
	}
}
