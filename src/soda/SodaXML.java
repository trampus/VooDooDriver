package soda;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SodaXML {
	
	private Document doc = null;
	private SodaElementsList types = null;
	private SodaTypes sodaTypes = null;
	private SodaEvents events = null;
	
	public SodaXML(String sodaTest) {
		File testFD = null;
		DocumentBuilderFactory dbf = null;
		DocumentBuilder db = null;

		try {
			testFD = new File(sodaTest);
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			doc = db.parse(testFD);
			sodaTypes = new SodaTypes();
			types = sodaTypes.getTypes();
			events = this.parse(doc.getDocumentElement().getChildNodes());
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}
	
	public SodaEvents getEvents() {
		return this.events;
	}
	
	private String findElementAccessor(SodaElements sodaelement, String accessor) {
		String result = null;
		int len = types.size() -1;
		SodaHash foundType = null;
		SodaHash accessors = null;
		
		for (int i = 0; i <= len; i++) {
			if (types.get(i).get("type") == sodaelement) {
				System.out.printf("Found type: %s\n", sodaelement.toString());
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
					System.out.printf("How: %s\n", accessor);
				}
				
			}
		}
		
		return map;
	}
	
	private SodaEvents parse(NodeList node) {
		SodaHash data = null;
		SodaEvents dataList = null; 
		
		int len = 0;
		try {
			dataList = new SodaEvents();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		
		len = node.getLength();
		for (int i = 0; i <= len -1; i++) {
			Node child = node.item(i);
			String name = child.getNodeName();
			
			if (name.startsWith("#")) {
				continue;
			}
			
			if (!sodaTypes.isValid(name)) {
				System.err.printf("Error: Invalid Soda Element: '%s'!\n", name);
			}
			
			data = new SodaHash();
			data.put("do", name);
			data.put("type", SodaElements.valueOf(name.toUpperCase()));
		
			if (child.hasAttributes()) {
				data = processAttributes(data, child);
			}
			
			if (child.hasChildNodes()) {
				data.put("children", parse(child.getChildNodes()));
			}
			
			if (!data.isEmpty()) {
				dataList.add(data);
			} else {
				System.out.printf("Note: No data found.\n");
			}
		}
		
		return dataList;
	}
	
}
