package net.site40.rodit.tinyrpg.game.render;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.object.Bounds;
import net.site40.rodit.tinyrpg.game.render.Strings.Values;
import net.site40.rodit.tinyrpg.game.render.effects.PPEffect;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;

public class TextOverlay extends PPEffect{
	
	public static final float TIME_NO_FADE = 0.7f;

	private String text;
	private long start = -1L;
	private long noFade = 0L;
	private long fade = 0L;
	
	public TextOverlay(String text, long duration){
		super();
		this.bounds = new Bounds(0f, 264f, 1280f, 192f);
		paint.setTypeface(Game.getDefaultPaint().getTypeface());
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(Values.FONT_SIZE_LARGE);
		this.text = text;
		
		this.noFade = (long)(TIME_NO_FADE * (double)duration);
		this.fade = duration - noFade;
	}
	
	public void setText(String text){
		this.text = text;
	}

	private float fadeFact = 1f;
	private long diff = 0l;
	@Override
	public void update(Game game){}
	
	@Override
	public void draw(Game game, Canvas canvas){}

	@Override
	public void preDraw(Game game, Canvas canvas){}
	
	@Override
	public void postDraw(Game game, Canvas canvas){
		if(start == -1)
			start = game.getTime();
		diff = game.getTime() - start;
		if(diff >= noFade){
			fadeFact = 1f - (float)(diff - noFade) / (float)fade;
			if(diff >= fade + noFade - 16L)
				game.getPostProcessor().remove(this);
		}
		
		paint.setColor(Color.BLACK);
		paint.setAlpha((int)(150f * fadeFact));
		canvas.drawRect(bounds.get(), paint);
		paint.setColor(Color.WHITE);
		paint.setAlpha((int)(255f * fadeFact));
		canvas.drawText(this.text, 640, 390, paint);
	}
}
