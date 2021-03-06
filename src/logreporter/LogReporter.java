package logreporter;
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
import java.io.*;
import java.util.*;
public class LogReporter {
	private static void printUsage() {
		String msg = "LogReporter - goes through each folder in given directory and generates html version of SODA log files,\n" +
				"report of each suite, and a summary of all suites\n\n"+
		"Usage: LogReporter.jar  --help --suitedir=\"path/to/suite/directory\" "+
				"--suite=\"/path/to/single/suite/diectory\"... \n\n" +
				"flags: \n" +
				"--suite:\t pass in a single suite file\n" +
				"--suitedir:\t pass in a directory of suites\n" +
				"--help:\t\t displays this message \n";
		
		System.out.printf("%s\n", msg);
	}
	
	/**
	 * from the directory path, return an ArrayList of either xml files or folders containing .log files
	 * @param path - path of the directory to get folders
	 * @param getFolder - returns ArrayList of folders if true, ArrayList of files if false
	 * @return ArrayList of folders/files
	 */
	private static ArrayList<File> getFolderContent(String path, boolean getFolder){
		ArrayList<File> list = new ArrayList<File>();
		/**
		 * get the list of files/folders in given directory
		 */
		File folder = new File(path);
		File[] filesList = folder.listFiles();
		
		/**
		 * look through the given directory
		 */
		for (int i = 0; i < filesList.length; i++){
			if (getFolder){
				/**
				 * check if is folder and not hidden
				 */
				if (filesList[i].isDirectory() && !filesList[i].isHidden()){
					/**
					 * put valid files in ArrayList 
					 */
					list.add(filesList[i]);
				}
			}
			//get files instead
			else {
				if (filesList[i].isFile() && !filesList[i].isHidden() && filesList[i].getName().endsWith("xml")){
					list.add(filesList[i]);
				}
			}
		}	
		
		return list;
	}
	
	/**
	 * processes the list of arguments, and returns a HashMap of options for main to use
	 * @return 
	 */
	private static HashMap<String, Object> cmdLineOptions(String[] args){
		HashMap<String, Object> options = new HashMap<String, Object>();
		
		try{
			for (int i = 0; i < args.length; i++){
				if (args[i].contains("--help")){
					options.put("help", true);
				}
				else if (args[i].contains("--suite=")){
					String str = args[i];
					str = str.replace("--suite=", "");
					/**
					 * add "/" to the end of path if none was given, prevent errors
					 */
					if (!str.endsWith("/")){
						str = str + "/";
					}
					System.out.println("Processing suite: "+str);
					options.put("suite", str);
				}
				else if (args[i].contains("--suitedir=")){
					String str = args[i];
					str = str.replace("--suitedir=", "");
					/**
					 * add "/" to the end of path if none was given, prevent errors
					 */
					if (!str.endsWith("/")){
						str = str + "/";
					}
					options.put("suitedir", str);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return options;
	}
	/**
	 * main
	 * @param args
	 */
	public static void main(String[] args){
		int count = 0;
		try{
			HashMap<String, Object> options = cmdLineOptions (args);
			if (options.isEmpty()){
				printUsage();
				System.exit(0);
			}
			if (options.containsKey("help")){
				printUsage();
				System.exit(0);
			}
			if (options.containsKey("suitedir")){
				String path = (String)options.get("suitedir");
				ArrayList<File> foldersList = getFolderContent(path, true);
				
				//generate log reports
				LogConverter htmlLogs = new LogConverter();
				File[] filesList;
				//look though folders
				if (foldersList.size() == 0){
					System.out.println("there are no directories to generate log reports from.");
				}
				for (int i = 0; i < foldersList.size(); i++){
					System.out.println("checking directory: "+foldersList.get(i).getName());
					//get files from folder
					filesList = foldersList.get(i).listFiles();
					System.out.println("number of files or folders: "+filesList.length);
					for (int j = 0; j < filesList.length; j ++){
						//check is file, not hidden, and is a log file
						if (!filesList[j].isHidden() && filesList[j].isFile() && filesList[j].getName().endsWith("log")){
							htmlLogs = new LogConverter(filesList[j].getAbsolutePath());
							htmlLogs.generateReport();
							count ++;
						}
					}
					if (count == 0){
						System.out.println("no log reports found.");
					}
					else {
						System.out.println("generated "+count+" log report(s)");	
					}
					//generate tests summary
					if (count != 0){
						System.out.println("printing test summary for suite: "+ foldersList.get(i).getAbsolutePath());
						SuiteReporter suiteSummary = new SuiteReporter(foldersList.get(i));
						suiteSummary.generateReport();
						System.out.println("generated test summary for suite: "+foldersList.get(i).getAbsolutePath());
					}
					count = 0;
				}
				
				//generate summary of suites
				if (getFolderContent(path, false).size() == 0){
					System.out.println("There are no files containing suite test information");
				}
				else {
					SummaryReporter summaryReport = new SummaryReporter(getFolderContent(path, false), path);
					summaryReport.generateReport();
					System.out.println("generated summary.html");
				}	
			}
			else if (options.containsKey("suite")){
				String path = (String)options.get("suite");
				File folder = new File(path);
				File[] filesList = folder.listFiles();
				//generate log reports
				LogConverter htmlLogs = new LogConverter();
				//look though folder
				System.out.println("number of files or folders in "+path+": "+filesList.length);
				for (int i = 0; i < filesList.length; i ++){
					if (!filesList[i].isHidden() && filesList[i].isFile() && filesList[i].getName().endsWith("log")){
						htmlLogs = new LogConverter(filesList[i].getAbsolutePath());
						htmlLogs.generateReport();
						count ++;
					}
				}
				if (count == 0){
					System.out.println("no log reports found.");
				}
				else {
					System.out.println("generated "+count+" log report(s)");	
				}
				//generate tests summary
				if (count != 0){
					System.out.println("printing test summary for suite: "+ path);
					SuiteReporter suiteSummary = new SuiteReporter(folder);
					suiteSummary.generateReport();
					System.out.println("generated test summary for suite: "+ path);	
				}
				count = 0;
			}
			
			
		}catch(NullPointerException e){
			System.err.println("invalid path");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}//end main
}
