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
import org.example.types.extractors.StudentSearchRequest;
import org.example.util.Extractor;
import org.example.util.LRU;
import org.example.util.Parser;
import org.example.util.Result;

// TODO: ADD Documentation
public class Student implements Extractor<StudentSearchRequest> {
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

	// TODO: ADD Documentation
	public static Result<Student, Err> search(long rollno) {
		Result<Connection, Err> result_cnx = Database.get_connection();
		if (result_cnx.isErr()) {
			return Result.err(result_cnx.err_msg());
		}

		try (
			Connection cnx = result_cnx.unwrap();
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT Name FROM Student WHERE RollNo = ?;"
			);
		) {

			stmt.setLong(1, rollno);
			ResultSet rst = stmt.executeQuery();
			if (!rst.next()) {
				return Result.err(new Err(
					ErrKind.ElementNotFound,
					"No Student with the given rollno"
				));
			}

			String name = rst.getString(1);
			Student std = new Student(rollno, name);
			return Result.ok(std);
		} catch (SQLTimeoutException e) {
			return Result.err(new Err(
				ErrKind.DBTimeout,
				e.getMessage()
			));
		} catch (SQLException e) {
			return Result.err(new Err(
				ErrKind.DBConnectionErr,
				e.getMessage()));
		} catch (Exception e) {
			return Result.err(new Err(
				ErrKind.Unreachable,
				e.getMessage()
			));
		}
	}

	// TODO: Implement Inset pagination
	// TODO: ADD Documentation
	public static Result<List<Student>, Err> search(String pattern) {
		Optional<List<Student>> cache_contents = cache.get(pattern);
		if (cache_contents.isPresent()) {
			return Result.ok(cache_contents.get());
		}

		String param = null;
		if (!pattern.endsWith("%")) {
			param = pattern.concat("%");
		} else {
			param = pattern;
		}

		Result<Connection, Err> result_cnx = Database.get_connection();
		if (result_cnx.isErr()) {
			return Result.err(result_cnx.err_msg());
		}

		try (
			Connection cnx = result_cnx.unwrap();
			PreparedStatement exists = cnx.prepareStatement(
				"SELECT 1 FROM Student WHERE Name LIKE ? LIMIT 1;"
			);
			// Hardcode 20 in here. Change it when you implement inset pagination
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT RollNo, Name FROM Student WHERE Name LIKE ? ORDER BY RollNo LIMIT 20;"
			);
		) {
			exists.setString(1, param);
			ResultSet exists_result = exists.executeQuery();

			if (!exists_result.next()) {
				exists.close();
				return Result.err(new Err(
					ErrKind.ElementNotFound,
					"No results for such pattern"
				));
			}

			stmt.setString(1, param);
			ResultSet result = stmt.executeQuery();
			List<Student> names = new ArrayList<>(20);

			while (result.next()) {
				long id = result.getLong(1);
				String name = result.getString(2);

				names.addLast(new Student(id, name));
			}

			cache.put(pattern, names);
			return Result.ok(names);
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

	public static Result<StudentSearchRequest, Err> extract(Map<String, String[]> map) {
		StudentSearchRequest out = new StudentSearchRequest();
		String[] patterns = map.get("pattern");
		if (patterns != null) {
			if (patterns.length != 1) {
				return Result.err(new Err(
					ErrKind.IllegalArgument, 
					"Pattern must be singular"
				));
			}
			out.setPattern(patterns[0]);
		}

		String[] rollnos = map.get("rollno");
		if (rollnos != null) {
			if (rollnos.length != 1) {
				return Result.err(new Err(
					ErrKind.IllegalArgument,
					"Number must be singular"
				));
			}
			Result<Long, Err> parsed_elem = Parser.parse_long(rollnos[0]);
			if (parsed_elem.isErr()) {
				return Result.err(parsed_elem.err_msg());
			}
			out.setRollno(parsed_elem.unwrap());
		}

		if (patterns == null || rollnos == null) {
			return Result.err(new Err(
				ErrKind.ElementNotFound,
				"Search Parameters unfulfilled"
			));
		} else {
			return Result.ok(out);
		}
	}

	public long rollNo;
	public String name;

	public Student(long rollno, String name) {
		this.rollNo = rollno;
		this.name = name;
	}
}
