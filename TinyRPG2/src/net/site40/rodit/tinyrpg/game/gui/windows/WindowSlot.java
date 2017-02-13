package net.site40.rodit.tinyrpg.game.gui.windows;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowSlotted.ProviderInfo;
import net.site40.rodit.tinyrpg.game.item.Hair;
import net.site40.rodit.tinyrpg.game.item.ItemStack;
import android.graphics.Canvas;

public class WindowSlot extends WindowComponent{

	public static final float SLOT_WIDTH = 128;
	public static final float SLOT_HEIGHT = 128;

	public static final String DEFAULT_SLOT_BACKGROUND = "gui/inventory/slot.png";
	public static final String DEFAULT_SLOT_OVERLAY = "gui/inventory/slot_overlay.png";

	private Object providerKey;
	private int slotTag;
	private int slotIndex;
	private String slotOverlay = DEFAULT_SLOT_OVERLAY;

	public WindowSlot(Object providerKey, int slotTag, int slotIndex){
		this(providerKey, slotTag, slotIndex, DEFAULT_SLOT_BACKGROUND, DEFAULT_SLOT_OVERLAY);
	}

	public WindowSlot(Object providerKey, int slotTag, int slotIndex, String background, String overlay){
		this.providerKey = providerKey;
		this.slotTag = slotTag;
		this.slotIndex = slotIndex;
		this.setBackground(STATE_IDLE, background);
		this.setBackground(STATE_DOWN, overlay);
		this.slotOverlay = overlay;

		this.setBounds(0, 0, SLOT_WIDTH, SLOT_HEIGHT);
	}

	public Object getProviderKey(){
		return providerKey;
	}

	public int getTag(){
		return slotTag;
	}

	public void setTag(int slotTag){
		this.slotTag = slotTag;
	}

	public int getIndex(){
		return slotIndex;
	}

	public void setIndex(int slotIndex){
		this.slotIndex = slotIndex;
	}

	public String getOverlay(){
		return slotOverlay;
	}

	public void setOverlay(String slotOverlay){
		this.slotOverlay = slotOverlay;
	}

	private WindowSlotted getParentSlotted(){
		if(getParent() instanceof WindowSlotted)
			return (WindowSlotted)getParent();
		else
			throw new IllegalArgumentException("WindowSlot cannot be added to " + getParent().getClass().getName() + ".");
	}

	@Override
	public void draw(Game game, Canvas canvas){
		super.draw(game, canvas);
		
		int itemsPerPage = getParentSlotted().getItemsPerPage(providerKey);
		ProviderInfo info = getParentSlotted().getProviderInfo(providerKey);
		ItemStack own = getParentSlotted().getProvider(providerKey).provide(slotTag, info.page[slotTag] * itemsPerPage + slotIndex);
		if(own != null && own.getItem() != null && !(own.getItem() instanceof Hair)){
			bounds.getPooled0().set(getScreenX() + 8f, getScreenY() + 8f, getScreenX() + bounds.getWidth() - 8f, getScreenY() + bounds.getHeight() - 8f);
			canvas.drawBitmap(game.getResources().getBitmap(own.getItem().getResource()), null, bounds.getPooled0(), null);
			float size = getPaint().getTextSize();
			getPaint().setTextSize(24f);
			canvas.drawText(String.valueOf(own.getAmount()), getScreenX() + bounds.getWidth() - 24f, getScreenY() + bounds.getHeight() - 16f, getPaint());
			getPaint().setTextSize(size);
		}
	}
}
