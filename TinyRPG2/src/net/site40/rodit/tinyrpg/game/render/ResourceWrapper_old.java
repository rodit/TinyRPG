package net.site40.rodit.tinyrpg.game.render;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import net.site40.rodit.tinyrpg.game.Game;

public class ResourceWrapper_old {

	private Object resource;
	
	public ResourceWrapper_old(Object resource){
		this.resource = resource;
	}
	
	public void setResource(Object resource){
		this.resource = resource;
	}
	
	private RectF dBounds = new RectF();
	public void draw(Game game, Canvas canvas, float x, float y, float width, float height, Paint paint){
		if(resource == null)
			return;
		Bitmap bitmap = resource instanceof Bitmap ? (Bitmap)resource : ((Animation)resource).getFrame(game.getTime());
		dBounds.set(x, y, x + width, y + height);
		canvas.drawBitmap(bitmap, null, dBounds, paint);
	}
	
	public void dispose(){
		this.resource = null;
	}
}
