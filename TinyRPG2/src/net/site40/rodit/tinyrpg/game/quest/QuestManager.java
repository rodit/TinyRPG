package net.site40.rodit.tinyrpg.game.quest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;

public class QuestManager {
	
	private ArrayList<Quest> quests;
	private HashMap<Quest, Integer> stages;
	
	public QuestManager(){
		this.quests = new ArrayList<Quest>();
		this.stages = new HashMap<Quest, Integer>();
	}
	
	public void addQuest(Quest quest){
		if(!quests.contains(quest))
			quests.add(quest);
	}
	
	public void removeQuest(Quest quest){
		quests.remove(quest);
	}
	
	public Quest get(String name){
		for(Quest quest : quests)
			if(quest.getName().equals(name))
				return quest;
		return null;
	}
	
	public int getStage(String quest){
		return getStage(get(quest));
	}
	
	public int getStage(Quest quest){
		Integer stage = stages.get(quest);
		if(stage == null)
			stage = -1;
		return stage;
	}
	
	public void setStage(String quest, int stage){
		setStage(get(quest), stage);
	}
	
	public void setStage(Quest quest, int stage){
		stages.put(quest, stage);
	}
	
	public boolean hasQuest(String quest){
		return hasQuest(get(quest));
	}
	
	public boolean hasQuest(Quest quest){
		return getStage(quest) > -1;
	}
	
	public void load(QuestManager questMan, TinyInputStream in)throws IOException{
		int count = in.readInt();
		for(int i = 0; i < count; i++){
			Quest quest = questMan.get(in.readString());
			int stage = in.readInt();
			stages.put(quest, stage);
		}
	}
	
	public void save(TinyOutputStream out)throws IOException{
		out.write(stages.size());
		for(Quest key : stages.keySet()){
			out.writeString(key.getName());
			out.write(stages.get(key));
		}
	}
}
