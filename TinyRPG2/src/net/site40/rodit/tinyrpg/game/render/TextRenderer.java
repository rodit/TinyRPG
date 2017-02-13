package net.site40.rodit.tinyrpg.game.render;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.object.GameObject;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;

public class TextRenderer extends GameObject{
	
	private String text;
	private float x;
	private float y;
	
	private Paint paint;

	public TextRenderer(){
		this("", 0f, 0f);
	}
	
	public TextRenderer(String text, float x, float y){
		this(text, x, y, Color.BLACK, Align.CENTER);
	}
	
	public TextRenderer(String text, float x, float y, int color, Align align){
		this.text = text;
		this.x = x;
		this.y = y;
		this.paint = Game.getDefaultPaint();
		paint.setColor(color);
		paint.setTextAlign(align);
	}
	
	public TextRenderer(String text, float x, float y, Paint paint){
		this.text = text;
		this.x = x;
		this.y = y;
		this.paint = paint;
	}
	
	@Override
	public void update(Game game){
		
	}
	
	@Override
	public void draw(Game game, Canvas canvas){
		super.preRender(game, canvas);
		
		String[] lines = text.split("\n");
		for(int i = 0; i < lines.length; i++)
			canvas.drawText(lines[i], x, y + (float)i * paint.getTextSize() + 5f, paint);
		
		super.postRender(game, canvas);
	}
	
	@Override
	public int getRenderLayer(){
		return RenderLayer.TOP;
	}
	
	@Override
	public boolean shouldScale(){
		return false;
	}
}
