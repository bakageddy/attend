package org.example.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import org.example.util.Result;
import org.example.util.Validator;

import com.google.errorprone.annotations.CheckReturnValue;

public class Attendance {
	public static Result<Void, String> enter_student(
		long rollno,
		long teacherid,
		long subjectid,
		String period,
		String date
	) {
		boolean valid_period = Validator.validate_period(period);
		if (!valid_period) {
			return Result.err("Period must be Upper Case Roman Numeral I-VIII");
		}

		Optional<String> valid_date = Validator.validate_date(date);
		if (valid_date.isEmpty()) {
			return Result.err("Date must be in the format YYYY-MM-DD");
		}

		Optional<Connection> optional_cnx = Database.get_connection().asOption();
		if (optional_cnx.isEmpty()) {
			return Result.err("Failed to acquire connection");
		}

		try (
			Connection cnx = optional_cnx.get();
		) {
			PreparedStatement stmt = cnx.prepareStatement(
				"INSERT INTO Attendance(Day, RollNo, Period, SubjectID, TeacherID) VALUES(?::date, ?, ?::period, ?, ?);"
			);
			stmt.setString(1, date);
			stmt.setLong(2, rollno);
			stmt.setString(3, period);
			stmt.setLong(4, subjectid);
			stmt.setLong(5, teacherid);
			int rows = stmt.executeUpdate();
			if (rows != 1) {
				return Result.err("Something went wrong. Cannot put attendance");
			}
			return Result.ok(null);
		} catch (Exception e) {
			return Result.err("NOT IMPLEMENTED");
		}
	}

	public static Result<Void, String> delete_student(
		long rollno,
		long teacherid,
		long subjectid,
		String period,
		String date
	) {
		boolean valid_period = Validator.validate_period(period);
		if (!valid_period) {
			return Result.err("Period must be Upper Case Roman Numeral I-VIII");
		}

		Optional<String> valid_date = Validator.validate_date(date);
		if (valid_date.isEmpty()) {
			return Result.err("Date must be in the format YYYY-MM-DD");
		}

		Optional<Connection> optional_cnx = Database.get_connection().asOption();
		if (optional_cnx.isEmpty()) {
			return Result.err("Failed to acquire connection");
		}

		try (
			Connection cnx = optional_cnx.get();
		) {
			PreparedStatement stmt = cnx.prepareStatement(
				"DELETE FROM Attendance WHERE Day=?::date AND RollNo=? AND TeacherID=? AND SubjectID=? AND Period=?::period;"
			);
			stmt.setString(1, date);
			stmt.setLong(2, rollno);
			stmt.setLong(3, teacherid);
			stmt.setLong(4, subjectid);
			stmt.setString(5, period);

			int rows = stmt.executeUpdate();
			if (rows != 1) {
				return Result.err("Something went wrong. Cannot delete attendance");
			}
			return Result.ok(null);
		} catch (Exception e) {
			return Result.err("NOT IMPLEMENTED");
		}
	}

	public static Result<Void, String> enter_batch(
		long batchid,
		long teacherid,
		long subjectid,
		String period,
		String date
	) {
		if (!Validator.validate_period(period)) {
			return Result.err("Period must be roman-numeral: I-VIII");
		}

		Optional<String> valid_date = Validator.validate_date(date);
		if (valid_date.isEmpty()) {
			return Result.err("Date must be in the format YYYY-MM-DD");
		}

		Optional<BatchData> opt_batchdata = BatchData.search(batchid);
		if (opt_batchdata.isEmpty()) {
			return Result.err("There is no batch with the given ID");
		}

		BatchData batchdata = opt_batchdata.get();
		if (batchdata.teacherid != teacherid) {
			return Result.err("You do not own this batch!");
		}

		String query = construct_query(
			date,
			period,
			teacherid,
			subjectid,
			batchdata.students
		);

		Optional<Connection> optional_cnx = Database.get_connection().asOption();
		if (optional_cnx.isEmpty()) {
			return Result.err("Failed to acquire Connection from Database");
		}

		try (
			Connection cnx = optional_cnx.get();
		) {

			PreparedStatement stmt = cnx.prepareStatement(query);
			int no = stmt.executeUpdate();
			if (no != batchdata.students.size()) {
				return Result.err("Failed to insert all the students");
			}
			return Result.err(null);
		} catch (Exception e) {
			return Result.err(e.getMessage());
		}
	}

	@CheckReturnValue
	private static String construct_query(
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
			query.append("('")
				.append(day)
				.append("'::date,")
				.append(students.get(i).rollNo)
				.append(",'")
				.append(period)
				.append("'::period,")
				.append(subjectid)
				.append(",")
				.append(teacherid);
			String dec = i == len - 1 ? ");" : "),";
			query.append(dec);
		}
		query.append(";");
		return query.toString();
	}

	@CheckReturnValue
	public static Result<Void, String> delete_batch(
		long batchid,
		long teacherid,
		long subjectid,
		String period,
		String date
	) {
		return Result.err("NOT IMPLEMENTED");
	}
}
