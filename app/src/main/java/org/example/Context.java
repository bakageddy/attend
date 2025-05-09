package org.example;

import java.util.List;

import org.example.data.Database;
import org.example.data.Student;
import org.example.data.Subject;
import org.example.data.Teacher;
import org.example.data.Batch;
import org.example.data.BatchData;
import org.example.util.Cache;
import org.example.util.LRU;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class Context implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		Student.set_cache(new LRU<>(30));
		Teacher.set_cache(new LRU<>(30));

		Subject.set_name_cache(new LRU<>(30));
		Subject.set_code_cache(new LRU<>(30));

		Batch.set_owner_cache(new LRU<>(30));
		Batch.set_pattern_cache(new LRU<>(30));

		BatchData.set_cache(new LRU<>(30));
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
