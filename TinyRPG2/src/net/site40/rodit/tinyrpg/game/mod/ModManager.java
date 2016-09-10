package net.site40.rodit.tinyrpg.game.mod;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;

public class ModManager {
	
	public static final String MOD_EXT = ".zip";
	
	private volatile boolean updated = false;

	public ModManager(){
		
	}
	
	public void enableMod(Game game, TinyMod mod){
		game.setGlobal("mod_enabled_" + mod.info.name, "true");
		try{
			game.getSaves().saveGlobals(game);
		}catch(IOException e){ e.printStackTrace(); }
		updated();
	}
	
	public void disableMod(Game game, TinyMod mod){
		game.setGlobal("mod_enabled_" + mod.info.name, "false");
		try{
			game.getSaves().saveGlobals(game);
		}catch(IOException e){ e.printStackTrace(); }
		updated();
	}
	
	public boolean modListUpdated(){
		return updated;
	}
	
	public void handledUpdate(){
		updated = false;
	}
	
	public void updated(){
		updated = true;
	}
	
	public boolean isModEnabled(Game game, TinyMod mod){
		return game.getGlobalb("mod_enabled_" + mod.info.name);
	}
	
	public ArrayList<TinyMod> listMods(Game game){
		ArrayList<TinyMod> mods = new ArrayList<TinyMod>();
		File modDir = new File(game.getContext().getFilesDir(), TinyMod.MOD_DIR);
		if(!modDir.exists())
			modDir.mkdir();
		for(File f : modDir.listFiles()){
			if(f.isFile() && f.getName().endsWith(MOD_EXT))
				mods.add(new TinyMod(f.getName()));
		}
		return mods;
	}
}
