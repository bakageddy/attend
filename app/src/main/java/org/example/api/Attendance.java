package org.example.api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;
import java.util.Date;

import org.example.util.Result;
import org.example.util.Parser;
import org.example.util.Validator;

import com.zaxxer.hikari.pool.HikariPool;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/attendance/")
public class Attendance extends HttpServlet {
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

		Optional<Long> parsed_rollno = Parser.parse_long(rollno_param);
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

		Optional<Long> parsed_subjectid = Parser.parse_long(subject_id_param);
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

		Optional<Long> parsed_teacherid = Parser.parse_long(teacher_id_param);
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

		if (!Validator.valid_period(period)) {
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

		Optional<String> valid_date = Validator.validate_date(date);
		if (valid_date.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Date must be in YYYY-MM-DD format"
			);
			resp.flushBuffer();
			return;
		}


		HikariPool pool = (HikariPool) getServletContext().getAttribute("cnx_pool");
		try (
			Connection cnx = pool.getConnection();
		) {
			Result<Void, String> result = set_attendance(
				cnx, 
				parsed_rollno.get(), 
				parsed_teacherid.get(), 
				parsed_subjectid.get(), 
				period, 
				valid_date.get()
			);

			if (result.isErr()) {
				resp.sendError(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					result.err_msg()
				);
				resp.flushBuffer();
				return;
			}

			resp.setStatus(HttpServletResponse.SC_OK);
			resp.flushBuffer();
			return;
		} catch (SQLException e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			resp.flushBuffer();
			return;
		}
	} 

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String rollno_param = req.getParameter("rollno");
		if (rollno_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Cannot set attendance without req"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> parsed_rollno = Parser.parse_long(rollno_param);
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

		Optional<Long> parsed_subjectid = Parser.parse_long(subject_id_param);
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

		Optional<Long> parsed_teacherid = Parser.parse_long(teacher_id_param);
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
			period = "I";  // TODO: Refactor Default period
		}
		if (!Validator.valid_period(period)) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Period must be uppercase roman numeral => 'I' to 'VII'"
			);
			resp.flushBuffer();
			return;
		}

		String date = req.getParameter("date");
		if (date == null) {
			date = default_date();
		}

		Optional<String> valid_date = Validator.validate_date(date);
		if (valid_date.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Date must be in YYYY-MM-DD format"
			);
			resp.flushBuffer();
			return;
		}

		HikariPool pool = (HikariPool) getServletContext().getAttribute("cnx_pool");
		try (
			Connection cnx = pool.getConnection();
		) {
			Result<Void, String> result = delete_attendance(
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

			resp.setStatus(HttpServletResponse.SC_OK);
			resp.flushBuffer();
			return;
		} catch (SQLException e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			resp.flushBuffer();
			return;
		}
	}

	private static Result<Void, String> delete_attendance(
		Connection cnx,
		Long student_id,
		Long teacher_id,
		Long subject_id,
		String period
	) {
		String date = default_date();
		return delete_attendance(cnx, student_id, teacher_id, subject_id, period, date);
	}

	private static Result<Void, String> delete_attendance(
		Connection cnx,
		Long student_id,
		Long teacher_id,
		Long subject_id,
		String period,
		String date
	) {
		try {
			PreparedStatement stmt = cnx.prepareStatement(
				"DELETE FROM Attendance WHERE Day=?::date AND RollNo=? AND TeacherID=? AND SubjectID=? AND Period=?::period;"
			);
			stmt.setString(1, date);
			stmt.setLong(2, student_id);
			stmt.setLong(3, teacher_id);
			stmt.setLong(4, subject_id);
			stmt.setString(5, period);
			stmt.executeUpdate();
			return Result.ok(null);
		} catch (Exception e) {
			return Result.err(e.getMessage());
		}
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
				"INSERT INTO Attendance(Day, RollNo, Period, SubjectID, TeacherID) VALUES(?::date, ?, ?::period, ?, ?);"
			);
			// You can do this in validation.
			// DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			// java.sql.Date d = new java.sql.Date(fmt.parse(date).getTime());

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
		} catch (Exception e) {
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

}
