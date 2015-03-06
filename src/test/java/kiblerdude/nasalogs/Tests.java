package kiblerdude.nasalogs;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

public class Tests {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws ParseException {
		String line1 = "199.120.110.21 - - [01/Jul/1995:00:00:09 -0400] \"GET /shuttle/missions/sts-73/mission-sts-73.html HTTP/1.0\" 200 4085";
		String line2 = "mizzou-ts3-03.missouri.edu - - [01/Jul/1995:03:05:40 -0400] \"GET /shuttle/missions/sts-67/images/images.html   HTTP/1.0\" 200 4464";
		print(line1);
		print(line2);
	}
	
	private void print(String line) throws ParseException {		
		System.out.println(line);		
	
		String[] columns = line.replaceAll("\"", "").split("\\s+");
		
		for ( int i = 0; i < columns.length; i++) {
			System.out.println(i + ": " + columns[i]);
		}
		// 0 = request server
		// 3 = date
		// 5 = method
		// 6 = endpoint
		// 8 = response code		
		String host = columns[0];
		String date = columns[3].replace("[", "");
		String endpoint = columns[6];
		Integer responseCode = Integer.parseInt(columns[8]);
		
		// 01/Jul/1995:00:00:09
		SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("EST"));
		Date d = format.parse(date);
		System.out.println(format.format(d));
		
		SimpleDateFormat dbFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		dbFormat.setTimeZone(TimeZone.getTimeZone("EST"));
		System.out.println(dbFormat.format(d));
		
//		System.out.println(host);
//		System.out.println(date);
//		System.out.println(endpoint);
//		System.out.println(responseCode);

		int i = 0;
		System.out.println(i++);
		System.out.println(i++);
	}

}
