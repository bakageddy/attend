package org.example.api;

import java.io.IOException;
import java.io.PrintWriter;

import org.example.data.Student;
import org.example.types.Err;
import org.example.types.extractors.StudentSearchRequest;
import org.example.util.Response;
import org.example.util.Result;
import org.example.util.Serializer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/student/search")
public class StudentSearch extends HttpServlet {
	private static Result<String, Err> search(StudentSearchRequest req) {
		if (req.getPattern() != null) {
			return Student.search(req.getPattern())
					.and_then(res -> Serializer.serialize(res));
		} else {
			return Student.search(req.getRollno())
					.and_then(res -> Serializer.serialize(res));
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
		throws ServletException, IOException {
		try (
			PrintWriter out = resp.getWriter();
		) {
			StudentSearchRequest
				.extract(req.getParameterMap())
				.and_then(search_request -> search(search_request))
				.and_then(results -> Response.send(results, out))
				.or_else(e -> Response.send_err(resp, e));
			resp.flushBuffer();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
