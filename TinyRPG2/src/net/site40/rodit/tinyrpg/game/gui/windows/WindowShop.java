package net.site40.rodit.tinyrpg.game.gui.windows;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.npc.TempShopOwner;
import net.site40.rodit.tinyrpg.game.item.Inventory.InventoryProvider;
import net.site40.rodit.tinyrpg.game.item.ItemStack;
import net.site40.rodit.tinyrpg.game.shop.Shop;

public class WindowShop extends WindowContainer {
	
	private Shop shop;
	
	public WindowShop(Game game, TempShopOwner owner, Shop shop){
		super(game, owner);
		this.shop = shop;
		initialize(game);
	}
	
	@Override
	public void initialize(Game game){
		if(shop == null)
			return;
		
		super.initialize(game);
		
		txtTitle.setText("Shop");
	}
	
	@Override
	public void onSlotSelected(Game game, WindowSlot slot){
		ProviderInfo info = getProviderInfo(slot.getProviderKey());
		if(info == null)
			return;
		ItemStack stack = info.provider.provide(InventoryProvider.TAB_ALL, info.page[info.selectedTab] * getItemsPerPage(slot.getProviderKey()) + slot.getIndex());
		if(stack == null)
			return;
		WindowShopItemInfo itemWindow = new WindowShopItemInfo(game, stack, info);
		itemWindow.zIndex = 1;
		game.getWindows().register(itemWindow);
		itemWindow.show();
	}
}
