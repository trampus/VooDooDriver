package soda;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SodaReporter {

	private String resultDir = "";
	private String reportLog = null;
	private FileOutputStream reportFD = null;
	private String datetime_str = "";
	private int Blocked = 0;
	private int Exceptions = 0;
	private int FailedAsserts = 0;
	private int PassedAsserts = 0;
	
	//google-05-04-2011-15-07-results/Report-google.html
	
	public SodaReporter(String reportName, String resultDir) {
		DateFormat dateformat = new SimpleDateFormat("MM-DD-yyyy-hh-m-s-S");
		Date now = new Date();
		String date_str = dateformat.format(now);
		
		if (resultDir != null) {
			File dir = new File(resultDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			this.resultDir = resultDir;
		} else {
			this.resultDir = System.getProperty("user.dir");
		}

		reportLog = this.resultDir + "/" + reportName + "-" + date_str + ".log";
		System.out.printf("ReportFile: %s\n", reportLog);
		
		try {
			reportFD = new FileOutputStream(reportLog);
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		
		
	}
	
}
