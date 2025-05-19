package org.example.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.example.types.Err;
import org.example.types.ErrKind;
import org.example.types.extractors.SubjectSearchRequest;
import org.example.util.Extractor;
import org.example.util.LRU;
import org.example.util.Parser;
import org.example.util.Result;

public class Subject implements Extractor<SubjectSearchRequest> {

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

	public static Result<Subject, Err> search(long subject_id) {
		Result<Connection, Err> result_cnx = Database.get_connection();
		if (result_cnx.isErr()) {
			return Result.err(result_cnx.err_msg());
		}

		try (
			Connection cnx = result_cnx.unwrap();
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT SubjectID, SubjectCode, Name FROM Subject WHERE SubjectID = ?;"
			);
		) {
			stmt.setLong(1, subject_id);
			ResultSet rst = stmt.executeQuery();

			if (!rst.next()) {
				stmt.close();
				return Result.err(new Err(
					ErrKind.ElementNotFound,
					"No subject with the given id"
				));
			}

			String subject_code = rst.getString(2);
			String subject_name = rst.getString(3);
			Subject s = new Subject(subject_id, subject_code, subject_name);

			return Result.ok(s);
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

	// TODO: Implement Inset pagination
	public static Result<List<Subject>, Err> search(String pattern) {
		Optional<List<Subject>> cache_contents = subject_name_cache.get(pattern);
		if (cache_contents.isPresent()) {
			return Result.ok(cache_contents.get());
		}

		String param = pattern;
		if (!pattern.endsWith("%")) {
			param += "%";
		}

		Result<Connection, Err> result_cnx = Database.get_connection();
		if (result_cnx.isErr()) {
			return Result.err(result_cnx.err_msg());
		}
		try (
			Connection cnx = result_cnx.unwrap();
			PreparedStatement exists = cnx.prepareStatement(
				"SELECT 1 FROM Subject WHERE Name LIKE ? LIMIT 1;"
			);
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT SubjectID, SubjectCode, Name FROM Subject WHERE Name LIKE ? LIMIT 20;"
			);
		) {

			exists.setString(1, param);

			ResultSet exists_rst = exists.executeQuery();
			if (!exists_rst.next()) {
				return Result.err(new Err(
					ErrKind.ElementNotFound,
					"No subject with such pattern"
				));
			}

			stmt.setString(1, param);

			ResultSet rst = stmt.executeQuery();
			List<Subject> subjects = new ArrayList<>();
			while (rst.next()) {
				long subject_id = rst.getLong(1);
				String subject_code = rst.getString(2);
				String subject_name = rst.getString(3);
				Subject s = new Subject(subject_id, subject_code, subject_name);
				subjects.addLast(s);
			}

			subject_name_cache.put(pattern, subjects);
			return Result.ok(subjects);
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

	// TODO: Implement Inset pagination
	public static Result<List<Subject>, Err> search_code(String code_pattern) {
		Optional<List<Subject>> cache_contents = subject_code_cache.get(code_pattern);
		if (cache_contents.isPresent()) {
			return Result.ok(cache_contents.get());
		}

		// Improve searching
		String param = code_pattern;
		if (!code_pattern.endsWith("%")) {
			param += "%";
		}

		Result<Connection, Err> result_cnx = Database.get_connection();
		if (result_cnx.isErr()) {
			return Result.err(result_cnx.err_msg());
		}

		try (
			Connection cnx = result_cnx.unwrap();
			PreparedStatement exists = cnx.prepareStatement(
				"SELECT 1 FROM Subject WHERE SubjectCode LIKE ? LIMIT 1;"
			);
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT SubjectID, SubjectCode, Name FROM Subject WHERE SubjectCode LIKE ? LIMIT 20;"
			);
		) {
			exists.setString(1, param);
			ResultSet exists_rst = exists.executeQuery();

			if (!exists_rst.next()) {
				return Result.err(new Err(
					ErrKind.ElementNotFound,
					"No subjects with the given code"
				));
			}

			stmt.setString(1, param);
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

			subject_code_cache.put(code_pattern, subjects);
			return Result.ok(subjects);

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

	public long subject_id;
	public String subject_code;
	public String name;

	public Subject(long subject_id, String subject_code, String name) {
		this.subject_id = subject_id;
		this.subject_code = subject_code;
		this.name = name;
	}
}
