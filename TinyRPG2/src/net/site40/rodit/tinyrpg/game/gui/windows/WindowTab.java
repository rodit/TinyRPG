package net.site40.rodit.tinyrpg.game.gui.windows;

import net.site40.rodit.tinyrpg.game.Game;
import android.graphics.Canvas;

public class WindowTab extends WindowComponent{
	
	public static final float TAB_WIDTH = 72f;
	public static final float TAB_HEIGHT = 72f;
	
	public static final String DEFAULT_TAB_BACKGROUND = "gui/inventory/tab.png";
	public static final String DEFAULT_TAB_BACKGROUND_HOVER = "gui/inventory/tab_selected.png";
	
	public static final int STATE_DRAW_TAB = 192;

	private int tabIndex;
	private Object providerKey;
	
	public WindowTab(int tabIndex, Object providerKey){
		this.tabIndex = tabIndex;
		this.providerKey = providerKey;
		this.setBackground(STATE_IDLE, DEFAULT_TAB_BACKGROUND);
		this.setBackground(STATE_DOWN, DEFAULT_TAB_BACKGROUND_HOVER);
	}
	
	public int getTabIndex(){
		return tabIndex;
	}
	
	public Object getProviderKey(){
		return providerKey;
	}
	
	@Override
	public void draw(Game game, Canvas canvas){
		super.draw(game, canvas);
		
		if(getBackground(STATE_DRAW_TAB) == null)
			setBackground(STATE_DRAW_TAB, "gui/inventory/tab/" + tabIndex + ".png");
		
		int oldState = getState();
		this.setState(STATE_DRAW_TAB);
		super.draw(game, canvas);
		this.setState(oldState);
	}
}
