package voodoodriver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class SodaUtils {
	
	public static String MD5(String data) {
		String res = "";
		int len = 0;
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(data.getBytes());
			byte[] bytes = md.digest();
			
			len = bytes.length -1;
			for (int i = 0; i <= len; i++) {
				res += Integer.toString((bytes[i] & 0xff ) + 0x100, 16).substring( 1 );
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		
		return res;
	}

	public static void PrintSuiteReportToConsole(String suitename, ArrayList<SodaTestResults> list) {
		String linemarker = StringUtils.repeat("#", 80);
		
		System.out.printf("\n%s\n", linemarker);
		System.out.printf("# Suite Name: %s\n", suitename);
		System.out.printf("%s\n", linemarker);
		
	}
	
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

	public static String GetRunTime(Date starttime, Date stoptime) {
		String result = "";
		long diff = 0;
		long mills = 0;
		long x = 0;
		long seconds = 0;
		long minutes = 0;
		long hours = 0;
		
		diff = stoptime.getTime() - starttime.getTime();
		mills = diff % 1000;
		x = diff / 1000;
		seconds = x % 60;
		x /= 60;
		minutes = x % 60;
		x /= 60;
		hours = x % 24;
		
		result = String.format("%d:%d:%d.%d", hours, minutes, seconds, mills);
		
		return result;
	}

}
