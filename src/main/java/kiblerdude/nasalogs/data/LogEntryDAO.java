package kiblerdude.nasalogs.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import kiblerdude.nasalogs.model.LogEntry;
import kiblerdude.nasalogs.services.Database;

/**
 * Data Access Object for Log entries.
 * 
 * @author kiblerj
 *
 */
public class LogEntryDAO {
	
	private final Database database;
	
	// a simple id generator, because hsqldb doesnt support auto increment
	private static int idGen = 1;
	
	private static final String SQL_INSERT = "insert into logs (id, host, date, endpoint, response, page) values ";
	private static final String SQL_VALUES = "(?,?,?,?,?,?)";	
	
	public LogEntryDAO(final Database database) {
		this.database = database;	
	}
	
	/**
	 * Inserts one or more {@link LogEntry} into the database.
	 * 
	 * @param logs
	 * @return True of false if the insert was successful.
	 * @throws SQLException
	 */
	public boolean storeLogEntries(List<LogEntry> logs) throws SQLException {

		// build a bulk insert sql
		StringBuilder query = new StringBuilder();
		query.append(SQL_INSERT);
		for (int i = 0; i < logs.size(); i++) {
			query.append(SQL_VALUES);
			if (i < logs.size() - 1) {
				query.append(",");
			}

		}
		
		// get the connection to the db and then execute the insert
		Connection connection = database.getConnection();
		try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
			int i = 1;
			for (LogEntry log : logs) {
				stmt.setInt(i++, idGen);
				stmt.setString(i++, log.getHost());
				stmt.setTimestamp(i++, log.getDate());
				stmt.setString(i++, log.getEndpoint());
				stmt.setInt(i++, log.getResponseCode());
				stmt.setBoolean(i++, log.isPage());
				idGen++;
			}			
			
			return stmt.execute();
		} catch (Exception e) {
			for (LogEntry log : logs) {
				System.out.println(log);
			}
			throw e;
		}
	}
}
