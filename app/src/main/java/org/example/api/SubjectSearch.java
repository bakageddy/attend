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

import org.example.Subject;
import org.example.util.LRU;
import org.example.util.Result;
import org.example.util.Parser;
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

		var ctx = getServletContext();
		HikariPool pool = (HikariPool) ctx.getAttribute("cnx_pool");
		LRU<String, String> pcache = (LRU) ctx.getAttribute("subject_pattern_cache");

		try (
			Connection cnx = pool.getConnection();
			PrintWriter out = resp.getWriter();
		) {
			String pattern_param = req.getParameter("pattern");
			if (pattern_param != null) {
				Optional<String> cache_content = pcache.get(pattern_param);
				if (!cache_content.isEmpty()) {
					out.write(cache_content.get());
					out.flush();
					return;
				}

				Result<String, String> result = search_by_name(cnx, pattern_param);
				if (result.isErr()) {
					resp.sendError(
						HttpServletResponse.SC_BAD_REQUEST,
						result.err_msg()
					);
					resp.flushBuffer();
					return;
				}
				out.write(result.unwrap());
				out.flush();
				pcache.put(pattern_param, cache_content.get());
				return;
			}
			String code_param = req.getParameter("code");
			if (code_param != null ) {
				Result<String, String> result = search_by_code(cnx, code_param);
				if (result.isErr()) {
					resp.sendError(
						HttpServletResponse.SC_BAD_REQUEST,
						result.err_msg()
					);
					resp.flushBuffer();
					return;
				}
				out.write(result.unwrap());
				out.flush();
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
			Result<String, String> result = search_by_id(cnx, parsed_long.get());
			if (result.isErr()) {
				resp.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					result.err_msg()
				);
				resp.flushBuffer();
				return;
			}
			out.write(result.unwrap());
			out.flush();
		} catch (Exception e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			resp.flushBuffer();
			return;
		}
	}

	private Result<String, String> search_by_id(Connection cnx, Long id) {
		try {
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT SubjectID, SubjectCode, Name FROM Subject WHERE SubjectID = ?;"
			);
			stmt.setLong(1, id);
			ResultSet rst = stmt.executeQuery();
			// Expect only one element
			if (rst.next()) {
				String subject_code = rst.getString(2);
				String subject_name = rst.getString(3);

				Gson serializer = new Gson();
				String payload = serializer.toJson(new Subject(id, subject_code, subject_name));

				return Result.ok(payload);
			} else {
				return Result.err("ID not found");
			}
		} catch (SQLException e) {
			return Result.err(e.getMessage());
		} catch (Exception e) {
			return Result.err("Beep boop, error at subject:search_by_id");
		}
	}

	private Result<String, String> search_by_code(Connection cnx, String pattern) {
		if (pattern == null) {
			return Result.err("Pattern cannot be null");
		}

		Optional<String> result = Validator.validate_sql(pattern);
		if (result.isEmpty()) {
			return Result.err("Pattern must be alphanumeric, NOT SQL -__-");
		}
		try {
			String validated_code = result.get();
			if (!validated_code.endsWith("%")) {
				validated_code += '%';
			}

			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT SubjectID, SubjectCode, Name FROM Subject WHERE SubjectCode LIKE ? LIMIT 20;"
			);

			stmt.setString(1, validated_code);
			ResultSet rst = stmt.executeQuery();

			List<Subject> subjects = new ArrayList<>();
			while (rst.next()) {
				Long subject_id = rst.getLong(1);
				String subject_code = rst.getString(2);
				String subject_name = rst.getString(3);
				subjects.addLast(new Subject(subject_id, subject_code, subject_name));
			}
			Gson serializer = new Gson();
			String payload = serializer.toJson(subjects);
			return Result.ok(payload);
		// TODO: Better than this please
		} catch (SQLException e) {
			return Result.err(e.getMessage());
		} catch (Exception e) {
			return Result.err("Beep boop, error at subject:search_by_code");
		}
	}

	private Result<String, String> search_by_name(Connection cnx, String pattern) {
		if (pattern == null) {
			return Result.err("Pattern cannot be null");
		}

		Optional<String> valid_pattern = Validator.validate_sql(pattern);
		if (valid_pattern.isEmpty()) {
			return Result.err("Pattern must be alphanumeric, NOT SQL -_-");
		}
		try {
			String valid = valid_pattern.get();
			if (!valid.endsWith("%")) {
				valid += '%';
			}

			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT SubjectID, SubjectCode, Name FROM Subject WHERE Name LIKE ? LIMIT 20;"
			);
			stmt.setString(1, valid);
			ResultSet rst = stmt.executeQuery();

			List<Subject> subjects = new ArrayList<>();
			while (rst.next()) {
				Long subjectid = rst.getLong(1);
				String subjectcode = rst.getString(2);
				String subjectname = rst.getString(3);
				subjects.addLast(new Subject(subjectid, subjectcode, subjectname));
			}

			Gson serializer = new Gson();
			String payload = serializer.toJson(subjects);
			return Result.ok(payload);

		} catch(SQLException e) {
			return Result.err(e.getMessage());
		} catch(Exception e) {
			return Result.err(e.getMessage());
		}

	}
}
