package net.site40.rodit.tinyrpg.game.entity.npc;

public class EntityNPC extends EntityAI{
	
	protected String displayName;
	
	public EntityNPC(){
		this("New Entity");
	}
	
	public EntityNPC(String displayName){
		super();
		this.displayName = displayName;
	}
}
