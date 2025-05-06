package org.example.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.Teacher;
import org.example.util.LRU;
import org.example.util.Parser;
import org.example.util.Result;
import org.example.util.Validator;

import com.google.gson.Gson;
import com.zaxxer.hikari.pool.HikariPool;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/teacher/search")
public class TeacherSearch extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		var ctx = getServletContext();
		HikariPool pool = (HikariPool) ctx.getAttribute("cnx_pool");
		LRU<String, String> pcache = (LRU) ctx.getAttribute("teacher_pattern_cache");
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

				Result<String, String> payload = search_by_pattern(cnx, pattern_param);
				if (payload.isErr()) {
					String err = payload.err_msg();
					if (err.equals("No results for such pattern")) {
						resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
						resp.flushBuffer();
						return;
					}

					resp.sendError(
						HttpServletResponse.SC_BAD_REQUEST,
						err
					);
					resp.flushBuffer();
					return;
				}
				out.write(payload.unwrap());
				out.flush();
				pcache.put(pattern_param, payload.unwrap());
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

			Result<String, String> payload = search_by_id(cnx, id.get());
			if (payload.isErr()) {
				resp.sendError(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					payload.err_msg()
				);
				resp.flushBuffer();
				return;
			}

			out.write(payload.unwrap());
			out.flush();
			return;
		} catch (Exception e) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				e.getMessage()
			);
			resp.flushBuffer();
			return;
		}
	}

	protected Result<String, String> search_by_pattern(Connection cnx, String pattern) {
		Optional<String> result = Validator.validate_sql(pattern);
		if (result.isEmpty()) {
			return Result.err("Need valid pattern. Not SQL T_T");
		}
		try {
			String validated_pattern = result.get();
			if (!validated_pattern.contains("%")) {
				validated_pattern += "%"; // Anchor the pattern
			}

			PreparedStatement exists = cnx.prepareStatement(
				"SELECT 1 FROM Teacher WHERE Name LIKE ? LIMIT 1;"
			);
			exists.setString(1, validated_pattern);
			ResultSet exists_rst = exists.executeQuery();
			if (!exists_rst.next()) {
				exists.close();
				exists_rst.close();
				return Result.err("No results for such pattern");
			}

			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT TeacherID, Name FROM Teacher WHERE Name LIKE ? LIMIT 20;"
			);

			stmt.setString(1, validated_pattern);
			ResultSet rst = stmt.executeQuery();

			List<Teacher> teachers = new ArrayList<>();
			while (rst.next()) {
				Long teacherid = rst.getLong(1);
				String name = rst.getString(2);
				teachers.addLast(new Teacher(teacherid, name));
			}

			Gson serializer = new Gson();
			String payload = serializer.toJson(teachers);

			exists_rst.close();
			exists.close();
			rst.close();
			stmt.close();
			return Result.ok(payload);
		} catch (Exception e) {
			return Result.err("Beep Boop, Error at search by pattern");
		}
	}

	protected Result<String, String> search_by_id(Connection cnx, Long id) {
		try {
			PreparedStatement stmt = cnx.prepareStatement(
					"SELECT Name FROM Teacher WHERE TeacherID = ?;");
			stmt.setLong(1, id);

			ResultSet rst = stmt.executeQuery();
			// Expect only one result!
			if (rst.next()) {
				String name = rst.getString(1);
				Gson serializer = new Gson();
				String payload = serializer.toJson(new Teacher(id, name));

				stmt.close();
				return Result.ok(payload);
			} else {
				stmt.close();
				return Result.err("No such id");
			}
		// TODO: You can do better than this dinesh
		} catch (Exception e) {
			return Result.err("Beep Boop, Error at search by id");
		}
	}
}
