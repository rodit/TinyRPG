package net.site40.rodit.tinyrpg.game.render;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.object.GameObject;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.TextUtils;

public class ResourceWrapper {
	
	private String resource;
	private Object resourceCache;
	
	private Bitmap bmpRef;
	private Animation animRef;
	private SpriteSheet sheetRef;
	
	private boolean fail = false;
	
	public ResourceWrapper(String resource){
		this.resource = resource;
	}
	
	public boolean isBitmap(){
		return bmpRef != null;
	}
	
	public boolean isAnimation(){
		return animRef != null;
	}
	
	public boolean isSpriteSheet(){
		return sheetRef != null;
	}
	
	public Object getCachedResource(){
		return resourceCache;
	}
	
	public void setResource(String resource){
		if(resource == null || !resource.equals(this.resource)){
			resourceCache = null;
			bmpRef = null;
			animRef = null;
			fail = false;
		}
		this.resource = resource;
	}
	
	public void cacheResource(Game game){
		resourceCache = game.getResources().getObject(resource);
		if(resourceCache instanceof Bitmap)
			bmpRef = (Bitmap)resourceCache;
		else if(resourceCache instanceof Animation)
			animRef = (Animation)resourceCache;
		else if(resourceCache instanceof SpriteSheet)
			sheetRef = (SpriteSheet)resourceCache;
	}
	
	public void draw(Game game, Canvas canvas, GameObject object){
		draw(game, canvas, object, object.getBounds().get());
	}
	
	public void draw(Game game, Canvas canvas, GameObject object, RectF bounds){
		if(fail || TextUtils.isEmpty(resource))
			return;
		if(resourceCache == null)
			cacheResource(game);
		if(resourceCache == null){
			fail = true;
			return;
		}
		
		if(bmpRef != null)
			canvas.drawBitmap(bmpRef, null, bounds, object.getPaint());
		else if(animRef != null)
			canvas.drawBitmap(animRef.getFrame(game.getTime()), null, bounds, object.getPaint());
		else if(sheetRef != null)
			canvas.drawBitmap(sheetRef.getBitmap(game, object), null, bounds, object.getPaint());
		else{
			resourceCache = null;
			fail = true;
		}
	}
}
