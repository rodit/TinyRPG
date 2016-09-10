package net.site40.rodit.tinyrpg.game.mod;

import java.io.File;
import java.io.IOException;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.util.Util;
import net.site40.rodit.util.ZipUtil;

import org.xml.sax.SAXException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class TinyMod {

	public static final String MOD_DIR = "mods";
	public static final String MOD_INFO_FILE = "mod.xml";

	protected String file;
	protected ModInfo info;
	private ZipUtil modFile;
	protected Bitmap icon;
	private boolean loaded = false;

	public TinyMod(String file){
		this.file = file;
	}
	
	public String getFile(){
		return file;
	}
	
	public ModInfo getInfo(){
		return info;
	}
	
	public Bitmap getIcon(){
		return icon;
	}
	
	public boolean isInfoLoaded(){
		return info != null;
	}
	
	public void loadInfo(Game game)throws IOException, SAXException{
		if(isInfoLoaded())
			return;
		this.modFile = new ZipUtil(game.getContext(), MOD_DIR + "/" + file);
		this.info = new ModInfo(new String(modFile.readFile(MOD_INFO_FILE)));
		try{
			byte[] data = modFile.readFile("icon.png");
			if(data == null || data.length == 0)
				throw new IOException("Invalid mod icon length (null or zero).");
			icon = BitmapFactory.decodeByteArray(data, 0, data.length);
		}catch(IOException e){
			icon = game.getResources().getBitmap("mod/default_icon.png");
		}
	}

	public void load(Game game)throws IOException, SAXException{
		if(loaded)
			return;
		if(!isInfoLoaded())
			loadInfo(game);
		if(info == null)
			throw new IOException("Invalid mod info for mod " + file + ".");
		if(info.name.contains("/"))
			throw new IOException("Mod name cannot contain '/'.");
		File scriptDir = new File(game.getContext().getCacheDir(), "script_cache/" + info.name);
		if(scriptDir.exists())
			Util.deleteDir(scriptDir);
		scriptDir.mkdirs();
		executeScriptByName(game, info.modClass);
		loaded = true;
	}

	public void executeScriptByName(Game game, String name){
		try{
			executeScript(game, new String(modFile.readFile(name)), name);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void executeScript(Game game, String scriptContents, String id){
		File tempScriptFile = new File(game.getContext().getCacheDir(), "script_cache/" + info.name + "/" + id + ".js");
		if(!tempScriptFile.exists()){
			tempScriptFile.getParentFile().mkdirs();
			Util.writeFile(tempScriptFile, scriptContents.getBytes());
		}
		game.getScripts().execute(game, info.name + "/" + id + ".js", new String[] { "mod" }, new Object[] { this }, true);
	}
}