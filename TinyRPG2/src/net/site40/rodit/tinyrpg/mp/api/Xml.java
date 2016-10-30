package net.site40.rodit.tinyrpg.mp.api;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class Xml {

	public static Document parse(String document){
		try{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(document));
			Document d = builder.parse(is);
			return d;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
