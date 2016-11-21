package net.site40.rodit.tinyrpg.game.render;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.site40.rodit.rlib.util.CollectionsUtil;
import net.site40.rodit.tinyrpg.game.Game;

public class DialogText {
	
	public static class DialogPart{

		protected LinkedHashMap<String, String> globalReqs;
		protected String text;

		public DialogPart(){
			this.globalReqs = new LinkedHashMap<String, String>();
			this.text = "";
		}
		
		public String getGlobalRequirement(String key){
			return globalReqs.get(key);
		}
		
		public void updateGlobalRequirement(int index, String key, String value){
			removeGlobalRequirement(index);
			setGlobalRequirement(key, value);
		}
		
		public void setGlobalRequirement(int index, String value){
			setGlobalRequirement(CollectionsUtil.getMapKeyByIndex(globalReqs, index), value);
		}
		
		public void setGlobalRequirement(String key, String value){
			globalReqs.put(key, value);
		}
		
		public void removeGlobalRequirement(String name){
			globalReqs.remove(name);
		}
		
		public void removeGlobalRequirement(int index){
			removeGlobalRequirement(CollectionsUtil.getMapKeyByIndex(globalReqs, index));
		}

		public LinkedHashMap<String, String> getGlobalRequirements(){
			return globalReqs;
		}

		public String getText(){
			return text;
		}
		
		public void setText(String text){
			this.text = text;
		}

		public boolean isCorrectDialog(Game game){
			for(String key : globalReqs.keySet()){
				String globalVal = game.getGlobals(key);
				if(!globalVal.equals(globalReqs.get(key)))
					return false;
			}
			return true;
		}
	}

	protected ArrayList<DialogPart> parts;

	public DialogText(InputStream in)throws IOException{
		this.parts = new ArrayList<DialogPart>();

		DialogPart part = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while((line = reader.readLine()) != null){
			String[] tokens = line.split("\\s+");
			if(tokens[0].equals("#")){
				if(part != null)
					parts.add(part);
				part = new DialogPart();
			}else if(tokens[0].equals("global")){
				if(tokens.length < 3)
					continue;
				part.setGlobalRequirement(tokens[1], tokens[2]);
			}else
				part.text += (part.text.length() == 0 ? "" : "~") + line;
		}
		if(!parts.contains(part))
			parts.add(part);
		reader.close();
	}

	public ArrayList<DialogPart> getParts(){
		return parts;
	}

	public String getDialog(Game game){
		for(DialogPart part : parts){
			if(part.isCorrectDialog(game))
				return part.text.replace("\\n", "\n");
		}
		return "";
	}

	public void run(Game game){
		String dialog = getDialog(game);
		String[] parts = dialog.split("~");
		for(String part : parts){
			String[] tokens = part.split("\\s+");
			if(tokens[0].equals("set")){
				if(tokens.length < 3)
					game.getHelper().dialog("The creators of the game messed up and my dialog is pretty darn broken.~Please contact the developers immediately!");
				else
					game.setGlobal(tokens[1], tokens[2]);
				dialog = dialog.replace("~" + part, "").replace(part, "");
			}
		}
		game.getHelper().dialog(dialog);
	}
}
