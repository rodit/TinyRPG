package net.site40.rodit.tinyrpg.game.render.effects;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.gui.GuiMenu;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Lighting extends PPEffect{

	private float darkness;
	
	public Lighting(){
		setDarkness(0f);
	}

	public float getDarkness(){
		return darkness;
	}

	public void setDarkness(float darkness){
		this.darkness = darkness;
		paint.setAlpha((int)(darkness * 255f));
	}

	public void update(Game game){

	}

	public void preDraw(Game game, Canvas canvas){}

	public void draw(Game game, Canvas canvas){
		if(!game.getGuis().isVisible(GuiMenu.class))
			canvas.drawRect(new RectF(game.getPlayer().getX() - 640, game.getPlayer().getY() - 360, game.getPlayer().getX() + 640, game.getPlayer().getY() + 360), paint);
	}

	public void postDraw(Game game, Canvas canvas){}
}
