package soda;

import org.openqa.selenium.WebDriver;

public interface SodaBrowserInterface {

	public void newBrowser(SodaSupportedBrowser browserType);
	
	public void refresh();
	
	public void back();
	
	public void forward();
	
	public void close();
	
	public void url(String url);
	
	public WebDriver getDriver();
	
}
