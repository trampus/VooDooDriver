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
			System.out.printf("TIMEOUT: Setting timeout to: %d seconds.\n", default_timeout);
		}
		
		default_timeout = default_timeout * 1000;
		
		try {
			System.out.printf("TIMEOUT: waiting: '%d' seconds.\n", (default_timeout / 1000));
			Thread.sleep(default_timeout);
			System.out.printf("TIMEOUT: finished.\n");
			result = true;
		} catch (Exception exp) {
		
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
