package org.example.api;

import java.io.IOException;
import java.io.PrintWriter;

import org.example.data.Teacher;
import org.example.types.Err;
import org.example.types.ErrKind;
import org.example.types.extractors.TeacherSearchRequest;
import org.example.util.Response;
import org.example.util.Result;
import org.example.util.Serializer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/teacher/search")
public class TeacherSearch extends HttpServlet {
	private static Result<String, Err> search_and_serialize(TeacherSearchRequest req) {
		if (req.getTeacherid().isPresent()) {
			return Teacher.search(req.getTeacherid().get())
					.and_then(res -> Serializer.serialize(res));
		} else if (req.getPattern() != null) {
			return Teacher.search(req.getPattern())
					.and_then(res -> Serializer.serialize(res));
		} else {
			return Result.err(new Err(
				ErrKind.Unreachable,
				"Exhaustive"
			));
		}
	}


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		try (
			PrintWriter out = resp.getWriter();
		){
			TeacherSearchRequest
				.extract(req.getParameterMap())
				.and_then(search_request -> search_and_serialize(search_request))
				.and_then(results -> Response.send(results, out))
				.or_else(e -> Response.send_err(resp, e));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
