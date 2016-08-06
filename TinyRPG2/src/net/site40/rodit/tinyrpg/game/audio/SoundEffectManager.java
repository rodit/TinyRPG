package net.site40.rodit.tinyrpg.game.audio;

import java.io.IOException;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

public class SoundEffectManager {

	private SoundPool pool;
	private HashMap<String, Integer> ids;
	private AssetManager assets;
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public SoundEffectManager(AssetManager assets){
		this.assets = assets;
		
		if(Build.VERSION.SDK_INT >= 21){
			AudioAttributes attrs = new AudioAttributes.Builder()
			.setUsage(AudioAttributes.USAGE_GAME)
			.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
			.build();
			pool = new SoundPool.Builder()
			.setMaxStreams(100)
			.setAudioAttributes(attrs)
			.build();
		}else
			this.pool = new SoundPool(100, AudioManager.STREAM_MUSIC, 0);

		precache();
	}

	protected void precache(){
		this.ids = new HashMap<String, Integer>();
		precache("sound/menu/menu_confirm.ogg");
		precache("sound/menu/menu_select.ogg");
		
		precache("sound/weather/lightning.ogg");
		precache("sound/weather/rain.ogg");
	}

	protected void precache(String file){
		try{
			ids.put(file, pool.load(assets.openFd(file), 1));
		}catch(IOException e){
			Log.e("SoundEffects", "Error while loading sound effect '" + file + "'.");
			e.printStackTrace();
		}
	}
	
	public int play(String soundFile){
		return play(soundFile, false);
	}

	public int play(String soundFile, boolean repeat){
		int id = ids.get(soundFile);
		return pool.play(id, 1f, 1f, 1, repeat ? -1 : 0, 1f);
	}
	
	public void stop(int id){
		pool.stop(id);
	}
}
