package soda;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.ie.*;

public class SodaFirefox extends SodaBrowser {

	private WebDriver driver = null;
	
	@Override
	public void newBrowser(SodaSupportedBrowser browserType) {
		try {
			driver = new FirefoxDriver();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}

}
