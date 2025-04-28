package org.example.api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.Student;
import org.example.util.Parser;
import org.example.util.Result;
import org.example.util.Validator;

import com.zaxxer.hikari.pool.HikariPool;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * BatchAttendance
 */
@WebServlet(urlPatterns = "/api/attendance/batch")
public class BatchAttendance extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String batchid_param = req.getParameter("batchid");
		if (batchid_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST, 
				"batchid must be set!"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> batchid = Parser.parse_long(batchid_param);
		if (batchid.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST, 
				"batchid must be numeric and positive!"
			);
			resp.flushBuffer();
			return;
		}

		String subjectid_param = req.getParameter("subjectid");
		if (subjectid_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"subjectid must be set!"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> subjectid = Parser.parse_long(subjectid_param);
		if (subjectid.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"subjectid must be numeric and positive!"
			);
			resp.flushBuffer();
			return;
		}

		String teacherid_param =  req.getParameter("teacherid");
		if (teacherid_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"teacherid must be set!"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> teacherid = Parser.parse_long(teacherid_param);
		if (teacherid.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"teacherid must be numeric and positive!"
			);
			resp.flushBuffer();
			return;
		}

		String period_param = req.getParameter("period");
		if (period_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"must set period!"
			);
			resp.flushBuffer();
			return;
		}

		if (!Validator.validate_period(period_param)) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"period must be uppercase roman numeral"
			);
			resp.flushBuffer();
			return;
		}

		String date_param = req.getParameter("date");
		if (date_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"date must be set"
			);
			resp.flushBuffer();
			return;
		}

		Optional<String> date = Validator.validate_date(date_param);
		if (date.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Date must be in the format: YYYY-MM-DD"
			);
			resp.flushBuffer();
			return;
		}

		ServletContext ctx = getServletContext();
		HikariPool pool = (HikariPool) ctx.getAttribute("cnx_pool");
		try (
			Connection cnx = pool.getConnection();
		) {
			Result<Void, String> result = set_attendance(
				cnx,
				batchid.get(),
				teacherid.get(),
				subjectid.get(),
				period_param,
				date.get()
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
			resp.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				e.getMessage()
			);
			resp.flushBuffer();
			return;
		}
	}

	// TODO: this is cachable :)
	private Result<List<Student>, String> fetch_students(Connection cnx, long batchid) {
		try {
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT RollNo FROM Batch JOIN BatchData ON Batch.BatchID = BatchData.BatchID WHERE Batch.BatchID = ?;"
			);
			stmt.setLong(1, batchid);

			ResultSet rst = stmt.executeQuery();
			List<Student> students = new ArrayList<>();
			while (rst.next()) {
				long rollno = rst.getLong(1);
				students.addLast(new Student(rollno, null));
			}
			return Result.ok(students);
		} catch (SQLException e) {
			return Result.err(e.getMessage());
		}
	}

	private Result<Void, String> set_attendance(
		Connection cnx, 
		long batchid, 
		long teacherid, 
		long subjectid, 
		String period, 
		String day
	) {
		try {
			Result<Boolean, String> owns = owns_batch(cnx, batchid, teacherid);
			if (owns.isErr()) {
				return Result.err(owns.err_msg());
			}

			if (!owns.unwrap()) {
				return Result.err("You do not own this batch!");
			}

			Result<List<Student>, String> students = fetch_students(cnx, batchid);
			if (students.isErr()) {
				return Result.err(students.err_msg());
			}

			String query = construct_query(
				day, 
				period, 
				teacherid, 
				subjectid, 
				students.unwrap()
			);
			PreparedStatement stmt = cnx.prepareStatement(query);
			stmt.executeUpdate();

			return Result.ok(null);
		} catch (SQLException e) {
			return Result.err(e.getMessage());
		}
	}

	private String construct_query(
		String day,
		String period,
		long teacherid,
		long subjectid,
		List<Student> students
	) {
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO Attendance(Day, RollNo, Period, SubjectID, TeacherID) VALUES");

		var len = students.size();
		for (int i = 0; i < len; i++) {
			query.append("(")
				.append(day)
				.append(",")
				.append(students.get(i).rollNo)
				.append(",")
				.append(period)
				.append("::period,")
				.append(subjectid)
				.append(",")
				.append(teacherid);
			String dec = i == len - 1 ? ");" : "),";
			query.append(dec);
		}
		query.append(";");
		return query.toString();
	}

	// I have to redundantly do this since there is no authentication
	private Result<Boolean, String> owns_batch(Connection cnx, long batchid, long teacherid) {
		try {
			PreparedStatement stmt = cnx.prepareStatement("SELECT 1 FROM Batch WHERE BatchID=? AND TeacherID=?;");
			stmt.setLong(1, batchid);
			stmt.setLong(2, teacherid);
			ResultSet rst = stmt.executeQuery();
			if (rst.next()) {
				long result = rst.getLong(1);
				return Result.ok(result == 1);
			} else {
				return Result.err("Result Set has too many elements");
			}
		} catch (SQLException e) {
			return Result.err(e.getMessage());
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO: Think if batch deleting attendance is a good idea...
		// Maybe, maybe not
		// Do i need to delete?
		resp.setStatus(418); // I am a teapot
	}
}
