package kiblerdude.nasalogs.data;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import kiblerdude.nasalogs.services.CsvGenerator;
import kiblerdude.nasalogs.services.Database;

/**
 * Data Access Object for Security reports.
 * 
 * @author kiblerj
 *
 */
public class SecurityDAO {
	
	private final Database database;
	private final CsvGenerator csv;
	
	private static final String SQL_400_RESPONSES_BY_HOST = 
			"select host, count(response) as amount " +
			"from logs " +
			"where response >= 400 AND response < 500 " +
			"group by host " +
			"order by amount desc";
	
	private static final String SQL_400_RESPONSES_UNIQUE_URLS = 
			"select distinct(endpoint) " +
			"from logs " +
			"where response >= 400 AND response < 500 " +
			"order by endpoint";
	
	private static final String SQL_RESPONSES_BY_URL_BY_DAY = "select dayofmonth(date), endpoint, response, count(*) as amount " +
			"from logs " +
			"group by dayofmonth(date), endpoint, response";	
	
	public SecurityDAO(final Database database, final CsvGenerator csv) {
		this.database = database;	
		this.csv = csv;
	}
	
	/**
	 * Generates a report of 400 responses by host.
	 * 
	 * @param logs
	 * @return True of false if the insert was successful.
	 * @throws SQLException
	 * @throws IOException 
	 */
	public boolean generate400ResponseByHost() throws SQLException, IOException {
		System.out.println("Generating report for 400 response by host");
		return executeQuery("report_400_responses_by_host.csv", SQL_400_RESPONSES_BY_HOST);
	}
	
	/**
	 * Generates a report of 400 responses by unique URL.
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public boolean generate400ResponsesByUniqueUrl() throws SQLException, IOException {
		System.out.println("Generating report for 400 response by unique URL");
		return executeQuery("report_400_responses_by_unique_url.csv", SQL_400_RESPONSES_UNIQUE_URLS);		
	}
	
	/**
	 * Genereates a report of response codes per URL per day.
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public boolean generateResponseCodesPerUrlPerDay() throws SQLException, IOException {
		System.out.println("Generating report for responses per URL per day");
		return executeQuery("report_responses_by_url_by_day.csv", SQL_RESPONSES_BY_URL_BY_DAY);
	}
	
	private boolean executeQuery(String filename, String query) throws SQLException, IOException {
		Connection connection = database.getConnection();
		try (PreparedStatement stmt = connection.prepareStatement(query);
			 ResultSet rs = stmt.executeQuery()) {
			csv.generateCsv(filename, false, rs);
			return true;
		}		
	}
}
