package org.example;

import org.example.util.LRU;
import org.example.util.Cache;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.pool.HikariPool;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.List;

@WebListener
public class Context implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			Class.forName("org.postgresql.Driver");
			String password = System.getenv("PGSQL_DATABASE_PASSWORD");
			if (password == null) {
				System.exit(1);
			}

			HikariConfig hconfig = new HikariConfig();
			hconfig.setJdbcUrl("jdbc:postgresql://localhost/college");
			hconfig.setUsername("postgres");
			hconfig.setPassword(password);
			HikariPool cpool = new HikariPool(hconfig);

			// NOTE: Research whether student_id_cache is important or not...
			// TODO: Must refactor to use Student, Teacher, Subject :(
			Cache<String, String> student_pattern_cache = new LRU<>(30);
			// LRU<Long, String> student_id_cache = new LRU<>(30);

			Cache<String, String> teacher_pattern_cache = new LRU<>(30);
			// LRU<Long, String> teacher_id_cache = new LRU<>(30);

			Cache<String, String> subject_pattern_cache = new LRU<>(30);
			// LRU<Long, String> subject_id_cache = new LRU<>(30);
			
			Cache<Long, List<Student>> batch_id_cache = new LRU<>(30);

			var ctx = sce.getServletContext();
			ctx.setAttribute("cnx_pool", cpool);

			ctx.setAttribute("student_pattern_cache", student_pattern_cache);
			// ctx.setAttribute("student_id_cache", student_id_cache);

			ctx.setAttribute("teacher_pattern_cache", teacher_pattern_cache);
			// ctx.setAttribute("teacher_id_cache", teacher_id_cache);

			ctx.setAttribute("subject_pattern_cache", subject_pattern_cache);
			// ctx.setAttribute("subject_id_cache", subject_id_cache);
			
			ctx.setAttribute("batch_id_cache", batch_id_cache);
		} catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
		}
	}
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
		try {
			HikariPool pool = (HikariPool) sce.getServletContext().getAttribute("cnx_pool");
			pool.shutdown();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
