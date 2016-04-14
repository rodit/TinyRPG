package net.site40.rodit.tinyrpg.game.item.armour;


public class ShoulderPlate extends Armour{
	
	public ShoulderPlate(){
		this("", "", "", "", "", Rarity.UNKNOWN, 0L, 0f);
	}

	public ShoulderPlate(String name, String showName, String description, String script, String resource, Rarity rarity, long value, float armourValue){
		super(name, showName, description, script, resource, rarity, value, SLOT_SHOULDERS, armourValue);
	}
}
