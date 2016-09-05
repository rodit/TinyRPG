package net.site40.rodit.tinyrpg.game.gui.windows;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.SuperCalc;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowSlotted.ProviderInfo;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowUserInput.InputCallback;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowUserInput.InputResult;
import net.site40.rodit.tinyrpg.game.item.ItemStack;
import net.site40.rodit.tinyrpg.game.shop.Shop;
import net.site40.rodit.util.Util;

public class WindowShopItemInfo extends WindowItemInfo{

	public WindowShopItemInfo(Game game, ItemStack stack, ProviderInfo info){
		super(game, stack, info);
		initialize(game);
	}

	public boolean isShop(){
		return !info.provider.getOwner().isPlayer();
	}

	public EntityLiving getCurrentShopOwner(Game game){
		Entity e = ((Shop)game.getGlobal("current_shop")).getOwner();
		if(e instanceof EntityLiving)
			return (EntityLiving)e;
		throw new RuntimeException("Current shop owner is not an EntityLiving!");
	}

	@Override
	public void initialize(Game game){
		if(info == null)
			return;
		
		super.initialize(game);

		remove(btnDispose);

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
								stack.setAmount(stack.getAmount() - count);
								if(tradeItem(game, stack, isShop)){
									if(isShop)
										game.getHelper().dialog("The shop does not have enough money to purchase this item.");
									else
										game.getHelper().dialog("You do not have enough money to purchase this item.");
								}
								hide();
								return true;
							}
							return false;
						}
					});
					game.getWindows().register(input);
					input.zIndex = 0;
					input.show();
				}else{
					tradeItem(game, stack, isShop);
					hide();
				}
			}
		});
	}

	public boolean isVanillaWindow(){
		return false;
	}

	public boolean tradeItem(Game game, ItemStack stack, boolean buying){
		long itemValue = buying ? SuperCalc.getStackValueFromShop(stack, game.getPlayer()) : SuperCalc.getStackValue(stack, game.getPlayer());
		boolean canAfford = buying ? game.getPlayer().getMoney() >= itemValue : getCurrentShopOwner(game).getMoney() >= itemValue;
		if(!canAfford)
			return false;
		if(buying){
			getCurrentShopOwner(game).getInventory().getItems().remove(stack);
			game.getPlayer().getInventory().add(stack);
		}else{
			game.getPlayer().getInventory().getItems().remove(stack);
			getCurrentShopOwner(game).getInventory().add(stack);
		}
		return true;
	}

	@Override
	public void initAfterInit(Game game){
		super.initAfterInit(game);
		
		if(info.provider.getOwner() instanceof EntityLiving)
			txtItemValue.setText("Price: " + SuperCalc.getStackValue(stack, (EntityLiving)info.provider.getOwner()) + " Gold");
		else
			txtItemValue.setText("Priceless...");
	}
}
