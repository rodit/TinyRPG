package net.site40.rodit.tinyrpg.game.audio;

import java.util.ArrayList;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.SparseArray;

public class AudioManager {
	
	private Context context;
	private SparseArray<MediaPlayer> playerCache;
	private volatile ArrayList<Integer> paused;
	
	public AudioManager(Context context){
		this.context = context;
		this.playerCache = new SparseArray<MediaPlayer>();
		this.paused = new ArrayList<Integer>();
	}
	
	public MediaPlayer get(int id){
		MediaPlayer player = playerCache.get(id);
		if(player == null)
			playerCache.put(id, player = MediaPlayer.create(context, id));
		return player;
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
