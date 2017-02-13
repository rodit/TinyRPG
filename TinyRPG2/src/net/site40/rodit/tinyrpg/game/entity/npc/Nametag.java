package net.site40.rodit.tinyrpg.game.entity.npc;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.object.GameObject;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;

public class Nametag extends GameObject{

	private EntityNPC entity;
	
	public Nametag(EntityNPC entity){
		this.entity = entity;
		this.paint = entity.getPaint();
	}
	
	private float dTextWidth;
	@Override
	public void draw(Game game, Canvas canvas){
		if(entity == null)
			return;
		
		paint.setColor(Color.argb(128, 0, 0, 0));
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(8f);
		dTextWidth = paint.measureText(entity.getDisplayName());
		
		bounds.set(entity.getBounds().getX() + entity.getBounds().getWidth() / 2 - dTextWidth / 2 - 4f, entity.getBounds().getY() - 10f, dTextWidth + 4f, 8f);
		canvas.drawRect(bounds.get(), paint);
		paint.setColor(Color.WHITE);
		canvas.drawText(entity.getDisplayName(), entity.getBounds().getX() + entity.getBounds().getWidth() / 2, entity.getBounds().getY() - 3.2f, paint);
	}

	@Override
	public void update(Game game){}

	@Override
	public int getRenderLayer(){
		return RenderLayer.TOP_OVER_PLAYER;
	}

	@Override
	public boolean shouldScale(){
		return true;
	}
}
