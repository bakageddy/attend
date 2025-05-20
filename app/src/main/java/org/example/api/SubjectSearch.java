package org.example.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.example.data.Subject;
import org.example.types.Err;
import org.example.types.extractors.SubjectSearchRequest;
import org.example.util.Response;
import org.example.util.Result;
import org.example.util.Serializer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/subject/search")
public class SubjectSearch extends HttpServlet {
	private static Result<Void, Err> search_and_serialize_to(SubjectSearchRequest req, Writer dst) {
		if (req.getSubjectname() != null) {
			return Subject.search(req.getSubjectname())
				.and_then(results -> Serializer.serialize_to(results, dst));
		} else if (req.getSubjectcode() != null) {
			return Subject.search_code(req.getSubjectcode())
				.and_then(results -> Serializer.serialize_to(results, dst));
		} else {
			return Subject.search(req.getSubjectid())
				.and_then(results -> Serializer.serialize_to(results, dst));
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try (
			PrintWriter out = resp.getWriter();
		) {
			SubjectSearchRequest
				.extract(req.getParameterMap())
				.and_then(search_request -> search_and_serialize_to(search_request, out))
				.or_else(e -> Response.send_err(resp, e));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
