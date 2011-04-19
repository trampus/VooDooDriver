import soda.SodaBrowser;
import soda.SodaFirefox;
import soda.SodaSupportedBrowser;
import soda.SodaTest;

public class SodaSuite {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String sodaTest = "/Users/trichmond/Documents/workspace/Soda-Project/src/test1.xml";
		SodaTest testobj = null;
		SodaBrowser browser = null;
		
		System.out.printf("Starting SodaSuite...\n");
		
		try {
			
			browser = new SodaFirefox();
			testobj = new SodaTest(sodaTest, browser);
		} catch(Exception exp) {
			exp.printStackTrace();
		}
		
		System.out.printf("SodaSuite Finished.\n");
	}

}
