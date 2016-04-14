package net.site40.rodit.tinyrpg.game.item.armour;




public class Shield extends Armour{
	
	public Shield(){
		this("", "", "", "", "", Rarity.UNKNOWN, 0L, 0f);
	}

	public Shield(String name, String showName, String description, String script, String resource, Rarity rarity, long value, float armourValue){
		super(name, showName, description, script, resource, rarity, value, SLOT_HAND_1, armourValue);
	}
}
