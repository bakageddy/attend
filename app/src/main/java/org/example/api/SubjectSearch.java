package org.example.api;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/subject/search")
public class SubjectSearch extends HttpServlet {
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
		// TODO: Setup up Tomcat's environment
		String password = "Dineshkumar4u!";

		// TODO: Setup a connection pool
		try (
			Connection cnx = DriverManager.getConnection(
				"jdbc:postgresql://localhost/college",
				"postgres",
				password
			);
			PrintWriter out = resp.getWriter();
		) {
		}
	}

	private Result<String, String> search_by_name(Connection cnx, String pattern) {
		if (pattern == null) {
			return Result.err("Pattern cannot be null");
		}

		Optional<String> result = validate_sql(pattern);
		if (result.isEmpty()) {
			return Result.err("Pattern must be alphanumeric, NOT SQL -_-");
		}
		try {
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT SubjectID, SubjectCode, Name FROM Subject WHERE SubjectName LIKE ? LIMIT 20;",
			);
			stmt.setString(result.get());
			ResultSet rst = stmt.executeQuery();


			List<Subject> subjects = new ArrayList<>();
			while (rst.next()) {
				Long subjectid = rst.getLong(1);
				String subjectcode = rst.getString(2);
				String subjectname = rst.getString(3);
				subjects.addLast(new Subject(subjectid, subjectcode, subjectname));
			}
			Gson serializer = new Gson();
			String payload = serializer.toJson();
			return Result.ok(payload);
		} catch(Exception e) {
			Result.err(e.getMessage());
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
