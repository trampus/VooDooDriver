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

import java.io.File;
import soda.SodaBlockList;
import soda.SodaBlockListParser;
import soda.SodaBrowser;
import soda.SodaCSV;
import soda.SodaCSVData;
import soda.SodaChrome;
import soda.SodaCmdLineOpts;
import soda.SodaFirefox;
import soda.SodaHash;
import soda.SodaIE;
import soda.SodaReporter;
import soda.SodaSuiteParser;
import soda.SodaSupportedBrowser;
import soda.SodaTest;
import soda.SodaTestList;

public class SodaSuite {

	public static String VERSION = "0.0.1";
	
	public static void printUsage() {
		String msg = "SodaSuite\n"+
		"Usage: SodaSuite --browser=\"firefox\" --test=\"sodatest1.xml\""+
		" --test=\"sodatest2.xml\" ...\n\n"+
		"Required Flags:\n"+
		"   --browser: This is any of the following supported web browser name.\n"+
		"      [ firefox, safari, ie ]\n\n"+
		"   --test: This is a soda test file.  This argument can be used more then"+
		"once when there are more then one soda tests to run.\n\n"+
		"   --savehtml: This flag will cause html pages to be saved when there is an"+
		" error testing the page.\n\n"+
		"   --hijack: This is a key/value pair that is used to hi jack any csv file\n"+
		"      values of the same name.  The key and value are split using \"::\"\n"+  
		"      Example: --hijack=\"username::sugaruser\"\n\n"+
		"   --resultdir: This allows you to override the default results directory.\n\n"+
		"   --gvar: This is a global var key/value pair to be injected into Soda.\n"+
		"      The key and value are split using \"::\"\n"+
		"      Example: --gvar=\"slayerurl::http://www.slayer.net\"\n\n"+
		"   --suite: This is a Soda suite xml test file.\n\n"+
		"   --skipcsserrors: This tells soda to not report on css errors.\n\n"+
		"   --testdelay: This forces a 10 second delay in between tests that run in a"+
		" suite.\n\n"+
		"   --blocklistfile: This is the XML file containing tests to block from running.\n\n"+
		"   --version: Print the Soda Version string.\n\n";
		
		System.out.printf("%s\n", msg);
	}
	
	public static void main(String[] args) {
		String sodaConfigFile = "soda-config.xml";
		File sodaConfigFD = null;
		String sodaTest = "/Users/trichmond/Documents/workspace/Soda-Project/src/test1.xml";
		String sodaCSV = "/Users/trichmond/Documents/workspace/Soda-Project/data.csv";
		SodaTest testobj = null;
		SodaBrowser browser = null;
		SodaCSV csv = null;
		SodaCSVData csv_data = null;
		SodaReporter reporter = null;
		String blockListFile = null;
		SodaBlockList blockList = null;
		SodaCmdLineOpts opts = null;
		SodaHash cmdOpts = null;
		SodaSupportedBrowser browserType = null;
		
		System.out.printf("Starting SodaSuite...\n");
		try {
			opts = new SodaCmdLineOpts(args);
			cmdOpts = opts.getOptions();
			
			SodaSuiteParser suiteP = new SodaSuiteParser("suite.xml");
			SodaTestList tmp = suiteP.getTests();
			
			for (int i = 0; i <= tmp.size() -1; i++) {
				String t = tmp.get(i);
				System.out.printf("File: %s\n", t);
			}
			
			System.exit(0);
			
			sodaConfigFD = new File(sodaConfigFile);
			if (sodaConfigFD.exists()) {
				System.out.printf("(*)Found SodaSuite config file: %s\n", sodaConfigFile);
				SodaConfigParser scp = new SodaConfigParser(sodaConfigFD);
			}
			
			if ((Boolean)cmdOpts.get("help")) {
				printUsage();
				System.exit(0);
			}
			
			if ((Boolean)cmdOpts.get("version")) {
				System.out.printf("(*)SodaSuite Version: %s\n", SodaSuite.VERSION);
				System.exit(0);
			}
			
			try {
				browserType = SodaSupportedBrowser.valueOf(cmdOpts.get("browser").toString().toUpperCase());
			} catch (Exception expBrowser) {
				System.out.printf("(!)Unsupported browser: '%s'!\n", cmdOpts.get("browser").toString());
				System.out.printf("(!)Exiting!\n\n");
				System.exit(2);
			}
			
			blockListFile = cmdOpts.get("blocklistfile").toString();
			if (blockListFile != null) {
				SodaBlockListParser sbp = new SodaBlockListParser(blockListFile);
				blockList = sbp.parse();
				
				// debug printing, will remove later //
				for (int i = 0; i <= blockList.size() -1; i++) {
					System.out.printf("(*)Blocking Test File: %s\n", blockList.get(i));
				}

			} else {
				System.out.printf("(*)No Block list file to parse.\n");
				blockList = new SodaBlockList();
			}
			
			switch (browserType) {
			case FIREFOX:
				browser = new SodaFirefox();
				break;
			case CHROME:
				browser = new SodaChrome();
				break;
			case IE:
				browser = new SodaIE();
				break;
			}
			
			browser.newBrowser();
			
			//if (cmdOpts.get("tests")
			
			
			long start = System.currentTimeMillis();
			testobj = new SodaTest(sodaTest, browser, (SodaHash)cmdOpts.get("gvars"), 
					(SodaHash)cmdOpts.get("hijacks"), blockList);
			testobj.runTest();
			long stop = System.currentTimeMillis();
			
			long diff = stop - start;
			int seconds = (int) ((diff / 1000) % 60);
			System.out.printf("Total Run Time in Seconds: %d\n", seconds);
			
		} catch(Exception exp) {
			exp.printStackTrace();
		}
		
		System.out.printf("SodaSuite Finished.\n");
	}

}
