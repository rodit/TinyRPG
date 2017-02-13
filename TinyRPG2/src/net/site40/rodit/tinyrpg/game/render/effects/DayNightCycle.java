package net.site40.rodit.tinyrpg.game.render.effects;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.object.GameObject;
import android.graphics.Canvas;

public class DayNightCycle extends GameObject{

	private static final long DAY_LENGTH_MS = 1000 * 60 * 25;
	private static final long NIGHT_LENGTH_MS = 1000 * 60 * 10;
	private static final long TRANSITION_LENGTH_MS = 1000 * 30;

	public static enum TIME{
		DAY, NIGHT, TRANSITION;
	}

	private long startTime;
	private TIME time;
	private TIME lastTime;

	public DayNightCycle(Game game){
		startTime = game.getTime();
		time = TIME.DAY;
		lastTime = TIME.DAY;
	}

	public TIME getTime(){
		return time;
	}

	public void setTime(TIME time, Game game){
		this.time = time;
		this.startTime = game.getTime();
	}

	public void update(Game game){
		long diff = game.getTime() - startTime;
		switch(time){
		case DAY:
			if(diff >= DAY_LENGTH_MS){
				lastTime = time;
				time = TIME.TRANSITION;
				startTime = game.getTime();
			}
			break;
		case NIGHT:
			if(diff >= NIGHT_LENGTH_MS){
				lastTime = time;
				time = TIME.TRANSITION;
				startTime = game.getTime();
			}
			break;
		case TRANSITION:
			if(diff >= TRANSITION_LENGTH_MS){
				TIME tempTime = lastTime;
				lastTime = time;
				time = tempTime == TIME.DAY ? TIME.NIGHT : TIME.DAY;
				startTime = game.getTime();
			}else{
				float transitionFactor = ((float)game.getTime() - (float)startTime) / (float)TRANSITION_LENGTH_MS;
				if(lastTime == TIME.NIGHT)
					transitionFactor = 1f - transitionFactor;
				//TODO: Make this compatible with map's static light map or remove the whole thing...
				//game.getLighting().setDarkness(transitionFactor);
			}
			break;
		}
	}

	public void draw(Game game, Canvas canvas){}
	public int getRenderLayer(){ return RenderLayer.BOTTOM; }
	public boolean shouldScale(){ return false; }
}
