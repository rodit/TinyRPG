package net.site40.rodit.tinyrpg.game.item;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.render.SpriteSheet;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Hair extends ItemEquippable{

	public static int MAX_ID = 22;
	
	private int id;
	private String color;
	
	public Hair(){
		super("hair", "Hair", "Hair item cos cba to implement it any other way...", "", "", Rarity.UNKNOWN, 0L, ItemEquippable.SLOT_HAIR);
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public String getColor(){
		return color;
	}
	
	public void setColor(String color){
		this.color = color;
	}
	
	public String getHairName(){
		return id + "_" + color;
	}
	
	@Override
	public String getResource(){
		return "character/m/hair/" + getHairName() + "_front.spr";
	}
	
	public boolean drawSpriteOverlay(Canvas canvas, Game game, EntityLiving equipper, Paint paint, String sprite){
		Object obj = game.getResources().getObject(getResource());
		if(obj instanceof SpriteSheet){
			SpriteSheet sheet = (SpriteSheet)obj;
			Bitmap current = sheet.getBitmap(game, equipper);
			if(current != null){
				spriteDrawBounds.set(0f, 0f, equipper.getBounds().getWidth(), equipper.getBounds().getHeight());
				canvas.drawBitmap(current, null, spriteDrawBounds.get(), paint);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String getDefaultSpriteSheet(){
		return getResource();
	}
}
