package kiblerdude.nasalogs.model;

import java.sql.Timestamp;

/**
 * Represents an entry in the Log.
 * 
 * @author kiblerj
 *
 */
public class LogEntry {
	private String host;
	private Timestamp date;
	private String endpoint;
	private Integer responseCode;
	private Boolean page;
	
	public LogEntry(String host, Timestamp date, String endpoint, Integer responseCode, Boolean page) {
		this.host = host;
		this.date = date;
		this.endpoint = endpoint;
		this.responseCode = responseCode;
		this.page = page;
	}
	
	public String getHost() {
		return host;
	}
	
	public Timestamp getDate() {
		return date;
	}
	
	public String getEndpoint() {
		return endpoint;
	}
	
	public Integer getResponseCode() {
		return responseCode;
	}
	
	public Boolean isPage() {
		return page;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(host).append(",").append(endpoint).append(",").append(responseCode);
		return builder.toString();
	}
}
