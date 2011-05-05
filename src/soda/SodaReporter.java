/*
Copyright 2011 Trampus Richmond. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

   1. Redistributions of source code must retain the above copyright notice, this list of
      conditions and the following disclaimer.

   2. Redistributions in binary form must reproduce the above copyright notice, this list
      of conditions and the following disclaimer in the documentation and/or other materials
      provided with the distribution.

THIS SOFTWARE IS PROVIDED BY TRAMPUS RICHMOND ``AS IS'' AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
TRAMPUS RICHMOND OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those of the authors and 
should not be interpreted as representing official policies, either expressed or implied, of Trampus Richmond.
 
 */

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
	private int OtherErrors = 0;
	private DateFormat Dateformater = null;
	
	public SodaReporter(String reportName, String resultDir) {
		DateFormat fd = new SimpleDateFormat("MM-d-yyyy-hh-m-s-S");
		this.Dateformater = new SimpleDateFormat("MM-d-yyyy-hh-m-s.S");
		Date now = new Date();
		String date_str = fd.format(now);
		
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
	
	private void _log(String msg) {
		Date now = new Date();
		DateFormat df = new SimpleDateFormat("MM/d/yyyy-hh:m:s.S");
		String date_str = df.format(now);
		String logstr = "[" + date_str + "]" + msg + "\n";
		
		try {
			this.reportFD.write(logstr.getBytes());
			System.out.printf("%s\n", msg);
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}
	
	public void closeLog() {
		try {
			this.reportFD.close();
			this.reportFD = null;
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}

	public void Log(String msg) {
		this._log("(*)" + msg);
	}
	
	public void ReportError(String msg) {
		this._log(String.format("(!)%s", msg));
		this.OtherErrors += 1;
	}
	
	/*
	 * ReportException -- Method
	 * 	This method formats a java exception class into a SODA log entry.  Both the message and the stack
	 * 	trace are reformatted and printed to the SODA log file, and the console.
	 * 
	 * Input:
	 * 	e: The exception that happened.
	 * 
	 * Output:
	 * 	None.
	 * 
	 */
	public void ReportException(Exception e) {
		this.Exceptions += 1;
		String msg = "--Exception Backtrace: ";
		StackTraceElement[] trace = e.getStackTrace();
		String message = "";
		
		String[] msg_lines = e.getMessage().split("\\n");
		for (int i = 0; i <= msg_lines.length -1; i++) {
			message += msg_lines[i] + "  ";
		}
		
		this._log("(!)Exception raised: " + message);
		
		for (int i = 0; i <= trace.length -1; i++) {
			String tmp = trace[i].toString();
			msg += "--" + tmp;
		}
		
		this._log("(!)" + msg);
	}
	
	protected void finalize() throws Throwable {
	    try {
	    	if (this.reportFD != null) {
	    		this.reportFD.close();
	    	}
	    } finally {
	        super.finalize();
	    }
	}
	
}
