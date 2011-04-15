package soda;

public class SodaEventDriver {

	private SodaEvents testEvents = null;
	private SodaBrowser Browser = null;
	
	public SodaEventDriver(SodaBrowser browser, SodaEvents events) {
		testEvents = events;
		this.Browser = browser;
	}
	
	public void processEvents(SodaEvents events) {
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
		}
		
		return result;
	}

	private boolean browserEvent(SodaHash event) {
		boolean result = false;
		SodaBrowserActions browser_action = null;
		
		if (event.containsKey("action")) {
			browser_action = SodaBrowserActions.valueOf(event.get("action").toString().toUpperCase());
		}
		
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
		
		return result;
	}
	
}
