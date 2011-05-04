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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.ie.*;

public class SodaTest {

	private SodaSupportedBrowser BrowserType = null;
	private WebDriver Driver = null;
	private SodaBrowser Browser = null;
	private String testFile = "";
	private SodaEventDriver eventDriver = null;
	private SodaEvents events = null;
	private SodaReporter reporter = null;
	
	public SodaTest(String testFile, SodaBrowser browser) {
		boolean master_result = false;
		this.Browser = browser;
		this.testFile = testFile;
		String report_name = "";
		File tmp_file = new File(testFile);
		
		report_name = tmp_file.getName();
		report_name = report_name.replaceAll(".xml$", "");
		master_result = loadTestFile();
		
		this.reporter = new SodaReporter(report_name, "/Users/trichmond/reports");
		eventDriver = new SodaEventDriver(this.Browser, events, this.reporter);
		this.reporter.closeLog();
	}
	
	private boolean loadTestFile() {
		boolean result = false;
		SodaXML xml = null;
		
		try {
			System.out.printf("Loading Soda Test: '%s'.\n", testFile);
			xml = new SodaXML(testFile);
			this.events = xml.getEvents();
			System.out.printf("Finished.\n");
		} catch (Exception exp) {
			exp.printStackTrace();
			result = false;
		}
		
		return result;
	}
	
	private boolean isTestValid() {
		boolean result = false;
		
		return result;
	}
	
	public boolean runTest() {
		boolean result = false;
		
		return result;
	}
	
	private boolean processEvents() {
		boolean result = false;
		
		return result;
	}
	
	private void Print(String msg) {
		System.out.printf("(*)%s\n", msg);
	}
}
