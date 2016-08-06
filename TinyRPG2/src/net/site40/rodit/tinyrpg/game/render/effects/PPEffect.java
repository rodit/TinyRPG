package net.site40.rodit.tinyrpg.game.render.effects;

import net.site40.rodit.tinyrpg.game.Game;
import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class PPEffect {
	
	protected Paint paint;
	
	public PPEffect(){
		this.paint = new Paint();
	}
	
	public abstract void update(Game game);
	public abstract void preDraw(Game game, Canvas canvas);
	public abstract void draw(Game game, Canvas canvas);
	public abstract void postDraw(Game game, Canvas canvas);
}
