package org.example.data;

import java.util.Optional;

import org.example.util.LRU;
import org.example.util.Result;
import org.example.util.Validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

/**
 * Batch
 */
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
		Batch.batch_owner_cache.flush();
	}

	public static void delete_pattern_cache() {
		Batch.batch_pattern_cache.flush();
	}

	public static Result<Batch, String> search(long batchid) {
		Optional<Connection> optional_cnx = Database.get_connection().asOption();
		if (optional_cnx.isEmpty()) {
			return Result.err("Failed to acquire connection");
		}
		try (
			Connection cnx = optional_cnx.get()
		) {
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT BatchID, TeacherID, Name FROM Batch WHERE BatchID = ?;"
			);
			stmt.setLong(1, batchid);
			ResultSet rst = stmt.executeQuery();

			if (!rst.next()) {
				return Result.err("No Batch found with the given ID");
			}

			long teacherid = rst.getLong(2);
			String name = rst.getString(3);
			return Result.ok(new Batch(batchid, teacherid, name));
		} catch (Exception e) {
			return Result.err(e.getMessage());
		}
	}

	public static Result<List<Batch>, String> search(String pattern) {
		Optional<String> validated_pattern = Validator.validate_sql(pattern);
		if (validated_pattern.isEmpty()) {
			return Result.err("Expect Valid SQL.");
		}

		Optional<Connection> optional_cnx = Database.get_connection().asOption();
		if (optional_cnx.isEmpty()) {
			return Result.err("Failed to acquire connection");
		}

		pattern = validated_pattern.get();
		Optional<List<Batch>> cache_contents = batch_pattern_cache.get(pattern);
		if (cache_contents.isPresent()) {
			return Result.ok(cache_contents.get());
		}

		if (!pattern.endsWith("%")) {
			pattern += "%";
		}

		try (
			Connection cnx = optional_cnx.get();
		) {
			PreparedStatement exists = cnx.prepareStatement(
				"SELECT 1 FROM Batch WHERE Name LIKE ? LIMIT 1;"
			);
			exists.setString(1, pattern);
			ResultSet exists_rst = exists.executeQuery();

			if (!exists_rst.next()) {
				return Result.err("No Batch with the given pattern");
			}

			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT BatchID, TeacherID, Name FROM Batch WHERE Name LIKE ? LIMIT 20;"
			);
			stmt.setString(1, pattern);

			ResultSet rst = stmt.executeQuery();
			List<Batch> batches = new ArrayList<>();
			while (rst.next()) {
				long batchid = rst.getLong(1);
				long teacherid = rst.getLong(2);
				String name = rst.getString(3);
				batches.addLast(new Batch(batchid, teacherid, name));
			}

			exists.close();
			stmt.close();
			batch_pattern_cache.put(validated_pattern.get(), batches);
			return Result.ok(batches);
		} catch (Exception e) {
			return Result.err(e.getMessage());
		}
	}

	public static Result<List<Batch>, String> search_teacherid(long teacherid) {
		Optional<Connection> optional_cnx = Database.get_connection().asOption();
		if (optional_cnx.isEmpty()) {
			return Result.err("Failed to acquire connection");
		}
		Optional<List<Batch>> cache_contents = batch_owner_cache.get(teacherid);
		if (cache_contents.isPresent()) {
			return Result.ok(cache_contents.get());
		}

		try (
			Connection cnx = optional_cnx.get()
		) {
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT BatchID, Name FROM Batch WHERE TeacherID = ?;"
			);
			stmt.setLong(1, teacherid);
			ResultSet rst = stmt.executeQuery();

			List<Batch> batches = new ArrayList<>();
			while (rst.next()) {
				long batchid = rst.getLong(1);
				String name = rst.getString(2);
				batches.addLast(new Batch(batchid, teacherid, name));
			}
			return Result.ok(batches);
		} catch (Exception e) {
			return Result.err(e.getMessage());
		}

	}

	public static Result<Long, String> create(long teacherid, String name) {
		Optional<String> validated_pattern = Validator.validate_sql(name);
		if (validated_pattern.isEmpty()) {
			return Result.err("You shalln't try to hack me T-T");
		}

		Optional<Connection> optional_cnx = Database.get_connection().asOption();
		if (optional_cnx.isEmpty()) {
			return Result.err("Failed to acquire connection");
		}

		try (
			Connection cnx = optional_cnx.get();
		) {
			PreparedStatement stmt = cnx.prepareStatement("INSERT INTO Batch(Name, TeacherID) VALUES(?, ?) RETURNING BatchID;");
			stmt.setString(1, name);
			stmt.setLong(2, teacherid);

			ResultSet rst = stmt.executeQuery();
			if (!rst.next()) {
				return Result.err("Failed to create batch");
			}

			long batchid = rst.getLong(1);
			return Result.ok(batchid);
		} catch (Exception e) {
			return Result.err(e.getMessage());
		}
	}

	public static Result<Void, String> delete(long batchid, long teacherid) {
		Optional<Connection> optional_cnx = Database.get_connection().asOption();
		if (optional_cnx.isEmpty()) {
			return Result.err("Failed to acquire connection");
		}

		try (
			Connection cnx = optional_cnx.get();
		) {
			PreparedStatement stmt = cnx.prepareStatement(
				"DELETE FROM Batch WHERE BatchID = ? AND TeacherID = ?;"
			);
			stmt.setLong(1, batchid);
			stmt.setLong(2, teacherid);

			int no = stmt.executeUpdate();
			if (no != 1) {
				return Result.err("Failed to delete batch");
			}

			return Result.ok(null);
		} catch (Exception e) {
			return Result.err(e.getMessage());
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
