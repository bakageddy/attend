package org.example.data;

import java.sql.Connection;
import java.util.Optional;

import org.postgresql.jdbc3.Jdbc3PoolingDataSource;

public class Database {

	private static Jdbc3PoolingDataSource data_source = null;

	public static synchronized Optional<Connection> get_connection() {
		try {
			Class.forName("org.postgres.Driver");
			if (data_source == null) {
				String password = System.getenv("PGSQL_DATABASE_PASSWORD");
				if (password == null) {
					return Optional.empty();
				}

				data_source = new Jdbc3PoolingDataSource();
				data_source.setDatabaseName("college");
				data_source.setURL("jdbc:postgresql://localhost/");
				data_source.setUser("postgres");
				data_source.setPassword(password);
				data_source.setMaxConnections(8);
			}
			return Optional.of(data_source.getConnection());
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public static void close() {
		Database.data_source.close();
	}
}
