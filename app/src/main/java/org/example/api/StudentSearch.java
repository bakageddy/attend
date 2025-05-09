package org.example.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

import org.example.data.Student;
import org.example.util.Parser;
import org.example.util.Result;
import org.example.util.Serializer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/student/search")
public class StudentSearch extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pattern_param = req.getParameter("pattern");

		if (pattern_param != null) {
			Result<List<Student>, String> result = Student.search(pattern_param);
			if (result.isErr()) {
				if (result.err_msg().equals("No results")) {
					resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
					resp.flushBuffer();
					return;
				}
				resp.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					result.err_msg()
				);
				resp.flushBuffer();
				return;
			}
			
			Result<String, String> payload = Serializer.serialize(result.unwrap());
			if (payload.isErr()) {
				resp.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"Failed to serialize content to JSON"
				);
				resp.flushBuffer();
				return;
			}

			PrintWriter out = resp.getWriter();
			out.write(payload.unwrap());
			out.flush();
			out.close();
			return;
		}

		String id_param = req.getParameter("rollno");
		if (id_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Must set rollno or pattern"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> id = Parser.parse_long(id_param);
		if (id.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"ID is not numeric"
			);
			resp.flushBuffer();
			return;
		}

		Result<Student, String> result = Student.search(id.get());
		if (result.isErr()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				result.err_msg()
			);
			resp.flushBuffer();
			return;
		}

		Result<String, String> payload = Serializer.serialize(result.unwrap());
		if (payload.isErr()) {
			resp.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				"Failed to serialize to JSON"
			);
			resp.flushBuffer();
			return;
		}

		// TODO: Duplicate code
		PrintWriter out = resp.getWriter();
		out.write(payload.unwrap());
		out.flush();
		out.close();
		return;
	}
}
