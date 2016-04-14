package net.site40.rodit.tinyrpg.game.map;

import java.util.HashMap;

public class MapProperties {

	private HashMap<String, String> properties;
	
	public MapProperties(){
		properties = new HashMap<String, String>();
	}
	
	public boolean isset(String key){
		return properties.get(key) != null;
	}
	
	public int getInt(String key){
		if(!isset(key))return -1;
		return Integer.valueOf(properties.get(key));
	}
	
	public void setInt(String key, int value){
		properties.put(key, value + "");
	}
	
	public float getFloat(String key){
		if(!isset(key))return -1;
		return Float.valueOf(properties.get(key));
	}
	
	public void setFloat(String key, float value){
		properties.put(key, value + "");
	}
	
	public boolean getBool(String key){
		if(!isset(key))return false;
		return Boolean.valueOf(properties.get(key));
	}
	
	public void setBool(String key, boolean value){
		properties.put(key, value + "");
	}
	
	public String getString(String key){
		if(!isset(key))return "";
		return properties.get(key);
	}
	
	public void setString(String key, String value){
		properties.put(key, value);
	}
}
