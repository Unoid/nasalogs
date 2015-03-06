package kiblerdude.nasalogs.services;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class CsvGenerator {
	/**
	 * Generates a CSV file named <code>filename</code> and writes one or more {@link ResultSet} to the CSV file.
	 * 
	 * @param filename
	 * @param rs
	 * @throws SQLException
	 * @throws IOException
	 */
	public void generateCsv(String filename, ResultSet... rs)
			throws SQLException, IOException {
		try (final PrintWriter writer = new PrintWriter(new File(filename));
				final CSVPrinter printer = CSVFormat.DEFAULT.print(writer)) {
			for (ResultSet r : rs) {
				printer.printRecords(r);
			}
		}
	}
}
