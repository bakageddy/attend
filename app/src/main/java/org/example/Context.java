package org.example;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.pool.HikariPool;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

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
			sce.getServletContext().setAttribute("cnx_pool", cpool);
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
