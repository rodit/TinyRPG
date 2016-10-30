package net.site40.rodit.tinyrpg.game.gui.windows;

import net.site40.rodit.tinyrpg.game.Game;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;

public class WindowSliderComponent extends WindowComponent{
	
	private float min;
	private float max;
	private float value;

	public WindowSliderComponent(){
		super();
	}
	
	public WindowSliderComponent(String name){
		super(name);
		this.min = max = value = 0f;
	}
	
	public float getMin(){
		return min;
	}
	
	public void setMin(float min){
		this.min = min;
	}
	
	public float getMax(){
		return max;
	}
	
	public void setMax(float max){
		this.max = max;
	}
	
	public float getValue(){
		return value;
	}
	
	public void setValue(float value){
		this.value = value;
	}
	
	public float getCompletionRatio(){
		return (value - min) / (max - min);
	}
	
	@Override
	public void update(Game game){
		super.update(game);
		if(value < min)
			value = min;
		if(value > max)
			value = max;
	}
	
	@Override
	public void draw(Game game, Canvas canvas){
		RectF drawBounds = getBoundsF();
		paint.setColor(Color.BLUE);
		canvas.drawRect(drawBounds, paint);
		
		drawBounds.right = drawBounds.width() * getCompletionRatio();
		paint.setColor(Color.WHITE);
		canvas.drawRect(drawBounds, paint);
	}
}
