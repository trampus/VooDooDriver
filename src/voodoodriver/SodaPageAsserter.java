/*
Copyright 2011 Trampus Richmond. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

   1. Redistributions of source code must retain the above copyright notice, this list of
      conditions and the following disclaimer.

   2. Redistributions in binary form must reproduce the above copyright notice, this list
      of conditions and the following disclaimer in the documentation and/or other materials
      provided with the distribution.

THIS SOFTWARE IS PROVIDED BY TRAMPUS RICHMOND ``AS IS'' AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
TRAMPUS RICHMOND OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those of the authors and 
should not be interpreted as representing official policies, either expressed or implied, of Trampus Richmond.
 
 */

package voodoodriver;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SodaPageAsserter {

	private ArrayList<String> ignores = null;
	private ArrayList<String> checkes = null;
	private File fd = null;
	private SodaReporter reporter = null;
	
	public SodaPageAsserter(String assertFile, SodaReporter reporter) {
		this.reporter = reporter;
		this.ignores = new ArrayList<String>();
		this.checkes = new ArrayList<String>();
		Document doc = null;
		File suiteFD = null;
		DocumentBuilderFactory dbf = null;
		DocumentBuilder db = null;
		
		fd = new File(assertFile);
		if (!fd.exists()) {
			String msg = String.format("Error failed to find assertpage file: '%s'!", assertFile);
			this.reporter.ReportError(msg);
		}
		
		try {
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			doc = db.parse(fd);
			this.parse(doc.getDocumentElement().getChildNodes());
		} catch (Exception exp) {
			this.reporter.ReportException(exp);
		}
	}
	
	public void assertPage(String pagesrc) {
		this.reporter.Log("Page Assert Starting.");
		int ignore_len = this.ignores.size() -1;
		int check_len = this.checkes.size() -1;
		
		for (int i = 0; i <= ignore_len; i++) {
			if (reporter.isRegex(this.ignores.get(i))) {
				String regex = reporter.strToRegex(this.ignores.get(i));
				pagesrc = pagesrc.replaceAll(regex, "");
			} else {
				pagesrc = pagesrc.replace(this.ignores.get(i), "");
			}
		}
		
		for (int i = 0; i <= check_len; i++) {
			if (reporter.isRegex(this.checkes.get(i))) {
				String regex = reporter.strToRegex(this.checkes.get(i));
				Pattern p = Pattern.compile(regex, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(pagesrc);
				if (m.find()) {
					String msg = String.format("Page Assert Found Match for: '%s'!", this.checkes.get(i));
					this.reporter.ReportError(msg);
					this.reporter.SavePage();
				}
			} else {
				if (pagesrc.contains(this.checkes.get(i))) {
					String msg = String.format("Page Assert Found Match for: '%s'!", this.checkes.get(i));
					this.reporter.ReportError(msg);
					this.reporter.SavePage();
				}
			}
		}
		
		this.reporter.Log("Page Assert Finished.");
	}
	
	private void parse(NodeList nodes) {
		int len = nodes.getLength() -1;
		
		for (int i = 0; i <= len; i++) {
			Node node = nodes.item(i);
			String name = node.getNodeName();
			
			if (name.contains("ignore")) {
				NodeList inodes = node.getChildNodes();
				int ilen = inodes.getLength() -1;
				
				for (int ii = 0; ii <= ilen; ii++) {
					Node tmp = inodes.item(ii);
					String tmp_name = tmp.getNodeName();
					if (!tmp_name.contains("regex")) {
						continue;
					}
					
					String value = tmp.getTextContent();
					this.ignores.add(value);
				}
				
				continue;
			}
			
			if (name.contains("checks")) {
				NodeList inodes = node.getChildNodes();
				int ilen = inodes.getLength() -1;
				
				for (int ii = 0; ii <= ilen; ii++) {
					Node tmp = inodes.item(ii);
					String tmp_name = tmp.getNodeName();
					if (!tmp_name.contains("regex")) {
						continue;
					}
					
					String value = tmp.getTextContent();
					this.checkes.add(value);
				}
			
				continue;
			}
		}
	}
	
}
