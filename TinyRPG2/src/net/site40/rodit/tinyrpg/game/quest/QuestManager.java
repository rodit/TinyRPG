package net.site40.rodit.tinyrpg.game.quest;

import java.util.ArrayList;
import java.util.HashMap;

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
}
