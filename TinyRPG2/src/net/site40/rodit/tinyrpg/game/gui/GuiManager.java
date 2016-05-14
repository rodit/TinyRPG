package net.site40.rodit.tinyrpg.game.gui;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class GuiManager {

	private ArrayList<Gui> guis;
	
	public GuiManager(){
		guis = new ArrayList<Gui>();
		
		//TODO ADD ALL GUIS
		guis.add(new GuiLoading());
		guis.add(new GuiMenu());
		guis.add(new GuiSetname());
		
		guis.add(new GuiInventory());
		guis.add(new GuiPlayerInventory());
		guis.add(new GuiInventoryPlayer());
		guis.add(new GuiQuest());
		guis.add(new GuiIngameMenu());
		guis.add(new GuiOptions());
		guis.add(new GuiIngame());
		guis.add(new GuiSaves());
		guis.add(new GuiMessage());
		guis.add(new GuiContainer());
		guis.add(new GuiShop());
	}
	
	public ArrayList<Gui> list(){
		return guis;
	}

	public Gui get(Class<?> type){
		for(Gui g : guis){
			if(type.isInstance(g))
				return g;
		}
		return null;
	}
	
	public boolean isVisible(Class<?> type){
		Gui g = get(type);
		return g == null || g.isActive();
	}

	public void show(Class<?> type){
		Gui g = get(type);
		show(g);
	}
	
	public void show(Gui g){
		if(g != null){
			if(!guis.contains(g))
				guis.add(g);
			g.setActive(true);
			g.onShown();
		}
	}
	
	public void hide(Gui g){
		if(g != null){
			g.setActive(false);
			g.onHidden();
		}
	}

	public void hide(Class<?> type){
		Gui g = get(type);
		hide(g);
	}
	
	public void hideAll(){
		for(Gui g : guis)
			hide(g.getClass());
	}

	public void update(Game game){
		for(Gui g : guis)
			g.update(game);
	}

	public void draw(Canvas canvas, Game game){
		for(Gui g : guis)
			g.draw(game, canvas);
	}

	public void input(MotionEvent event, Game game){
		for(Gui g : guis)
			g.input(event, game);
	}

	public void keyInput(KeyEvent event, Game game){
		for(Gui g : guis)
			g.keyInput(event, game);
	}
}
