package net.site40.rodit.tinyrpg.game.entity.npc;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.item.Inventory;
import net.site40.rodit.tinyrpg.game.shop.Shop;
import android.graphics.Canvas;

public class TempShopOwner extends EntityLiving{
	
	private Shop shop;
	
	public TempShopOwner(Shop shop){
		super();
		this.shop = shop;
	}
	
	@Override
	public Inventory getInventory(){
		return shop.getInventory();
	}
	
	@Override
	public void update(Game game){ return; }
	@Override
	public void draw(Game game, Canvas canvas){ return; }
}
