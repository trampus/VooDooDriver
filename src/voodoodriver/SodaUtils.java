package voodoodriver;

import java.io.BufferedReader;
import java.io.FileReader;

public class SodaUtils {

	public static String FileToStr(String filename) {
		String result = "";
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line = "";
			while ((line = reader.readLine()) != null) {
				result = result.concat(line + "\n");
			}
			reader.close();
		} catch (Exception exp) {
			exp.printStackTrace();
			result = null;
		}
		
		return result;
	}
	
}
