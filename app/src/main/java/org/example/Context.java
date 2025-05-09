package org.example;

import java.util.List;

import org.example.data.Database;
import org.example.data.Student;
import org.example.data.Teacher;
import org.example.util.Cache;
import org.example.util.LRU;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class Context implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// NOTE: Research whether student_id_cache is important or not...
		// TODO: Must refactor to use Student, Teacher, Subject :(
		
		Student.set_cache(new LRU<String, List<Student>>(30));
		Teacher.set_cache(new LRU<String, List<Teacher>>(30));
		Cache<String, String> subject_pattern_cache = new LRU<>(30);
		Cache<Long, List<Student>> batch_id_cache = new LRU<>(30);

		// ctx.setAttribute("cnx_pool", cpool);

		// ctx.setAttribute("student_pattern_cache", student_pattern_cache);
		// // ctx.setAttribute("student_id_cache", student_id_cache);
		//
		// ctx.setAttribute("teacher_pattern_cache", teacher_pattern_cache);
		// // ctx.setAttribute("teacher_id_cache", teacher_id_cache);
		//
		// ctx.setAttribute("subject_pattern_cache", subject_pattern_cache);
		// // ctx.setAttribute("subject_id_cache", subject_id_cache);
		// 
		// ctx.setAttribute("batch_id_cache", batch_id_cache);
	}
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
		try {
			Database.close();
			Student.delete_cache();
			Teacher.delete_cache();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
