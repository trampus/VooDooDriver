import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import soda.SodaElementsList;
import soda.SodaEvents;
import soda.SodaReporter;
import soda.SodaSupportedBrowser;
import soda.SodaTest;
import soda.SodaTypes;
import soda.SodaXML;

public class SodaSuite {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SodaXML sodaXml = null;
		SodaTypes sodaTypes = null;
		SodaElementsList types = null;
		String sodaTest = "/Users/trichmond/Documents/workspace/Soda-Project/src/test1.xml";
		SodaEvents events = null;
		SodaReporter rep = null;
		SodaTest testobj = null;
		
		System.out.printf("Starting SodaSuite...\n");
		
		try {
			/*
			File tmp = new File(sodaTest);
			String basename = tmp.getName();
			basename = basename.substring(0, basename.length() -4);
			rep = new SodaReporter(basename, null);
			sodaXml = new SodaXML(sodaTest);
			events = sodaXml.process();
			sodaTypes = new SodaTypes();
			types = sodaTypes.getTypes();
			*/
			
			testobj = new SodaTest(sodaTest, SodaSupportedBrowser.FIREFOX);
			
		} catch(Exception exp) {
			exp.printStackTrace();
		}
		
		sodaXml = null;
		System.out.printf("SodaSuite Finished.\n");
	}

}
