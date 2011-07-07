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

import java.io.File;
import java.lang.reflect.Constructor;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import voodoodriver.SodaEvents;
import voodoodriver.SodaHash;

/**
 * Class to parse a VooDooDriver config xml file.
 * 
 * @author trampus
 *
 */
public class SodaConfigParser {

	private File configFile = null;
	
	/**
	 * {@link Constructor}
	 * 
	 * @param configfile VooDooDriver config file.
	 */
	public SodaConfigParser(File configfile) {
		this.configFile = configfile;
	}
	
	/**
	 * Parses the VooDooDriver config file.
	 * 
	 * @return {@link SodaEvents}
	 */
	public SodaEvents parse() {
		SodaEvents options = new SodaEvents();
		DocumentBuilderFactory dbf = null;
		DocumentBuilder db = null;
		Document doc = null;
		NodeList nodes = null;
		int node_count = 0;
		
		try {
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			doc = db.parse(this.configFile);	
		} catch (Exception exp) {
			exp.printStackTrace();
			System.exit(2);
		}
		
		nodes = doc.getDocumentElement().getChildNodes();
		node_count = nodes.getLength() -1;
		
		for (int i = 0; i <= node_count; i++) {
			SodaHash data = new SodaHash();
			Node tmp = nodes.item(i);
			String name = tmp.getNodeName();
			
			
			if (name.contains("#text")) {
				continue;
			}
			
			if (tmp.hasAttributes()) {
				NamedNodeMap attrs = tmp.getAttributes();
				int attrs_count = attrs.getLength() -1;
				
				for (int x = 0; x <= attrs_count; x++) {
					Node tmp_attr = attrs.item(x);
					String attr_name = tmp_attr.getNodeName();
					String attr_value = tmp_attr.getNodeValue();
					data.put(attr_name, attr_value);
				}
			}
			
			String value = tmp.getTextContent();
			data.put("type", name);
			data.put("value", value);
			options.add(data);
		}
		
		return options;
	}
}
