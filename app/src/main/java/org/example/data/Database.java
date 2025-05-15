package org.example.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.example.util.Result;
import org.example.util.Parser;
import org.postgresql.ds.PGPoolingDataSource;

import com.google.errorprone.annotations.ThreadSafe;
import com.google.errorprone.annotations.concurrent.GuardedBy;

import org.example.types.Err;
import org.example.types.ErrKind;

@ThreadSafe
public class Database {

	private static PGPoolingDataSource data_source = null;

	/**
	 * @param props - Properties should contain the following fields<br/>
	 * <code>db_connection_timeout</code>: <code>int</code> : Timeout for each connection<br/>
	 * <code>db_init_pool_size</code>: <code>int</code> : Initial Connections when initializing pool<br/>
	 * <code>db_max_pool_size</code>: <code>int</code> : Max Connections possible usable within pool<br/>
	 * <code>db_name</code>: <code>String</code> : Name of the Database<br/>
	 * <code>db_user</code>: <code>String</code> : Name of the User to connect<br/>
	 * <code>db_password</code>: <code>String</code> : Pasword used to authenticate<br/>
	 * @return <code>Result<Void, Err></code><br/>
	 * Possible ErrKind =><br/>
	 *		InitiliazationFailure<br/>
	 *		ClassNotFound<br/>
	 *		IllegalState<br/>
	 *		OutOfMemory<br/>
	 *		Null<br/>
	 *		NumberFormat<br/>
	 */

	@GuardedBy("data_source")
	public static Result<Void, Err> init(Properties props) {
		
		try {
			Class.forName("org.postgresql.Driver");

			String db_connection_timeout = props.getProperty("db_connection_timeout");
			if (db_connection_timeout == null) {
				return Result.err(new Err(
					ErrKind.Null, 
					"`db_connection_timeout` is null"
				));
			}

			Result<Integer, Err> timeout = Parser.parse_int(db_connection_timeout);
			if (timeout.isErr()) {
				return Result.err(new Err(
					ErrKind.NumberFormat,
					"Cannot parse `db_connection_timeout`"
				));
			}

			String db_init_pool = props.getProperty("db_init_pool");
			if (db_init_pool == null) {
				return Result.err(new Err(
					ErrKind.Null, 
					"`db_init_pool` is null"
				));
			}

			Result<Integer, Err> init_pool = Parser.parse_int(db_init_pool);
			if (init_pool.isErr()) {
				return Result.err(new Err(
					ErrKind.NumberFormat,
					"Cannot parse `db_init_pool`"
				));
			}

			String db_max_pool_size = props.getProperty("db_max_pool_size");
			if (db_max_pool_size == null) {
				return Result.err(new Err(
					ErrKind.Null,
					"`db_max_pool_size` is null"
				));
			}
			Result<Integer, Err> max_pool_size = Parser.parse_int(db_max_pool_size);
			if (max_pool_size.isErr()) {
				return Result.err(new Err(
					ErrKind.NumberFormat,
					"Cannot parse `db_max_pool_size`"
				));
			}

			String db_name = props.getProperty("db_name");
			if (db_name == null) {
				return Result.err(new Err(
					ErrKind.Null,
					"`db_name` is null"
				));
			}

			String db_username = props.getProperty("db_username");
			if (db_username == null) {
				return Result.err(new Err(
					ErrKind.Null,
					"`db_username` is null"
				));
			}

			String db_password = props.getProperty("db_password");
			if (db_password == null) {
				return Result.err(new Err(
					ErrKind.Null,
					"`db_password` is null"
				));
			}

			synchronized (data_source) {
				data_source = new PGPoolingDataSource();
				if (data_source == null) {
					return Result.err(new Err(
						ErrKind.InitiliazationFailure,
						"Cannot Initiliaze Connection Pool"
					));
				}

				data_source.setConnectTimeout(timeout.unwrap()); // 2s
				data_source.setInitialConnections(init_pool.unwrap());
				data_source.setMaxConnections(max_pool_size.unwrap());

				data_source.setServerName("localhost");
				data_source.setDatabaseName(db_name);
				data_source.setUser(db_username);
				data_source.setPassword(db_password);

				return Result.ok(null);
			}
		} catch (ClassNotFoundException e) {
			return Result.err(new Err(ErrKind.ClassNotFound, "Cannot load Postgres Driver"));
		} catch (IllegalStateException e) {
			return Result.err(new Err(ErrKind.IllegalState, "DB State cannot be overwritten"));
		} catch (OutOfMemoryError e) {
			return Result.err(new Err(ErrKind.OutOfMemory, "JVM Out of Memory"));
		}
	}

	@GuardedBy("data_source")
	public static Result<Connection, Err> get_connection() {
		try {
			if (data_source == null) {
				return Result.err(new Err(
					ErrKind.InitiliazationFailure, 
					"cannot get connection before Initiliazation")
				);
			}

			synchronized(data_source) {
				Connection cnx = data_source.getConnection();
				if (cnx != null)
					return Result.ok(cnx);
				else return Result.err(new Err(
					ErrKind.Null,
					"DB Connection is null"
				));
			}
		} catch (SQLException e) {
			return Result.err(new Err(
				ErrKind.InitiliazationFailure,
				"Cannot get connection from Database"
			));
		}
	}

	// Is this fine??
	// TODO: Analyze if closing the connection pool 
	//		 releases all the resources the connections hold
	@GuardedBy("data_source")
	public static void close() throws InterruptedException {
		synchronized(data_source) {
			if (data_source != null)
				Database.data_source.close();
		}
	}
}
