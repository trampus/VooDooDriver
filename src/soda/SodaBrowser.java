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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

public abstract class SodaBrowser implements SodaBrowserInterface {
	
	private WebDriver Driver = null;
	
	public SodaBrowser() {
		
	}
	
	public void setDriver(WebDriver driver) {
		this.Driver = driver;
	}
	
	public WebDriver getDriver() {
		return this.Driver;
	}
	
	public void newBrowser() {
		
	}
	
	public Object executeJS(String script, WebElement element) {
		Object result = null;
		JavascriptExecutor js =  (JavascriptExecutor)this.Driver;
		
		result = js.executeScript(script, element);
		
		return result;
	}
	
	public String fire_event(WebElement element, String eventType) {
		String result = "";
		String eventjs_src = "";
		JavascriptEventTypes type = null;
		eventType = eventType.toLowerCase();
		String tmp_type = eventType.replaceAll("on", "");
		
		try {
			UIEvents.valueOf(tmp_type.toUpperCase());
			type = JavascriptEventTypes.UIEvent;
		} catch (Exception exp) {
			type = null;
		}
		
		if (type == null) {
			try {
				HTMLEvents.valueOf(tmp_type.toUpperCase());
				type = JavascriptEventTypes.HTMLEvent;
			} catch (Exception exp) {
				type = null;
			}
		}
		
		if (type == null) {
			return null;
		}
		
		switch (type) {
		case HTMLEvent:
			
			break;
		case UIEvent:
			eventjs_src = this.generateUIEvent(UIEvents.valueOf(tmp_type.toUpperCase()));
			break;
		}
		
		System.out.printf("Event: %s\n", eventjs_src);
		result = this.executeJS(eventjs_src, element).toString();
		System.out.printf("Result: %s\n", result);
		
		return result;
	}
	
	public String generateUIEvent(UIEvents type) {
		String result = "var ele = arguments[0];\n"; 
		result += "var evObj = document.createEvent('MouseEvents');\n";
		result += "evObj.initMouseEvent( '" + type.toString().toLowerCase() + "', true, true, window, 1, 12, 345, 7, 220,"+ 
         "false, false, true, false, 0, null );\n";
		result += "ele.dispatchEvent(evObj);\n";
		result += "return 0;\n";
		
		return result;
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
	
	/*
	 * findElement -- method
	 * 	This method finds a given WebElement.  The retryTime is used to keep looking for the
	 * 	element until the timeout has happened.
	 * 
	 * Input:
	 * 	by: this is how we find the element.
	 * 	retryTime: This is how many seconds we would wait for the element to showup in the dom
	 *		before giving up on the search.
	 *
	 *	Output:
	 *	returns null on failure to find the element, else a WebElement is returned.
	 * 
	 */
	public WebElement findElement(By by, int retryTime) {
		WebElement result = null;
		long end = System.currentTimeMillis() + retryTime * 1000;
		
		while (System.currentTimeMillis() < end) {
			try {
				result = this.Driver.findElement(by);
			} catch (Exception exp) {
				result = null;
			}
			
			if (result != null) {
				break;
			}
		}
		
		return result;
	}
	
	public void url(String url) {
		try {
			this.Driver.navigate().to(url);
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		
	}
}
