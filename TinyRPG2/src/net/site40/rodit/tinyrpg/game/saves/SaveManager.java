package net.site40.rodit.tinyrpg.game.saves;

import java.io.File;
import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;

public class SaveManager {

	private ArrayList<SaveGame> saves;
	
	public SaveManager(Game game){
		this.saves = new ArrayList<SaveGame>();
		refresh(game);
	}
	
	public void refresh(Game game){
		File saveDir = new File(game.getContext().getFilesDir(), SaveGame.SAVE_DIR);
		if(!saveDir.exists())
			saveDir.mkdir();
		for(File file : saveDir.listFiles()){
			if(file.getName().endsWith(SaveGame.SAVE_EXT)){
				SaveGame save = new SaveGame(file.getName().substring(0, file.getName().length() - SaveGame.SAVE_EXT.length()));
				save.initialize(game.getContext());
				saves.add(save);
			}
		}
	}
	
	public ArrayList<SaveGame> all(){
		return new ArrayList<SaveGame>(saves);
	}
	
	public SaveGame newSave(Game game, boolean add){
		for(int i = 0; i < Integer.MAX_VALUE; i++){
			String saveName = game.getPlayer().getUsername() + " " + i;
			SaveGame save = new SaveGame(saveName);
			save.initialize(game.getContext());
			if(!save.getFile().exists()){
				if(add)
					saves.add(save);
				return save;
			}
		}
		return null;
	}
	
	public SaveGame getSave(Game game, String saveName){
		for(SaveGame save : saves)
			if(save.getName().equals(saveName))
				return save;
		return null;
	}
}
