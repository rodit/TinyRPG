package net.site40.rodit.tinyrpg.game.saves;

import java.util.HashMap;

import net.site40.rodit.rlib.util.Data;

public class Options {

	private HashMap<String, String> options;
	
	public Options(){
		this.options = new HashMap<String, String>();
	}
	
	public HashMap<String, String> getMap(){
		return options;
	}
	
	public void put(String key, String value){
		options.put(key, value);
	}
	
	public void put(String key, Object value){
		put(key, String.valueOf(value));
	}
	
	public String get(String key){
		return options.get(key);
	}
	
	public int getInt(String key){
		return Data.convertInt(get(key));
	}
	
	public float getFloat(String key){
		return Data.convertFloat(get(key));
	}
	
	public long getLong(String key){
		return Data.convertLong(get(key));
	}
	
	public double getDouble(String key){
		return Data.convertDouble(key);
	}
	
	public boolean getBool(String key){
		return Data.convertBool(get(key));
	}
	
	public void initDefaults(){
		put("hardware_render", true);
		put("music_bg", true);
		put("controls_alpha", 50);
		put("chat_bg_alpha", 50);
		put("chat_text_alpha", 75);
	}
}
