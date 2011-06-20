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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SodaBlockListParser {
	
	private String file_name = null;
	
	public SodaBlockListParser(String file) {
		this.file_name = file;
	}
	
	public SodaBlockList parse() {
		SodaBlockList list = null;
		File FD = null;
		DocumentBuilderFactory dbf = null;
		DocumentBuilder db = null;
		Document doc = null;
		NodeList nodes = null;

		try {
			FD = new File(this.file_name);
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			doc = db.parse(FD);
			list = new SodaBlockList();
		} catch (Exception exp) {
			System.err.printf("(!)Error: %s\n", exp.getMessage());
			list = null;
		}
		
		nodes = doc.getDocumentElement().getChildNodes();
		for (int i = 0; i <= nodes.getLength() -1; i++) {
			Node n = nodes.item(i);
			String name = n.getNodeName();
			if (!name.contains("block")) {
				continue;
			}
			
			NodeList kids = n.getChildNodes();
			SodaHash tmp = new SodaHash();
			for (int x = 0; x <= kids.getLength() -1; x++) {
				Node kid = kids.item(x);

				if (kid.getNodeName().contains("#text")) {
					continue;
				}
				
				String kid_name = kid.getNodeName();
				String value = kid.getTextContent();
				if (value != null) {
					tmp.put(kid_name, value);
				}
			}
			list.add(tmp);
		}
		
		return list;
	}
	
}
