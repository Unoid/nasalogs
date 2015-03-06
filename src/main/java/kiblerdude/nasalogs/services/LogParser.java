package kiblerdude.nasalogs.services;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

import kiblerdude.nasalogs.data.LogEntryDAO;
import kiblerdude.nasalogs.model.LogEntry;

/**
 * Parses the requests from the provided file and stores them in the Database.
 * 
 * @author kiblerj
 * 
 */
public class LogParser {
	
	private static final int BATCH_SIZE = 1000;
	
	private static final String[] PAGE_SUFFIXES = {".html", ".htm", "/"};
	
	private final LogEntryDAO dao;
	private final String filename;

	public LogParser(LogEntryDAO dao, String filename) {
		this.dao = dao;
		this.filename = filename;
	}
	
	private boolean isPage(String endpoint) {
		for (String suffix : PAGE_SUFFIXES) {
			if (endpoint.endsWith(suffix)) return true;
		}
		return false;
	}

	public void parse() throws Exception {
		List<LogEntry> batch = new ArrayList<>(BATCH_SIZE);
		
		try (FileInputStream bais = new FileInputStream(filename);
				GZIPInputStream gzis = new GZIPInputStream(bais);
				InputStreamReader reader = new InputStreamReader(gzis);
				BufferedReader in = new BufferedReader(reader);) {
			List<String> problems = new ArrayList<>();
			System.out.print("Parsing logs");
			int count = 0;
			int errors = 0;
			String log;
			while ((log = in.readLine()) != null) {
				String[] columns = log.replaceAll("\"", "").split("\\s+");
				
				if (columns.length >= 8) {
					String method = columns[5];
					String host = columns[0];
					String dateStr = columns[3].replace("[", "");
					String endpoint = columns[6];
					Integer responseCode = Integer.parseInt(columns[columns.length - 2]);
					
					SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss");
					format.setTimeZone(TimeZone.getTimeZone("EST"));
					Date date = format.parse(dateStr);
					Timestamp timestamp = new Timestamp(date.getTime());
					Boolean page = isPage(endpoint);
					
					if ("GET".equals(method)) {
						LogEntry entry = new LogEntry(host, timestamp, endpoint, responseCode, page);
						batch.add(entry);
					}
					
					if (batch.size() == BATCH_SIZE) {
						dao.storeLogEntries(batch);
						batch.clear();
					}
					
				} else {
					problems.add(log);
					errors++;
				}
				count++;
				if (count % 25000 == 0) {
					System.out.print(".");
				}
			}
			
			// process the last batch of log entries
			if (!batch.isEmpty()) {
				dao.storeLogEntries(batch);
				batch.clear();
			}			
			
			System.out.println();
			System.out.println(String.format("Finished parsing logs.  %d lines parsed.  %d errors.", count, errors));
			
			for (String problem : problems) {
				System.out.println("Problematic log: " + problem);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
