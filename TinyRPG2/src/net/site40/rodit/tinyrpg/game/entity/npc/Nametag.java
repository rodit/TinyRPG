package net.site40.rodit.tinyrpg.game.entity.npc;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.GameObject;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.RectF;

public class Nametag extends GameObject{

	private EntityNPC entity;
	
	public Nametag(EntityNPC entity){
		this.entity = entity;
		this.paint = entity.getPaint();
	}
	
	@Override
	public void draw(Game game, Canvas canvas){
		if(entity == null)
			return;
		
		paint.setColor(Color.argb(128, 0, 0, 0));
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(8f);
		float textWidth = paint.measureText(entity.getDisplayName());
		
		canvas.drawRect(new RectF(entity.getX() + entity.getWidth() / 2 - textWidth / 2 - 4f, entity.getY() - 10f, entity.getX() + entity.getWidth() / 2 + textWidth / 2 + 2f, entity.getY() - 2f), paint);
		paint.setColor(Color.WHITE);
		canvas.drawText(entity.displayName, entity.getX() + entity.getWidth() / 2, entity.getY() - 3.2f, paint);
	}

	@Override
	public void update(Game game){}

	@Override
	public RenderLayer getRenderLayer(){
		return RenderLayer.TOP_OVERRIDE_PLAYER;
	}

	@Override
	public boolean shouldScale(){
		return true;
	}
}
