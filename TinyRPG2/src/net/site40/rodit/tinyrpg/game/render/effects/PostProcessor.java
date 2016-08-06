package net.site40.rodit.tinyrpg.game.render.effects;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import android.graphics.Canvas;

public class PostProcessor {

	private ArrayList<PPEffect> addQueue;
	private ArrayList<PPEffect> removeQueue;
	private ArrayList<PPEffect> effects;

	public PostProcessor(){
		this.addQueue = new ArrayList<PPEffect>();
		this.removeQueue = new ArrayList<PPEffect>();
		this.effects = new ArrayList<PPEffect>();
	}

	public void add(PPEffect effect){
		synchronized(addQueue){
			if(!addQueue.contains(effect))
				addQueue.add(effect);
		}
	}

	public void remove(PPEffect effect){
		synchronized(removeQueue){
			removeQueue.add(effect);
		}
	}

	public void update(Game game){
		synchronized(effects){
			synchronized(addQueue){
				for(PPEffect effect : addQueue)
					if(!effects.contains(effect))
						effects.add(effect);
				addQueue.clear();
			}
			synchronized(removeQueue){
				effects.removeAll(removeQueue);
				removeQueue.clear();
			}
		}
		for(PPEffect effect : effects)
			effect.update(game);
	}

	public void preDraw(Game game, Canvas canvas){
		for(PPEffect effect : effects)
			effect.preDraw(game, canvas);
	}

	public void draw(Game game, Canvas canvas){
		for(PPEffect effect : effects)
			effect.draw(game, canvas);
	}

	public void postDraw(Game game, Canvas canvas){
		for(PPEffect effect : effects)
			effect.postDraw(game, canvas);
	}
}
