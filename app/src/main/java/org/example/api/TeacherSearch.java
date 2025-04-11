package org.example.api;

import org.example.util.Result;
import org.example.Teacher;

import com.google.code.Gson;

import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.HttpServlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;

@WebServlet(urlPatterns = "/api/teacher/search")
public class TeacherSearch extends HttpServlet {
	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	@Override
	protected void goGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		// TODO: Setup tomcat's environment
		String password = "Dineshkumar4u!";

		// TODO: Setup a connection pool
		try (
			Connection cnx = DriverManager.getConnection(
				"jdbc:postgresql://localhost/college",
				"postgres",
				password;
			);
			PrintWriter out = resp.getWriter();
		) {
			String pattern_param = req.getParameter("pattern");
			if (pattern_param != null) {
				Result<String, Result> payload = search_by_pattern(cnx, pattern_param);
				if (payload.isErr()) {
					resp.sendError(
						HttpServletResponse.SC_BAD_REQUEST,
						payload.err_msg();
					);
					resp.flushBuffer();
					return;
				}
				out.write(payload.unwrap());
				out.flush();
				return;
			}

			String id_param = req.getParameter("teacherid");
			if (id_param == null) {
				resp.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"Must set rollno or pattern"
				);
				resp.flushBuffer();
				return;
			}

			Optional<Long> id = parse_long(id_param);
			if (id.isEmpty()) {
				resp.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"ID must be a number"
				);
				resp.flushBuffer();
				return;
			}
			Result<String, String> payload = search_by_id(id.get());
			if (payload.isErr()) {
				resp.sendError(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					payload.err_msg();
				);
				resp.flushBuffer();
				return;
			}

			out.write(payload);
			out.flush();
			return;

		} catch (Exception e) {
			resp.sendError(e.getMessage());
			resp.flushBuffer();
			return;
		}
	}

	protected Result<String, String> search_by_pattern(Connection cnx, String pattern) {
		Optional<String> result = validate_sql(pattern);
		if (result.isEmpty()) {
			return Result.err("Need valid pattern. Not SQL T_T");
		}
		try {
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT TeacherID, Name FROM Teacher WHERE Name LIKE ? LIMIT 20;"
			);

			stmt.setString(1, result.get());
			ResultSet rst = stmt.executeQuery();

			List<Teacher> teachers = new ArrayList<>();
			while (rst.next()) {
				Long teacherid = rst.getLong(1);
				String name = rst.getString(2);
				teachers.addLast(new Teacher(teacherid, name));
			}

			Gson serializer = new Gson();
			String payload = serializer.toJson(teachers);
			return Result.ok(payload);
		} catch (Exception e) {
			return Result.err("Beep Boop, Error at search by pattern");
		}
	}

	protected Result<String, String> search_by_id(Connection cnx, long id) {
		try {
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT Name FROM Teacher WHERE TeacherID = ?;"
			);
			stmt.setLong(1, id);

			ResultSet rst = stmt.executeQuery(stmt);
			// Expect only one result!
			if (rst.next()) {
				String name = rst.getString(1);
				Gson serializer = new Gson();
				String payload = serializer.toJson(new Teacher(id, name));
				return Result.ok(payload);
			} else {
				return Result.err("No such id");
			}
		// TODO: You can do better than this dinesh
		} catch (Exception e) {
			return Result.err("Beep Boop, Error at search by id");
		}
	}

	public static Optional<String> validate_sql(String input) {
		if (input.contains("DROP") || 
			input.contains("SELECT") || 
			input.contains("UPDATE") ||	
			input.contains("INSERT") ||	
			input.contains(";"))
		{
			return Optional.empty();
		} else {
			return Optional.of(input);
		}
	}
}
