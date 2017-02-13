package net.site40.rodit.tinyrpg.game.gui.windows;

import net.site40.rodit.tinyrpg.game.Dialog.DialogCallback;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.SuperCalc;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowSlotted.ProviderInfo;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowUserInput.InputCallback;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowUserInput.InputResult;
import net.site40.rodit.tinyrpg.game.item.ItemStack;
import net.site40.rodit.tinyrpg.game.render.Strings.GameData;
import net.site40.rodit.tinyrpg.game.shop.Shop;
import net.site40.rodit.util.Util;

public class WindowShopItemInfo extends WindowItemInfo{

	private Shop shop;

	public WindowShopItemInfo(Game game, Shop shop, ItemStack stack, ProviderInfo info){
		super(game, stack, info);
		this.shop = shop;
		initialize(game);
	}

	@Override
	public void hide(){
		super.hide();

	}

	public boolean isShop(){
		return !info.provider.getOwner().isPlayer();
	}

	public Entity getShopOwner(){
		return shop.getOwner();
	}

	@Override
	public void initialize(Game game){
		if(info == null)
			return;

		super.initialize(game);

		btnDispose.setFlag(WindowComponent.FLAG_INVISIBLE, true);

		final boolean isShop = isShop();
		if(!isShop){
			this.setX(64f);
			this.setY(152f);

			btnEquipUse.setText("Sell");
		}else
			btnEquipUse.setText("Buy");

		btnEquipUse.getListeners().clear();
		btnEquipUse.addListener(new WindowListener(){
			public void touchUp(final Game game, WindowComponent component){
				if(game.getPlayer().isEquipped(stack)){
					game.getWindows().get(WindowShop.class).hide();
					DialogCallback callback = new DialogCallback(){
						@Override
						public void onSelected(int u){
							game.getWindows().get(WindowShop.class).show();
							WindowShopItemInfo.this.show();
						}
					};
					WindowShopItemInfo.this.hide();
					game.getHelper().dialog("You cannot sell a currently equipped item.", GameData.EMPTY_STRING_ARRAY, callback);
				}
				if(stack.getAmount() > 1){
					WindowUserInput input = new WindowUserInput(game, "How Many", String.valueOf(stack.getAmount()), new InputCallback(){
						public boolean onResult(WindowUserInput window, Object result){
							if(result == InputResult.CANCELLED)
								return true;
							int count = Util.tryGetInt(String.valueOf(result));
							if(count > stack.getAmount())
								window.get("input").setText(String.valueOf(stack.getAmount()));
							else if(count < 1)
								window.get("input").setText("1");
							else{
								ItemStack nStack = new ItemStack(stack.getItem(), count);
								if(!tradeItem(game, nStack, isShop, false))
									handleFailedBuy(game, isShop);
								else{
									stack.setAmount(stack.getAmount() - count);
									if(stack.getAmount() == 0){
										if(isShop)
											shop.getInventory().getItems().remove(stack);
										else
											game.getPlayer().getInventory().getItems().remove(stack);
									}
									if(isShop)
										game.getPlayer().getInventory().add(nStack);
									else
										shop.getInventory().add(nStack);
								}
								close();
								return true;
							}
							return false;
						}
					});
					game.getWindows().register(input);
					input.zIndex = 0;
					input.show();
				}else{
					if(!tradeItem(game, stack, isShop, true))
						handleFailedBuy(game, isShop);
					else
						close();
				}
			}
		});
	}
	private void handleFailedBuy(final Game game, boolean isShop){
		game.getWindows().get(WindowShop.class).hide();
		DialogCallback callback = new DialogCallback(){
			@Override
			public void onSelected(int u){
				game.getWindows().get(WindowShop.class).show();
				WindowShopItemInfo.this.show();
			}
		};
		this.hide();
		if(isShop)
			game.getHelper().dialog("You do not have enough money to purchase this item.", GameData.EMPTY_STRING_ARRAY, callback);
		else
			game.getHelper().dialog("The shop does not have enough money to purchase this item.", GameData.EMPTY_STRING_ARRAY, callback);
	}

	public boolean isVanillaWindow(){
		return false;
	}

	public boolean tradeItem(Game game, ItemStack stack, boolean buying, boolean shiftStack){
		long itemValue = buying ? SuperCalc.getStackValueFromShop(stack, game.getPlayer()) : SuperCalc.getStackValue(stack, game.getPlayer());
		boolean canAfford = buying ? game.getPlayer().getMoney() >= itemValue : shop.getOwner().getMoney() >= itemValue;
		if(!canAfford)
			return false;
		if(buying){
			if(shiftStack){
				shop.getOwner().getInventory().getItems().remove(stack);
				game.getPlayer().getInventory().add(stack);
			}
			shop.getOwner().addMoney(itemValue);
			game.getPlayer().subtractMoney(itemValue);
		}else{
			if(shiftStack){
				game.getPlayer().getInventory().getItems().remove(stack);
				shop.getOwner().getInventory().add(stack);
			}
			game.getPlayer().addMoney(itemValue);
			shop.getOwner().subtractMoney(itemValue);
		}
		return true;
	}

	@Override
	public void initAfterInit(Game game){
		super.initAfterInit(game);

		if(info.provider.getOwner() instanceof EntityLiving)
			txtItemValue.setText("Price: " + (isShop() ? SuperCalc.getStackValueFromShop(stack, game.getPlayer()) : SuperCalc.getStackValue(stack, game.getPlayer())) + " Gold");
		else
			txtItemValue.setText("Priceless...");
	}
}
