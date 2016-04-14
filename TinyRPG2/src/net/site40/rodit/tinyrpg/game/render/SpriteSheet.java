package net.site40.rodit.tinyrpg.game.render;

import java.util.HashMap;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.util.Direction;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

public class SpriteSheet {

	public static enum MovementState{
		IDLE, WALK
	}

	public static final int DEFAULT_WIDTH = 32;
	public static final int DEFAULT_HEIGHT = 32;
	public static final int DEFAULT_DELAY = 100;

	private Bitmap sheet;
	private HashMap<MovementState, HashMap<Direction, Animation>> animations;
	private int width;
	private int height;
	private int delay;

	public SpriteSheet(Bitmap sheet){
		this(sheet, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public SpriteSheet(Bitmap sheet, int width, int height){
		this(sheet, width, height, DEFAULT_DELAY);
	}

	public SpriteSheet(Bitmap sheet, int width, int height, int delay){
		this.sheet = sheet;
		this.width = width;
		this.height = height;
		this.delay = delay;
		this.animations = new HashMap<MovementState, HashMap<Direction, Animation>>();
		init();
	}

	private void init(){
		if(sheet == null)
			return;
		Bitmap[] bmps = new Bitmap[12];
		int k = 0;
		for(int y = 0; y < sheet.getHeight(); y += height){
			for(int x = 0; x < sheet.getWidth(); x += width){
				Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
				Canvas canvas = new Canvas(bmp);
				canvas.drawBitmap(sheet, new Rect(x, y, x + width, y + height), new RectF(0, 0, width, height), null);
				bmps[k++] = bmp;
			}
		}
		Animation downIdle = new Animation(new Bitmap[] { bmps[1] }, true, Integer.MAX_VALUE);
		Animation downWalk = new Animation(new Bitmap[] { bmps[0], bmps[1], bmps[2], bmps[1] }, true, delay);
		Animation leftIdle = new Animation(new Bitmap[] { bmps[4] }, true, Integer.MAX_VALUE);
		Animation leftWalk = new Animation(new Bitmap[] { bmps[3], bmps[4], bmps[5], bmps[4] }, true, delay);
		Animation rightIdle = new Animation(new Bitmap[] { bmps[7] }, true, Integer.MAX_VALUE);
		Animation rightWalk = new Animation(new Bitmap[] { bmps[6], bmps[7], bmps[8], bmps[7] }, true, delay);
		Animation upIdle = new Animation(new Bitmap[] { bmps[10] }, true, Integer.MAX_VALUE);
		Animation upWalk = new Animation(new Bitmap[] { bmps[9], bmps[10], bmps[11], bmps[10] }, true, delay);
		HashMap<Direction, Animation> idleAnims = new HashMap<Direction, Animation>();
		idleAnims.put(Direction.D_DOWN, downIdle);
		idleAnims.put(Direction.D_LEFT, leftIdle);
		idleAnims.put(Direction.D_RIGHT, rightIdle);
		idleAnims.put(Direction.D_UP, upIdle);
		animations.put(MovementState.IDLE, idleAnims);
		HashMap<Direction, Animation> walkAnims = new HashMap<Direction, Animation>();
		walkAnims.put(Direction.D_DOWN, downWalk);
		walkAnims.put(Direction.D_LEFT, leftWalk);
		walkAnims.put(Direction.D_RIGHT, rightWalk);
		walkAnims.put(Direction.D_UP, upWalk);
		animations.put(MovementState.WALK, walkAnims);
	}
	
	public Bitmap getBitmap(Game game, Sprite sprite){
		return animations.get(sprite.getMoveState()).get(sprite.getDirection()).getFrame(game.getTime());
	}
}
