package net.site40.rodit.tinyrpg.game.entity;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowContainer;
import net.site40.rodit.tinyrpg.game.item.ItemStack;

public class EntityItemDrop extends Entity{
	
	public static final float DEFAULT_WIDTH = 16f;
	public static final float DEFAULT_HEIGHT = 16f;
	
	public EntityItemDrop(ItemStack stack){
		super();
		
		this.setResource("entity/bag.png");
		this.setWidth(DEFAULT_WIDTH);
		this.setHeight(DEFAULT_HEIGHT);
	}
	
	@Override
	public void onAction(Game game, Entity actor){
		game.setGlobal("current_container", this);
		WindowContainer containerWindow = new WindowContainer(game, this);
		game.getWindows().register(containerWindow);
		containerWindow.show();
	}
}
