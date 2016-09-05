package net.site40.rodit.tinyrpg.game.effect;

import java.io.IOException;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Scheduler.ScheduledEvent;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;
import net.site40.rodit.util.Util;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.w3c.dom.Element;

public class TimedEffect extends Effect{

	private ScheduledEvent lastEvent;

	protected long delay;
	protected int count;
	protected int run;

	private Function jsEffect;

	public TimedEffect(){
		super();
	}

	@Override
	public void copy(Effect effect){
		super.copy(effect);
		if(effect instanceof TimedEffect){
			TimedEffect te = (TimedEffect)effect;
			this.delay = te.delay;
			this.count = te.count;
			this.run = te.run;
			this.jsEffect = te.jsEffect;
		}
	}

	public long getDelay(){
		return delay;
	}

	public void setDelay(long delay){
		this.delay = delay;
	}

	public int getCount(){
		return count;
	}

	public void setCount(int count){
		this.count = count;
	}

	public int getRun(){
		return run;
	}

	public void setRun(int run){
		this.run = run;
	}

	public void effect(Game game, EntityLiving entity){
		initCallbacks(game);
		if(jsEffect != null)
			game.getScripts().executeFunction(game, jsEffect, this, new String[0], new Object[0], new Object[] { entity });
		if(run <= count)
			run(game, entity);
		else
			stop(game, entity);
	}

	public void registerCallbacks(Object start, Object stop, Object effect){
		super.registerCallbacks(start, stop);
		this.jsEffect = (Function)Context.jsToJava(effect, Function.class);
	}

	public void run(final Game game, final EntityLiving entity){
		lastEvent = game.getScheduler().schedule(new Runnable(){
			public void run(){
				if(run > count){
					game.getScheduler().unschedule(lastEvent);
					TimedEffect.this.stop(game, entity);
					return;
				}
				effect(game, entity);
				run++;
			}
		}, game.getTime() - (run == 0 ? delay : 0), delay);
	}

	@Override
	public void start(Game game, EntityLiving entity){
		super.start(game, entity);
		run(game, entity);
	}
	
	@Override
	public void deserializeXmlElement(Element element){
		super.deserializeXmlElement(element);
		this.delay = Util.tryGetLong(element.getAttribute("delay"), 0);
		this.count = Util.tryGetInt(element.getAttribute("count"), 0);
	}
	
	@Override
	public void load(Game game, TinyInputStream in, EntityLiving ent)throws IOException{
		super.load(game, in, ent);
		this.delay = in.readLong();
		this.count = in.readInt();
		this.run = in.readInt();
		run(game, ent);
	}
	
	@Override
	public void save(TinyOutputStream out)throws IOException{
		super.save(out);
		out.write(delay);
		out.write(count);
		out.write(run);
	}
}
