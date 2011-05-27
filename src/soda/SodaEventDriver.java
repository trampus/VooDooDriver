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
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SodaEventDriver implements Runnable {

	private SodaEvents testEvents = null;
	private SodaBrowser Browser = null;
	private SodaHash sodaVars = null;
	private SodaReporter report = null;
	private SodaHash globalVars = null;
	private SodaHash hijacks = null;
	private Date threadTime = null;
	private volatile Thread runner;
	private volatile Boolean threadStop = false;
	
	public SodaEventDriver(SodaBrowser browser, SodaEvents events, SodaReporter reporter, SodaHash gvars,
			SodaHash hijacks) {
		testEvents = events;
		this.Browser = browser;
		this.report = reporter;
		this.globalVars = gvars;
		this.hijacks = hijacks;
		
		sodaVars = new SodaHash();
		
		if (gvars != null) {
			int len = gvars.keySet().size();
			
			for (int i = 0; i <= len -1; i++) {
				String key = gvars.keySet().toArray()[i].toString();
				String value = gvars.get(key).toString();
				System.out.printf("--)'%s' => '%s'\n", key, value);
				this.sodaVars.put(key, value);
			}
		}
		
		this.threadTime = new Date();
		this.runner = new Thread(this, "SodaEventDriver-Thread");
		runner.start();
	}
	
	public boolean isAlive() {
		return this.runner.isAlive();
	}
	
	public Thread getThread() {
		return this.runner;
	}
	
	public void stop() {
		synchronized(this.threadStop) {
			this.threadStop = true;
			this.runner.interrupt();
		}
	}
	
	public boolean isStopped() {
		boolean result = false;
		
		synchronized(this.threadStop) {
			result = this.threadStop;
		}
		
		return result;
	}
	
	public void run() {
		System.out.printf("Thread Running...\n");
		this.threadTime = new Date();
		int i = 0;
		int event_count = this.testEvents.size() -1;
		
		while ( (!this.runner.isInterrupted()) && (i <= event_count)) {
			handleSingleEvent(this.testEvents.get(i), null);
			i += 1;
		}
	}
	
	private void resetThreadTime() {
		synchronized (this.threadTime) {
			this.threadTime = new Date();
		}
	}
	
	public Date getThreadTime() {
		Date tmp = null;
		
		synchronized (this.threadTime) {
			tmp = this.threadTime;
		}
		
		return tmp;
	}
	
	private void processEvents(SodaEvents events, WebElement parent) {
		int event_count = events.size() -1;
		boolean result = false;
		
		for (int i = 0; i <= event_count; i++) {
			if (isStopped()) {
				break;
			}
			result = handleSingleEvent(events.get(i), parent);
		}
	}
	
	public SodaEvents getElements() {
		return testEvents;
	}
	
	private boolean handleSingleEvent(SodaHash event, WebElement parent) {
		boolean result = false;
		
		if (isStopped()) {
			return result;
		}
		
		this.resetThreadTime();
		
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
		case SCRIPT:
			result = scriptEvent(event);
			break;
		case DIV:
			result = divEvent(event, parent);
			break;
		default:
			System.out.printf("(*)Unknown command: '%s'!\n", event.get("type").toString());
			System.exit(1);
		}
		
		this.resetThreadTime();
		
		return result;
	}
	
	private String replaceString(String str) {
		String result = str;
		Pattern patt = null;
		Matcher matcher = null;
		
		this.resetThreadTime();
		
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
				break;
			} else if (this.sodaVars.containsKey(tmp)) {	
				String value = this.sodaVars.get(tmp).toString();
				result = result.replace(m, value);
				break;
			}
		}
		
		this.resetThreadTime();
		
		return result;
	}
	
	private boolean divEvent(SodaHash event, WebElement parent) {
		boolean result = false;
		boolean required = true;
		WebElement element = null;
		
		if (event.containsKey("required")) {
			required = this.clickToBool(event.get("required").toString());
		}
		
		try {
			element = this.findElement(event, parent, required);
			
			if (event.containsKey("assert")) {
				String src = element.getText();
				String value = event.get("assert").toString();
				value = this.replaceString(value);
				this.report.Assert(value, src);
			}
			
			if (event.containsKey("assertnot")) {
				String src = element.getText();
				String value = event.get("assertnot").toString();
				value = this.replaceString(value);
				this.report.AssertNot(value, src);
			}
			
			if (event.containsKey("children")) {
				this.processEvents((SodaEvents)event.get("children"), element);
			}
			
			result = true;
		} catch (Exception exp) {
			this.report.ReportException(exp);
			result = false;
		}
		
		return result;
	}
	
	private boolean scriptEvent(SodaHash event) {
		boolean result = false;
		SodaXML xml = null;
		String testfile = "";	
		File fd = null;
		SodaEvents newEvents = null;
		
		testfile = event.get("file").toString();
		testfile = this.replaceString(testfile);
		
		try {
			fd = new File(testfile);
			if (!fd.exists()) {
				String msg = String.format("Failed to find file: '%s'!", testfile);
				this.report.ReportError(msg);
				return false;
			}
			fd = null;
			
			xml = new SodaXML(testfile);
			newEvents = xml.getEvents();
			this.processEvents(newEvents, null);
			
		} catch (Exception exp) {
			exp.printStackTrace();
			result = false;
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
		boolean required = true;
		WebElement element = null;
		
		this.resetThreadTime();
		
		if (event.containsKey("required")) {
			required = this.clickToBool(event.get("required").toString());
		}
		
		try {
			element = this.findElement(event, parent, required);
			if (element == null) {
				result = false;
				return result;
			}
			
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
		
		this.resetThreadTime();
		
		return result;
	}
	
	private boolean linkEvent(SodaHash event, WebElement parent) {
		boolean result = false;
		boolean click = true;
		boolean required = true;
		WebElement element = null;
		
		this.resetThreadTime();
		
		if (event.containsKey("required")) {
			required = this.clickToBool(event.get("required").toString());
		}
		
		try {
			this.report.Log("Link Event Started.");
			element = this.findElement(event, parent, required);
			if (element == null) {
				result = false;
				return result;
			}

			if (event.containsKey("alert")) {
				boolean alert = this.clickToBool(event.get("alert").toString());
				this.report.Log(String.format("Setting Alert Hack to: '%s'", alert));
				this.Browser.alertHack(alert);
			}
			
			if (event.containsKey("click")) {
				click = this.clickToBool(event.get("click").toString());
			}
			
			if (click) {
				String how = event.get("how").toString();
				String value = event.get(how).toString();
				this.report.Log(String.format("Clicking Link: '%s' => '%s'", how, value));
				element.click();
			}
			
			if (event.containsKey("jscriptevent")) {
				this.report.Log("Firing Javascript Event: "+ event.get("jscriptevent").toString());
				this.Browser.fire_event(element, event.get("jscriptevent").toString());
				Thread.sleep(1000);
				this.report.Log("Javascript event finished.");
			}
			
			this.report.Log("Link Event Finished.");
		} catch (Exception exp) {
			this.report.ReportException(exp);
			result = false;
		}
		
		this.resetThreadTime();
		
		return result;
	}
	
	private boolean csvEvent(SodaHash event) {
		boolean result = false;
		SodaCSV csv = null;
		SodaCSVData csv_data = null;
		String var_name = event.get("var").toString();
		String csv_filename = "";

		this.resetThreadTime();
		
		csv_filename = event.get("file").toString();
		csv_filename = replaceString(csv_filename);
		
		csv = new SodaCSV(csv_filename, this.report);
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
				this.processEvents((SodaEvents)event.get("children"), null);
			}
		}
		
		this.resetThreadTime();
		
		return result;
	}
	
	private boolean waitEvent(SodaHash event) {
		boolean result = false;
		int default_timeout = 5;
		
		this.resetThreadTime();
		
		if (event.containsKey("timeout")) {
			Integer int_out = new Integer(event.get("timeout").toString());
			default_timeout = int_out.intValue();
			this.report.Log(String.format("WAIT: Setting timeout to: %d seconds.", default_timeout));
		} else {
			this.report.Log(String.format("WAIT: default timeout: %d seconds.", default_timeout));
		}
		
		default_timeout = default_timeout * 1000;
		
		try {
			this.report.Log(String.format("WAIT: waiting: '%d' seconds.", (default_timeout / 1000)));
			int wait_seconds = default_timeout / 1000;
			
			for (int i = 0; i <= wait_seconds -1; i++) {
				if (isStopped()) {
					break;
				}
				Thread.sleep(1000);
			}
			
			this.report.Log("WAIT: finished.");
			result = true;
		} catch (InterruptedException exp) {
			result = false;
		}
		
		this.resetThreadTime();
		return result;
	}

	private boolean browserEvent(SodaHash event) {
		boolean result = false;
		SodaBrowserActions browser_action = null;
		
		this.resetThreadTime();
		
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
					
					String value = "";
					switch (method) {
					case BROWSER_url:
						String url = event.get(key).toString();
						url = this.replaceString(url);
						this.report.Log(String.format("URL: '%s'", url));
						this.Browser.url(url);
						break;
					case BROWSER_assert:
						value = event.get("assert").toString();
						value = this.replaceString(value);
						result = this.Browser.Assert(value);
						
						if (!result) {
							String msg = String.format("Browser Assert Failed to find this in page: '%s'", value);
							this.report.ReportError(msg);
						}
						break;
					case BROWSER_assertnot:
						value = event.get("assertnot").toString();
						value = this.replaceString(value);
						result = this.Browser.AssertNot(value);

						if (!result) {
							String msg = String.format("Browser AssertNot Found text in page: '%s'", value);
							this.report.ReportError(msg);
						}
						break;
					default:
						System.out.printf("(!)ERROR: Unknown browser method: '%s'!\n", key_id);
						System.exit(3);
					}
				}
			}
		} catch (Exception exp) {
			this.report.ReportException(exp);
		}

		this.resetThreadTime();
		
		return result;
	}
	
	private WebElement findElement(SodaHash event, WebElement parent, boolean required) {
		WebElement element = null;
		By by = null;
		boolean href = false;
		boolean value = false;
		String how = "";
		
		
		this.resetThreadTime();
		
		try {
			how = event.get("how").toString();
			
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
			case HREF:
				by = By.tagName("a");
				href = true;
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
			case VALUE:
				value = true;
				break;
			default:
				this.report.ReportError(String.format("Error: findElement, unknown how: '%s'!\n", how));
				System.exit(4);
				break;
			}
			
			if (href) {
				element = this.findElementByHref(event.get("href").toString(), parent);
			} else if (value) {
				element = this.slowFindElement(event.get("do").toString(), event.get(how).toString());
			}else {
				if (parent == null) {	
					element = this.Browser.findElement(by, 5);
				} else {
					element = parent.findElement(by);
				}
			}
		} catch (Exception exp) {
			this.report.ReportException(exp);
			element = null;
		}
		
		this.resetThreadTime();
		
		if (element == null) {
			String val = event.get(how).toString();
			if (required) {
				String msg = String.format("Failed to find element: '%s' => '%s'", how, val);
				this.report.ReportError(msg);
			} else {
				String msg = String.format("Failed to find element, but required => 'false' : '%s' => '%s'", how, val);
				this.report.Log(msg);
			}
		}
		
		return element;
	}
	
	private WebElement slowFindElement(String ele_type, String how) {
		WebElement element = null;
		String msg = "";
		
		msg = String.format("Looking for elements by value is very very slow!  You should never do this!");
		this.report.Log(msg);
		msg = String.format("Look for element: '%s' => '%s'.", ele_type, how);
		this.report.Log(msg);
		System.exit(-1);
		return element;
	}
	
	private WebElement findElementByHref(String href, WebElement parent) {
		WebElement element = null;
		List<WebElement> list = null;
		
		if (parent != null) {
			list = parent.findElements(By.tagName("a"));
		} else {
			list = this.Browser.getDriver().findElements(By.tagName("a"));
		}
		
		int len = list.size() -1;
		for (int i = 0; i <= len; i++) {
			String value = list.get(i).getAttribute("href");
			if (href.compareTo(value) == 0) {
				element = list.get(i);
				break;
			}
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
		boolean click = true;
		boolean required = true;
		WebElement element = null;
		
		
		this.resetThreadTime();
		
		if (event.containsKey("required")) {
			required = this.clickToBool(event.get("required").toString());
		}
		
		try {
			
			element = this.findElement(event, parent, required);
			if (element == null) {
				result = false;
				return result;
			}
			
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
		
		this.resetThreadTime();
		
		return result;
	}
	
	private boolean textfieldEvent(SodaHash event, WebElement parent) {
		boolean result = false;
		boolean required = true;
		WebElement element = null;
		
		this.resetThreadTime();
		
		if (event.containsKey("required")) {
			required = this.clickToBool(event.get("required").toString());
		}
		
		try {
			element = this.findElement(event, parent, required);
			if (element == null) {
				result = false;
				return result;
			}
			
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
		
		this.resetThreadTime();
		
		return result;
	}
	
	private boolean putsEvent(SodaHash event) {
		boolean result = false;
		String msg = "";
		
		this.resetThreadTime();
		
		msg = this.replaceString(event.get("text").toString());
		
		this.report.Log(msg);
		result = true;
		
		this.resetThreadTime();
		
		return result;
	}
	
}
