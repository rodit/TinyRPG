package net.site40.rodit.tinyrpg.game.gui;

import net.site40.rodit.tinyrpg.game.Game;


public interface ComponentListener {

	public void update(Component component, Game game);
	public void touchDown(Component component, Game game);
	public void touchUp(Component component, Game game);
	
	public static class ComponentListenerImpl implements ComponentListener{

		public void update(Component component, Game game){}
		public void touchDown(Component component, Game game){}
		public void touchUp(Component component, Game game){}
	}
}
