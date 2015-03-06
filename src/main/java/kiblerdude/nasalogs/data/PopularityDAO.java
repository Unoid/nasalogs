package kiblerdude.nasalogs.data;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

import kiblerdude.nasalogs.services.CsvGenerator;
import kiblerdude.nasalogs.services.Database;

/**
 * Data Access Object for Popularity reports.
 * 
 * @author kiblerj
 *
 */
public class PopularityDAO {

	private static final String SQL_TOP_10_POPULAR_PAGES_BETWEEN_TIMES = 
			"select date(date), endpoint, count(*) as count " +
			"from logs " +
			"where page = true and dayofmonth(date) = ? and hour(date) BETWEEN ? AND ? " +
			"group by endpoint " +
			"order by count desc " +
			"limit 10";	
	
	private static final String SQL_TOP_10_POPULAR_PAGES_FOR_JULY = 
			"select endpoint, count(*) as count " +
			"from logs " +
			"where page = true " +
			"group by endpoint " +
			"order by count desc " +
			"limit 10";
	
	private final Database database;
	private final CsvGenerator csv;
	
	public PopularityDAO(final Database database, final CsvGenerator csv) {
		this.database = database;
		this.csv = csv;
	}
	
	/**
	 * Generates the Top 10 Pages between 0 and 8 on a daily basis.
	 * 
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */	
	public boolean generatePopularPagesBetween0And8() throws SQLException, IOException, ParseException {
		System.out.println("Generating report for top between 0 and 8");
		return generatePopularPagesBetween("report_top_10_pages_between_0000_and_0800.csv", 0, 8);
	}
	
	/**
	 * Generates the Top 10 Pages between 8 and 10 on a daily basis.
	 * 
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */	
	public boolean generatePopularPagesBetween8And10() throws SQLException, IOException, ParseException {
		System.out.println("Generating report for top between 8 and 10");
		return generatePopularPagesBetween("report_top_10_pages_between_0800_and_1000.csv", 8, 10);
	}
	
	/**
	 * Generates the Top 10 Pages for July.
	 * 
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public boolean generateTop10PagesForJuly() throws SQLException, IOException {
		System.out.println("Generating report for top 10 pages for july");
		return executeQuery("report_top_10_pages_for_july.csv", SQL_TOP_10_POPULAR_PAGES_FOR_JULY);
	}
	
	private boolean executeQuery(String filename, String query) throws SQLException, IOException {
		Connection connection = database.getConnection();
		try (PreparedStatement stmt = connection.prepareStatement(query);
			 ResultSet rs = stmt.executeQuery()) {
			csv.generateCsv(filename, false, rs);
			return true;
		}		
	}
	
	private boolean generatePopularPagesBetween(String filename, int startHour, int stopHour) throws SQLException, IOException, ParseException {
		Connection connection = database.getConnection();
		
		// query for each day of the month
		for (int day = 1; day < 32; day++) {
			try (PreparedStatement stmt = connection.prepareStatement(SQL_TOP_10_POPULAR_PAGES_BETWEEN_TIMES)) {
				System.out.println("querying for day " + day);
				
				stmt.setInt(1, day);
				stmt.setInt(2, startHour);
				stmt.setInt(3, stopHour);
				
				ResultSet rs = stmt.executeQuery();
				csv.generateCsv(filename, day > 1, rs);
				rs.close();
			}			
		}
		
		return true;
	}
}
