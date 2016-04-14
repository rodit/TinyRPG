package net.site40.rodit.tinyrpg.game.item.armour;



public class Helmet extends Armour{
	
	public Helmet(){
		this("", "", "", "", "", Rarity.UNKNOWN, 0L, 0f);
	}

	public Helmet(String name, String showName, String description, String script, String resource, Rarity rarity, long value, float armourValue){
		super(name, showName, description, script, resource, rarity, value, SLOT_HELMET, armourValue);
	}
}
