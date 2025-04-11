package org.example.api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.example.util.Result;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/attendance")
public class Attendance extends HttpServlet {
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
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String rollno_param = req.getParameter("rollno");
		if (rollno_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Cannot set attendance without req"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> parsed_rollno = parse_long(rollno_param);
		if (parsed_rollno.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"RollNo must be numeric"
			);
			resp.flushBuffer();
			return;
		}

		String subject_id_param = req.getParameter("subjectid");
		if (subject_id_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Cannot set attendance without subjectid"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> parsed_subjectid = parse_long(subject_id_param);
		if (parsed_subjectid.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Subject ID must be numeric"
			);
			resp.flushBuffer();
			return;
		}

		String teacher_id_param = req.getParameter("teacherid");
		if (teacher_id_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Teacher ID is required for attendance"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> parsed_teacherid = parse_long(teacher_id_param);
		if (parsed_teacherid.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Teacher ID must be numeric"
			);
			resp.flushBuffer();
			return;
		}

		String period = req.getParameter("period");
		if (period == null) {
			period = "I"; // TODO: Refactor Default Period, there must be something better than this..
		}

		if (!valid_period(period)) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Period must Uppercase Roman Numeral => 'I' to 'VIII'"
			);
			resp.flushBuffer();
			return;
		}

		String date = req.getParameter("date");
		if (date == null) {
			date = default_date();
		}

		Optional<String> valid_date = validate_date(date);
		if (valid_date.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Date must be in YYYY-MM-DD format"
			);
			resp.flushBuffer();
			return;
		}

		try (
			Connection cnx = DriverManager.getConnection(
				"jdbc:postgresql://localhost/college",
				"postgres",
				"Dineshkumar4u!"
			);
		) {
			// TODO: Set Attendance
			Result<Void, String> result = set_attendance(
				cnx, 
				parsed_rollno.get(), 
				parsed_teacherid.get(), 
				parsed_subjectid.get(), 
				period, 
				valid_date.get()
			);

			if (result.isErr()) {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				resp.flushBuffer();
				return;
			}
		} catch (SQLException e) {
			return;
		}

	} 

	// TODO: Delete attendance;
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
	}

	private static Result<Void, String> set_attendance(
		Connection cnx,
		Long student_id,
		Long teacher_id,
		Long subject_id,
		String period
	) {
		String date = default_date();
		return set_attendance(cnx, student_id, teacher_id, subject_id, period, date);
	}

	private static Result<Void, String> set_attendance(
		Connection cnx,
		Long student_id,
		Long teacher_id,
		Long subject_id,
		String period,
		String date
	) {
		try {
			PreparedStatement stmt = cnx.prepareStatement(
				"INSERT INTO Attendance(Day, RollNo, Period, SubjectID, TeacherID) VALUES(?, ?, ?, ?, ?);"
			);
			stmt.setString(1, date);
			stmt.setLong(2, student_id);
			stmt.setString(3, period);
			stmt.setLong(4, subject_id);
			stmt.setLong(5, teacher_id);
			int rows = stmt.executeUpdate();
			if (rows != 1) {
				return Result.err("Something went wrong");
			}
			return Result.ok(null);
		} catch (SQLException e) {
			return Result.err(e.getMessage());
		}
	}

	private static String default_date() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1; // For whatever reason, January is 0 ffs.
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String dateString = String.format("%04d-%02d-%02d", year, month, day);
		return dateString;
	}

	public static boolean valid_period(String input) {
		return (
			input.equals("I")	||
			input.equals("II")	||
			input.equals("III") ||
			input.equals("IV")	||
			input.equals("V")	||
			input.equals("VI")	||
			input.equals("VII") ||
			input.equals("VIII")
		);
	}

	private static Optional<Long> parse_long(String input) {
		try {
			return Optional.of(Long.parseLong(input));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	private static Optional<String> validate_date(String input) {
		String[] delim_str = input.strip().split("-", 3);
		int[] date = Arrays.stream(delim_str).mapToInt(num -> Integer.parseInt(num)).toArray();
		if (date.length != 3)
			return Optional.empty();

		int year = date[0];
		if (year < 1970) // Unix EPOCH
			return Optional.empty();

		int month = date[1];
		if (month < 1 || month > 12)
			return Optional.empty();

		// TODO: Corner case: Invalid dates like 31-02-2004
		int day = date[2];
		if (day < 1 || day > 31)
			return Optional.empty();

		return Optional.of(input);
	} 
}
