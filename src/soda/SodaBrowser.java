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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
	
	public WebElement findElement(By by) {
		return this.Driver.findElement(by);
	}
	
	public void url(String url) {
		try {
			this.Driver.navigate().to(url);
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		
	}
}
