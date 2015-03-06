package kiblerdude.nasalogs.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.Server;

/**
 * Creates an embedded HSQLDB (@link Server} and provides access to a {@link Connection} to the database.
 * 
 * @author kiblerj
 *
 */
public class Database {
	
	private final HsqlProperties properties;
	private final Server server;
	private Connection connection;
	
	private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS logs";
	
	private static final String SQL_CREATE_TABLE =
			"CREATE TABLE logs (" +
			"id INT NOT NULL," +
			"host VARCHAR(64) NULL," +
			"date DATE NULL," +
			"endpoint VARCHAR(256) NULL," +
			"response INT NULL," +
			"page BOOLEAN NULL," +
			"PRIMARY KEY (id))";
	
	public Database() {
		properties = new HsqlProperties();
		properties.setProperty("server.database.0","file:./nasalogs.db");
		properties.setProperty("server.dbname.0","nasalogs");
		server = new Server();
	}
	
	/**
	 * Starts the HSQLDB {@link Server} and establishes the {@link Connection}.
	 * @throws Exception
	 */
	public void start() throws Exception {
		server.setProperties(properties);
		//server.start();
		//connection = DriverManager.getConnection("jdbc:hsqldb:file:./nasalogs.db", "sa", "");
		connection = DriverManager.getConnection("jdbc:mysql://localhost/nasalogs?user=test&password=test");
		
		// drop the table (if it exists)
		PreparedStatement delete = connection.prepareStatement(SQL_DROP_TABLE);
		delete.execute();
		delete.close();
		
		// create the table
		PreparedStatement create = connection.prepareStatement(SQL_CREATE_TABLE);
		create.execute();
		create.close();
	}
	
	/**
	 * Stops the HSQLDB {@link Server}.
	 * @throws Exception 
	 */
	public void stop() throws Exception {
		connection.close();
		//server.stop();
	}
	
	/**
	 * Returns a {@link Connection} to the database.
	 * @return {@link Connection} or <code>null</code> if the Database has not been started.
	 */
	public Connection getConnection() {
		return connection;
	}
}
