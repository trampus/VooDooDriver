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

public class SodaTypes {
	private Document doc = null;
	private SodaElementsList datatypes = null;
	
	public SodaTypes() {
		File testFD = null;
		DocumentBuilderFactory dbf = null;
		DocumentBuilder db = null;

		try {
			testFD = new File(getClass().getResource("SodaElements.xml").getFile());
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			doc = db.parse(testFD);
			datatypes = this.parse(doc.getDocumentElement().getChildNodes());
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}
	
	private SodaElementsList parse(NodeList node) {
		SodaElementsList dataList = null; 
		
		int len = 0;
		try {
			dataList = new SodaElementsList();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		
		len = node.getLength();
		for (int i = 0; i <= len -1; i++) {
			SodaHash data = new SodaHash();
			Node child = node.item(i);
			String name = child.getNodeName();
			
			if (name.startsWith("#")) {
				continue;
			}
			
			if (name.contains("comment")) {
				continue;
			}
			
			data.put(name, 0);
			data.put("type", SodaElements.valueOf(name.toUpperCase()));
			if (child.hasChildNodes()) {
				NodeList kids = child.getChildNodes();
				for (int x = 0; x <= kids.getLength() -1; x++) {
					Node tmp = kids.item(x);
					String kid_name = tmp.getNodeName();
					if (kid_name.contains("soda_attributes") || kid_name.contains("accessor_attributes")) {
						data.put(kid_name, parseAccessors(tmp.getChildNodes()));
					}
				}
			}
			
			if (!data.isEmpty()) {
				dataList.add(data);
			}
		}
		
		return dataList;
	}
	
	private SodaHash parseAccessors(NodeList nodes) {
		SodaHash hash = new SodaHash();
		int len = nodes.getLength() -1;
		
		for (int i = 0; i <= len; i++) {
			String node_name = nodes.item(i).getNodeName();
			if (node_name == "#text") {
				continue;
			}
			
			if (node_name != "action") {
				String value = nodes.item(i).getTextContent();
				if (value.isEmpty() || value.startsWith("\n")) {
					continue;
				}
				hash.put(value, 0);
			} else {
				SodaHash act_hash = new SodaHash();
				NodeList actions = nodes.item(i).getChildNodes();
				int actlen = actions.getLength() -1;
				
				for(int x = 0; x <= actlen; x++) {
					String act = actions.item(x).getTextContent();
					String act_name = actions.item(x).getNodeName();
					
					if (act_name == "name") {
						act_hash.put(act_name, act);
					}
				}
				hash.put(node_name, act_hash);
			}
		}
		
		return hash;
	}
	
	public SodaElementsList getTypes() {
		return datatypes;
	}

	// this needs to be redone //
	public boolean isValid(String name) {
		boolean result = false;
		int len = datatypes.size() -1;
		
		for (int i = 0; i <= len; i++) {
			if (datatypes.get(i).containsKey(name)) {
				result = true;
				break;
			} else {
				result = false;
			}
		}
		
		return result;
	}
}
