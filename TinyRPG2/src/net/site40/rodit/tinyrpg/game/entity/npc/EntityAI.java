package net.site40.rodit.tinyrpg.game.entity.npc;

import java.io.IOException;
import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;

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
	
	@Override
	public void load(Game game, TinyInputStream in)throws IOException{
		super.load(game, in);
		this.tasks = new ArrayList<AITask>();
		int taskCount = in.readInt();
		int taskRead = 0;
		while(taskRead < taskCount){
			String taskCls = in.readString();
			try{
				Class<? extends AITask> cls = (Class<? extends AITask>)Class.forName(taskCls);
				AITask task = cls.newInstance();
				task.setOwner(this);
				tasks.add(task);
			}catch(Exception e){
				e.printStackTrace();
			}
			taskRead++;
		}
	}
	
	@Override
	public void save(TinyOutputStream out)throws IOException{
		super.save(out);
		out.write(tasks.size());
		for(AITask task : tasks)
			out.writeString(task.getClass().getCanonicalName());
	}
}
