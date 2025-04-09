package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("Cannot open the POSTGRESQL DRIVER");
			System.err.println("ERR: " + e.getMessage());
			System.exit(1);
		}

		String url = "jdbc:postgresql://localhost:5432/college";

		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(url);
		config.setUsername("postgres");
		config.setPassword("Dineshkumar4u!");
		config.setMaximumPoolSize(10);

		try (
			HikariDataSource ds = new HikariDataSource(config);
		) {
			event.getServletContext().setAttribute("dbpool", ds);
		} catch (Exception e) {
			System.err.println("ERR: " + e.getMessage());
			System.exit(1);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// Do nothing
	}
}
