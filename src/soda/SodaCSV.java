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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SodaCSV {

	private SodaReporter report = null;
	private ArrayList<String> keys = null;
	private SodaCSVData data = null;
	
	public SodaCSV(String csvfile, SodaReporter reporter) {
		FileInputStream fs = null;
		BufferedReader br = null;
	
		this.report = reporter;
		
		try {
			this.keys = new ArrayList<String>();
			data = new SodaCSVData();
			
			fs = new FileInputStream(csvfile);
			br = new BufferedReader(new InputStreamReader(fs));
			this.findKeys(br);
			this.createData(br);
		} catch (Exception e) {
			this.report.ReportException(e);
		}
	}
	
	/*
	 * getData -- method
	 * 	This method returns the data generated from a soda CSV file.
	 * 
	 * Input:
	 * 	None.
	 * 
	 * 
	 * Output:
	 * 	returns a SodaCSVData object.
	 * 
	 */
	public SodaCSVData getData() {
		return this.data;
	}
	
	/*
	 * createData -- method
	 * 	This method reads the csv file and process the file into an array of hashes.
	 * 
	 * Input:
	 * 	br: the BufferedReader for the open csv file, after the key line has been read.
	 * 
	 * Output:
	 * 	None.
	 * 
	 */
	private void createData(BufferedReader br) {
		String line = "";
		String[] linedata;
		
		try {
			while ((line = br.readLine()) != null) {
				line = line.replaceAll("\\n", "");
				if (line.isEmpty()) {
					continue;
				}
				
				linedata = line.split(",");
				int linelen = linedata.length -1;
				SodaHash tmphash = new SodaHash();
				for (int i = 0; i <= this.keys.size() -1; i++) {
					if (i <= linelen) {
						tmphash.put(this.keys.get(i), linedata[i]);
					} else {
						tmphash.put(this.keys.get(i), "");
					}
				}
				this.data.add(tmphash);
			}
		} catch (Exception exp) {
			this.report.ReportException(exp);
		}
	}
	
	/*
	 * findKeys -- method
	 * 	This method finds the csv files key line and processes the line into an array.
	 * 
	 * Input:
	 * 	br: The BufferedReader for the open file starting at the beginning of the file.
	 * 
	 * Output:
	 * 	None.
	 * 
	 */
	private void findKeys(BufferedReader br) {
		String line = "";
		String[] lines;
		
		try {
			keys = new ArrayList<String>();
			
			while ((line = br.readLine()) != null) {
				line = line.replaceAll("\\n", "");
				if (line.isEmpty()) {
					continue;
				} else {
					break;
				}
			}
			
			lines = line.split(",");
			for (int i = 0; i <= lines.length -1; i++) {
				this.keys.add(lines[i]);
			}
			
		} catch (Exception exp) {
			this.report.ReportException(exp);
		}
	}
}
