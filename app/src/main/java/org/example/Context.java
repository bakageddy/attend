package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.example.data.Batch;
import org.example.data.BatchData;
import org.example.data.Database;
import org.example.data.Student;
import org.example.data.Subject;
import org.example.data.Teacher;
import org.example.util.LRU;
import org.example.util.Result;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

// TODO: Add Documentation
@WebListener
public class Context implements ServletContextListener {
	// TODO: Add Documentation
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			InputStream in = sce.getServletContext()
								.getResourceAsStream("/WEB-INF/classes/config/Application.properties");
			Properties props = new Properties();
			props.load(in);

			Result<Void, String> result 
					= Database.init(props)
							.map_err(e -> e.toString());

			if (result.isErr()) {
				System.err.println(result.err_msg());
				System.exit(1);
			}
		} catch ( IOException e ) {
			System.err.println("Err reading from config file" + e.getMessage());
		} catch ( IllegalArgumentException e) {
			System.err.println("Err reading malformed unicode in config" + e.getMessage());
		} catch ( NullPointerException e) {
			System.err.println("Err reading from null InputStream config" + e.getMessage());
		}

		Student.set_cache(new LRU<String, List<Student>>(30));
		Teacher.set_cache(new LRU<String, List<Teacher>>(30));

		Subject.set_name_cache(new LRU<String, List<Subject>>(30));
		Subject.set_code_cache(new LRU<String, List<Subject>>(30));

		Batch.set_owner_cache(new LRU<Long, List<Batch>>(30));
		Batch.set_pattern_cache(new LRU<String, List<Batch>>(30));

		BatchData.set_cache(new LRU<Long, List<Student>>(30));
	}

	// TODO: Add Documentation
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
