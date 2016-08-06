package net.site40.rodit.tinyrpg.game.gui.windows;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.npc.TempShopOwner;
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
	}
}
