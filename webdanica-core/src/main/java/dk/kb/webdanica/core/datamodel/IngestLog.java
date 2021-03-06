package dk.kb.webdanica.core.datamodel;

import java.util.Date;
import java.util.List;


//https://datastax.github.io/java-driver/manual/#cql-to-java-type-mapping
//TODO have altered inserted_date from timestamp to bigint, because conversion from timestamp via LocalDate to Date could be a problem

public class IngestLog {

	private Date insertedDate;
	private String filename;
	private List<String> logEntries;
	private long linecount;
	private long insertedcount;
	private long rejectedcount;
	private long duplicatecount;
	private long errorcount;
	
	public IngestLog(List<String> entries, String filename, long linecount, long insertedcount, long rejectedcount, long duplicatecount, long errorcount) {
		this.logEntries = entries;
		this.filename = filename;
		this.linecount= linecount;
		this.insertedcount = insertedcount;
		this.rejectedcount = rejectedcount;
		this.duplicatecount= duplicatecount;
		this.errorcount = errorcount;
	}
	
	public IngestLog(List<String> entries, String filename, Date insertedDate, long linecount, long insertedcount, long rejectedcount, long duplicatecount, long errorcount) {
		this.filename = filename;
		this.logEntries = entries;
		this.insertedDate = insertedDate;
		this.linecount= linecount;
		this.insertedcount = insertedcount;
		this.rejectedcount = rejectedcount;
		this.duplicatecount= duplicatecount;
		this.errorcount = errorcount;
	}
	
	public String getFilename() {
	    return this.filename;
    }
	
	public Date getDate() {
	    return insertedDate;
    }
	
	public List<String> getLogEntries() {
		return logEntries;
	}

	public long getLinecount() {
	    return linecount;
    }

		public long getInsertedcount() {
	    return insertedcount;
    }

	public long getRejectedcount() {
	    return rejectedcount;
    }

	
	public long getDuplicatecount() {
	    return duplicatecount;
    }
	
	public long getErrorcount() {
        return errorcount;
    }

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Ingest at " + (insertedDate==null?new Date():insertedDate)  + " from file " +  filename + " with " + logEntries.size() + " Logentries\n");
		sb.append("linecount=" + linecount +", insertedcount=" + insertedcount + ", rejectedcount=" + rejectedcount + ", duplicatecount=" + duplicatecount + ", errorcount=" + errorcount + "\n");
		sb.append("loglines: \n");
		int count=0;
		for (String logline: logEntries) {
			count++;
			sb.append("logline #" + count + ": " + logline + "\n");  
		}
				
		return sb.toString();
	}
	
	public String getStatistics() {
		StringBuilder sb = new StringBuilder();
		sb.append("Ingest at " + (insertedDate==null?new Date():insertedDate)  + " from file " +  filename + " with " + logEntries.size() + " log-entries\n");
		sb.append("linecount=" + linecount +", insertedcount=" + insertedcount + ", rejectedcount=" + rejectedcount + ", duplicatecount=" + duplicatecount + ", errorcount=" + errorcount +"\n");
		return sb.toString();
	}
	
}
