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

	public boolean isCompleted(String quest){
		return isCompleted(get(quest));
	}

	public boolean isCompleted(Quest quest){
		return getStage(quest) == quest.getStages();
	}

	public boolean isTurnedIn(String quest){
		return isTurnedIn(get(quest));
	}

	public boolean isTurnedIn(Quest quest){
		return getStage(quest) == quest.getStages() + 1;
	}

	public void turnIn(String quest){
		turnIn(get(quest));
	}

	public void turnIn(Quest quest){
		setStage(quest, quest.getStages() + 1);
	}
	
	public ArrayList<Quest> getAccepted(){
		ArrayList<Quest> accepted = new ArrayList<Quest>();
		getAccepted(accepted);
		return accepted;
	}

	public ArrayList<Quest> getAccepted(ArrayList<Quest> accepted){
		for(Quest quest : quests)
			if(hasQuest(quest) && !isTurnedIn(quest))
				accepted.add(quest);
		return accepted;
	}

	public ArrayList<Quest> getTurnedIn(){
		ArrayList<Quest> turnedIn = new ArrayList<Quest>();
		for(Quest quest : quests)
			if(isTurnedIn(quest))
				turnedIn.add(quest);
		return turnedIn;
	}

	public void load(TinyInputStream in)throws IOException{
		int count = in.readInt();
		for(int i = 0; i < count; i++){
			Quest quest = get(in.readString());
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
