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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import soda.SodaEvents;
import soda.SodaHash;

public class SodaPluginParser {

	private SodaEvents plugins = null;
	private NodeList Nodedata = null;
	
	public SodaPluginParser(String filename) throws Exception {
		File fd = null;
		DocumentBuilderFactory dbf = null;
		DocumentBuilder db = null;
		Document doc = null;
		
		fd = new File(filename);
		if (!fd.exists()) {
			throw new Exception("Failed to find file: " + filename);
		}
		
		dbf = DocumentBuilderFactory.newInstance();
		db = dbf.newDocumentBuilder();
		doc = db.parse(fd);
		this.Nodedata = doc.getDocumentElement().getChildNodes();
	}
	
	public SodaEvents parse() throws Exception {
		SodaEvents data = null;
		int len = this.Nodedata.getLength() -1;
		
		data = new SodaEvents();
		
		for (int i = 0; i <= len; i++) {
			Node child = this.Nodedata.item(i);
			String name = child.getNodeName();
			
			if (!name.contains("plugin")) {
				continue;
			}
			
			if (!child.hasChildNodes()) {
				System.out.printf("(!)Error: Failed to find all needed data for plugin node!\n");
				continue;
			}
			
			SodaHash tmp = new SodaHash();
			
			int clen = child.getChildNodes().getLength() -1;
			for (int cindex = 0; cindex <= clen; cindex++) {
				Node info = child.getChildNodes().item(cindex);
				String cname = info.getNodeName();
				cname = cname.toLowerCase();
				
				if (cname.contains("#text")) {
					continue;
				}
				
				String value = info.getTextContent();
				tmp.put(cname, value);
			}
			data.add(tmp);
		}
		
		return data;
	}
}
