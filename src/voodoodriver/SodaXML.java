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

/**
 * This is a simple class for reading a soda xml test file into a SodaEvents class.
 * @author trampus
 *
 */

public class SodaXML {
	
	private Document doc = null;
	private SodaElementsList types = null;
	private SodaTypes sodaTypes = null;
	private SodaEvents events = null;
	private SodaReporter reporter = null;
	
	/*
	 * SodaXML: Constructor
	 * 
	 * Input:
	 * 	sodaTest: A full path to a soda test file.
	 * 
	 * Output:
	 * 	None.
	 */
	public SodaXML(String sodaTest, SodaReporter reporter) {
		File testFD = null;
		DocumentBuilderFactory dbf = null;
		DocumentBuilder db = null;

		this.reporter = reporter;
		
		try {
			testFD = new File(sodaTest);
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			doc = db.parse(testFD);
			sodaTypes = new SodaTypes();
			types = sodaTypes.getTypes();
			events = this.parse(doc.getDocumentElement().getChildNodes());
		} catch (Exception exp) {
			this.events = null;
			if (this.reporter == null) {
				exp.printStackTrace();
			} else {
				this.reporter.ReportException(exp);
			}
		}
	}
	
	/*
	 * getEvents:
	 * 	This method returns the events that were created from the soda test file.
	 * 
	 * Input:
	 * 	None.
	 * 
	 * Output:
	 * 	returns a SodaEvents object.
	 * 
	 */
	public SodaEvents getEvents() {
		return this.events;
	}
	
	/*
	 * findElementAccessor:
	 * 	This method finds an accessor for a given soda element.
	 * 
	 * Intput:
	 * 	sodaelement: This is the element to find an accessor for.
	 * accessor: This is the accessor to find for the given element.
	 * 
	 * Output:
	 * 	returns a string with the given accessor if one exists, else null.
	 * 
	 */
	private String findElementAccessor(SodaElements sodaelement, String accessor) {
		String result = null;
		int len = types.size() -1;
		SodaHash foundType = null;
		SodaHash accessors = null;
		
		for (int i = 0; i <= len; i++) {
			if (types.get(i).get("type") == sodaelement) {
				foundType = types.get(i);
				break;
			}
		}
		
		if (foundType == null) {
			System.out.printf("foundType == null!\n");
			return null;
		}
		
		if (!foundType.containsKey("accessor_attributes")) {
			return null;
		}
		
		accessors = (SodaHash)foundType.get("accessor_attributes");
		if (accessors.containsKey(accessor)) {
			result = accessor;
		}
		
		return result;
	}
	
	/*
	 * processAttributes:
	 * 	This method gets all of the attributes for a given soda element.
	 * 
	 * Intput:
	 * 	map: This is the soda element's map.
	 *  node: This is the xml node for the given element.
	 * 
	 * Output:
	 * 	returns a SodaHash object filled with the node's attributes if it has any.  If there are
	 * 	no attributes then an empty SodaHash is returned.
	 * 
	 */
	private SodaHash processAttributes(SodaHash map, Node node) {
		int len = node.getAttributes().getLength();
		
		if (node.hasAttributes()) {
			for (int i = 0; i <= len -1; i++) {
				Node tmp = node.getAttributes().item(i);
				String name = tmp.getNodeName();
				String value = tmp.getNodeValue();
				String accessor = findElementAccessor((SodaElements)map.get("type"), name); 
				map.put(name, value);
				
				if (accessor != null) {
					map.put("how", accessor);
				}
				
			}
		}
		
		return map;
	}
	
	/*
	 * parse:
	 * 	This method parses all the xml nodes into a SodaEvents object.
	 * 
	 * Input:
	 * 	node:  This is a node list from the soda xml test.
	 * 
	 * Output:
	 * 	returns a SodaEvents object.
	 */
	private SodaEvents parse(NodeList node) throws Exception{
		SodaHash data = null;
		SodaEvents dataList = null;
		boolean err = false;
		int len = 0;
		
		dataList = new SodaEvents();

		len = node.getLength();
		for (int i = 0; i <= len -1; i++) {
			Node child = node.item(i);
			String name = child.getNodeName();
			
			if (name.startsWith("#")) {
				continue;
			}
			
			if (!sodaTypes.isValid(name)) {
				if (this.reporter == null) {
					System.err.printf("Error: Invalid Soda Element: '%s'!\n", name);
				} else {
					this.reporter.ReportError(String.format("Error: Invalid Soda Element: '%s'!", name));
				}
				
				err = true;
				break;
			}
			
			data = new SodaHash();
			data.put("do", name);
			data.put("type", SodaElements.valueOf(name.toUpperCase()));
		
			if (child.hasAttributes()) {
				data = processAttributes(data, child);
			}
			
			if (name.contains("javascript")) {
				String tmp = child.getTextContent();
				if (!tmp.isEmpty()) {
					data.put("content", tmp);
				}
			}
			
			if (child.hasChildNodes()) {
				if (name.contains("execute")) {
					String[] list = processArgs(child.getChildNodes());
					data.put("args", list);
				} else {
					SodaEvents tmp = parse(child.getChildNodes());
					if (tmp != null) {
						data.put("children", tmp);
					} else {
						err = true;
						break;
					}
				}
			}
			
			if (!data.isEmpty()) {
				dataList.add(data);
			} else {
				System.out.printf("Note: No data found.\n");
			}
		}
		
		if (err) {
			dataList = null;
		}
		
		return dataList;
	}
	
	private String[] processArgs(NodeList nodes) {
		int len = nodes.getLength() -1;
		String[] list;
		int arg_count = 0;
		int current = 0;
		
		for (int i = 0; i <= len; i++) {
			String name = nodes.item(i).getNodeName();
			if (name.contains("arg")) {
				arg_count += 1;
			}
		}
		
		list = new String[arg_count];
		
		for (int i = 0; i <= len; i++) {
			String name = nodes.item(i).getNodeName();
			if (name.contains("arg")) {
				String value = nodes.item(i).getTextContent();
				list[current] = value;
				current += 1;
			}
		}
		
		return list;
	}
	
}
