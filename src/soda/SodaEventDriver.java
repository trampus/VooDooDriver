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

public class SodaEventDriver {

	private SodaEvents testEvents = null;
	private SodaBrowser Browser = null;
	private SodaHash sodaVars = null;
	
	public SodaEventDriver(SodaBrowser browser, SodaEvents events) {
		testEvents = events;
		this.Browser = browser;
		
		sodaVars = new SodaHash();
		
		processEvents(events);
	}
	
	private void processEvents(SodaEvents events) {
		int event_count = events.size() -1;
		boolean result = false;
		
		for (int i = 0; i <= event_count; i++) {
			result = handleSingleEvent(events.get(i));
		}
	}
	
	public SodaEvents getElements() {
		return testEvents;
	}
	
	private boolean handleSingleEvent(SodaHash event) {
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
		}
		
		return result;
	}
	
	private boolean waitEvent(SodaHash event) {
		boolean result = false;
		int default_timeout = 5;
		
		if (event.containsKey("timeout")) {
			Integer int_out = new Integer(event.get("timeout").toString());
			default_timeout = int_out.intValue();
			System.out.printf("WAIT: Setting timeout to: %d seconds.\n", default_timeout);
		} else {
			System.out.printf("WAIT: default timeout: %d seconds.\n", default_timeout);
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
