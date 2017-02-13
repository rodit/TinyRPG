package net.site40.rodit.tinyrpg.game.render;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.object.GameObject;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class BitmapRenderer extends GameObject{

	private Bitmap[] bmps;

	public BitmapRenderer(Bitmap... bmps){
		this.bmps = bmps;
	}

	public float getX(){
		return 0f;
	}

	public float getY(){
		return 0f;
	}

	@Override
	public void update(Game game){}

	@Override
	public void draw(Game game, Canvas canvas){
		super.preRender(game, canvas);

		for(Bitmap bmp : bmps){
			if(bmp != null && !bmp.isRecycled())
				canvas.drawBitmap(bmp, getX(), getY(), null);
		}

		super.postRender(game, canvas);
	}

	@Override
	public int getRenderLayer(){
		return RenderLayer.TOP;
	}

	public boolean shouldScale(){
		return true;
	}
}
