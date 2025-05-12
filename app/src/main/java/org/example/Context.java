package org.example;

import org.example.data.Batch;
import org.example.data.BatchData;
import org.example.data.Database;
import org.example.data.Student;
import org.example.data.Subject;
import org.example.data.Teacher;
import org.example.util.LRU;
import org.example.util.Result;

import java.util.List;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class Context implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		Result<Void, String> result = Database.init();
		if (result.isErr()) {
			System.err.println(result.err_msg());
		}

		System.out.println("Initialized Database Connection pool");

		Student.set_cache(new LRU<String, List<Student>>(30));
		Teacher.set_cache(new LRU<String, List<Teacher>>(30));

		Subject.set_name_cache(new LRU<String, List<Subject>>(30));
		Subject.set_code_cache(new LRU<String, List<Subject>>(30));

		Batch.set_owner_cache(new LRU<Long, List<Batch>>(30));
		Batch.set_pattern_cache(new LRU<String, List<Batch>>(30));

		BatchData.set_cache(new LRU<Long, List<Student>>(30));
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		try {
			Database.close();

			Student.delete_cache();
			Teacher.delete_cache();

			Subject.delete_name_cache();
			Subject.delete_code_cache();

			Batch.delete_owner_cache();
			Batch.delete_pattern_cache();

			BatchData.delete_cache();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
