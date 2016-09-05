package net.site40.rodit.tinyrpg.game.saves;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.map.MapState;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;
import android.content.Context;

public class SaveManager {
	
	public static final String SAVE_DIR = "save";
	public static final String SAVE_EXT = ".dat";
	public static final String MAIN_SAVE = "game";
	
	public SaveManager(Game game){
		checkDirs(game);
	}
	
	protected void checkDirs(Game game){
		File saveDir = new File(game.getContext().getFilesDir(), SAVE_DIR);
		if(!saveDir.exists())
			saveDir.mkdir();
		File mapSaveDir = getSaveFile(game.getContext(), "map");
		if(!mapSaveDir.exists())
			mapSaveDir.mkdir();
	}
	
	public boolean canContinue(Game game){
		return getSaveFile(game.getContext(), MAIN_SAVE + SAVE_EXT).exists();
	}
	
	public File getSaveFile(Context context, String name){
		return new File(context.getFilesDir(), SAVE_DIR + "/" + name);
	}
	
	public void load(Game game)throws IOException{
		File mainFile = getSaveFile(game.getContext(), MAIN_SAVE + SAVE_EXT);
		FileInputStream fin = new FileInputStream(mainFile);
		TinyInputStream tin = new TinyInputStream(fin);
		game.load(tin);
		tin.close();
	}
	
	public void save(Game game)throws IOException{
		checkDirs(game);
		File mainFile = getSaveFile(game.getContext(), MAIN_SAVE + SAVE_EXT);
		FileOutputStream fout = new FileOutputStream(mainFile);
		TinyOutputStream tout = new TinyOutputStream(fout);
		game.save(tout);
		tout.close();
	}
	
	public MapState loadMap(Game game, String map)throws IOException{
		MapState state = new MapState(null);
		File saveFile = getSaveFile(game.getContext(), map + SAVE_EXT);
		if(saveFile.exists()){
			FileInputStream fin = new FileInputStream(saveFile);
			TinyInputStream tin = new TinyInputStream(fin);
			state.load(game, tin);
			tin.close();
			return state;
		}else
			return null;
	}
	
	public void saveMap(Game game, MapState state)throws IOException{
		File saveFile = getSaveFile(game.getContext(), state.getMap().getFile() + SAVE_EXT);
		FileOutputStream fout = new FileOutputStream(saveFile);
		TinyOutputStream tout = new TinyOutputStream(fout);
		state.save(game, tout);
		tout.close();
	}
}
