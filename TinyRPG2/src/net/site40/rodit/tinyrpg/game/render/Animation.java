package net.site40.rodit.tinyrpg.game.render;

import java.util.regex.Pattern;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

public class Animation {

	private Bitmap[] frames;
	private int delay;

	public Animation(){
		this(new Bitmap[0], false, 100);
	}

	public Animation(Bitmap[] frames, boolean loop, int delay){
		this.frames = frames;
		this.delay = delay;
	}

	public Bitmap[] getFrames(){
		return frames;
	}

	public void addFrame(Bitmap frame){
		Bitmap[] nframes = new Bitmap[frames.length + 1];
		nframes[frames.length] = frame;
		frames = nframes;
	}
	
	public int getDelay(){
		return delay;
	}

	public void setDelay(int delay){
		this.delay = delay;
	}

	public Bitmap getFrame(long time){
		if(frames == null || frames.length == 0)
			return null;
		int frameNumber = getFrameIndex(time);
		return frames[frameNumber];
	}
	
	public int getFrameIndex(long time){
		if (frames.length == 1)
			return 0;
		int frameNumber = (int)(time / delay);
		frameNumber = frameNumber % frames.length;
		return Math.abs(frameNumber);
	}

	public long getRunTime(){
		return frames.length * delay;
	}
	
	public void recycle(){
		for(int i = 0; i < frames.length; i++)
			frames[i].recycle();
		frames = null;
	}
	
	public void loadDirect(String meta, ResourceManager resources){
		String sheet = "";
		int rows = 0;
		int columns = 0;
		int start = 0;
		int length = 0;
		int delay = 0;
		for(String s : meta.split(Pattern.quote(";"))){
			String line = s.trim();
			String[] parts = line.split(Pattern.quote(":"));
			if(parts.length != 2)
				continue;
			String key = parts[0];
			String val = parts[1];
			if(key.equals("sheet"))
				sheet = val;
			else if(key.equals("rows"))
				rows = Integer.valueOf(val);
			else if(key.equals("columns"))
				columns = Integer.valueOf(val);
			else if(key.equals("start"))
				start = Integer.valueOf(val);
			else if(key.equals("length"))
				length = Integer.valueOf(val);
			else if(key.equals("delay"))
				delay = Integer.valueOf(val);
		}
		Bitmap sBmp = resources.readBitmap(sheet);
		int width = sBmp.getWidth() / columns;
		int height = sBmp.getHeight() / rows;
		frames = new Bitmap[length];
		int k = 0;
		int done = 0;
		for(int y = 0; y < rows * height; y += height){
			for(int x = 0; x < columns * width; x += width){
				if(k >= start && done < length){
					Bitmap frame = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
					Canvas c = new Canvas(frame);
					c.drawBitmap(sBmp, new Rect(x, y, x + width, y + height), new Rect(0, 0, width, height), null);
					frames[done++] = frame;
				}
				k++;
			}
		}
		this.delay = delay;
	}

	public void load(String asset, ResourceManager resources){
		loadDirect(new String(resources.readAsset(asset)), resources);
	}
	
	public static Animation mergeSpriteIdleAnimations(Animation... anims){
		Bitmap merged = Bitmap.createBitmap(SpriteSheet.DEFAULT_WIDTH, SpriteSheet.DEFAULT_HEIGHT, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(merged);
		for(int i = 0; i < anims.length; i++){
			canvas.drawBitmap(anims[i].frames[0], null, SpriteSheet.DEFAULT_BOUNDS, null);
		}
		return new Animation(new Bitmap[] { merged }, true, Integer.MAX_VALUE);
	}
	
	public static Animation mergeSpriteMoveAnimations(Animation... anims){
		Bitmap[] merged = new Bitmap[3];
		for(int i = 0; i < merged.length; i++){
			merged[i] = Bitmap.createBitmap(SpriteSheet.DEFAULT_WIDTH, SpriteSheet.DEFAULT_HEIGHT, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(merged[i]);
			for(int j = 0; j < anims.length; j++){
				canvas.drawBitmap(anims[j].frames[i], null, SpriteSheet.DEFAULT_BOUNDS, null);
			}
		}
		return new Animation(merged, true, SpriteSheet.DEFAULT_DELAY);
	}
}
