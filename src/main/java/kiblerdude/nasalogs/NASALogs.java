package kiblerdude.nasalogs;

import kiblerdude.nasalogs.data.LogEntryDAO;
import kiblerdude.nasalogs.data.PopularityDAO;
import kiblerdude.nasalogs.data.SecurityDAO;
import kiblerdude.nasalogs.services.CsvGenerator;
import kiblerdude.nasalogs.services.Database;
import kiblerdude.nasalogs.services.LogParser;

/**
 * We want to gather statistics for what interests people on the NASA website. Here are the breakdowns
 * that are most important for conversions according to a highly placed resource in our team's space 
 * exploration division:
 * 
 * 1. Top 10 popular pages between 0000 EST and 0800 EST on a daily basis.
 * 2. Top 10 popular pages between 0800 EST and 1000 EST on a daily basis.
 * 3. Top 10 popular pages for July.
 * 
 * The tin hats at security somehow heard that you might be working on this program and requested a couple of other features:
 * 
 * 1. List of source getting 400 responses, sorted in descending order of number of 400 responses.
 * 2. A list of unique URLs that got 400 response from the servers.
 * 3. The number of 200, 400 and 500 responses per URL per day.
 * 
 * @author kiblerj
 *
 */
public class NASALogs {
	
	public void process() throws Exception {

		Database database = new Database();
		CsvGenerator csv = new CsvGenerator();
		LogEntryDAO logEntryDao = new LogEntryDAO(database);
		SecurityDAO securityDao = new SecurityDAO(database, csv);
		PopularityDAO popularityDao = new PopularityDAO(database, csv);
		LogParser parser = new LogParser(logEntryDao, "NASA_access_log_Jul95.gz");
		
		long startTime = System.currentTimeMillis();
		
		// initialize the database
		database.start();
		
		// parse the access logs
		parser.parse();

		// generate the reports
		popularityDao.generatePopularPagesBetween0And8();
		popularityDao.generatePopularPagesBetween8And10();
		popularityDao.generateTop10PagesForJuly();
		securityDao.generate400ResponseByHost();
		securityDao.generate400ResponsesByUniqueUrl();
		securityDao.generateResponseCodesPerUrlPerDay();
		
		long stopTime = System.currentTimeMillis();
		
		database.stop();

		System.out.println(String.format("Total execution time was %d ms", stopTime - startTime));
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("Starting NASALogs");
		new NASALogs().process();
	}
}
