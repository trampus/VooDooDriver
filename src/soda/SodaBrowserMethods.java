package soda;

public enum SodaBrowserMethods {
	BROWSER_cssprop,
     BROWSER_cssvalue,
     BROWSER_assertnot,
     BROWSER_url,
     BROWSER_send_keys,
     BROWSER_assertPage,
     BROWSER_exist,
     BROWSER_jscriptevent,
     BROWSER_assert,;

	static public boolean isMember(String aName) {
		boolean result = false;
		SodaBrowserMethods[] values = SodaBrowserMethods.values();
		
		for (SodaBrowserMethods amethod : values) {
			if (amethod.name().equals(aName)) {
				result = true;
				break;
			}
		}

		return result;
	}
}