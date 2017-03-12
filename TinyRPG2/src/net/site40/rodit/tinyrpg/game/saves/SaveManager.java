package net.site40.rodit.tinyrpg.game.saves;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.site40.rodit.rlib.util.io.StreamUtils;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.map.MapState;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;

public class SaveManager {

	public static final String SAVE_DIR = "save";

	private File root;

	private String lastSlot;
	private SaveSlot currentSlot;

	public SaveManager(Game game){
		this.root = new File(game.getContext().getFilesDir(), SAVE_DIR);
		checkDirs();
		checkLastSlot();
	}

	public boolean isSaveLoaded(){
		return currentSlot != null;
	}
	
	public ArrayList<SaveSlot> getSlots(){
		ArrayList<SaveSlot> slots = new ArrayList<SaveSlot>();
		for(File file : root.listFiles()){
			if(file.isDirectory()){
				SaveSlot slot = new SaveSlot(file);
				if(slot.loadInfo())
					slots.add(slot);
			}
		}
		return slots;
	}

	public SaveSlot getCurrentSlot(){
		return currentSlot;
	}
	
	public void setCurrentSlot(SaveSlot currentSlot){
		setLastSlot(currentSlot.getRoot().getName());
		this.currentSlot = currentSlot;
	}

	protected void checkDirs(){
		if(!root.exists())
			root.mkdir();
	}

	protected void checkLastSlot(){
		File lastSlotFile = getSaveFile(".last_slot");
		if(lastSlotFile.exists()){
			try{
				lastSlot = StreamUtils.readFileAsString(lastSlotFile);
				File file = getSaveFile(lastSlot);
				if(!file.isDirectory())
					throw new IOException("Could not find last slot directory.");
			}catch(IOException e){
				lastSlot = null;
			}
		}else
			lastSlot = null;
	}
	
	public String getLastSlot(){
		checkLastSlot();
		return lastSlot;
	}
	
	public void setLastSlot(String slotName){
		File lastSlotFile = getSaveFile(".last_slot");
		try{
			StreamUtils.write(lastSlotFile, slotName.getBytes());
			checkLastSlot();
		}catch(IOException e){}
	}
	
	public void loadSlot(String slotName){
		File slot = getSaveFile(slotName);
		if(slot.isDirectory()){
			setLastSlot(slotName);
			currentSlot = new SaveSlot(slot);
		}
	}
	
	public void createSlot(String name){
		getSaveFile(name).mkdir();
	}

	public boolean canContinue(){
		checkLastSlot();
		return lastSlot != null && lastSlot.length() > 0;
	}

	public File getSaveFile(String name){
		return new File(root, name);
	}
	
	public int getSlotCount(){
		int count = 0;
		for(File file : root.listFiles())
			if(file.isDirectory())
				count++;
		return count;
	}

	public void load(Game game)throws IOException{
		if(currentSlot == null)
			throw new IOException("No save slot is loaded.");
		currentSlot.load(game);
	}

	public void loadOptions(Game game)throws IOException{
		File optionsFile = getSaveFile("options.dat");
		if(!optionsFile.exists()){
			game.getOptions().initDefaults();
			saveOptions(game);
		}
		FileInputStream fin = new FileInputStream(optionsFile);
		TinyInputStream tin = new TinyInputStream(fin);
		int count = tin.readInt();
		for(int i = 0; i < count; i++)
			game.getOptions().put(tin.readString(), tin.readString());
		tin.close();
	}

	public void save(Game game)throws IOException{
		if(currentSlot == null)
			throw new IOException("No save slot is loaded.");
		currentSlot.save(game);
	}

	public void saveOptions(Game game)throws IOException{
		File optionsFile = getSaveFile("options.dat");
		FileOutputStream fout = new FileOutputStream(optionsFile);
		TinyOutputStream tout = new TinyOutputStream(fout);
		int size = game.getOptions().getMap().size();
		tout.write(size);
		for(String key : game.getOptions().getMap().keySet()){
			tout.writeString(key);
			tout.writeString(game.getOptions().get(key));
		}
		tout.close();
	}

	public MapState loadMap(Game game, String map)throws IOException{
		if(currentSlot == null)
			throw new IOException("No save slot is loaded.");
		return currentSlot.loadMap(game, map);
	}

	public void saveMap(Game game, MapState state)throws IOException{
		if(currentSlot == null)
			throw new IOException("No save slot is loaded.");
		currentSlot.saveMap(game, state);
	}
}
