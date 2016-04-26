package net.site40.rodit.tinyrpg.game.render;

import java.io.IOException;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.GameObject;
import net.site40.rodit.tinyrpg.game.render.SpriteSheet.MovementState;
import net.site40.rodit.tinyrpg.game.util.Direction;
import net.site40.rodit.util.ISavable;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;

public class Sprite extends GameObject implements ISavable{

	protected float x;
	protected float y;
	protected float width;
	protected float height;
	protected String resource;
	protected String name;
	protected Object cache;
	public boolean ignoreScroll;
	protected Direction direction;
	protected MovementState moveState;

	private boolean bitmapCache;

	public Sprite(){
		this(0f, 0f, 0f, 0f, "", "");
	}

	public Sprite(float x, float y, float width, float height, String resource, String name){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.resource = resource;
		this.name = name;
		this.direction = Direction.D_DOWN;
		this.moveState = MovementState.IDLE;
		this.cache = null;
	}

	public void resetCache(){
		cache = null;
	}

	public float getX(){
		return x;
	}

	public void setX(float x){
		this.x = x;
	}

	public float getY(){
		return y;
	}

	public void setY(float y){
		this.y = y;
	}

	public float getWidth(){
		return width;
	}

	public void setWidth(float width){
		this.width = width;
		resetCache();
	}

	public float getHeight(){
		return height;
	}

	public void setHeight(float height){
		this.height = height;
		resetCache();
	}

	public Object getResourceCache(){
		return cache;
	}

	public String getResource(){
		return resource;
	}

	public void setResource(String resource){
		this.resource = resource;
		resetCache();
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public RectF getBounds(){
		return new RectF(x, y, x + width, y + height);
	}

	public float getCenterX(){
		return x + width / 2f;
	}

	public float getCenterY(){
		return y + height / 2f;
	}

	public Direction getDirection(){
		return direction;
	}
	
	public void setDirection(Direction direction){
		this.direction = direction;
	}
	
	public MovementState getMoveState(){
		return moveState;
	}

	@Override
	public void update(Game game){
		if(disposed){
			Log.w("Sprite", "Disposed sprite update.");
			new Exception().printStackTrace();
		}
	}
	
	@Override
	public void draw(Game game, Canvas canvas){
		if(ignoreScroll)
			game.pushTranslate(canvas);
		
		super.preRender(game, canvas);
		
		if(resource == null || TextUtils.isEmpty(resource))
			return;
		if(cache == null){
			Object o = game.getResources().getObject(resource);
			if(o instanceof Bitmap){
				cache = (Bitmap)o;
				bitmapCache = true;
			}else if(o instanceof Animation){
				cache = ((Animation)o).getFrame(game.getTime());
				bitmapCache = false;
			}else if(o instanceof SpriteSheet){
				cache = ((SpriteSheet)o).getBitmap(game, this);
			}
		}
		if(cache != null){
			if(cache instanceof Bitmap)
				canvas.drawBitmap((Bitmap)cache, null, getBounds(), paint);
			else if(cache instanceof Animation)
				canvas.drawBitmap(((Animation)cache).getFrame(game.getTime()), null, getBounds(), paint);
			else if(cache instanceof SpriteSheet)
				canvas.drawBitmap(((SpriteSheet)cache).getBitmap(game, this), null, getBounds(), paint);
		}
		if(!bitmapCache)
			resetCache();
		
		super.postRender(game, canvas);
		
		if(ignoreScroll)
			game.popTranslate(canvas);
	}

	@Override
	public RenderLayer getRenderLayer(){
		return RenderLayer.MIDDLE;
	}

	@Override
	public boolean shouldScale(){
		return true;
	}

	@Override
	public void serialize(TinyOutputStream out)throws IOException{
		out.write(x);
		out.write(y);
		out.write(width);
		out.write(height);
		out.writeString(resource);
		out.writeString(name);
	}

	@Override
	public void deserialize(TinyInputStream in)throws IOException{
		x = in.readFloat();
		y = in.readFloat();
		width = in.readFloat();
		height = in.readFloat();
		resource = in.readString();
		name = in.readString();
	}
	

	
	private boolean disposed = false;
	@Override
	public void dispose(Game game){
		this.disposed = true;
	}
}
