package dk.kb.webdanica.core.datamodel.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import dk.kb.webdanica.core.datamodel.Cassandra;
import dk.kb.webdanica.core.datamodel.Database;
import dk.kb.webdanica.core.datamodel.IngestLog;

/**
 * DAO for logging skipped entries during ingest in a single entry.
  
  CREATE table ingestLog (
   loglines list<text>,
   filename text,
   inserted_date long, //millis since epoch
   PRIMARY KEY (inserted_date));  
 * 
 */
public class CassandraIngestLogDAO implements IngestLogDAO, Database {
	
	public static void main(String args[]) {
		CassandraIngestLogDAO dao = CassandraIngestLogDAO.getInstance();
		List<String> entries = new ArrayList<String>();
		entries.add("Line one in sample loglist");
		entries.add("Line two in sample loglist");
		long linecount=2;
		long rejectedcount=2;
		long insertedcount=0;
		long duplicatecount=2;
		long errorcount=0L;
		try {
			IngestLog log = new IngestLog(entries,"unknown",  linecount, insertedcount, rejectedcount, duplicatecount, errorcount) ;
			dao.insertLog(log);
			for (Long date: dao.getIngestDates()) {
				System.out.println(new Date(date));
				IngestLog readLog = dao.readIngestLog(date);
				System.out.println(readLog);
			}
			
		} finally {
			dao.close();
		}
	}

	static CassandraIngestLogDAO instance;
	
	private Database db;

	private Session session;
	
	private PreparedStatement preparedInsert;
	
	public synchronized static CassandraIngestLogDAO getInstance(){
		if (instance == null) {
			instance = new CassandraIngestLogDAO();
		} 
		return instance;
	}
	
	public CassandraIngestLogDAO() {
		db = new Cassandra(CassandraSettings.getDefaultSettings());
	}

	@Override
	public boolean insertLog(IngestLog log){
		init();
		Long insertedDate = System.currentTimeMillis();
		if (log.getDate() != null) {
			insertedDate = log.getDate().getTime();
		}
		
		BoundStatement bound = preparedInsert.bind(log.getLogEntries(), log.getFilename(), insertedDate, log.getLinecount(), log.getInsertedcount(), 
		        log.getRejectedcount(), log.getDuplicatecount(), log.getErrorcount());
		ResultSet results = session.execute(bound); 
		// TODO can we check, if the insert was successful?
		// Possible solution: http://stackoverflow.com/questions/21147871/cassandara-java-driver-how-are-insert-update-and-delete-results-reported
		Row row = results.one();
		boolean insertFailed = row.getColumnDefinitions().contains("loglines");
		if (insertFailed){
			System.out.println("Insert failed");
		}
		return !insertFailed;
	}

	@Override
	public List<Long> getIngestDates() { // as represented as millis from epoch
		init();
		ResultSet results = session.execute("SELECT inserted_date from ingestLog");
		List<Long> ingestDates = new ArrayList<Long>();
		for (Row row: results.all()) {
				ingestDates.add(row.getLong("inserted_date"));
		}
		return ingestDates;
	}

	@Override
	public IngestLog readIngestLog(Long timestamp) {
		PreparedStatement statement = getSession().prepare("SELECT * FROM ingestLog WHERE inserted_date=?");
		BoundStatement bStatement = statement.bind(timestamp);
		ResultSet results = session.execute(bStatement);
		Row singleRow = results.one();
		IngestLog retrievedLog = new IngestLog(singleRow.getList("logLines", String.class), 
				singleRow.getString("filename"), new Date(singleRow.getLong("inserted_date")), 
				singleRow.getLong("linecount"), 
				singleRow.getLong("insertedcount"),
				singleRow.getLong("rejectedcount"),
				singleRow.getLong("duplicatecount"),
				singleRow.getLong("errorcount"));
		return retrievedLog;
	}
	
	/** Initialize session and preparedStatement if necessary */
	private void init() {
		if (session == null || session.isClosed()) {
			session = db.getSession();
		}
		if (preparedInsert == null) {
			preparedInsert = session.prepare("INSERT INTO ingestLog (logLines, filename, inserted_date, linecount, insertedcount, rejectedcount, duplicatecount, errorcount) VALUES (?, ?, ?, ?, ?, ?, ?, ?) IF NOT EXISTS");
		}
	}

	@Override
    public boolean isClosed() {
	    return db.isClosed();
    }

	@Override
    public void close() {
	    if (!db.isClosed()) {
	    	db.close();
	    }
	    
    }

	@Override
    public Session getSession() {
	    return session;
    }

}
