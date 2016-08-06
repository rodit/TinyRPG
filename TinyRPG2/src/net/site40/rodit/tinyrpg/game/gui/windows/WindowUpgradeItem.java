package net.site40.rodit.tinyrpg.game.gui.windows;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.forge.ForgeRegistry.ForgeRecipy.ForgeType;

public class WindowUpgradeItem extends WindowForge{

	public WindowUpgradeItem(Game game){
		super(game);
	}
	
	@Override
	public void initialize(Game game){
		this.type = ForgeType.UPGRADE;
		
		super.initialize(game);
		
		txtTitle.setText("Upgrade Item");
	}
}
