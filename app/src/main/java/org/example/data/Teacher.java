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
import org.example.types.extractors.TeacherSearchRequest;
import org.example.util.LRU;
import org.example.util.Parser;
import org.example.util.Result;

// TODO: Documentation
public class Teacher {

	private static LRU<String, List<Teacher>> cache = null;

	public static void set_cache(LRU<String, List<Teacher>> cache) {
		if (Teacher.cache != null) {
			Teacher.cache.flush();
		}
		Teacher.cache = cache;
	}

	public static void delete_cache() {
		cache.flush();
		cache = null;
	}

	// TODO: Documentation
	public static Result<Teacher, Err> search(long teacher_id) {
		Result<Connection, Err> result_cnx = Database.get_connection();
		if (result_cnx.isErr()) {
			return Result.err(result_cnx.err_msg());
		}

		try (
			Connection cnx = result_cnx.unwrap();
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT Name FROM Teacher WHERE TeacherID = ?;"
			);
		) {
			stmt.setLong(1, teacher_id);

			ResultSet rst = stmt.executeQuery();
			if (!rst.next()) {
				return Result.err(new Err(
					ErrKind.ElementNotFound,
					"No element with such id"
				));
			}
			String name = rst.getString(1);
			Teacher teacher = new Teacher(teacher_id, name);
			return Result.ok(teacher);
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

	// TODO: Documentation
	// TODO: Implement Inset pagination
	public static Result<List<Teacher>, Err> search(String pattern) {
		Optional<List<Teacher>> cache_contents = cache.get(pattern);
		if (cache_contents.isPresent()) {
			return Result.ok(cache_contents.get());
		}

		// TODO: you can do better than this dinesh
		String param = pattern;
		if (!param.endsWith("%")) {
			param += "%";
		}

		Result<Connection, Err> result_cnx = Database.get_connection();
		if (result_cnx.isErr()) {
			return Result.err(result_cnx.err_msg());
		}

		try (
			Connection cnx = result_cnx.unwrap();
			PreparedStatement exists = cnx.prepareStatement(
				"SELECT 1 FROM Teacher WHERE Name LIKE ? LIMIT 1;"
			);
			// NOTE: Hardcode 20 in here. Change it when you implement inset pagination
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT TeacherID, Name FROM Teacher WHERE Name LIKE ? LIMIT 20;"
			);
		) {
			exists.setString(1, param);
			ResultSet exists_rst = exists.executeQuery();

			if (!exists_rst.next()) {
				return Result.err(new Err(
					ErrKind.ElementNotFound,
					"No Teacher with the given ID"
				));
			}

			stmt.setString(1, param);
			ResultSet rst = stmt.executeQuery();

			// NOTE: Hardcode value
			List<Teacher> teachers = new ArrayList<>(20);
			while (rst.next()) {
				long teacher_id = rst.getLong(1);
				String name = rst.getString(2);
				teachers.addLast(new Teacher(teacher_id, name));
			}

			cache.put(pattern, teachers);
			return Result.ok(teachers);
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

	public long teacher_id;
	public String name;

	public Teacher(long teacher_id, String name) {
		this.name = name;
		this.teacher_id = teacher_id;
	}
}
