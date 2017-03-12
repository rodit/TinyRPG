package net.site40.rodit.tinyrpg.game.gui.windows;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.item.Inventory.InventoryProvider;
import net.site40.rodit.tinyrpg.game.item.ItemStack;
import net.site40.rodit.tinyrpg.game.render.Strings.Values;
import net.site40.rodit.tinyrpg.game.shop.ShopLinker;
import android.graphics.Paint.Align;

public class WindowShop extends WindowContainer {
	
	private ShopLinker shop;
	private WindowComponent txtBuyerInfo;
	private WindowComponent txtSellerInfo;
	
	public WindowShop(Game game, ShopLinker shop){
		super(game, shop.getStockOwner());
		this.shop = shop;
		initialize(game);
	}
	
	@Override
	public void initialize(Game game){
		if(shop == null)
			return;
		
		super.initialize(game);
		
		txtTitle.setText("Shop");
		
		this.txtBuyerInfo = new WindowComponent("txtBuyerInfo");
		txtBuyerInfo.setX(136);
		txtBuyerInfo.setY(128);
		txtBuyerInfo.getPaint().setTextAlign(Align.LEFT);
		txtBuyerInfo.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		txtBuyerInfo.setFlag(WindowComponent.FLAG_MULTILINE_TEXT, true);
		add(txtBuyerInfo);
		
		this.txtSellerInfo = new WindowComponent("txtSellerInfo");
		txtSellerInfo.setX(bounds.getWidth() - txtBuyerInfo.getBounds().getX());
		txtSellerInfo.setY(txtBuyerInfo.getBounds().getY());
		txtSellerInfo.getPaint().setTextAlign(Align.RIGHT);
		txtSellerInfo.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		txtSellerInfo.setFlag(WindowComponent.FLAG_MULTILINE_TEXT, true);
		add(txtSellerInfo);
	}
	
	@Override
	public void update(Game game){
		super.update(game);
		
		EntityLiving buyer = getProvider(PLAYER_KEY).getOwnerLiving();
		Entity seller = shop.getLinkedOwner();
		txtBuyerInfo.setText(buyer.getDisplayName() + "\n" + "Gold: " + buyer.getMoney());
		txtSellerInfo.setText(seller.getDisplayName() + "\n" + "Gold: " + shop.getMoney());
	}
	
	@Override
	public void onSlotSelected(Game game, WindowSlot slot){
		ProviderInfo info = getProviderInfo(slot.getProviderKey());
		if(info == null)
			return;
		ItemStack stack = info.provider.provide(InventoryProvider.TAB_ALL, info.page[info.selectedTab] * getItemsPerPage(slot.getProviderKey()) + slot.getIndex());
		if(stack == null)
			return;
		WindowShopItemInfo itemWindow = new WindowShopItemInfo(game, shop, stack, info);
		itemWindow.zIndex = 1;
		game.getWindows().register(itemWindow);
		itemWindow.show();
	}
}
