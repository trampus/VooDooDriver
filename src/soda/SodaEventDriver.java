package soda;

public class SodaEventDriver {

	private SodaEvents testEvents = null;
	
	public SodaEventDriver(SodaBrowser browser, SodaEvents events) {
		testEvents = events;
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
		
		
		return result;
	}
			
	
}
