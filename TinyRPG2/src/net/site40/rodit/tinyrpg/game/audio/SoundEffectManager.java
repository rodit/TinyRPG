package net.site40.rodit.tinyrpg.game.audio;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import net.site40.rodit.rlib.util.ExtendedRandom;
import net.site40.rodit.tinyrpg.game.render.ResourceManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

public class SoundEffectManager {

	private static final int STREAMS = 8;
	
	private SoundPool pool;
	private HashMap<String, SoundEffectGroup> groups;
	private ResourceManager resources;
	
	protected SoundEffectManager(){}
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public SoundEffectManager(ResourceManager resources){
		this.resources = resources;
		
		if(Build.VERSION.SDK_INT >= 21){
			AudioAttributes attrs = new AudioAttributes.Builder()
			.setUsage(AudioAttributes.USAGE_GAME)
			.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
			.build();
			pool = new SoundPool.Builder()
			.setMaxStreams(STREAMS)
			.setAudioAttributes(attrs)
			.build();
		}else
			this.pool = new SoundPool(STREAMS, AudioManager.STREAM_MUSIC, 0);
	}

	public void buildCache(){
		this.groups = new HashMap<String, SoundEffectGroup>();
		
		Document document = resources.readDocument("sound/sounds.xml");
		NodeList groups = document.getElementsByTagName("group");
		int totalSounds = 0;
		for(int i = 0; i < groups.getLength(); i++){
			Node n = groups.item(i);
			if(n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element e = (Element)n;
			String name = e.getAttribute("name");
			String base = e.getAttribute("base");
			SoundEffectGroup group = new SoundEffectGroup(name, base);
			String groupLoaded = resources.readString(base + "group.txt");
			for(String file : groupLoaded.split(";")){
				file = file.replace(";", "").trim();
				group.putSound(file, load(base + file));
				Log.d("SoundEffectManager", "Loaded sound " + file + " from group " + name + " with base " + base + ".");
				totalSounds++;
			}
			this.groups.put(name, group);
		}
		Log.d("SoundEffectManager", "Loaded " + totalSounds + " sound effects from " + groups.getLength() + " groups.");
	}
	
	protected int load(String file){
		try{
			return pool.load(resources.getAssetFileDescriptor(file), 1);
		}catch(IOException e){
			Log.e("SoundEffects", "Error while loading sound effect '" + file + "'.");
			e.printStackTrace();
		}
		return -1;
	}
	
	protected int play(int id, boolean repeat){
		return pool.play(id, 1f, 1f, 1, repeat ? -1 : 0, 1f);
	}
	
	public int play(String group, String sound){
		return play(group, sound, false);
	}
	
	public int play(String group, String sound, boolean repeat){
		SoundEffectGroup segroup = groups.get(group);
		if(segroup == null){
			Log.e("SoundEffects", "Sound effect group not found: " + group + ".");
			return -1;
		}
		return play(segroup.getId(sound), repeat);
	}
	
	public int playRandom(String group){
		return playRandom(group, false);
	}
	
	public int playRandom(String group, boolean repeat){
		SoundEffectGroup segroup = groups.get(group);
		if(segroup == null){
			Log.e("SoundEffects", "Sound effect group not found: " + group + ".");
			return -1;
		}
		return play(segroup.getRandom(), repeat);
	}
	
	public boolean hasGroup(String name){
		return groups.containsKey(name);
	}
	
	public SoundEffectGroup getGroup(String name){
		return groups.get(name);
	}
	
	public void stop(int id){
		pool.stop(id);
	}
	
	public void dispose(){
		pool.release();
	}
	
	public static class SoundEffectGroup{
		
		static final ExtendedRandom random = new ExtendedRandom();
		
		private String name;
		private String baseDir;
		private LinkedHashMap<String, Integer> sounds;
		
		public SoundEffectGroup(String name, String baseDir){
			this.name = name;
			this.baseDir = baseDir;
			this.sounds = new LinkedHashMap<String, Integer>();
		}
		
		public String getName(){
			return name;
		}
		
		public String getBaseDir(){
			return baseDir;
		}
		
		public int getRandom(){
			if(sounds.size() < 1)
				return -1;
			int index = sounds.size() > 1 ? random.nextInt(1, sounds.size()) - 1 : 0;
			int i = 0;
			for(Integer j : sounds.values())
				if(i++ == index)
					return j;
			return -1;
		}
		
		public int getId(String name){
			return sounds.get(name);
		}
		
		protected void putSound(String name, int id){
			sounds.put(name, id);
		}
	}
}
