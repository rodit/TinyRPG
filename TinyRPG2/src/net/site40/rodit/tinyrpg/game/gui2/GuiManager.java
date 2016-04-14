package net.site40.rodit.tinyrpg.game.gui2;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.EntityPlayer;
import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class GuiManager {

	private EntityPlayer player;
	private ArrayList<Gui> guis;

	public GuiManager(Game game, EntityPlayer player){
		this.player = player;
		this.guis = new ArrayList<Gui>();

		hide(LinkedGui.load(game, "gui/xml/menu.xml"));
		hide(new GuiIngame());
		//hide(LinkedGui.load(game, "gui/xml/ingame.xml"));
		hide(LinkedGui.load(game, "gui/xml/setname.xml"));
	}

	public EntityPlayer getPlayer(){
		return player;
	}

	public void setPlayer(EntityPlayer player){
		this.player = player;
	}

	public ArrayList<Gui> getGuis(){
		return guis;
	}

	public void show(String guiName){
		Gui g = get(guiName);
		show(g);
	}

	public void show(Gui g){
		if(g == null)
			return;
		if(!guis.contains(g))
			guis.add(g);
		g.setVisible(true);
		g.setActive(true);
		g.onShow();
	}

	public void hide(String guiName){
		Gui g = get(guiName);
		if(g != null)
			hide(g);
	}

	public void hide(Gui g){
		show(g);
		g.setVisible(false);
		g.setActive(false);
		g.onHide();
	}

	public Gui get(String name){
		for(Gui g : guis)
			if(g.getName().equals(name))
				return g;
		return null;
	}

	public Gui get(Class<? extends Gui> type){
		for(Gui g : guis)
			if(g.getClass() == type)
				return g;
		return null;
	}

	public boolean isShowing(String name){
		return isShowing(get(name));
	}

	public boolean isShowing(Gui g){
		if(g == null)
			return false;
		return g.isVisible();
	}

	public void handleTouchInput(Game game, MotionEvent event){
		for(Gui g : guis)
			if(g.isActive())
				g.handleTouchInput(game, event);
	}

	public void handleKeyInput(Game game, KeyEvent event){
		for(Gui g : guis)
			if(g.isActive())
				g.handleKeyInput(game, event);
	}

	public void update(Game game){
		for(Gui g : guis)
			if(g.isActive())
				g.update(game);
	}

	public void draw(Canvas canvas, Game game){
		for(Gui g : guis)
			if(g.isVisible())
				g.draw(canvas, game);
	}
}
