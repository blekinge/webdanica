package dk.kb.webdanica.core.tools;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import dk.kb.webdanica.core.datamodel.criteria.CriteriaIngest;
import dk.kb.webdanica.core.datamodel.dao.DAOFactory;
import dk.kb.webdanica.core.datamodel.dao.HBasePhoenixDAOFactory;
import dk.kb.webdanica.core.utils.DatabaseUtils;

public class CriteriaIngestTool {

	public static void main(String[] args) throws IOException, SQLException {
		if (args.length != 2) {
			System.err.println("dk.kb.webdanica.tools.CriteriaIngestTool: Missing arguments");
			System.err.println("Usage: CriteriaIngestTool <harvestlogfile> <criteria-results-dir>");
			System.exit(1);
		}
		File harvestLogFile = new File(args[0]);
		File criteriaresultsdir = new File(args[1]);
		if (!harvestLogFile.isFile()) {
			System.err.println("The harvestLogfile located at '" + harvestLogFile.getAbsolutePath() 
					+ "' does not exist or is not a proper file");
			System.exit(1);
		}

		if (!criteriaresultsdir.isDirectory()) {
			System.err.println("The criteriaresultdir located at '" + criteriaresultsdir.getAbsolutePath() 
					+ "' does not exist or is not a proper directory");
			System.exit(1);
		}
		DAOFactory daofactory = DatabaseUtils.getDao();
		try {
			CriteriaIngest.ingest(harvestLogFile, criteriaresultsdir, true, daofactory);
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			daofactory.close();
			System.exit(0);
		}
		
	}

}
