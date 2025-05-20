package org.example.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.types.Err;
import org.example.types.ErrKind;
import org.example.util.LRU;
import org.example.util.Result;

public class BatchData {
	private static LRU<Long, BatchData> cache;
	public static void set_cache(LRU<Long, BatchData> cache) {
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

	public static Result<BatchData, Err> search(long batchid) {
		Optional<BatchData> cache_contents = cache.get(batchid);
		if (cache_contents.isPresent()) {
			return Result.fromOption(cache_contents);
		}

		Result<Connection, Err> result_cnx = Database.get_connection();
		if (result_cnx.isErr()) {
			return Result.err(result_cnx.err_msg());
		}

		try (
			Connection cnx = result_cnx.unwrap();
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT Batch.TeacherID, Student.RollNo, Student.Name FROM Batch JOIN BatchData ON Batch.BatchID = BatchData.BatchID JOIN Student ON BatchData.RollNo = Student.RollNo WHERE Batch.BatchID = ?;"
			);
		) {
			stmt.setLong(1, batchid);
			ResultSet rst = stmt.executeQuery();

			Optional<Long> teacherid = Optional.empty();
			// NOTE: This 20 is hardcoded
			List<Student> students = new ArrayList<>(20);
			while (rst.next()) {
				teacherid = Optional.of(rst.getLong(1));
				long rollno = rst.getLong(2);
				String name = rst.getString(3);

				students.addLast(new Student(rollno, name));
			}

			if (teacherid.isEmpty()) {
				students.clear();
				return Result.err(new Err(
					ErrKind.ElementNotFound,
					"Cannot find batch with the given batchid"
				));
			}
			BatchData data = new BatchData(batchid, teacherid.get(), students);
			cache.put(batchid, data);
			return Result.ok(data);

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

	public static Result<Void, Err> add(long batchid, long rollno) {
		Result<Connection, Err> result_cnx = Database.get_connection();
		if (result_cnx.isErr()) {
			return Result.err(result_cnx.err_msg());
		}

		try (
			Connection cnx = result_cnx.unwrap();
			PreparedStatement stmt = cnx.prepareStatement(
				"INSERT INTO BatchData(batchid, rollno) VALUES(?, ?);"
			);
		) {
			stmt.setLong(1, batchid);
			stmt.setLong(2, rollno);

			int no_of_rows = stmt.executeUpdate();
			if (no_of_rows != 1) {
				return Result.err(new Err(
					ErrKind.InsertionErr,
					"Failed to insert batchid and rollno"
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

	public static Result<Void, Err> add(long batchid, long[] rollnos) {
		Result<Connection, Err> result_cnx = Database.get_connection();
		if (result_cnx.isErr()) {
			return Result.err(result_cnx.err_msg());
		}

		try (
			Connection cnx = result_cnx.unwrap();
			PreparedStatement stmt = cnx.prepareStatement(
				"INSERT INTO BatchData (BatchID, RollNo) VALUES (?, ?);"
			);
		) {
			for (long rollno : rollnos) {
				stmt.setLong(1, batchid);
				stmt.setLong(2, rollno);
				stmt.addBatch();
			}

			int[] no_of_rows = stmt.executeBatch();
			if (no_of_rows.length != rollnos.length) {
				return Result.err(new Err(
					ErrKind.InsertionErr,
					"Failed to insert all of the entries"
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

	public static Result<Void, Err> delete(long batchid, long rollno) {

		Result<Connection, Err> result_cnx = Database.get_connection();
		if (result_cnx.isErr()) {
			return Result.err(result_cnx.err_msg());
		}

		try (
			Connection cnx = result_cnx.unwrap();
			PreparedStatement stmt = cnx.prepareStatement(
				"DELETE FROM BatchData WHERE BatchID = ? AND RollNo = ?;"
			);
		) {
			stmt.setLong(1, batchid);
			stmt.setLong(2, rollno);

			int no = stmt.executeUpdate();
			if (no != 1) {
				return Result.err(new Err(
					ErrKind.DeleteErr,
					"Failed to delete entry"
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

	public static Result<Void, Err> delete(long batchid, long rollnos[]) {
		Result<Connection, Err> optional_cnx = Database.get_connection();
		if (optional_cnx.isErr()) {
			return Result.err(optional_cnx.err_msg());
		}

		try (
			Connection cnx = optional_cnx.unwrap();
			PreparedStatement stmt = cnx.prepareStatement(
				"DELETE FROM BatchData WHERE BatchID = ? AND RollNo = ?;"
			);
		) {

			for (long rollno : rollnos) {
				stmt.setLong(1, batchid);
				stmt.setLong(2, rollno);
				stmt.addBatch();
			}

			int[] no = stmt.executeBatch();
			if (no.length != rollnos.length) {
				return Result.err(new Err(
					ErrKind.DeleteErr,
					"Failed to delete all of the entries"
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

	public static Result<Void, Err> delete_all(long batchid) {
		Result<Connection, Err> result_cnx = Database.get_connection();
		if (result_cnx.isErr()) {
			return Result.err(result_cnx.err_msg());
		}

		try (
			Connection cnx = result_cnx.unwrap();
			PreparedStatement stmt = cnx.prepareStatement(
				"DELETE FROM BatchData WHERE BatchID = ?;"
			);
		) {
			stmt.setLong(1, batchid);
			stmt.executeUpdate();
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
