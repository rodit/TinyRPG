package net.site40.rodit.tinyrpg.game.render;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;

public class DialogText {

	public static class DialogPart{

		protected ArrayList<String> globalNames;
		protected ArrayList<String> globalValues;
		protected String text;

		public DialogPart(){
			this.globalNames = new ArrayList<String>();
			this.globalValues = new ArrayList<String>();
			this.text = "";
		}

		public boolean isCorrectDialog(Game game){
			for(int i = 0; i < globalNames.size() && i < globalValues.size(); i++){
				String var = globalNames.get(i);
				String val = globalValues.get(i);
				String globalVal = game.getGlobals(var);
				if(!globalVal.equals(val))
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
				part.globalNames.add(tokens[1]);
				part.globalValues.add(tokens[2]);
			}else
				part.text += (part.text.length() == 0 ? "" : "~") + line;
		}
		if(!parts.contains(part))
			parts.add(part);
		reader.close();
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
				if(parts.length < 3)
					game.getHelper().dialog("The creators of the game messed up and my dialog is pretty darn broken.~Please contact the developers immediately!");
				else
					game.setGlobal(tokens[1], tokens[2]);
				dialog = dialog.replace("~" + part, "").replace(part, "");
			}
		}
		game.getHelper().dialog(dialog);
	}
}
