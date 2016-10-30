package net.site40.rodit.tinyrpg.game.quest;

import net.site40.rodit.tinyrpg.game.entity.EntityPlayer;
import net.site40.rodit.tinyrpg.game.item.Inventory;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.util.Util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.text.TextUtils;

public class Quest {
	
	public static enum QuestImportance{
		STORY, OPTIONAL;
	}

	private String name;
	private String showName;
	private String description;
	private QuestImportance importance;
	private int stages;
	private int rewardXp;
	private long rewardGold;
	private Inventory rewardItems;

	public Quest(){
		this("", "", "", QuestImportance.OPTIONAL, 0, 0, 0);
	}

	public Quest(String name, String showName, String description, QuestImportance importance, int stages, int rewardXp, long rewardGold){
		this(name, showName, description, QuestImportance.OPTIONAL, stages, rewardXp, rewardGold, new Inventory());
	}

	public Quest(String name, String showName, String description, QuestImportance importance, int stages, int rewardXp, long rewardGold, Inventory rewardItems){
		this.name = name;
		this.showName = showName;
		this.description = description;
		this.stages = stages;
		this.rewardXp = rewardXp;
		this.rewardGold = rewardGold;
		this.rewardItems = rewardItems;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public String getDescription(){
		return description;
	}

	public void setDescription(String description){
		this.description = description;
	}
	
	public QuestImportance getImportance(){
		return importance;
	}
	
	public void setImportance(QuestImportance importance){
		this.importance = importance;
	}

	public int getStages(){
		return stages;
	}

	public void setStages(int stages){
		this.stages = stages;
	}

	public int getRewardXp() {
		return rewardXp;
	}

	public void setRewardXp(int rewardXp) {
		this.rewardXp = rewardXp;
	}

	public long getRewardGold() {
		return rewardGold;
	}

	public void setRewardGold(long rewardGold) {
		this.rewardGold = rewardGold;
	}

	public Inventory getRewardItems() {
		return rewardItems;
	}

	public void setRewardItems(Inventory rewardItems) {
		this.rewardItems = rewardItems;
	}
	
	public void reward(EntityPlayer player){
		player.getStats().addXp(rewardXp);
		player.addMoney(rewardGold);
		player.getInventory().add(rewardItems);
	}

	public void deserializeXmlElement(Element e){
		name = e.getAttribute("name");
		showName = e.getAttribute("showName");
		description = e.getAttribute("description");
		importance = Util.tryGetQuestImportance(e.getAttribute("importance"));
		String sStages = e.getAttribute("stages");
		if(!TextUtils.isEmpty(sStages) && TextUtils.isDigitsOnly(sStages))
			stages = Integer.valueOf(sStages);
		NodeList rewards = e.getElementsByTagName("reward");
		for(int i = 0; i < rewards.getLength(); i++){
			Node n = rewards.item(i);
			if(n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element re = (Element)n;
			String type = re.getAttribute("type");
			String sAmount = re.getAttribute("amount");
			int amount = 0;
			long amountGold = 0;
			if(!TextUtils.isEmpty(sAmount) && TextUtils.isDigitsOnly(sAmount)){
				amount = Integer.valueOf(sAmount);
				amountGold = Long.valueOf(sAmount);
			}else if(type.equals("item"))
				amount = 1;
			if(type.equals("xp"))
				rewardXp = amount;
			else if(type.equals("gold"))
				rewardGold = amountGold;
			else if(type.equals("item")){
				String itemName = re.getAttribute("name");
				Item item = Item.get(itemName);
				if(item == null)
					continue;
				rewardItems.add(item, amount);
			}
		}
	}

	@Override
	public int hashCode(){
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj){
		return obj != null && obj instanceof Quest && obj.hashCode() == hashCode();
	}
}
