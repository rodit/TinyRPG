package net.site40.rodit.tinyrpg.game.saves;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.gui.GuiIngame;
import net.site40.rodit.tinyrpg.game.gui.GuiMenu;
import net.site40.rodit.tinyrpg.game.gui.GuiSaves;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;
import android.annotation.SuppressLint;
import android.content.Context;

public class SaveGame {
	
	public static final String SAVE_DIR = "saves";
	public static final String SAVE_EXT = ".sav";
	
	private String name;
	private File file;
	
	public SaveGame(String name){
		this.name = name;
	}
	
	public void initialize(Context context){
		this.file = new File(getSavesDir(context), name + SAVE_EXT);
	}
	
	public File getSavesDir(Context context){
		return new File(context.getFilesDir(), SAVE_DIR);
	}
	
	public File getFile(){
		return file;
	}
	
	public void load(Game game)throws IOException{
		game.deserialize(new TinyInputStream(new FileInputStream(getFile().getAbsolutePath())));
		game.getGuis().hide(GuiSaves.class);
		game.getGuis().hide(GuiMenu.class);
		game.getGuis().show(GuiIngame.class);
//		WindowIngame wing = new WindowIngame(game);
//		game.getWindows().register(wing);
//		wing.show();
	}
	
	public void save(Game game)throws IOException{
		game.serialize(new TinyOutputStream(new FileOutputStream(getFile().getAbsolutePath())));
	}
	
	public String getName(){
		return name;
	}
	
	public long getTime(){
		return file.isFile() ? file.lastModified() : 0L;
	}
	
	private String humanReadableTime;
	@SuppressLint("SimpleDateFormat")
	public String getHumanReadableTime(){
		return humanReadableTime == null ? humanReadableTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(getTime()) : humanReadableTime;
	}
}
