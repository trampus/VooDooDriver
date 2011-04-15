package soda;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class SodaChrome extends SodaBrowser {
	
	private WebDriver browser = null; 
	
	@Override
	public void newBrowser(SodaSupportedBrowser browserType) {
		try {
			browser = new ChromeDriver();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}

}
