# NASA Request Logs

This simple project parses the provided NASA's web server request logs  (`NASA_access_log_Jul95.gz`) from July 1995, and generates a few reports about the requests.

# How to run

The following dependencies are required to run this application:

- Java 7
- Maven 3

To run, simply clone the project and use maven to compile and execute the application:

	$ git clone https://github.com/kiblerdude/nasalogs.git
	$ cd nasalogs
	$ mvn clean compile
	$ mvn exec:java
	Parsing logs................................................
	...
	Generating report for top between 0 and 8
	...
	Generating report for responses per URL per day
	Total execution time was 211144 ms
	$

The application will take a few minutes to process the request logs and  generate the reports.

The application will generate six reports in CSV format in the base project directory:

	$ ls -ltr report*
	report_top_10_pages_between_0000_and_0800.csv
	report_top_10_pages_between_0800_and_1000.csv
	report_top_10_pages_for_july.csv
	report_400_responses_by_host.csv
	report_400_responses_by_unique_url.csv
	report_responses_by_url_by_day.csv
	$

# Solution

The solution I used was to write a simple Java application to parse the logs, store the data in an embedded database (HSQLDB), and then run SQL queries against the database to generate the reports.

I considered two alternative approaches:

1. We could probably use command line fanciness to process the logs and generate reports (zgrep, awk, sort, uniq).  Some of the reports would be super easy to generate this way, but some of the others would be more complicated, such as top 10 URLs between certain times on a daily basis.  I think the command line magic would have been cryptic, and I would have spent a while trying to figure it out.  If I could come up with the command line magic, this might be the fastest approach to solving this problem.
2. I considered using an Observer pattern to publish each parsed web server request to subscribing instances of a `ReportGenerator`.  There would be a `ReportGenerator` for each report type, and each instance would have all sorts of data structures to keep track of the results they were looking for.  This would be more of a real time data processing approach.