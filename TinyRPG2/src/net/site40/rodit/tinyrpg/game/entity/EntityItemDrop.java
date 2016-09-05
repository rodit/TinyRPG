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

		if(stack != null)
			inventory.add(stack);
		
		ticker.setInterval(500L);
	}
	
	@Override
	public void onAction(Game game, Entity actor){
		game.setGlobal("current_container", this);
		WindowContainer containerWindow = new WindowContainer(game, this);
		game.getWindows().register(containerWindow);
		containerWindow.show();
	}
	
	@Override
	public void tick(Game game){
		if(inventory.isEmpty() && !game.getWindows().anyVisibleInstancesOf(WindowContainer.class))
			game.getMap().despawn(game, this);
	}
}
