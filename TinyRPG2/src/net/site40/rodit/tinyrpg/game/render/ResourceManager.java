package net.site40.rodit.tinyrpg.game.render;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.site40.rodit.tinyrpg.game.Game;

import org.w3c.dom.Document;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;

public class ResourceManager {

	private AssetManager assets;
	private HashMap<String, Object> resources;

	public ResourceManager(AssetManager assets){
		this.assets = assets;
		this.resources = new HashMap<String, Object>();
	}

	public HashMap<String, Object> getResources(){
		return resources;
	}

	public Object getObject(String key){
		if(key == null || TextUtils.isEmpty(key))
			return null;
		if((key.endsWith(".png") || key.endsWith(".spr")) || key.endsWith(".anm") && !key.startsWith("bitmap/"))
			key = "bitmap/" + key;
		Object value = resources.containsKey(key) ? resources.get(key) : null;
		if(value == null){
			if(key.endsWith(".tlk")){
				try{
					DialogText dlg = new DialogText(openAsset(key));
					resources.put(key, dlg);
					return dlg;
				}catch(IOException e){
					e.printStackTrace();
				}
			}
			byte[] data = readAsset(key);
			if(data == null)
				return null;
			if(key.endsWith(".png"))
				value = BitmapFactory.decodeByteArray(data, 0, data.length);
			else if(key.endsWith(".ttf"))
				value = readFont(key);
			else if(key.endsWith(".txt") || key.endsWith(".js"))
				value = readString(key);
			else if(key.endsWith(".tmx"))
				return XmlResourceLoader.loadMap(this, key);//value = XmlResourceLoader.loadMap(this, key);
			else if(key.endsWith(".spr"))
				value = new SpriteSheet(BitmapFactory.decodeByteArray(data, 0, data.length));
			else if(key.endsWith(".anm")){
				Animation a = new Animation();
				a.load(key, this);
				value = a;
			}
			else
				Log.w("ResourceManager", "Resource requested with unsupported extension: " + key + ".");
			resources.put(key, value);
		}
		return value;
	}

	public void release(Object object){
		String remKey = null;
		for(String key : resources.keySet()){
			Object o = resources.get(key);
			if(o == object){
				remKey = key;
				break;
			}
		}
		if(remKey != null)
			resources.remove(remKey);
	}

	public Bitmap getBitmap(String key){
		return (Bitmap)getObject(key);
	}

	public Animation getAnimation(String key){
		return (Animation)getObject(key);
	}

	public String getString(String key){
		return (String)getObject(key);
	}

	public DialogText getDialogText(String key){
		return (DialogText)getObject(key);
	}

	public void putObject(String key, Object object){
		resources.put(key, object);
	}

	public Bitmap readBitmap(String key){
		InputStream in = openAsset(key);
		Bitmap b = BitmapFactory.decodeStream(in);
		try{
			in.close();
		}catch(IOException e){ e.printStackTrace(); }
		return b;
	}

	public Typeface getFont(String key){
		return (Typeface)getObject(key);
	}

	public Typeface readFont(String key){
		return Typeface.createFromAsset(assets, key);
	}

	public String readString(String key){
		return new String(readAsset(key));
	}

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

	public Document readDocument(String file){
		try{
			return builder.parse(openAsset(file));
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public InputStream openAsset(String name){
		try{
			return assets.open(name);
		}catch(IOException e){
			if(!notFounds.contains(name)){
				if(name.endsWith(".png")){
					Log.w("ResourceManager", "Asset not found Bitmap:(" + name + ")");
					return openAsset("bitmap/undefined.png");
				}
				if(Game.DEBUG)
					e.printStackTrace();
				notFounds.add(name);
			}
		}
		return null;
	}

	private ArrayList<String> notFounds = new ArrayList<String>();
	public byte[] readAsset(String name){
		try{
			InputStream in = openAsset(name);
			byte[] data = new byte[in.available()];
			in.read(data, 0, data.length);
			return data;
		}catch(Exception e){
			if(!notFounds.contains(name)){
				if(Game.DEBUG)
					e.printStackTrace();
				notFounds.add(name);
			}
		}
		return null;
	}
}
