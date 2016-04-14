package net.site40.rodit.tinyrpg.game.map;

import java.util.HashMap;

import android.graphics.RectF;

public class MapObject {

	private HashMap<String, String> properties;
	private String group;
	private float x;
	private float y;
	private float width;
	private float height;
	
	public MapObject(){
		this("", 0f, 0f, 0f, 0f);
	}
	
	public MapObject(String group, float x, float y, float width, float height){
		this.group = group;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.properties = new HashMap<String, String>();
	}
	
	public HashMap<String, String> getProperties(){
		return properties;
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
	
	public long getLong(String key){
		if(!isset(key))
			return 0L;
		return Long.valueOf(getString(key));
	}
	
	public void setLong(String key, long value){
		properties.put(key, value + "");
	}
	
	public String getGroup(){
		return group;
	}
	
	public void setGroup(String group){
		this.group = group;
	}

	public float getX(){
		return x;
	}

	public void setX(float x){
		this.x = x;
	}

	public float getY(){
		return y;
	}

	public void setY(float y){
		this.y = y;
	}

	public float getWidth(){
		return width;
	}

	public void setWidth(float width){
		this.width = width;
	}

	public float getHeight(){
		return height;
	}

	public void setHeight(float height){
		this.height = height;
	}
	
	public RectF getBounds(){
		return new RectF(x, y, x + width, y + height);
	}
}
