package kiblerdude.nasalogs;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.Test;

public class AppendFileTest {

    @Test
    public void test() throws IOException {
        String filename = "test.txt";
        StringBuilder buffer = new StringBuilder();

        try (final PrintWriter writer = new PrintWriter(new File(filename));
                final CSVPrinter printer = CSVFormat.DEFAULT.print(writer)) {
            printer.print("line1");
        }

        try (final FileWriter writer = new FileWriter(new File(filename), true);
                final CSVPrinter printer = CSVFormat.DEFAULT.print(buffer)) {
            printer.print("line2");
            //writer.println(buffer);
            writer.append(buffer);
        }
        
        new File(filename).delete();

    }

}
