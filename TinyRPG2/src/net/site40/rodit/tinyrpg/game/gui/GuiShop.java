package net.site40.rodit.tinyrpg.game.gui;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.entity.npc.EntityNPC;
import net.site40.rodit.tinyrpg.game.gui.ComponentListener.ComponentListenerImpl;
import net.site40.rodit.tinyrpg.game.shop.Shop;
import android.graphics.Paint.Align;
import android.view.MotionEvent;


public class GuiShop extends GuiContainer{
	
	private boolean replacedListener = false;
	
	protected Component txtPlayerMoney;
	protected Component txtShopMoney;

	public GuiShop(){
		super();
	}
	
	@Override
	public void onShown(){
		replacedListener = false;
		super.onShown();
	}
	
	@Override
	public void init(){
		super.init();
		
		txtPlayerMoney = new Component("txtPlayerMoney", "Your Gold: 0");
		txtPlayerMoney.setX(92);
		txtPlayerMoney.setY(132);
		txtPlayerMoney.getPaint().setTextAlign(Align.LEFT);
		txtPlayerMoney.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		txtPlayerMoney.addListener(new ComponentListenerImpl(){
			public void update(Component component, Game game){
				component.setText("Your Gold: " + game.getPlayer().getMoney());
			}
		});
		add(txtPlayerMoney);
		
		txtShopMoney = new Component("txtPlayerMoney", "Shop Gold: 0");
		txtShopMoney.setX(1024);
		txtShopMoney.setY(192);
		txtShopMoney.getPaint().setTextAlign(Align.RIGHT);
		txtShopMoney.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		txtShopMoney.addListener(new ComponentListenerImpl(){
			public void update(Component component, Game game){
				Shop shop = getCurrentShop(game);
				String shopName = shop.getOwner() instanceof EntityNPC ? ((EntityNPC)shop.getOwner()).getDisplayName() : "Shop";
				shopName = shopName.endsWith("s") ? shopName + "'" : shopName + "'s";
				component.setText(shopName + " Gold: " + game.getPlayer().getMoney());
			}
		});
		add(txtShopMoney);
	}
	
	@Override
	public void onSlotSelected(Game game){
		super.onSlotSelected(game);

		if(selected == -1 || selectedItem == null){
			selected = -1;
			if(btnBack != null)
				btnBack.simulateInput(game, MotionEvent.ACTION_UP);
			return;
		}
		
		if(!replacedListener){
			btnMove.getListeners().clear();
			btnMove.setText("Buy");
			btnMove.getListeners().add(new ComponentListenerImpl(){
				public void touchUp(Component component, Game game){
					if(currentProvider == playerProvider){
						if(!getCurrentShop(game).sell(selectedItem, game.getPlayer()))
							game.getHelper().dialog("This shop cannot afford to purchase that!");
					}else if(currentProvider == containerProvider)
						if(!getCurrentShop(game).purchase(selectedItem, game.getPlayer()))
							game.getHelper().dialog("You cannot afford to purchase that!");
					btnBack.simulateInput(game, MotionEvent.ACTION_UP);
				}
				
				public void update(Component component, Game game){
					if(currentProvider == playerProvider)
						component.setText("Sell");
					else if(currentProvider == containerProvider)
						component.setText("Buy");
					else
						component.setText("Hmmm...");
				}
			});
			replacedListener = true;
		}
	}
	
	public Shop getCurrentShop(Game game){
		return (Shop)game.getGlobal("current_shop");
	}
}
