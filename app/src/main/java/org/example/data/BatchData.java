package org.example.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.util.LRU;
import org.example.util.Result;

public class BatchData {
	private static LRU<Long, List<Student>> cache;
	public static void set_cache(LRU<Long, List<Student>> cache) {
		if (BatchData.cache != null) {
			BatchData.cache.flush();
		}

		BatchData.cache = cache;
	}

	public static void delete_cache() {
		if (BatchData.cache == null) return;
		BatchData.cache.flush();
		BatchData.cache = null;
	}

	public static Optional<BatchData> search(long batchid) {
		Optional<Connection> optional_cnx = Database.get_connection().asOption();
		if (optional_cnx.isEmpty()) {
			return Optional.empty();
		}

		try (
			Connection cnx = optional_cnx.get();
		) {
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT Teacher.TeacherID, Student.RollNo, Student.Name FROM BatchData JOIN Student ON BatchData.RollNo = Student.RollNo WHERE BatchData.BatchID = ?;"
			);
			stmt.setLong(1, batchid);
			ResultSet rst = stmt.executeQuery();

			Optional<Long> teacherid = Optional.empty();
			List<Student> students = new ArrayList<>();
			while (rst.next()) {
				teacherid = Optional.of(rst.getLong(1));
				long rollno = rst.getLong(2);
				String name = rst.getString(3);

				students.addLast(new Student(rollno, name));
			}

			if (teacherid.isEmpty()) {
				students.clear();
				return Optional.empty();
			}
			return Optional.of(new BatchData(batchid, teacherid.get(), students));

		} catch (Exception e) {
			return Optional.empty();
		}

	}

	public static Result<Void, String> add(long batchid, long rollno) {
		Optional<Connection> optional_cnx = Database.get_connection().asOption();
		if (optional_cnx.isEmpty()) {
			return Result.err("Failed to acquire connection");
		}

		try (
			Connection cnx = optional_cnx.get();
		) {
			PreparedStatement stmt = cnx.prepareStatement(
				"INSERT INTO BatchData(batchid, rollno) VALUES(?, ?);"
			);
			stmt.setLong(1, batchid);
			stmt.setLong(2, rollno);

			int no_of_rows = stmt.executeUpdate();
			if (no_of_rows != 1) {
				return Result.err("BEEP BOOP, Insert failed");
			}

			return Result.ok(null);
		} catch (Exception e) {
			return Result.err(e.getMessage());
		}
	}

	public static Result<Void, String> add(long batchid, long[] rollnos) {
		Optional<Connection> optional_cnx = Database.get_connection().asOption();
		if (optional_cnx.isEmpty()) {
			return Result.err("Failed to acquire connection");
		}

		try (
			Connection cnx = optional_cnx.get();
		) {
			// String query = construct_query(batchid, rollnos);
			PreparedStatement stmt = cnx.prepareStatement(
				"INSERT INTO BatchData (BatchID, RollNo) VALUES (?, ?);"
			);
			for (long rollno : rollnos) {
				stmt.setLong(1, batchid);
				stmt.setLong(2, rollno);
				stmt.addBatch();
			}

			int[] no_of_rows = stmt.executeBatch();
			if (no_of_rows.length != rollnos.length) {
				return Result.err("BEEP BOOP, failed to add students");
			}

			return Result.ok(null);
		} catch (Exception e) {
			return Result.err(e.getMessage());
		}
	}

	public static Result<Void, String> delete(long batchid, long rollno) {

		Optional<Connection> optional_cnx = Database.get_connection().asOption();
		if (optional_cnx.isEmpty()) {
			return Result.err("Failed to acquire connection");
		}

		try (
			Connection cnx = optional_cnx.get();
		) {
			PreparedStatement stmt = cnx.prepareStatement("DELETE FROM BatchData WHERE BatchID = ? AND RollNo = ?;");
			stmt.setLong(1, batchid);
			stmt.setLong(2, rollno);

			int no = stmt.executeUpdate();
			if (no != 1) {
				return Result.err("BEEP BOOP, cannot delete student from batch");
			}

			return Result.ok(null);
		} catch (Exception e) {
			return Result.err(e.getMessage());
		}
	}

	public static Result<Void, String> delete(long batchid, long rollnos[]) {
		Optional<Connection> optional_cnx = Database.get_connection().asOption();
		if (optional_cnx.isEmpty()) {
			return Result.err("Failed to acquire connection");
		}
		try (
			Connection cnx = optional_cnx.get();
		) {
			PreparedStatement stmt = cnx.prepareStatement(
				"DELETE FROM BatchData WHERE BatchID = ? AND RollNo = ?;"
			);

			for (long rollno : rollnos) {
				stmt.setLong(1, batchid);
				stmt.setLong(2, rollno);
				stmt.addBatch();
			}

			int[] no = stmt.executeBatch();
			if (no.length != rollnos.length) {
				return Result.err("BEEP BOOP, cannot delete student from batch");
			}

			return Result.ok(null);
		} catch (Exception e) {
			return Result.err(e.getMessage());
		}
	}

	public static Result<Void, String> delete_all(long batchid) {
		Optional<Connection> optional_cnx = Database.get_connection().asOption();
		if (optional_cnx.isEmpty()) {
			return Result.err("Failed to acquire connection");
		}

		try (
			Connection cnx = optional_cnx.get();
		) {
			PreparedStatement stmt = cnx.prepareStatement("DELETE FROM BatchData WHERE BatchID = ?;");
			stmt.setLong(1, batchid);

			stmt.executeUpdate();
			return Result.ok(null);
		} catch (Exception e) {
			return Result.err(e.getMessage());
		}
	}

	public long batchid;
	public long teacherid;
	public List<Student> students = null;

	public BatchData(long batchid, long teacherid) {
		this.batchid = batchid;
		this.teacherid = teacherid;
		this.students = null;
	}

	public BatchData(long batchid, long teacherid, List<Student> students) {
		this.batchid = batchid;
		this.teacherid = teacherid;
		this.students = students;
	}
}
