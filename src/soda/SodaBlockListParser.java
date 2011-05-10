package soda;

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
			for (int x = 0; x <= kids.getLength() -1; x++) {
				Node kid = kids.item(x);
				if (!kid.getNodeName().contains("testfile")) {
					continue;
				}
				
				String value = kid.getTextContent();
				if (value != null) {
					list.add(value);
				}
			}
		}
		
		return list;
	}
	
}
