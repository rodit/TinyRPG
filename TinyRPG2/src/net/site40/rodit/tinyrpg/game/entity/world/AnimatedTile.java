package net.site40.rodit.tinyrpg.game.entity.world;

import java.util.regex.Pattern;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.render.Animation;
import net.site40.rodit.util.Util;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Point;

public class AnimatedTile extends Entity{
	
	public static final String TILE_DEF_SEPERATOR = "#TILEDEF";

	private String definition;
	private Animation fullAnimBlock;
	
	public AnimatedTile(String definition){
		super();
		this.definition = definition;
		this.noclip = true;
	}
	
	@Override
	public int getRenderLayer(){
		return RenderLayer.BOTTOM;
	}
	
	public void resetCache(){
		fullAnimBlock = null;
	}
	
	@Override
	public void update(Game game){
		if(fullAnimBlock == null){
			String loadDef = game.getResources().getString(definition);
			String[] defParts = loadDef.split(Pattern.quote(TILE_DEF_SEPERATOR));
			Animation[] tileAnims = new Animation[defParts.length];
			Point[] offsets = new Point[defParts.length];
			for(int i = 0; i < defParts.length; i++){
				tileAnims[i] = new Animation();
				tileAnims[i].loadDirect(defParts[i], game.getResources());
				for(String line : defParts[i].split(Pattern.quote(";"))){
					String[] lParts = line.split(":");
					if(lParts[0].trim().equals("offset")){
						String[] offsetParts = lParts[1].split(Pattern.quote(","));
						offsets[i] = new Point(Util.tryGetInt(offsetParts[0]), Util.tryGetInt(offsetParts[1]));
					}
				}
			}
			Bitmap sample = tileAnims[0].getFrames()[0];
			int boundsRight = 0;
			int boundsBottom = 0;
			for(int i = 0; i < offsets.length; i++){
				Point offset = offsets[i];
				int cBoundsRight = offset.x + sample.getWidth();
				if(cBoundsRight > boundsRight)
					boundsRight = cBoundsRight;
				int cBoundsBottom = offset.y + sample.getHeight();
				if(cBoundsBottom > boundsBottom)
					boundsBottom = cBoundsBottom;
			}
			Bitmap[] fullFrames = new Bitmap[tileAnims[0].getFrames().length];
			for(int i = 0; i < fullFrames.length; i++){
				fullFrames[i] = Bitmap.createBitmap(boundsRight, boundsBottom, Config.ARGB_8888);
				Canvas canvas = new Canvas(fullFrames[i]);
				for(int x = 0; x < tileAnims.length; x++){
					Animation cAnim = tileAnims[x];
					Bitmap cFrame = cAnim.getFrames()[i];
					Point cOffset = offsets[i];
					if(cFrame == null)
						continue;
					canvas.drawBitmap(cFrame, cOffset.x, cOffset.y, null);
				}
			}
			fullAnimBlock = new Animation(fullFrames, true, tileAnims[0].getDelay());
		}
	}
	
	@Override
	public void draw(Game game, Canvas canvas){
		if(fullAnimBlock == null)
			return;
		
		int wPer = fullAnimBlock.getFrames()[0].getWidth();
		int hPer = fullAnimBlock.getFrames()[0].getHeight();
		int fullDrawnX = 0;
		int fullDrawnY = hPer;
		while(fullDrawnX < bounds.getWidth()){
			float realX = bounds.getX() + (float)fullDrawnX;
			float realY = bounds.getY();
			canvas.drawBitmap(fullAnimBlock.getFrame(game.getTime()), realX, realY, null);
			fullDrawnX += wPer;
		}
		
		while(fullDrawnY < bounds.getWidth()){
			float realY = bounds.getY() + (float)fullDrawnY;
			for(int realX = (int)bounds.getX(); realX < bounds.getX() + fullDrawnX; realX += wPer)
				canvas.drawBitmap(fullAnimBlock.getFrame(game.getTime()), realX, realY, null);
			fullDrawnY += hPer;
		}
	}
}
