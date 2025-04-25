package org.example.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.Student;
import org.example.util.Result;
import org.example.util.LRU;
import org.example.util.Parser;
import org.example.util.Validator;

import com.google.gson.Gson;
import com.zaxxer.hikari.pool.HikariPool;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/student/search")
public class StudentSearch extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ServletContext ctx = getServletContext();
		HikariPool pool = (HikariPool) ctx.getAttribute("cnx_pool");
		LRU<String, String> pcache = (LRU<String, String>) ctx.getAttribute("student_pattern_cache");
		LRU<Long, String> icache = (LRU<Long, String>) ctx.getAttribute("student_id_cache");
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
				if (!payload.isErr()) {
					out.write(payload.unwrap());
					out.flush();
					pcache.put(pattern_param, payload.unwrap());
					return;
				}

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

			Long student_id = id.get();
			Result<String, String> payload = search_by_id(cnx, student_id);
			if (payload.isErr()) {
				resp.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					payload.err_msg()
				);
				resp.flushBuffer();
				return;
			}

			out.write(payload.unwrap());
			out.flush();
		} catch (Exception e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage()); // Internal Server Error
			resp.flushBuffer();
			System.err.println(e.getMessage());
		}
	}

	// TODO: Implement Error enums
	public static Result<String, String> search_by_id(Connection cnx, Long id) {
		String payload;

		try {
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT Name FROM Student WHERE RollNo = ?;"
			);

			stmt.setLong(1, id);
			ResultSet rst = stmt.executeQuery();

			// Expect only one result
			if (rst.next()) {
				String name = rst.getString(1);
				Gson serializer = new Gson();
				payload = serializer.toJson(new Student(id, name));

				// TODO: Duplicate code. Do something?
				stmt.close();
				rst.close();
				return Result.ok(payload);
			} else {
				// TODO: Duplicate code. Do something?
				stmt.close();
				rst.close();
				return Result.err("RollNo not found");
			}
		// TODO: You can do better than this dinesh
		} catch (Exception e) {
			return Result.err(e.getMessage());
		}
	}

	// TODO: Implement Inset pagination
	// TODO: Implement Error enums
	public Result<String, String> search_by_pattern(Connection cnx, String pattern) {
		String payload;
		Optional<String> result = Validator.validate_sql(pattern);
		if (result.isEmpty()) {
			return Result.err("Pattern must be alphanumeric, not SQL -__-");
		}
		try {
			String validated_pattern = result.get();
			if (!validated_pattern.endsWith("%"))
				validated_pattern += "%"; // Anchor the pattern
			
			PreparedStatement exists = cnx.prepareStatement(
				"SELECT 1 FROM Student WHERE Name LIKE ? LIMIT 1;"
			);
			exists.setString(1, validated_pattern);

			ResultSet exists_rst = exists.executeQuery();
			if (!exists_rst.next()) {
				return Result.err("No results for such pattern");
			}
			
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT RollNo, Name FROM Student WHERE Name LIKE ? ORDER BY RollNo LIMIT 20;"
			);
			stmt.setString(1, validated_pattern);

			ResultSet rst = stmt.executeQuery();

			List<Student> names = new ArrayList<>();
			while (rst.next()) {
				Long id = rst.getLong(1);
				String name = rst.getString(2);
				names.addLast(new Student(id, name));
			}

			Gson serializer = new Gson();
			payload = serializer.toJson(names.toArray());

			exists.close();
			stmt.close();
			return Result.ok(payload);
		} catch (Exception e) {
			return Result.err(e.getMessage());
		}
	}
}
