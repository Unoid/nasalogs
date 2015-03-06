package kiblerdude.nasalogs.data;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import kiblerdude.nasalogs.services.CsvGenerator;
import kiblerdude.nasalogs.services.Database;

/**
 * Data Access Object for Popularity reports.
 * 
 * @author kiblerj
 *
 */
public class PopularityDAO {
	
	//BETWEEN '1995-07-01 08:00:00' AND '1995-07-01 10:00:00'
	private static final String SQL_TOP_10_POPULAR_PAGES_BETWEEN_TIMES = 
			"select dayofmonth(date), endpoint, count(*) as count " +
			"from logs " +
			"where page = true and date BETWEEN ? AND ? " +
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
		Timestamp startTime = getTimestamp("1995-07-01 00:00:00");
		Timestamp stopTime = getTimestamp("1995-07-01 08:00:00");
		return generatePopularPagesBetween("report_top_10_pages_between_0000_and_0800.csv", startTime, stopTime);
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
		Timestamp startTime = getTimestamp("1995-07-01 08:00:00");
		Timestamp stopTime = getTimestamp("1995-07-01 10:00:00");
		return generatePopularPagesBetween("report_top_10_pages_between_0800_and_1000.csv", startTime, stopTime);
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
			csv.generateCsv(filename, rs);
			return true;
		}		
	}
	
	private boolean generatePopularPagesBetween(String filename, Timestamp startTime, Timestamp stopTime) throws SQLException, IOException, ParseException {
		Connection connection = database.getConnection();
		Calendar start = Calendar.getInstance(TimeZone.getTimeZone("EST"));
		start.setTime(startTime);
		Calendar stop =  Calendar.getInstance(TimeZone.getTimeZone("EST"));
		stop.setTime(stopTime);
		
		// query for each day of the month
		for (int day = 1; day < 3; day++) {
			try (PreparedStatement stmt = connection.prepareStatement(SQL_TOP_10_POPULAR_PAGES_BETWEEN_TIMES)) {
				System.out.println("querying for day " + day);
				start.set(Calendar.DAY_OF_MONTH, day);
				stop.set(Calendar.DAY_OF_MONTH, day);
				
				stmt.setTimestamp(1, new Timestamp(start.getTime().getTime()));
				stmt.setTimestamp(2, new Timestamp(stop.getTime().getTime()));
				
				ResultSet rs = stmt.executeQuery();
				csv.generateCsv(filename, rs);
				rs.close();
			}			
		}
		
		return true;
	}

	private Timestamp getTimestamp(String input) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("EST"));
		Date date = format.parse(input);
		return new Timestamp(date.getTime());
	}	
}
