package org.example.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.util.LRU;
import org.example.util.Result;
import org.example.util.Validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Teacher
 */
public class Teacher {

	private static LRU<String, List<Teacher>> cache = null;

	public static void set_cache(LRU<String, List<Teacher>> cache) {
		if (Teacher.cache == null) {
			Teacher.cache.flush();
		}
		Teacher.cache = cache;
	}

	public static void delete_cache() {
		cache.flush();
		cache = null;
	}

	// TODO: Implement Error enums
	public static Result<Teacher, String> search(long teacher_id) {
		Optional<Connection> optional_cnx = Database.get_connection();
		if (optional_cnx.isEmpty()) {
			return Result.err("Failed to open a connection");
		}

		try (
			Connection cnx = optional_cnx.get();
		) {
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT Name FROM Teacher WHERE TeacherID = ?;"
			);
			stmt.setLong(1, teacher_id);

			ResultSet rst = stmt.executeQuery();
			if (!rst.next()) {
				stmt.close();
				return Result.err("No Teacher with such element");
			}
			String name = rst.getString(1);

			stmt.close();
			return Result.ok(new Teacher(teacher_id, name));
		} catch (SQLException e) {
			return Result.err(e.getMessage());
		}
	}

	// TODO: Implement Inset pagination
	// TODO: Implement Error enums
	// TODO: Cache
	public static Result<List<Teacher>, String> search(String pattern) {
		Optional<String> valid_pattern = Validator.validate_sql(pattern);
		if (valid_pattern.isEmpty()) {
			return Result.err("Need Valid Pattern. Not SQL T-T");
		}
		Optional<Connection> optional_cnx = Database.get_connection();
		try (
			Connection cnx = optional_cnx.get();
		) {
			PreparedStatement exists = cnx.prepareStatement(
				"SELECT 1 FROM Teacher WHERE Name LIKE ? LIMIT 1;"
			);
			exists.setString(1, valid_pattern.get());
			ResultSet exists_rst = exists.executeQuery();

			if (!exists_rst.next()) {
				exists.close();
				exists_rst.close();
				return Result.err("No results");
			}

			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT TeacherID, Name FROM Teacher WHERE Name LIKE ? LIMIT 20;"
			);

			stmt.setString(1, pattern);
			ResultSet rst = stmt.executeQuery();

			List<Teacher> teachers = new ArrayList<>();
			while (rst.next()) {
				long teacher_id = rst.getLong(1);
				String name = rst.getString(2);
				teachers.addLast(new Teacher(teacher_id, name));
			}

			exists_rst.close();
			rst.close();
			return Result.ok(teachers);
		} catch (SQLException e) {
			return Result.err(e.getMessage());
		}
	}

	public long teacher_id;
	public String name;

	public Teacher(long teacher_id, String name) {
		this.name = name;
		this.teacher_id = teacher_id;
	}
}
