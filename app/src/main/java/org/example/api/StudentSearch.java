package org.example.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.Student;

import com.google.gson.Gson;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/student/search")
public class StudentSearch extends HttpServlet {
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
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// String param = req.getParameter("pattern");
		// if (param == null) {
		// 	resp.sendError(
		// 		HttpServletResponse.SC_BAD_REQUEST,
		// 		"Provide a pattern to search"
		// 	);
		// 	resp.setStatus(400);
		// 	resp.flushBuffer();
		// 	return;
		// }
		//
		// Optional<String> oparam = validate_sql(param);
		//
		// if (oparam.isEmpty()) {
		// 	resp.sendError(
		// 		HttpServletResponse.SC_BAD_REQUEST,
		// 		"Provide a valid pattern to search"
		// 	);
		// 	resp.flushBuffer();
		// 	return;
		// }


		// String password = System.getenv("PGSQL_DB_PASSWORD"); -- Not working, I do not know why
		// if (password == null) {
		// 	resp.sendError(
		// 		HttpServletResponse.SC_BAD_REQUEST,
		// 		"Internal Server Problems"
		// 	);
		// 	resp.flushBuffer();
		// 	return;
		// }
		// NOTE: Maybe the tomcat user cannot see the same System.getenv as mine
		String password = "Dineshkumar4u!"; // TODO: Setup Tomcat's environment

		PrintWriter out = resp.getWriter();
		// TODO: Setup a connection pool
		try (
			Connection cnx = DriverManager.getConnection(
				"jdbc:postgresql://localhost/college",
				"postgres",
				password
			);
		) {
			String pattern_param = req.getParameter("pattern");
			if (pattern_param != null) {
				Optional<String> payload = search_by_pattern(cnx, pattern_param);
				if (payload.isEmpty()) {
					resp.sendError(
						HttpServletResponse.SC_BAD_REQUEST,
						"Failed to generate payload"
					);
					resp.flushBuffer();
					return;
				}
				out.write(payload.get());
				out.flush();
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
			Optional<Long> id = parse_long(id_param);
			if (id.isEmpty()) {
				// TODO: Handle error path
				resp.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"ID is not numeric"
				);
				resp.flushBuffer();
				return;
			}

			Optional<String> payload = search_by_id(cnx, id.get());
			if (payload.isEmpty()) {
				resp.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"Failed to generate payload"
				);
				resp.flushBuffer();
				return;
			}
			out.write(payload.get());
			out.flush();

		} catch (Exception e) {
			out.write(e.getMessage());
			resp.setStatus(500); // Internal Server Error
			resp.flushBuffer();
			System.err.println(e.getMessage());
		} finally {
			out.close();
		}
	}

	public static Optional<String> search_by_id(Connection cnx, Long id) {
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
				return Optional.of(payload);
			} else {
				return Optional.empty();
			}
		// TODO: You can do better than this dinesh
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	// TODO: Implement Inset pagination
	public Optional<String> search_by_pattern(Connection cnx, String pattern) {
		String payload;
		Optional<String> result = validate_sql(pattern);
		if (result.isEmpty()) {
			return Optional.empty();
		}
		try {
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT RollNo, Name FROM Student WHERE Name LIKE ? ORDER BY RollNo LIMIT 20;"
			);

			stmt.setString(1, result.get());
			ResultSet rst = stmt.executeQuery();

			List<Student> names = new ArrayList<>();
			while (rst.next()) {
				Long id = rst.getLong(1);
				String name = rst.getString(2);
				names.addLast(new Student(id, name));
			}

			Gson serializer = new Gson();
			payload = serializer.toJson(names.toArray());

			return Optional.of(payload);
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public static Optional<Long> parse_long(String input) {
		try {
			return Optional.of(Long.parseLong(input));
		} catch (Exception e) {
			return Optional.empty();
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
