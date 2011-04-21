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
import org.openqa.selenium.WebElement;

public class SodaEventDriver {

	private SodaEvents testEvents = null;
	private SodaBrowser Browser = null;
	private SodaHash sodaVars = null;
	private SodaReporter report = null;
	
	public SodaEventDriver(SodaBrowser browser, SodaEvents events, SodaReporter reporter) {
		testEvents = events;
		this.Browser = browser;
		this.report = reporter;
		
		sodaVars = new SodaHash();
		
		processEvents(events);
	}
	
	private void processEvents(SodaEvents events) {
		int event_count = events.size() -1;
		boolean result = false;
		
		for (int i = 0; i <= event_count; i++) {
			result = handleSingleEvent(events.get(i), null);
		}
	}
	
	public SodaEvents getElements() {
		return testEvents;
	}
	
	private boolean handleSingleEvent(SodaHash event, WebElement parent) {
		boolean result = false;
		
		switch ((SodaElements)event.get("type")) {
		case BROWSER: 
			result = browserEvent(event);
			break;
		case PUTS:
			result = putsEvent(event);
			break;
		case WAIT:
			result = waitEvent(event);
			break;
		case TEXTFIELD:
			result = textfieldEvent(event, parent);
			
		}
		
		return result;
	}
	
	private boolean waitEvent(SodaHash event) {
		boolean result = false;
		int default_timeout = 5;
		
		if (event.containsKey("timeout")) {
			Integer int_out = new Integer(event.get("timeout").toString());
			default_timeout = int_out.intValue();
			String msg = String.format("WAIT: Setting timeout to: %d seconds.", default_timeout);
			this.report.Log(msg);
		} else {
			String msg = String.format("WAIT: default timeout: %d seconds.", default_timeout);
			this.report.Log(msg);
		}
		
		default_timeout = default_timeout * 1000;
		
		try {
			System.out.printf("WAIT: waiting: '%d' seconds.\n", (default_timeout / 1000));
			Thread.sleep(default_timeout);
			System.out.printf("WAIT: finished.\n");
			result = true;
		} catch (Exception exp) {
			exp.printStackTrace();
			result = false;
		}
		
		return result;
	}

	private boolean browserEvent(SodaHash event) {
		boolean result = false;
		SodaBrowserActions browser_action = null;
		
		try {
			if (event.containsKey("action")) {
				browser_action = SodaBrowserActions.valueOf(event.get("action").toString().toUpperCase());
				switch (browser_action) {
				case REFRESH:
					this.Browser.refresh();
					break;
				case CLOSE:
					this.Browser.close();
					break;	
				case BACK:
					this.Browser.back();
					break;
				case FORWARD:
					this.Browser.forward();
					break;
				}
			} else {
				int event_count = event.keySet().size() -1;
				for (int i = 0; i <= event_count; i++) {
					String key = event.keySet().toArray()[i].toString();
					String key_id = "BROWSER_" + key;
					SodaBrowserMethods method = null;
					
					if (SodaBrowserMethods.isMember(key_id)) {
						method = SodaBrowserMethods.valueOf(key_id); 
					} else {
						continue;
					}
					
					switch (method) {
					case BROWSER_url:
						System.out.printf("URL: %s\n",event.get(key).toString());
						this.Browser.url(event.get(key).toString());
						break;
					}
				}
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}

		return result;
	}
	
	private WebElement findElement(SodaHash event, WebElement parent) {
		WebElement element = null;
		By by = null;
		
		try {
			String how = event.get("how").toString();
			
			switch (SodaElementsHow.valueOf(how.toUpperCase())) {
			case ID:
				by = By.id(event.get(how).toString());
				break;
			case CLASS:
				by = By.className(event.get(how).toString());
				break;
			case CSS:
				by = By.cssSelector(event.get(how).toString());
				break;
			case LINK:
				by = By.linkText(event.get(how).toString());
				break;
			case NAME:
				by = By.name(event.get(how).toString());
				break;
			case PARLINK:
				by = By.partialLinkText(event.get(how).toString());
				break;
			case TAGNAME:
				by = By.tagName(event.get(how).toString());
				break;
			case XPATH:
				by = By.xpath(event.get(how).toString());
				break;
			default:
				System.err.printf("Error: findElement, unknown how: '%s'!\n", how);
				break;
			}
			
			if (parent == null) {
				element = this.Browser.findElement(by);
			} else {
				element = parent.findElement(by);
			}
			
		} catch (Exception exp) {
			exp.printStackTrace();
			element = null;
		}
		
		return element;
	}
	
	private boolean buttonEvent(SodaHash event, WebElement parent) {
		boolean result = false;
		WebElement element = null;
		
		try {
			element = this.findElement(event, parent);
			element.click();
			result = true;
		} catch (Exception exp) {
			exp.printStackTrace();
			result = false;
		}
		
		return result;
	}
	
	private boolean textfieldEvent(SodaHash event, WebElement parent) {
		boolean result = false;
		WebElement element = null;
		
		try {
			element = this.findElement(event, parent);
			if (event.containsKey("set")) {
				System.out.printf("TEXTFIELD: Setting Value to: '%s'.\n", event.get("set").toString());
				element.sendKeys(event.get("set").toString());
				System.out.printf("TEXTFIELD: Finished.\n");
			}
			
			result = true;
		} catch (Exception exp) {
			exp.printStackTrace();
			result = false;
		}
		
		return result;
	}
	
	private boolean putsEvent(SodaHash event) {
		boolean result = false;
		
		System.out.printf("SodaPuts: '%s'\n", event.get("text").toString());
		result = true;
		
		return result;
	}

	private SodaHash replaceString(SodaHash event) {
		return event;
	}
	
}
