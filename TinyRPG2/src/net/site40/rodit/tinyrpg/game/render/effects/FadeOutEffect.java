package net.site40.rodit.tinyrpg.game.render.effects;

import net.site40.rodit.tinyrpg.game.Game;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;

public class FadeOutEffect extends PPEffect implements IChainableEffect{
	
	private long time;
	private int color;
	
	private long startTime;
	
	public FadeOutEffect(){
		this(1000L);
	}
	
	public FadeOutEffect(long time){
		this(time, Color.BLACK);
	}
	
	public FadeOutEffect(long time, int color){
		super();
		this.time = time;
		this.color = color;
		this.startTime = 0L;
		
		paint.setStyle(Style.FILL);
	}
	
	private int getCurrentAlpha(Game game){
		long diff = game.getTime() - startTime;
		double multi = (double)diff / (double)time;
		return (int)(255d * multi);
	}

	public void update(Game game){
		if(startTime == 0L)
			startTime = game.getTime();
		
		if(game.getTime() - startTime >= time)
			game.getPostProcessor().remove(this);
	}

	public void preDraw(Game game, Canvas canvas){}

	public void draw(Game game, Canvas canvas){}
	
	public void postDraw(Game game, Canvas canvas){
		paint.setColor(color);
		paint.setAlpha(getCurrentAlpha(game));
		bounds.set(0, 0, 1280, 720);
		canvas.drawRect(bounds.get(), paint);
	}
	
	public boolean isComplete(Game game){
		return game.getTime() - startTime >= time;
	}

	public PPEffect getEffect(){
		return this;
	}
}
