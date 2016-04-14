package net.site40.rodit.tinyrpg.game.render;

import java.util.regex.Pattern;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

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

	public void load(String asset, ResourceManager resources){
		String meta = new String(resources.readAsset(asset));
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
		frames = new Bitmap[rows * columns];
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
}
