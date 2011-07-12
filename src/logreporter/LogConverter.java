package logreporter;
import java.io.*;
import java.util.regex.Pattern;
/*
# Copyright (c) 2011, SugarCRM, Inc.
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#    * Redistributions of source code must retain the above copyright
#      notice, this list of conditions and the following disclaimer.
#    * Redistributions in binary form must reproduce the above copyright
#      notice, this list of conditions and the following disclaimer in the
#      documentation and/or other materials provided with the distribution.
#    * Neither the name of SugarCRM, Inc. nor the
#      names of its contributors may be used to endorse or promote products
#      derived from this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
# ARE DISCLAIMED. IN NO EVENT SHALL SugarCRM, Inc. BE LIABLE FOR ANY
# DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
# (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
# LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
# ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
# (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
# SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/**
 * converts a single .log file into a readable HTML file. The output file will be in the same directory as the .log source
 * 
 * @param inputFile - absolute path and name of file. If no path is given, will assume is in same directory. 
 *
 */
public class LogConverter{
	
	private String fileName;
	
	/**
	 * file reading stuff
	 */
	private FileReader input;
	private BufferedReader br;
	private String strLine;
	
	/**
	 * file output stuff
	 */
	private FileOutputStream output;
	private PrintStream repFile;
	
	/**
	 * other stuff
	 */
	private int backTraceID;
	private int eventDumpID;
	
	/////////////////
	//constructors
	/////////////////
	/**
	 * default constructor for class LogConverter
	 */
	public LogConverter(){
		fileName = "";
		strLine = "";
		backTraceID = 0;
		eventDumpID = 0;
	}

	/**
	 * Constructor for class LogConverter.
	 * @param inputFile - name of the file to be converted. Should be in correct format, with .log extention
	 */
	public LogConverter(String inputFile){
		strLine = new String();
		backTraceID = 0;
		eventDumpID = 0 ;
		
		/**
		 * set up file reading 
		 */
		try{
			/*sets up file reader to read input one character at a time*/
			input = new FileReader(inputFile);
			/*sets up buffered reader to read input one line at a time*/
			br = new BufferedReader(input);
		}catch (FileNotFoundException e){
			System.err.println("file not found: "+inputFile);
		}catch (Exception e){
			System.err.println("error reading file" + inputFile);
		}
		
			
		/**
		 * add the correct extension and "Report-" prefix to the output html
		 */
		fileName = inputFile.substring(inputFile.lastIndexOf("/")+1, inputFile.length()-4);
		String filePath = inputFile.substring(0, inputFile.lastIndexOf('/')+1);
		fileName = "Report-"+fileName+".html";
		System.out.println(fileName);
		
		/**
		 * sets up output file
		 */
		try{
			output = new FileOutputStream(filePath+fileName);
			repFile = new PrintStream(output);
		}catch (Exception e){
			System.err.println("Error writing to file "+fileName);
			//e.printStackTrace();
		}
	}
	
	////////////////////////////////
	//main report-creating methods
	////////////////////////////////
	/**
	 * Generates a new html table row from a raw .log file line
	 * 
	 * @param line - A line from the raw .log file
	 * @return A string of html that is a table row
	 */
	private String generateTableRow(String line){
		String htmlRow = "";
		/**
		 * index 0 = date
		 * index 1 = message type
		 * index 2 = message
		 */
		String[] rowData = new String[3]; 
		String trStyle = "tr_normal";
		
		char msgType = line.charAt(line.indexOf("(")+1); 
		String message = line.substring(line.indexOf(")")+1, line.length());
		
		/**
		 * case switches for different message types
		 */
		//assertion passed
		if (message.contains("Assert Passed")){
			rowData = formatAssertionPassed(line, message);
			trStyle = "tr_assert_passed";
		}
		//assertion failed
		else if (message.contains("Assert Failed")){
			rowData = formatAssertionFailed(line, message);
			trStyle = "tr_error";
		}
		//test modules
		else if (message.startsWith("Test") || message.startsWith("Lib")|| message.startsWith("Module")){
			rowData = formatModuleLine(line, message);
			trStyle = "tr_module";
		}
		//clicking elements
		else if (message.startsWith("Clicking Link")){
			rowData = formatClickingElement(line, message);		
		}
		//setting element values
		else if (message.startsWith("Setting Value") || message.startsWith("Setting Select") || message.startsWith("Setting SODA")){
			rowData = formatClickingElement(line, message);
		}
		//looking for element values
		else if (message.startsWith("Looking for element:")){
			rowData = formatClickingElement(line, message);
		}
		//SODA test report line
		else if (message.startsWith("Soda Test Report")){
			rowData = formatTestResults(line,message);
		}
		//Exception backtrace line
		else if (message.startsWith("--Exception Backtrace")){
			rowData = formatExceptionBT(line,message);
		}
		//Major exception
		else if (message.startsWith("Major Exception")){
			rowData = formatMajorException(line, message);
		}
		//HTML saved
		else if (message.startsWith("(?i)html saved")){
			rowData = this.formatHTMLSavedResults(line, message, msgType);
		}
		//event dump
		else if (msgType == 'E'){
			rowData = formatEventDump(line, message);
		}
		//JS and CSS error
		else if (message.contains("(?i)css error") || message.contains("(?i)javascript error")){
			rowData = formatJSError(line, message);
		}
		//replacing string
		else if (message.contains("(?i)replacing string")){
			rowData = formatReplaceString(line, message);
		}
		//trying to find element
		else if (message.startsWith("Tring to find")){
			rowData = formatFindingElement(line, message);
		}
		//screenshot
		else if (message.startsWith("(?i)element")){
			rowData = formatClickingElement(line, message);
			if (message.contains("(?i)screenshot taken")){
				rowData = formatScreenShot(line, message);
			}	
		}
		else{
			rowData = formatDefaultLine(line, message);
		}
		
		/**
		 * if there are no data for some reason, return empty html row
		 */
		if (rowData[0].isEmpty() && rowData[2].isEmpty()){
	         return "";
		}
		
		/**
		 * special message types
		 */
		if (msgType == '!'){
			rowData[1] = "Failure";
			trStyle = "tr_error";
		}
		else if (msgType == 'W'){
			rowData[1] = "Warning";
			trStyle = "tr_warning";
		}
		else if (msgType == 'M'){
			rowData[1] = "Un/Load";
			trStyle = "tr_module";
		}
		
		htmlRow = "<tr class=\""+ trStyle +"\" "+
		         "onMouseOver=\"this.className='highlight'\" " +
		         "onMouseOut=\"this.className='"+ trStyle +"'\">\n";
		htmlRow += "\t<td>" + rowData[0] + "</td>\n";
		htmlRow += "\t<td class=\"td_msgtype\">" + rowData[1] + "</td>\n";
		htmlRow += "\t<td>" + rowData[2] + "</td>\n</tr>\n";
		
		return htmlRow;
	}

	/**
	 * generates a html report file 
	 */
	public void generateReport(){
		generateHtmlHeader();
		
		try{
			/**
			 * read first line
			 */
			strLine = br.readLine();		
			while (strLine != null){
				repFile.println(generateTableRow(strLine));
				strLine = br.readLine();
			}
			
		}catch (Exception e){
			System.err.println("error reading input file");
			e.printStackTrace();
		}
		
		repFile.print("\n</table>\n</body>\n</html>\n");
		repFile.close();
	}
	////////////////////////////////////////////
	//line cases
	////////////////////////////////////////////
	/**
	 * reads a raw .log file and formats it into a generic, default report line
	 * 
	 * @param line - A line from the raw .log file
	 * @return array with the expected format
	 */
	private String[] formatDefaultLine(String line, String message){
		String[] rowData = new String[3];
		
		rowData[0] = generateDateTime (line);
		rowData[1] = "Log";
		rowData[2] = safeHTMLString(message);
		
		return rowData;
	}
	
	/**
	 * takes the results line from the raw log file and generates a nice html row
	 * 
	 * @param line - the "Soda Test Report:" line from the raw log file
	 * @return array in expected format
	 */
	private String[] formatTestResults (String line, String message){
		String[] rowData = new String[3];
		String tableHTML = "\n<table>\n";
		String[] resData = message.split("--");
		
		for (int i = 1; i < resData.length; i++){
			tableHTML += "<tr class=\"tr_normal\""+
		            " \"onMouseOver=\"this.className='highlight_report'\" "+
		            "onMouseOut=\"this.className='tr_normal'\"> \n";
			String[] lineData = resData[i].split(":");
			//data type
			if (lineData[0].equals("failedasserts")){
				tableHTML += "\t<td><b>failed asserts: </b></td>\n";
			}
			else if (lineData[0].equals("passedasserts")){
				tableHTML += "\t<td><b>passed asserts: </b></td>\n";
			}
			else {
				tableHTML += "\t<td><b>" + lineData[0] + ":</b></td>\n";
			}
			
			//table data
			if (!lineData[1].contentEquals("0") && !lineData[0].equals("passedasserts")){
				tableHTML += "\t<td><font color=\"#FF0000\">\n";
				tableHTML += "<b>" + lineData[1] + "</b>\n\t</td>\n";
			}
			else {
				tableHTML += "\t<td>\n";
				tableHTML += "<b>" + lineData[1] + "</b>\n\t</td>\n";
			}
			//System.out.println(lineData[0] + " "+ lineData[1]);
			
		}
		
		tableHTML += "</table>";
		rowData[0] = generateDateTime (line);
		rowData[1] = "Results";
		rowData[2] = tableHTML;
		
		return rowData;
	}
	
	/** 
	 * takes the "HTML saved" line from the raw .log file and generates a happy html row from it
	 *
	 * @param line - the "HTML saved" line from the raw SODA log file
	 * @return an array in the expected format
	 */
	private String[] formatHTMLSavedResults (String line, String message, char msgType){
		String[] rowData = new String[3];
		
		rowData[0] = generateDateTime (line);
		rowData[1] = "Log";
		
		String baseName = this.fileName;
		rowData[2] = "<b>"+rowData[0]+"</b>" +
		         "<a href=\""+baseName+"\" target=\"_blank\">"+msgType+"</a>";
		
		return rowData;
	}
	
	/** 
	 * takes an exception bt from the raw .log file and makes a html table row from it
	 * 
	 * @param line - the bt line from the raw SODA log file
	 * @return an array in the expected format
	 */
	private String[] formatExceptionBT (String line, String message){
		String[] rowData = new String[3];
		String btID = "bt_div_"+backTraceID;
		String hrefID = "href_div_"+backTraceID;
		backTraceID += 1;
		
		rowData[0] = generateDateTime (line);
		rowData[1] = "Backtrace";
		
		String rowHTML =  "\t<a id=\""+hrefID+"\" href=\"javascript:showdiv('"+btID+"',"+
		         " '"+hrefID+"')\">[ Expand Backtrace ]<b>+</b><br>\n" +
		         "</a><br>\t<div id=\""+btID+"\" style=\"display: none\">\n";
		
		String[] eData = message.split("--");
		for (int i = 1; i < eData.length; i++){
			rowHTML += "\t\t"+eData[i]+"<br>\n";
		}
		rowHTML += "\t<a href=\"javascript:hidediv('"+btID+"', '"+hrefID+"')\">" +
		         "[ Collaspe Backtrace ]<b>-</b></a>\t\t</div>\n\n";
		rowData[2] = rowHTML;
		return rowData;
	}
	
	/**
	 * takes a major exception line from the raw .log file and creates a nice html row
	 * 
	 * @param line - the "exception" line from the raw .log
	 * @return an array in the expected format
	 */
	private String[] formatMajorException (String line, String message){
		String[] rowData = new String[3];
		
		String[] msgData = message.split("--");
		msgData[0] = msgData[0].replaceAll("(?i)major exception", "<b>Major Exception:</b>");
		msgData[1] = msgData[1].replaceAll("(?i)exception message", "<b>Exception Message:</b>");
		
		rowData[0] = generateDateTime (line);
		rowData[1] = "Failure";
		rowData[2] = msgData[0]+"</br>"+ msgData[1];
		
		return rowData;
	}
	
	/** 
	 * takes an "assertion failed" line from the raw .log file and makes a happy html row 
	 * 
	 * @param line - the "assertion failed" line from the .log file
	 * @return an array in the expected format
	 */
	private String[] formatAssertionFailed (String line, String message){
		String[] rowData = new String[3];
		
		rowData[0] = generateDateTime (line);
		rowData[1] = "Failure";
		
		String[] assertData = message.split(":");
		assertData[1] = assertData[1].replaceFirst("'", "<b>'");
		assertData[1] += "</b>";
		rowData[2] = assertData[0] + assertData[1];
		
		return rowData;
	}
	
	/**
	 * takes an "assertion passed" line from the raw .log file and makes a happy html row 
	 * 
	 * @param line - the "assertion passed" line from the .log file
	 * @return an array in the expected format
	 */
	private String[] formatAssertionPassed(String line, String message){
		String[] rowData = new String[3];
		
		rowData[0] = generateDateTime (line);
		rowData[1] = "Assertion Passed";
		rowData[2] = safeHTMLString(message);
		
		return rowData;
	}
	
	/** 
	 * formats an "event dump" line from the .log file into html row
	 * 
	 * @param line - event dump line from the raw .log file
	 * @return an array in the expected format
	 */
	private String[] formatEventDump (String line, String message){
		String[] rowData = new String[3];
		String edID = "ed_div_"+eventDumpID;
		String hrefID = "href_div_ed_"+eventDumpID;
		eventDumpID ++;
		
		rowData[0] = generateDateTime (line);
		rowData[1] = "Event Dump";
		
		String msgText = message.substring(0, message.indexOf(".")+1);
		String msgData = message.substring(message.indexOf("--"), message.length()-1);
		
		String rowHTML = "\t<b>"+msgText+":</b>" +
		         "\t<a id=\""+hrefID+"\" href=\"javascript:showdiv('"+edID+"',"+
		         " '"+hrefID+"')\">[ Expand Event Dump ]<b>+</b><br>\n" +
		         "</a><br>\t<div id=\""+edID+"\" style=\"display: none\">\n";
		
		String[] eData = msgData.split("--");
		for (int i = 0; i < eData.length; i ++){
			rowHTML += "\t\t"+eData[i]+"<br>\n";
		}
		
		rowHTML += "\t<a href=\"javascript:hidediv('"+edID+"'" +
			       ", '"+hrefID+"')\">" +
			         "[ Collaspe Event Dump ]<b>-</b></a>\t\t</div>\n"; 
		
		rowData[2] = rowHTML;
		return rowData;
	}
	
	/**
	 * formats a "javascript error" line into html row
	 * 
	 * @param line - the js error line from the raw .log file
	 * @return an array in the expected format
	 */
	private String[] formatJSError (String line, String message){
		String[] rowData = new String[3];
		String rowHTML = "";
		
		rowData[0] = generateDateTime (line);
		rowData[1] = "Log";
		
		String[] msgData = message.split("--");
		for (int i = 0; i < msgData.length; i++){
			String[] info = msgData[i].split("::");
			if (info.length < 2){
				rowHTML += "\t<b>"+info[0]+"</b><br>\n";
			}
			else{
				rowHTML += "\t<b>"+info[0]+":</b> "+info[1]+"<br>\n";
			}
		}
		
		rowData[2] = rowHTML;
		return rowData;
	}
	
	/** 
	 * formats module lines into html row
	 * 
	 * @param line - the module line from the raw .log file
	 * @return an array in the expected format
	 */
	private String[] formatModuleLine (String line, String message){
		String[] rowData = new String[3];
		
		rowData[0] = generateDateTime (line);
		rowData[1] = "Un/Load";
		
		message = message.replaceFirst("Test:", "<b>Test:</b>");
		message = message.replaceFirst("Lib:", "<b>Lib:</b>");
		message = message.replaceFirst("Module:", "<b>Module:</b>");
		
		rowData[2] = message;
		
		return rowData;
	}
	
	/**
	 * converts the screenshot line into a nice html row
	 * 
	 * @param line - the screenshot line from the raw .log file
	 * @return an array in the expected format
	 */
	private String[] formatScreenShot(String line, String message){
		String[] rowData = new String[3];
		
		rowData[0] = generateDateTime (line);
		rowData[1] = "Log";
		
		String[] data = message.split(":");
		
		rowData[2] = "<b>"+data[0]+":</b> <a href=\""+data[1]+"\">#{data[1]}</a>";
		return rowData;
	}
	
	/**
	 * formats the replace string message line into html row
	 * 
	 * @param line - the replace string line from the raw .log file
	 * @return an array in the expected format
	 */
	private String[] formatReplaceString (String line, String message){
		String[] rowData = new String[3];
		
		rowData[0] = generateDateTime (line);
		rowData[1] = "Log";
		
		message = Pattern.quote(message);
		rowData[2] = message.replaceAll("with", "<b>'"+message+"'</b>");
		
		return rowData;
	}
	
	/** 
	 * converts the clicking element message line into a nice html row
	 * 
	 * @param line - the "clicking element" line from the raw .log file
	 * @return an array in the expected format
	 */
	private String[] formatClickingElement(String line, String message){
		String[] rowData = new String[3];
		
		rowData[0] = generateDateTime (line);
		rowData[1] = "Log";
		message = safeHTMLString(message);
		message = message.replaceAll(": ", ": <b>");
		message += "</b>";
		rowData[2] = message;
		
		return rowData;
	}
	
	/** 
	 * converts the trying to find element message line into a nice html row
	 * 
	 * @param line - the "trying to find" line from the raw .log file
	 * @return an array in the expected format
	 */
	private String[] formatFindingElement(String line, String message){
		String[] rowData = new String[3];
		rowData[0] = generateDateTime (line);
		rowData[1] = "Log";
		
		String[] msgData = message.split(":");
		String temp = msgData[0].substring(msgData[0].indexOf("'"), msgData[0].indexOf("by")-1);
		msgData[0] = msgData[0].replaceFirst(temp, "<b>"+temp+"</b> ");
		msgData[1] = msgData[1].replaceFirst("'", "<b>'");
		msgData[1] += "</b>";	
		
		rowData[2] = msgData[0] + msgData[1];
		return rowData;
	}
	
	/////////////////////
	//misc. methods
	/////////////////////
	/** 
	 * Generates the proper html header for the report file
	 * 
	 */
	private void generateHtmlHeader(){
		final String title = "SODA Test report";
		repFile.println("<html>");
		repFile.println("<script language=javascript type='text/javascript'>");
		repFile.println("function hidediv(name, href_id) {");
		repFile.println("\t document.getElementById(name).style.display = 'none';");
		repFile.println("\t document.getElementById(href_id).innerHTML=\"[ Expand Backtrace ]<b>+</b>\";");
		repFile.println("\t document.getElementById(href_id).href=\"javascript:showdiv('\" + name +\"', '\" + href_id + \"')\";"); 
		repFile.println("}");
		repFile.println("function showdiv(name, href_id) {");
		repFile.println("\t document.getElementById(name).style.display = 'inline';");
		repFile.println("\t document.getElementById(href_id).innerHTML=\"[ Collapse Backtrace ]<b>-</b>\";");
		repFile.println("\t document.getElementById(href_id).href=\"javascript:hidediv('\" + name + \"', '\" + href_id + \"')\";");
		repFile.println("}");
		repFile.println("</script>");
		repFile.println("<style type=\"text/css\">");
		repFile.println("body{");
		repFile.println("\t margin: 0px;");
		repFile.println("\t font-family: Arial, Verdana, Helvetica, sans-serif;");
		repFile.println("}");
		repFile.println("fieldset, table, pre{");
		repFile.println("\t margin-bottom:0;");
		repFile.println("}");
		repFile.println("p{");
		repFile.println("\t margin-top: 0px;");
		repFile.println("\t margin-bottom: 0px;");
		repFile.println("}");
		repFile.println("textarea{");
		repFile.println("\t fontfamily: Arial, Verdana,Helvetica,sans-serif;");
		repFile.println("}");
		repFile.println("td{");
		repFile.println("\t text-align:left;");
		repFile.println("\t vertical-align: top;");
		repFile.println("}");
		repFile.println(".td_msgtype{");
		repFile.println("\t text-align: center;");
		repFile.println("\t vertical-align: middle;");
		repFile.println("}");
		repFile.println(".tr_normal{");
		repFile.println("\t background: #e5eef3;");
		repFile.println("}");
		repFile.println(".tr_header{");
		repFile.println("\t background: #a4a4a4;");
		repFile.println("}");
		repFile.println(".tr_module{");
		repFile.println("\t background: #3c78c8;");
		repFile.println("}");
		repFile.println(".tr_error{");
		repFile.println("\t background: #ff0000;");
		repFile.println("}");
		repFile.println(".tr_warning{");
		repFile.println("\t background: #eeff30;");
		repFile.println("}");
		repFile.println(".tr_assert_passed{");
		repFile.println("\t background: #7ff98a;");
		repFile.println("}");
		repFile.println(".highlight {");
		repFile.println("\t background-color: #8888FF;");
		repFile.println("}");
		repFile.println(".highlight_report {");
		repFile.println("\t background-color: #5dec6d;");
		repFile.println("}");
		repFile.println("table {");
		repFile.println("\t background: #ffff;");
		repFile.println("\t border: 1px solid black;");
		repFile.println("\t border-bottom: 1px solid #0000;");
		repFile.println("\t border-right: 1px solid #0000;");
		repFile.println("\t color: #0000;");
		repFile.println("\t padding: 4px;");
		repFile.println("\t font-size: 11px;");
		repFile.println("}");
		repFile.println("</style>");
		repFile.println("<title>"+title+"</title>");
		repFile.println("<body>");
		repFile.println("<table>");
		repFile.println("<tr class=\"tr_header\">");
		repFile.println("\t <td nowrap> <b>Date Time: </b></td>");
		repFile.println("\t <td nowrap> <b>Message Type: </b></td>");
		repFile.println("\t <td> <b>Message: </b></td>");
		repFile.println("</tr >");
		repFile.println("");
	}
	
	/**
	 * Generates html code formatted to display date and time of log message from a raw .log file line
	 * 
	 * @param line - A line from the raw .log file
	 * @return A string of html that is the date and time of the log line
	 */
	private String generateDateTime (String line){
		String htmlDateTime = line.substring(1, line.indexOf("]"));
		return htmlDateTime.replaceFirst("-", "- <br />");
	}
	
	/**
	 * Takes a String and makes it HTML-safe by performing proper escapes
	 * 
	 * @param str - String to be properly escaped
	 * @return the properly escaped String 
	 */
	private String safeHTMLString (String str){
		str = str.replaceAll("<", "&lt;");
		str = str.replaceAll(">", "&gt;");
		return str;
	}
	
	
	/**
	 * returns the .html file name
	 * @return output file name
	 */
	public String getFileName(){
		return fileName;
	}
}
