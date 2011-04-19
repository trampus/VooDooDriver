package soda;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

public abstract class SodaBrowser implements SodaBrowserInterface {
	
	private WebDriver Driver = null;
	
	public SodaBrowser() {
		
	}
	
	public WebDriver getDriver() {
		return this.Driver;
	}
	
	public void newBrowser(SodaSupportedBrowser browserType) {
		
		try {
			switch (browserType) {
			case FIREFOX:
				this.Driver = new FirefoxDriver();
				break;
			case IE:
				this.Driver = new InternetExplorerDriver();
				break;
			case CHROME:
				this.Driver = new ChromeDriver();
			}
			
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}
	
	public void refresh() {
		this.Driver.navigate().refresh();
	}
	
	public void forward() {
		this.Driver.navigate().forward();
	}
	
	public void back() {
		this.Driver.navigate().back();
	}
	
	public void close() {
		this.Driver.close();
	}
	
	public void url(String url) {
		try {
			this.Driver.navigate().to(url);
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		
	}
}
