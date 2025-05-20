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

public class Batch {
	private static LRU<Long, List<Batch>> batch_owner_cache = null;
	private static LRU<String, List<Batch>> batch_pattern_cache = null;

	public static void set_owner_cache(LRU<Long, List<Batch>> cache) {
		if (Batch.batch_owner_cache != null) {
			Batch.batch_owner_cache.flush();
		}
		Batch.batch_owner_cache = cache;
	}

	public static void set_pattern_cache(LRU<String, List<Batch>> cache) {
		if (Batch.batch_pattern_cache != null) {
			Batch.batch_pattern_cache.flush();
		}
		Batch.batch_pattern_cache = cache;
	}

	public static void delete_owner_cache() {
		if (batch_owner_cache != null)
			Batch.batch_owner_cache.flush();
	}

	public static void delete_pattern_cache() {
		if (batch_pattern_cache != null)
			Batch.batch_pattern_cache.flush();
	}

	public static Result<Batch, Err> search(long batchid) {
		Result<Connection, Err> result_cnx = Database.get_connection();
		if (result_cnx.isErr()) {
			return Result.err(result_cnx.err_msg());
		}
		try (
			Connection cnx = result_cnx.unwrap();
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT BatchID, TeacherID, Name FROM Batch WHERE BatchID = ?;"
			);
		) {
			stmt.setLong(1, batchid);
			ResultSet rst = stmt.executeQuery();

			if (!rst.next()) {
				return Result.err(new Err(
					ErrKind.ElementNotFound,
					"Cannot find batch with the given ID"
				));
			}

			long teacherid = rst.getLong(2);
			String name = rst.getString(3);
			return Result.ok(new Batch(batchid, teacherid, name));
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

	public static Result<List<Batch>, Err> search(String pattern) {

		Optional<List<Batch>> cache_contents = batch_pattern_cache.get(pattern);
		if (cache_contents.isPresent()) {
			return Result.ok(cache_contents.get());
		}

		String param = pattern;
		if (!pattern.endsWith("%")) {
			param += "%";
		}

		Result<Connection, Err> optional_cnx = Database.get_connection();
		if (optional_cnx.isErr()) {
			return Result.err(optional_cnx.err_msg());
		}

		try (
			Connection cnx = optional_cnx.unwrap();
			PreparedStatement exists = cnx.prepareStatement(
				"SELECT 1 FROM Batch WHERE Name LIKE ? LIMIT 1;"
			);
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT BatchID, TeacherID, Name FROM Batch WHERE Name LIKE ? LIMIT 20;"
			);
		) {
			exists.setString(1, param);
			ResultSet exists_rst = exists.executeQuery();

			if (!exists_rst.next()) {
				return Result.err(new Err(
					ErrKind.ElementNotFound,
					"Cannot find any batch with the given name"
				));
			}

			stmt.setString(1, param);

			ResultSet rst = stmt.executeQuery();
			// NOTE: This is hardcoded, REFACTOR to use Inset Pagination
			List<Batch> batches = new ArrayList<>(20);
			while (rst.next()) {
				long batchid = rst.getLong(1);
				long teacherid = rst.getLong(2);
				String name = rst.getString(3);
				batches.addLast(new Batch(batchid, teacherid, name));
			}

			batch_pattern_cache.put(pattern, batches);
			return Result.ok(batches);
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

	public static Result<List<Batch>, Err> search_teacherid(long teacherid) {
		Optional<List<Batch>> cache_contents = batch_owner_cache.get(teacherid);
		if (cache_contents.isPresent()) {
			return Result.ok(cache_contents.get());
		}

		Result<Connection, Err> optional_cnx = Database.get_connection();
		if (optional_cnx.isErr()) {
			return Result.err(optional_cnx.err_msg());
		}

		try (
			Connection cnx = optional_cnx.unwrap();
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT BatchID, Name FROM Batch WHERE TeacherID = ? LIMIT 20;"
			);
		) {
			stmt.setLong(1, teacherid);
			ResultSet rst = stmt.executeQuery();

			// NOTE: 20 is hardcoded, use Inset pagination
			List<Batch> batches = new ArrayList<>(20);
			while (rst.next()) {
				long batchid = rst.getLong(1);
				String name = rst.getString(2);
				batches.addLast(new Batch(batchid, teacherid, name));
			}
			return Result.ok(batches);
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

	public static Result<Long, Err> create(long teacherid, String name) {
		Result<Connection, Err> result_cnx = Database.get_connection();
		if (result_cnx.isErr()) {
			return Result.err(result_cnx.err_msg());
		}

		try (
			Connection cnx = result_cnx.unwrap();
			PreparedStatement stmt = cnx.prepareStatement(
				"INSERT INTO Batch(Name, TeacherID) VALUES(?, ?) RETURNING BatchID;"
			);
		) {
			stmt.setString(1, name);
			stmt.setLong(2, teacherid);

			ResultSet rst = stmt.executeQuery();
			if (!rst.next()) {
				return Result.err(new Err(
					ErrKind.InsertionErr,
					"Cannot create batch"
				));
			}

			long batchid = rst.getLong(1);
			return Result.ok(batchid);
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

	public static Result<Void, Err> delete(long batchid, long teacherid) {
		Result<Connection, Err> result_cnx = Database.get_connection();
		if (result_cnx.isErr()) {
			return Result.err(result_cnx.err_msg());
		}

		try (
			Connection cnx = result_cnx.unwrap();
			PreparedStatement stmt = cnx.prepareStatement(
				"DELETE FROM Batch WHERE BatchID = ? AND TeacherID = ?;"
			);
		) {
			stmt.setLong(1, batchid);
			stmt.setLong(2, teacherid);

			int no = stmt.executeUpdate();
			if (no != 1) {
				return Result.err(new Err(
					ErrKind.DeleteErr,
					"Cannot delete batch with the given batchid and teacherid"
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

	long batchid;
	long teacherid;
	String name;

	public Batch(long batchid, long teacherid, String name) {
		this.batchid = batchid;
		this.teacherid = teacherid;
		this.name = name;
	}
}
