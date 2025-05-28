package org.example.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;

import org.example.types.Err;
import org.example.types.ErrKind;
import org.example.util.Result;

import com.google.errorprone.annotations.CheckReturnValue;

public class Attendance {

	@CheckReturnValue
	public static Result<Void, Err> enter_student(
		long rollno,
		long teacherid,
		long subjectid,
		String period,
		String date
	) {
		Result<Connection, Err> result_cnx = Database.get_connection();
		if (result_cnx.isErr()) {
			return Result.err(result_cnx.err_msg());
		}

		try (
			Connection cnx = result_cnx.unwrap();
			PreparedStatement stmt = cnx.prepareStatement(
				"INSERT INTO Attendance(Day, RollNo, Period, SubjectID, TeacherID) VALUES(?::date, ?, ?::period, ?, ?);"
			);
		) {
			stmt.setString(1, date);
			stmt.setLong(2, rollno);
			stmt.setString(3, period);
			stmt.setLong(4, subjectid);
			stmt.setLong(5, teacherid);
			int rows = stmt.executeUpdate();
			if (rows != 1) {
				return Result.err(new Err(
					ErrKind.InsertionErr,
					"Failed to insert attendance record"
				));
			}
			return Result.ok(null);
		} catch (SQLTimeoutException e) {
			return Result.err(new Err(
				ErrKind.DBTimeout,
				e.getMessage()
			));

		} catch (SQLException e) {
			return Result.err(new Err(
				ErrKind.DBConnectionErr,
				e.getMessage()
			));
		} catch (Exception e) {
			return Result.err(new Err(
				ErrKind.Unreachable,
				e.getMessage()
			));
		}
	}

	public static Result<Void, Err> delete_student(
		long rollno,
		long teacherid,
		long subjectid,
		String period,
		String date
	) {
		Result<Connection, Err> result_cnx = Database.get_connection();
		if (result_cnx.isErr()) {
			return Result.err(result_cnx.err_msg());
		}

		try (
			Connection cnx = result_cnx.unwrap();
			PreparedStatement stmt = cnx.prepareStatement(
				"DELETE FROM Attendance WHERE Day=?::date AND RollNo=? AND TeacherID=? AND SubjectID=? AND Period=?::period;"
			);
		) {
			stmt.setString(1, date);
			stmt.setLong(2, rollno);
			stmt.setLong(3, teacherid);
			stmt.setLong(4, subjectid);
			stmt.setString(5, period);

			int rows = stmt.executeUpdate();
			if (rows != 1) {
				return Result.err(new Err(
					ErrKind.DeleteErr,
					"Failed to delete attendance record"
				));
			}
			return Result.ok(null);
		} catch (SQLTimeoutException e) {
			return Result.err(new Err(
				ErrKind.DBTimeout,
				e.getMessage()
			));

		} catch (SQLException e) {
			return Result.err(new Err(
				ErrKind.DBConnectionErr,
				e.getMessage()
			));
		} catch (Exception e) {
			return Result.err(new Err(
				ErrKind.Unreachable,
				e.getMessage()
			));
		}
	}

	public static Result<Void, Err> enter_batch(
		long batchid,
		long teacherid,
		long subjectid,
		String period,
		String date
	) {
		Result<BatchData, Err> opt_batchdata = BatchData.search(batchid);
		if (opt_batchdata.isErr()) {
			return Result.err(new Err(
				ErrKind.IllegalArgument,
				"BatchID does not exist / it is empty"
			));
		}

		BatchData batchdata = opt_batchdata.unwrap();
		if (batchdata.teacherid != teacherid) {
			return Result.err(new Err(
				ErrKind.IllegalState, 
				"You do not own this batch"
			));
		}

		Result<Connection, Err> optional_cnx = Database.get_connection();
		if (optional_cnx.isErr()) {
			return Result.err(optional_cnx.err_msg());
		}

		try (
			Connection cnx = optional_cnx.unwrap();
			PreparedStatement stmt = cnx.prepareStatement(
				"INSERT INTO Attendance(Day, RollNo, Period, SubjectID, TeacherID) VALUES(?::date, ?, ?::period, ?, ?);"
			);
		) {
			for (Student student : batchdata.students) {
				stmt.setString(1, date);
				stmt.setLong(2, student.rollNo);
				stmt.setString(3, period);
				stmt.setLong(4, subjectid);
				stmt.setLong(5, teacherid);
				stmt.addBatch();
			}

			int[] no = stmt.executeBatch();
			if (no.length != batchdata.students.size()) {
				return Result.err(new Err(
					ErrKind.InsertionErr,
					"Failed to insert Attendance Record(s)"
				));
			}

			return Result.err(null);
		} catch (SQLTimeoutException e) {
			return Result.err(new Err(
				ErrKind.DBTimeout,
				e.getMessage()
			));

		} catch (SQLException e) {
			return Result.err(new Err(
				ErrKind.DBConnectionErr,
				e.getMessage()
			));
		} catch (Exception e) {
			return Result.err(new Err(
				ErrKind.Unreachable,
				e.getMessage()
			));
		}
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
