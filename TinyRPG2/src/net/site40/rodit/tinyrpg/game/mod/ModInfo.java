package net.site40.rodit.tinyrpg.game.mod;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.site40.rodit.util.Util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class ModInfo {

	private static DocumentBuilderFactory factory;
	private static DocumentBuilder builder;
	static{
		try{
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	protected String name;
	protected String showName;
	protected String description;
	protected String version;
	protected int versionNumber;
	protected String modClass;

	public ModInfo(String toParse)throws IOException, SAXException{
		InputSource is = new InputSource(new StringReader(toParse));
		Document d = builder.parse(is);
		Element root = (Element)d.getElementsByTagName("mod").item(0);
		if(root == null)
			throw new IOException("Invalid mod info file. Expected root element with tag 'mod' but it was not found.");
		name = root.getAttribute("name");
		showName = root.getAttribute("showName");
		description = root.getAttribute("description");
		version = root.getAttribute("version");
		versionNumber = Util.tryGetInt("versionNumber", -1);
		modClass = root.getAttribute("main");
	}

	public String getName(){
		return name;
	}

	public String getShowName(){
		return showName;
	}

	public String getDescription(){
		return description;
	}

	public String getVersion(){
		return version;
	}

	public int getVersionNumber(){
		return versionNumber;
	}
}
