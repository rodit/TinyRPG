package net.site40.rodit.tinyrpg.game.audio;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.render.ResourceManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.SparseArray;

public class AudioManager {
	
	private Context context;
	private SparseArray<MediaPlayer> playerCache;
	private volatile ArrayList<Integer> paused;
	private SoundEffectManager soundEffects;
	
	public AudioManager(Context context, ResourceManager resources){
		this.context = context;
		this.playerCache = new SparseArray<MediaPlayer>();
		this.paused = new ArrayList<Integer>();
		this.soundEffects = Game.USE_OPENAL ? new ALManager(resources) : new SoundEffectManager(resources);
	}
	
	public MediaPlayer get(int id){
		MediaPlayer player = playerCache.get(id);
		if(player == null)
			playerCache.put(id, player = MediaPlayer.create(context, id));
		return player;
	}
	
	public SoundEffectManager getEffects(){
		return soundEffects;
	}
	
	public void pauseAll(){
		for(int i = 0; i < playerCache.size(); i++){
			int key = playerCache.keyAt(i);
			MediaPlayer player = get(key);
			if(player.isPlaying()){
				player.pause();
				paused.add(key);
			}
		}
	}
	
	public void unpauseAll(){
		for(int i = 0; i < paused.size(); i++)
			get(paused.get(i)).start();
		paused.clear();
	}
}
