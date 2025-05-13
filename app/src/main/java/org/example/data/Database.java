package org.example.data;

import java.sql.Connection;
import java.util.Properties;

import org.example.util.Result;
import org.postgresql.ds.PGPoolingDataSource;

public class Database {

	private static PGPoolingDataSource data_source = null;

	// public static synchronized Result<Void, String> init() {
	// 	try {
	// 		Class.forName("org.postgresql.Driver");
	// 		String password = System.getenv("PGSQL_DATABASE_PASSWORD");
	// 		if (password == null) {
	// 			return Result.err("Cannot find Password");
	// 		}
	//
	// 		data_source = new PGPoolingDataSource();
	// 		data_source.setConnectTimeout(2000); // 2s
	// 		data_source.setInitialConnections(10);
	// 		data_source.setURL("jdbc:postgresql://localhost/college");
	// 		data_source.setUser("postgres");
	// 		System.out.println(("Password provided: " + props.getProperty("db_password")));
	// 		data_source.setPassword(password);
	//
	// 		if (data_source == null) {
	// 			return Result.err("Failed to initialize database");
	// 		}
	//
	// 		return Result.ok(null);
	// 	} catch (Exception e) {
	// 		return Result.err(e.getMessage());
	// 	}
	// }

	public static synchronized Result<Void, String> init(Properties props) {
		try {
			Class.forName("org.postgresql.Driver");


			data_source = new PGPoolingDataSource();
			if (data_source == null) {
				return Result.err("Failed to Initialize Database");
			}

			data_source.setConnectTimeout(Integer.parseInt(props.getProperty("db_connection_timeout"))); // 2s
			data_source.setInitialConnections(Integer.parseInt(props.getProperty("db_init_pool_size")));
			data_source.setMaxConnections(Integer.parseInt(props.getProperty("db_max_pool_size")));

			data_source.setServerName("localhost");
			data_source.setDatabaseName(props.getProperty("db_name"));
			data_source.setUser(props.getProperty("db_username"));
			data_source.setPassword(props.getProperty("db_password"));

			return Result.ok(null);
		} catch (Exception e) {
			return Result.err(e.getMessage());
		}
	}

	public static synchronized Result<Connection, String> get_connection() {
		try {
			if (data_source == null) {
				return Result.err("Call INIT before get_connection");
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
