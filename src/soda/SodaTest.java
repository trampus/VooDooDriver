package soda;

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
	
	public SodaTest(String testFile, SodaBrowser browser) {
		boolean master_result = false;
		this.Browser = browser;
		this.testFile = testFile;
		
		master_result = loadTestFile();
		
		browser.newBrowser(SodaSupportedBrowser.FIREFOX);
		
		eventDriver = new SodaEventDriver(this.Browser, events);
		
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
	
	private void newDriver() {
		
		switch(BrowserType) {
			case FIREFOX:
				Browser = new SodaFirefox();
			break;
			
			case CHROME:
				System.out.printf("(*)Creating New Chrome browser...\n");
				Driver = new ChromeDriver();
				System.out.printf("(*)Finished.\n");
			break;
			
			case IE:
				System.out.printf("(*)Creating New IE browser...\n");
				Driver = new InternetExplorerDriver();
				System.out.printf("(*)Finished.\n");
			break;
			
			default:
				Driver = new FirefoxDriver();
			break;
		}
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
