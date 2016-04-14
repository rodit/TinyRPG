package net.site40.rodit.tinyrpg.game.entity.npc;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;

public class EntityAI extends EntityLiving{
	
	protected ArrayList<AITask> tasks;
	
	public EntityAI(){
		super();
		this.tasks = new ArrayList<AITask>();
	}
	
	@Override
	public void update(Game game){
		super.update(game);
		
		ArrayList<AITask> doneTasks = new ArrayList<AITask>();
		for(AITask task : tasks){
			task.update(game);
			if(task.isDone(game))
				doneTasks.add(task);
		}
		tasks.removeAll(doneTasks);
	}
}
