package net.site40.rodit.tinyrpg.game.saves;

import java.util.HashMap;

import net.site40.rodit.rlib.util.Data;

public class Options {
	
	public static final String USE_HARDWARE_RENDER = "hardware_render";
	public static final String BACKGROUND_MUSIC = "music_bg";
	public static final String CONTROLS_ALPHA = "alpha_controls";
	public static final String CHAT_BG_ALPHA = "alpha_chat_bg";
	public static final String CHAT_TEXT_ALPHA = "alpha_chat_text";
	public static final String TRANSITION_SPEED = "transition_speed";
	
	public static final String TRANSITION_SPEED_FAST = "fast";
	public static final String TRANSITION_SPEED_NORMAL = "normal";
	public static final String TRANSITION_SPEED_SLOW = "slow";

	private HashMap<String, String> options;
	private OptionChangedListener changeListener;
	
	public Options(){
		this.options = new HashMap<String, String>();
	}
	
	public HashMap<String, String> getMap(){
		return options;
	}
	
	public void setChangeListener(OptionChangedListener changeListener){
		this.changeListener = changeListener;
	}
	
	public void put(String key, String value){
		if(changeListener != null)
			changeListener.onOptionChanged(key, get(key), value);
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
		put(USE_HARDWARE_RENDER, true);
		put(BACKGROUND_MUSIC, true);
		put(CONTROLS_ALPHA, 50);
		put(CHAT_BG_ALPHA, 50);
		put(CHAT_TEXT_ALPHA, 75);
		put(TRANSITION_SPEED, TRANSITION_SPEED_NORMAL);
	}
	
	public static interface OptionChangedListener{
		
		public void onOptionChanged(String name, String oldValue, String newValue);
	}
}
