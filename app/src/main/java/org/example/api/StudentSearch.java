package org.example.api;

import java.io.IOException;
import java.io.Writer;

import org.example.data.Student;
import org.example.types.Err;
import org.example.types.ErrKind;
import org.example.types.extractors.StudentSearchRequest;
import org.example.util.Result;
import org.example.util.Serializer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/student/search")
public class StudentSearch extends HttpServlet {
	private static Result<Void, Err> search_and_serialize_to(StudentSearchRequest req, Writer dst) {
		if (req.getPattern() != null) {
			return Student.search(req.getPattern())
					.and_then(res -> Serializer.serialize_to(res, dst));
		} else {
			return Student.search(req.getRollno())
					.and_then(res -> Serializer.serialize_to(res, dst));
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
		throws ServletException, IOException {
		try {
			Writer out = resp.getWriter();
			Result<Void, Err> result = Student.extract(req.getParameterMap())
				.and_then(search_request -> search_and_serialize_to(search_request, out));
			if (result.isErr()) {
				Err e = result.err_msg();
				switch (e.kind) {
					case ElementNotFound:
						resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
						break;
					case OutOfMemory:
					case ClassNotFound:
					case IllegalState:
					case DBTimeout:
					case JsonIOError:
					case JsonSerializeError:
						resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						break;
					default: {
						resp.sendError(
							HttpServletResponse.SC_BAD_REQUEST,
							e.toString()
						);
					}
				}
			} else {
				resp.setStatus(HttpServletResponse.SC_OK);
			}
			resp.flushBuffer();
			return;
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
