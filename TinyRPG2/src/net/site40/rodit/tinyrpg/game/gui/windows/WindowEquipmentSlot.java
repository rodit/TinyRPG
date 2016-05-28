package net.site40.rodit.tinyrpg.game.gui.windows;

import net.site40.rodit.tinyrpg.game.item.Inventory.InventoryProvider;

public class WindowEquipmentSlot extends WindowSlot{

	public WindowEquipmentSlot(Object providerKey, int slotIndex){
		super(providerKey, InventoryProvider.TAB_EQUIPPED, slotIndex);
	}
	
	@Override
	public void setTag(int slotTag){}
}
