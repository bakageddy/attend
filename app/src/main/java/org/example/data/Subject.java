package org.example.data;

import org.example.util.LRU;
import org.example.util.Result;
import org.example.util.Validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Subject
 */
public class Subject {

	private static LRU<String, List<Subject>> subject_name_cache;
	private static LRU<String, List<Subject>> subject_code_cache;

	public static void set_name_cache(LRU<String, List<Subject>> cache) {
		if (Subject.subject_name_cache != null) {
			Subject.subject_name_cache.flush();
		}
		Subject.subject_name_cache = cache;
	}

	public static void set_code_cache(LRU<String, List<Subject>> cache) {
		if (Subject.subject_code_cache != null) {
			Subject.subject_code_cache.flush();
		}
		Subject.subject_code_cache = cache;
	}

	public static void delete_name_cache() {
		Subject.subject_name_cache.flush();
		Subject.subject_name_cache = null;
	}

	public static void delete_code_cache() {
		Subject.subject_code_cache.flush();
		Subject.subject_code_cache = null;
	}

	// TODO: Implement Error enums
	public static Result<Subject, String> search(long subject_id) {
		Optional<Connection> optional_cnx = Database.get_connection().asOption();
		if (optional_cnx.isEmpty()) {
			return Result.err("Failed to acquire connection");
		}

		try (
			Connection cnx = optional_cnx.get();
		) {
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT SubjectID, SubjectCode, Name FROM Subject WHERE SubjectID = ?;"
			);
			stmt.setLong(1, subject_id);

			ResultSet rst = stmt.executeQuery();
			if (!rst.next()) {
				stmt.close();
				return Result.err("No subject with the given subjectid");
			}

			String subject_code = rst.getString(2);
			String subject_name = rst.getString(3);

			stmt.close();
			return Result.ok(new Subject(subject_id, subject_code, subject_name));
		} catch (SQLException e) {
			return Result.err(e.getMessage());
		}
	}

	// TODO: Implement Inset pagination
	// TODO: Implement Error enums
	// TODO: Cache
	public static Result<List<Subject>, String> search(String pattern) {
		Optional<String> valid_pattern = Validator.validate_sql(pattern);
		if (valid_pattern.isEmpty()) {
			return Result.err("Need Valid Pattern. Not SQL T-T");
		}

		Optional<List<Subject>> cache_contents = subject_name_cache.get(pattern);
		if (cache_contents.isPresent()) {
			return Result.ok(cache_contents.get());
		}

		pattern = valid_pattern.get();
		if (!pattern.endsWith("%")) {
			pattern = pattern.concat("%");
		}

		Optional<Connection> optional_cnx = Database.get_connection().asOption();
		if (optional_cnx.isEmpty()) {
			return Result.err("Failed to acquire connection");
		}
		try (
			Connection cnx = optional_cnx.get();
		) {
			PreparedStatement exists = cnx.prepareStatement(
				"SELECT 1 FROM Teacher WHERE Name LIKE ? LIMIT 1;"
			);
			exists.setString(1, pattern);
			ResultSet exists_rst = exists.executeQuery();

			if (!exists_rst.next()) {
				exists_rst.close();
				return Result.err("No results");
			}

			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT SubjectID, SubjectCode, Name FROM Subject WHERE Name LIKE ? LIMIT 20;"
			);
			stmt.setString(1, pattern);
			ResultSet rst = stmt.executeQuery();

			List<Subject> subjects = new ArrayList<>();
			while (rst.next()) {
				long subject_id = rst.getLong(1);
				String subject_code = rst.getString(2);
				String subject_name = rst.getString(3);
				subjects.addLast(new Subject(subject_id, subject_code, subject_name));
			}

			stmt.close();
			subject_name_cache.put(pattern, subjects);
			return Result.ok(subjects);

		} catch (Exception e) {
			return Result.err(e.getMessage());
		}
	}

	// TODO: Implement Inset pagination
	// TODO: Implement Error enums
	// TODO: Cache Results
	public static Result<List<Subject>, String> search_code(String code_pattern) {
		Optional<String> valid_pattern = Validator.validate_sql(code_pattern);
		if (valid_pattern.isEmpty()) {
			return Result.err("Need Valid Pattern. Not SQL T-T");
		}

		Optional<List<Subject>> cache_contents = subject_code_cache.get(code_pattern);
		if (cache_contents.isPresent()) {
			return Result.ok(cache_contents.get());
		}

		code_pattern = valid_pattern.get();
		if (!code_pattern.endsWith("%")) {
			code_pattern += "%";
		}

		Optional<Connection> optional_cnx = Database.get_connection().asOption();
		if (optional_cnx.isEmpty()) {
			return Result.err("Failed to obtain connection");
		}

		try (
			Connection cnx = optional_cnx.get();
		) {
			PreparedStatement exists = cnx.prepareStatement(
				"SELECT 1 FROM Subject WHERE SubjectCode LIKE ? LIMIT 1;"
			);
			exists.setString(1, code_pattern);
			ResultSet exists_rst = exists.executeQuery();

			if (!exists_rst.next()) {
				exists_rst.close();
				return Result.err("No results");
			}

			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT SubjectID, SubjectCode, Name FROM Subject WHERE SubjectCode LIKE ? LIMIT 20;"
			);
			stmt.setString(1, code_pattern);
			ResultSet rst = stmt.executeQuery();

			List<Subject> subjects = new ArrayList<>();
			while (rst.next()) {
				long subject_id = rst.getLong(1);
				String subject_code = rst.getString(2);
				String subject_name = rst.getString(3);
				subjects.addLast(
					new Subject(subject_id, subject_code, subject_name)
				);
			}

			exists_rst.close();
			stmt.close();

			subject_code_cache.put(code_pattern, subjects);
			return Result.ok(subjects);

		} catch (Exception e) {
			return Result.err(e.getMessage());
		}
	}

	public long subject_id;
	public String subject_code;
	public String name;

	public Subject(long subject_id, String subject_code, String name) {
		this.subject_id = subject_id;
		this.subject_code = subject_code;
		this.name = name;
	}
}
