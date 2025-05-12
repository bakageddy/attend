package org.example.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

import org.example.data.Teacher;
import org.example.util.Parser;
import org.example.util.Result;
import org.example.util.Serializer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/teacher/search")
public class TeacherSearch extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pattern_param = req.getParameter("pattern");
		if (pattern_param != null) {
			Result<List<Teacher>, String> result = Teacher.search(pattern_param);
			if (result.isErr()) {
				String err = result.err_msg();
				if (err.equals("No results")) {
					resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
					resp.flushBuffer();
					return;
				}

				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, err);
				resp.flushBuffer();
				return;
			}

			Result<String, String> payload = Serializer.serialize(result.unwrap());
			if (payload.isErr()) {
				resp.sendError(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					payload.err_msg()
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


		String id_param = req.getParameter("id");
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
				"ID must be a number"
			);
			resp.flushBuffer();
			return;
		}

		Result<Teacher, String> result = Teacher.search(id.get());
		if (result.isErr()) {
			resp.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				result.err_msg()
			);
		}

		Result<String, String> payload = Serializer.serialize(result.unwrap());
		if (payload.isErr()) {
			resp.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				payload.err_msg()
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
}
