package net.site40.rodit.tinyrpg.game.gui.windows;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowSlotted.ProviderInfo;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowUserInput.InputCallback;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowUserInput.InputResult;
import net.site40.rodit.tinyrpg.game.item.ItemStack;
import net.site40.rodit.util.Util;

public class WindowContainerItemInfo extends WindowItemInfo{

	public WindowContainerItemInfo(Game game, ItemStack stack, ProviderInfo info){
		super(game, stack, info);
		initialize(game);
	}

	public boolean isContainer(){
		return !info.provider.getOwner().isPlayer();
	}
	
	public Entity getCurrentContainer(Game game){
		return (Entity)game.getGlobal("current_container");
	}
	
	@Override
	public void initialize(Game game){
		if(info == null)
			return;
		
		super.initialize(game);

		final boolean container = isContainer();
		if(!container){
			this.setX(64f);
			this.setY(152f);

			btnEquipUse.setText("Store");
		}else
			btnEquipUse.setText("Take");

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
								if(container){
									stack.setAmount(stack.getAmount() - count);
									game.getPlayer().getInventory().add(stack.getItem(), count);
								}else{
									stack.setAmount(stack.getAmount() - count);
									getCurrentContainer(game).getInventory().add(stack.getItem(), count);
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
					if(container){
						getCurrentContainer(game).getInventory().getItems().remove(stack);
						game.getPlayer().getInventory().add(stack);
					}else{
						game.getPlayer().getInventory().getItems().remove(stack);
						getCurrentContainer(game).getInventory().add(stack);
					}
					hide();
				}
			}
		});
	}
}
