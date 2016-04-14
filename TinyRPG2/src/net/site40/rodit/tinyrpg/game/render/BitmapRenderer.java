package net.site40.rodit.tinyrpg.game.render;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.GameObject;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class BitmapRenderer extends GameObject{

	private Bitmap bmp;

	public BitmapRenderer(Bitmap bmp){
		this.bmp = bmp;
	}

	@Override
	public void update(Game game){}

	@Override
	public void draw(Game game, Canvas canvas){
		super.preRender(game, canvas);

		if(bmp != null && !bmp.isRecycled())
			canvas.drawBitmap(bmp, 0, 0, null);

		super.postRender(game, canvas);
	}
	
	@Override
	public RenderLayer getRenderLayer(){
		return RenderLayer.TOP;
	}

	public boolean shouldScale(){
		return true;
	}
}
