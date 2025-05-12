package org.example.data;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import org.example.util.Result;
import org.example.util.Validator;
import org.example.util.LRU;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Student {
	private static LRU<String, List<Student>> cache = null;

	public static void set_cache(LRU<String, List<Student>> cache) {
		if (Student.cache != null) {
			Student.cache.flush();
		}
		Student.cache = cache;
	}

	public static void delete_cache() {
		cache.flush();
		Student.cache = null;
	}

	// TODO: Implement Error enums
	public static Result<Student, String> search(long rollno) {
		Optional<Connection> optional_cnx = Database.get_connection().asOption();
		if (optional_cnx.isEmpty()) {
			return Result.err("Failed to open a connection");
		}

		try (
			Connection cnx = optional_cnx.get();
		) {

			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT Name FROM Student WHERE RollNo = ?;"
			);
			stmt.setLong(1, rollno);

			ResultSet rst = stmt.executeQuery();
			if (!rst.next()) {
				stmt.close();
				return Result.err("No Student with the given rollno");
			}

			String name = rst.getString(1);
			Student std = new Student(rollno, name);
			return Result.ok(std);
		} catch (Exception e) {
			return Result.err(e.getMessage());
		}
	}

	// TODO: Implement Inset pagination
	// TODO: Implement Error enums
	// TODO: Cache
	public static Result<List<Student>, String> search(String pattern) {
		Optional<String> valid_pattern = Validator.validate_sql(pattern);
		if (valid_pattern.isEmpty()) {
			return Result.err("Pattern must be alphanumeric, not SQL -___-");
		}

		pattern = valid_pattern.get();
		if (!pattern.endsWith("%")) {
			pattern = pattern.concat("%");
		}

		Result<Connection, String> optional_cnx = Database.get_connection();
		if (optional_cnx.isErr()) {
			return Result.err(optional_cnx.err_msg());
		}
		try (
			Connection cnx = optional_cnx.unwrap()
		){
			PreparedStatement exists = cnx.prepareStatement(
				"SELECT 1 FROM Student WHERE Name LIKE ? LIMIT 1;"
			);
			exists.setString(1, pattern);

			ResultSet exists_result = exists.executeQuery();
			if (!exists_result.next()) {
				exists.close();
				return Result.err("No results");
			}

			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT RollNo, Name FROM Student WHERE Name LIKE ? ORDER BY RollNo LIMIT 20;"
			);
			stmt.setString(1, pattern);

			ResultSet result = stmt.executeQuery();
			List<Student> names = new ArrayList<>();

			while (result.next()) {
				long id = result.getLong(1);
				String name = result.getString(2);

				names.addLast(new Student(id, name));
			}

			exists.close();
			result.close();

			return Result.ok(names);
		} catch (SQLException e) {
			return Result.err(e.getMessage());
		}
	}

	public long rollNo;
	public String name;

	public Student(long rollno, String name) {
		this.rollNo = rollno;
		this.name = name;
	}
}
