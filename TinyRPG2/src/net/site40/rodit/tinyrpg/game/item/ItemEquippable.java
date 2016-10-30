package net.site40.rodit.tinyrpg.game.item;

import java.util.regex.Pattern;

import org.w3c.dom.Element;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.render.SpriteSheet;
import net.site40.rodit.util.Util;

public class ItemEquippable extends Item{
	
	public static final int SLOT_HELMET = 0;
	public static final int SLOT_CHEST = 1;
	public static final int SLOT_SHOULDERS = 2;
	public static final int SLOT_NECK = 3;
	public static final int SLOT_FINGER_0 = 4;
	public static final int SLOT_FINGER_1 = 5;
	public static final int SLOT_HAND_0 = 6;
	public static final int SLOT_HAND_1 = 7;
	public static final int SLOT_HAIR = 8;

	protected int[] equipSlots = new int[0];
	
	public ItemEquippable(){
		this("", "", "", "", "", Rarity.UNKNOWN, 0L, -1);
	}

	public ItemEquippable(String name, String showName, String description, String script, String resource, Rarity rarity, long value, int... equipSlots){
		super(name, showName, description, script, resource, rarity, value);
		this.equipSlots = equipSlots == null ? new int[0] : equipSlots;
		this.stackable = false;
		this.stackSize = 1;
	}

	public int[] getEquipSlots(){
		return equipSlots;
	}
	
	public boolean canPlaceInSlot(int slotId){
		return Util.arrayContains(equipSlots, slotId);
	}
	
	public boolean drawSpriteOverlay(Canvas canvas, Game game, EntityLiving equipper, Paint paint){
		String[] tmp = getResource().split(Pattern.quote("/"));
		String spriteName = tmp[tmp.length - 1].replace(".png", "");
		return drawSpriteOverlay(canvas, game, equipper, paint, spriteName);
	}
	
	public static final String SPRITE_DIR = "item/sprite";
	public static final String SPRITE_EXT = "spr";
	public boolean drawSpriteOverlay(Canvas canvas, Game game, EntityLiving equipper, Paint paint, String sprite){
		Object obj = game.getResources().getObject(SPRITE_DIR + "/" + sprite + "." + SPRITE_EXT);
		if(obj instanceof SpriteSheet){
			SpriteSheet sheet = (SpriteSheet)obj;
			Bitmap current = sheet.getBitmap(game, equipper);
			if(current != null){
				canvas.drawBitmap(current, null, new RectF(0f, 0f, equipper.getWidth(), equipper.getHeight()), paint);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void deserializeXmlElement(Element e){
		super.deserializeXmlElement(e);
		setStackable(Util.tryGetBool(e.getAttribute("stackable"), false));
		setStackSize(Util.tryGetInt(e.getAttribute("stackSize"), 1));
		String eSlot = e.getAttribute("equipSlot");
		if(this.equipSlots.length == 0 && TextUtils.isEmpty(eSlot))
			this.equipSlots = new int[] { 0 };
		else if(!TextUtils.isEmpty(eSlot)){
			String[] slots = eSlot.split(",");
			equipSlots = new int[slots.length];
			for(int i = 0; i < slots.length; i++)
				equipSlots[i] = Util.tryGetInt(slots[i]);
		}
	}
}
