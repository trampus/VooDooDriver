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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SodaEventDriver {

	private SodaEvents testEvents = null;
	private SodaBrowser Browser = null;
	private SodaHash sodaVars = null;
	private SodaReporter report = null;
	private SodaHash globalVars = null;
	private SodaHash hijacks = null;
	
	public SodaEventDriver(SodaBrowser browser, SodaEvents events, SodaReporter reporter, SodaHash gvars,
			SodaHash hijacks) {
		testEvents = events;
		this.Browser = browser;
		this.report = reporter;
		this.globalVars = gvars;
		this.hijacks = hijacks;
		
		sodaVars = new SodaHash();
		
		if (gvars != null) {
			int len = gvars.keySet().size() -1;
			
			for (int i = 0; i <= len -1; i++) {
				String key = gvars.keySet().toArray()[i].toString();
				String value = gvars.get(key).toString();
				this.sodaVars.put(key, value);
			}
		}
		
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
			break;
		case BUTTON:
			result = buttonEvent(event, parent);
			break;
		case CSV:
			result = csvEvent(event);
			break;
		case LINK:
			result = linkEvent(event, parent);
			break;
		case CHECKBOX:
			result = checkboxEvent(event, parent);
			break;
		case VAR:
			result = varEvent(event);
			break;
		}
		
		return result;
	}
	
	/*
	 * 
	 */
	private String replaceString(String str) {
		String result = str;
		Pattern patt = null;
		Matcher matcher = null;
		
		patt = Pattern.compile("\\{@[\\w\\.]+\\}", Pattern.CASE_INSENSITIVE);
		matcher = patt.matcher(str);
		
		while (matcher.find()) {
			String m = matcher.group();
			String tmp = m;
			tmp = tmp.replace("{@", "");
			tmp = tmp.replace("}", "");
			
			if (this.hijacks.containsKey(tmp)) {
				String value = this.hijacks.get(tmp).toString();
				result = result.replace(m, value);
			} else if (this.sodaVars.containsKey(tmp)) {	
				String value = this.sodaVars.get(tmp).toString();
				result = result.replace(m, value);
			}
		}
		
		return result;
	}
	
	/*
	 * varEvent -- method
	 * 	This method sets a SODA var that can then be used in the follow script.
	 * 
	 * Input:
	 * 	event: A Soda event.
	 * 
	 * Output:
	 * 	returns true on success or false on fail.
	 * 
	 */
	private boolean varEvent(SodaHash event) {
		boolean result = false;
		String var_name = "";
		String var_value = "";
		
		try {
			if (event.containsKey("set")) {
				var_name = event.get("var").toString();
				var_value = event.get("set").toString();
				this.sodaVars.put(var_name, var_value);
				this.report.Log("Setting SODA variable: '"+ var_name + "' => '" + var_value + "'.");
			}
			
			if (event.containsKey("unset")) {
				var_name = event.get("var").toString();
				this.report.Log("Unsetting SODA variable: '" + var_name + "'.");
				if (!this.sodaVars.containsKey(var_name)) {
					this.report.Log("SODA variable: '" + var_name + "' not found, nothing to unset.");
				} else {
					this.sodaVars.remove(var_name);
				}
			}
		} catch (Exception exp) {
			this.report.ReportException(exp);
			result = false;
		}
		
		return result;
	}
	
	private boolean checkboxEvent(SodaHash event, WebElement parent) {
		boolean result = false;
		boolean click = false;
		WebElement element = null;
		
		try {
			element = this.findElement(event, parent);
			if (event.containsKey("click")) {
				click = this.clickToBool(event.get("click").toString());
				if (click) {
					element.click();
				}
			}
			
			if (event.containsKey("set")) {
				int val = Integer.valueOf(event.get("set").toString());
				if (val == 0) {
					element.clear();
				} else {
					element.setSelected();
				}
			}
			
		} catch (Exception exp) {
			this.report.ReportException(exp);
		}
		
		return result;
	}
	
	private boolean linkEvent(SodaHash event, WebElement parent) {
		boolean result = false;
		boolean click = true;
		WebElement element = null;
		
		try {
			element = this.findElement(event, parent);
			if (event.containsKey("alert")) {
				boolean alert = this.clickToBool(event.get("alert").toString());
				this.Browser.alertHack(alert);
			}
			
			if (event.containsKey("click")) {
				click = this.clickToBool(event.get("click").toString());
			}
			
			if (click) {
				element.click();
			}
			
			if (event.containsKey("jscriptevent")) {
				this.report.Log("Firing Javascript Event: "+ event.get("jscriptevent").toString());
				this.Browser.fire_event(element, event.get("jscriptevent").toString());
				Thread.sleep(1000);
				this.report.Log("Javascript event finished.");
			}
			
		} catch (Exception exp) {
			this.report.ReportException(exp);
			result = false;
		}
		
		
		return result;
	}
	
	private boolean csvEvent(SodaHash event) {
		boolean result = false;
		SodaCSV csv = null;
		SodaCSVData csv_data = null;
		String var_name = event.get("var").toString();
		//children
		
		csv = new SodaCSV(event.get("file").toString(), this.report);
		csv_data = csv.getData();
		
		for (int i = 0; i <= csv_data.size() -1; i++) {
			int keys_len = csv_data.get(i).keySet().size() -1;
			
			for (int key_index = 0; key_index <= keys_len; key_index++) {
				String key = csv_data.get(i).keySet().toArray()[key_index].toString();
				String sodavar_name = var_name + "." + key;
				String sodavar_value = csv_data.get(i).get(key).toString();
				
				if (this.hijacks.containsKey(sodavar_name)) {
					sodavar_value = this.hijacks.get(sodavar_name).toString();
					this.report.Log("Hijacking SodaVar: '" + sodavar_name+"' => '" +sodavar_value+"'.");
				}
				this.sodaVars.put(sodavar_name, sodavar_value);
			}
			
			if (event.containsKey("children")) {
				this.processEvents((SodaEvents)event.get("children"));
			}
		}
		
		return result;
	}
	
	private boolean waitEvent(SodaHash event) {
		boolean result = false;
		int default_timeout = 5;
		
		if (event.containsKey("timeout")) {
			Integer int_out = new Integer(event.get("timeout").toString());
			default_timeout = int_out.intValue();
			this.report.Log(String.format("WAIT: Setting timeout to: %d seconds.", default_timeout));
		} else {
			this.report.Log(String.format("WAIT: default timeout: %d seconds.", default_timeout));
		}
		
		default_timeout = default_timeout * 1000;
		
		try {
			this.report.Log(String.format("WAIT: waiting: '%d' seconds.\n", (default_timeout / 1000)));
			Thread.sleep(default_timeout);
			this.report.Log("WAIT: finished.\n");
			result = true;
		} catch (Exception exp) {
			this.report.ReportException(exp);
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
						this.report.Log(String.format("URL: '%s'",event.get(key).toString()));
						this.Browser.url(event.get(key).toString());
						break;
					}
				}
			}
		} catch (Exception exp) {
			this.report.ReportException(exp);
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
			case TEXT:
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
				this.report.ReportError(String.format("Error: findElement, unknown how: '%s'!\n", how));
				break;
			}
			
			if (parent == null) {
				element = this.Browser.findElement(by, 5);
			} else {
				element = parent.findElement(by);
			}
			
		} catch (Exception exp) {
			this.report.ReportException(exp);
			element = null;
		}
		
		return element;
	}
	
	/*
	 * clickToBool -- method
	 * 	This method converts a string into a boolean type.
	 * 
	 * Input:
	 *  clickstr: a string containing "true" or "false".  Case doesn't matter.
	 *  
	 *  Output:
	 *   returns a boolean type.
	 */
	private boolean clickToBool(String clickstr) {	
		return Boolean.valueOf(clickstr).booleanValue();
	}
	
	private boolean buttonEvent(SodaHash event, WebElement parent) {
		boolean result = false;
		WebElement element = null;
		boolean click = true;
		
		try {
			
			element = this.findElement(event, parent);
			
			if (event.containsKey("click")) {
				click = this.clickToBool(event.get("click").toString());
			}
			
			if (click) {
				element.click();
			}
			
			result = true;
		} catch (Exception exp) {
			this.report.ReportException(exp);
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
				String value = this.replaceString(event.get("set").toString());
				this.report.Log(String.format("TEXTFIELD: Setting Value to: '%s'.", value));
				element.sendKeys(value);
				this.report.Log("TEXTFIELD: Finished.");
			}
			
			result = true;
		} catch (Exception exp) {
			this.report.ReportException(exp);
			result = false;
		}
		
		return result;
	}
	
	private boolean putsEvent(SodaHash event) {
		boolean result = false;
		String msg = "";
		
		msg = this.replaceString(event.get("text").toString());
		
		this.report.Log(msg);
		result = true;
		return result;
	}
	
}
