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

import soda.SodaBrowser;
import soda.SodaCSV;
import soda.SodaCSVData;
import soda.SodaFirefox;
import soda.SodaReporter;
import soda.SodaSupportedBrowser;
import soda.SodaTest;

public class SodaSuite {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String sodaTest = "/Users/trichmond/Documents/workspace/Soda-Project/src/test1.xml";
		String sodaCSV = "/Users/trichmond/Documents/workspace/Soda-Project/data.csv";
		SodaTest testobj = null;
		SodaBrowser browser = null;
		SodaCSV csv = null;
		SodaCSVData csv_data = null;
		SodaReporter reporter = null;
		
		System.out.printf("Starting SodaSuite...\n");
		
		try {
			reporter = new SodaReporter("csv-test", "/Users/trichmond");
			csv = new SodaCSV(sodaCSV, reporter);
			csv_data = csv.getData();
			
			browser = new SodaFirefox();
			testobj = new SodaTest(sodaTest, browser);
		} catch(Exception exp) {
			exp.printStackTrace();
		}
		
		System.out.printf("SodaSuite Finished.\n");
	}

}
