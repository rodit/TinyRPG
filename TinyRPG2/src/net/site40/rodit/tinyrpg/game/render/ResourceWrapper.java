package net.site40.rodit.tinyrpg.game.render;

import net.site40.rodit.tinyrpg.game.Game;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class ResourceWrapper {

	private Object resource;
	
	public ResourceWrapper(Object resource){
		this.resource = resource;
	}
	
	public void draw(Game game, Canvas canvas, float x, float y, float width, float height, Paint paint){
		if(resource == null)
			return;
		Bitmap bitmap = resource instanceof Bitmap ? (Bitmap)resource : ((Animation)resource).getFrame(game.getTime());
		canvas.drawBitmap(bitmap, null, new RectF(x, y, x + width, y + height), paint);
	}
	
	public void dispose(){
		this.resource = null;
	}
}
