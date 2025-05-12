package org.example.data;

import java.sql.Connection;
import java.util.Properties;

import org.example.util.Result;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Database {

	private static HikariDataSource data_source = null;

	public static synchronized Result<Void, String> init() {
		try {
			Class.forName("org.postgresql.Driver");
			String password = System.getenv("PGSQL_DATABASE_PASSWORD");
			if (password == null) {
				return Result.err("Cannot find Password");
			}

			HikariConfig config = new HikariConfig();
			config.setJdbcUrl("jdbc:postgresql://localhost/college");
			config.setUsername("postgres");
			config.setPassword(password);
			config.setConnectionTimeout(2000); // 2s
			config.setMaximumPoolSize(10);

			data_source = new HikariDataSource(config);
			if (data_source == null) {
				return Result.err("Failed to initialize database");
			}

			return Result.ok(null);
		} catch (Exception e) {
			return Result.err(e.getMessage());
		}
	}

	public static synchronized Result<Void, String> init(Properties props) {
		try {
			Class.forName("org.postgresql.Driver");

			HikariConfig config = new HikariConfig();
			config.setUsername(props.getProperty("db_username"));
			config.setPassword(props.getProperty("db_password"));
			config.setJdbcUrl(props.getProperty("db_url"));
			config.setConnectionTimeout(Integer.parseInt(props.getProperty("db_connection_timeout")));
			config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db_max_pool")));

			data_source = new HikariDataSource(config);
			if (data_source == null) {
				return Result.err("Failed to Initialize Database");
			}

			return Result.ok(null);
		} catch (Exception e) {
			return Result.err(e.getMessage());
		}
	}

	public static synchronized Result<Connection, String> get_connection() {
		try {
			if (data_source == null) {
				System.out.println("Datasource is null. INITIALIZING");
				Result<Void, String> result = init();
				return Result.err(result.err_msg());
			}

			Connection cnx = data_source.getConnection();
			if (cnx != null)
				return Result.ok(cnx);
			else return Result.err("Datasource gave me a NULL");
		} catch (Exception e) {
			return Result.err(e.getMessage());
		}
	}

	public static void close() throws InterruptedException {
		if (data_source != null)
			Database.data_source.close();
	}
}
