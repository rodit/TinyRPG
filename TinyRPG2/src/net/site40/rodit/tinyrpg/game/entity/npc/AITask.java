package net.site40.rodit.tinyrpg.game.entity.npc;

import net.site40.rodit.tinyrpg.game.Game;

public abstract class AITask {

	protected EntityAI owner;
	
	public AITask(EntityAI owner){
		this.owner = owner;
	}
	
	public EntityAI getOwner(){
		return owner;
	}
	
	public void setOwner(EntityAI owner){
		this.owner = owner;
	}
	
	public abstract void update(Game game);
	public abstract boolean isDone(Game game);
}
