package org.example.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.data.Subject;
import org.example.util.LRU;
import org.example.util.Parser;
import org.example.util.Result;
import org.example.util.Serializer;
import org.example.util.Validator;

import com.google.gson.Gson;
import com.zaxxer.hikari.pool.HikariPool;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/subject/search")
public class SubjectSearch extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pattern_param = req.getParameter("pattern");

		if (pattern_param != null) {
			Result<List<Subject>, String> result = Subject.search(pattern_param);
			if (result.isErr()) {
				String err = result.err_msg();
				if (err.equals("No results")) {
					resp.sendError(HttpServletResponse.SC_NO_CONTENT);
					resp.flushBuffer();
					return;
				}
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, result.err_msg());
				resp.flushBuffer();
				return;
			}

			Result<String, String> payload = Serializer.serialize(result.unwrap());
			if (payload.isErr()) {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, payload.err_msg());
			}

			PrintWriter out = resp.getWriter();
			out.write(payload.unwrap());
			out.flush();
			out.close();
			return;
		}

		String code_param = req.getParameter("code");
		if (code_param != null ) {
			Result<List<Subject>, String> result = Subject.search_code(code_param);
			if (result.isErr()) {
				String err = result.err_msg();
				if (err.equals("No results")) {
					resp.sendError(HttpServletResponse.SC_NO_CONTENT);
					resp.flushBuffer();
					return;
				}
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, result.err_msg());
				resp.flushBuffer();
				return;
			}

			Result<String, String> payload = Serializer.serialize(result.unwrap());
			if (payload.isErr()) {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, payload.err_msg());
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
				"Must set id or code or pattern"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> parsed_long = Parser.parse_long(id_param);
		if (parsed_long.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"ID must be numeric"
			);
			resp.flushBuffer();
			return;
		}

		Result<Subject, String> result = Subject.search(parsed_long.get());
		if (result.isErr()) {
			resp.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				result.err_msg()
			);
			resp.flushBuffer();
			return;
		}

		Result<String, String> payload = Serializer.serialize(result.unwrap());
		if (payload.isErr()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				result.err_msg()
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
