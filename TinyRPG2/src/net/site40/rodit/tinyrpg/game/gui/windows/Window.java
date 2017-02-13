package net.site40.rodit.tinyrpg.game.gui.windows;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.object.GameObject;
import net.site40.rodit.util.RenderUtil;
import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;

public abstract class Window extends GameObject{
	
	public int zIndex = 99;

	protected boolean drawBackground;
	private boolean visible;
	private boolean hasFocus;
	private boolean swallowInput = true;
	protected boolean canClose;
	private boolean closed;
	private ArrayList<WindowComponent> components;
	private ArrayList<WindowComponent> addQueue;
	private ArrayList<WindowComponent> removeQueue;

	public Window(Game game){
		this(game, true);
	}

	public Window(Game game, boolean drawBackground){
		this.drawBackground = drawBackground;
		this.visible = false;
		this.hasFocus = false;
		this.canClose = true;
		this.components = new ArrayList<WindowComponent>();
		this.addQueue = new ArrayList<WindowComponent>();
		this.removeQueue = new ArrayList<WindowComponent>();
		
		initialize(game);
	}

	public abstract void initialize(Game game);

	public void add(WindowComponent component){
		synchronized(addQueue){
			addQueue.add(component);
		}
	}

	public void remove(WindowComponent component){
		synchronized(removeQueue){
			removeQueue.remove(component);
		}
	}
	
	public boolean hasFocus(){
		return hasFocus;
	}
	
	public void setFocus(boolean hasFocus){
		this.hasFocus = hasFocus;
	}

	public void show(){
		setVisible(true);
		setFocus(true);
	}

	public void hide(){
		setVisible(false);
		setFocus(false);
	}
	
	public boolean isVisible(){
		return visible;
	}

	public void setVisible(boolean visible){
		this.visible = visible;
	}
	
	public boolean isClosed(){
		return closed;
	}
	
	public void close(){
		this.closed = true;
		hide();
	}

	public void unfocusAll(Game game){
		for(WindowComponent component : components){
			component.setFocus(false, game);
			component.setState(WindowComponent.STATE_IDLE);
		}
	}
	
	public ArrayList<WindowComponent> getComponents(){
		return components;
	}
	
	public WindowEventStatus touchInput(Game game, MotionEvent event){
		if(visible && bounds.get().contains(event.getX(), event.getY()))
			hasFocus = true;
		
		boolean overrideHandled = false;
		
		if(hasFocus && drawBackground)
			overrideHandled = true;
		
		if(!visible || !hasFocus)
			return WindowEventStatus.UNHANDLED;

		WindowEventStatus status = WindowEventStatus.UNHANDLED;
		for(WindowComponent component : components){
			if(component.touchInput(game, event) == WindowEventStatus.HANDLED)
				status = WindowEventStatus.HANDLED;
		}
		
		return overrideHandled ? WindowEventStatus.HANDLED : status;
	}
	
	public void keyInput(Game game, KeyEvent event){
		for(WindowComponent component : components)
			component.keyInput(game, event);
	}
	
	public WindowComponent get(String name){
		for(WindowComponent component : components)
			if(component.getName().equals(name))
				return component;
		return null;
	}
	
	public boolean swallowsInput(){
		return swallowInput;
	}
	
	public void setSwallowInput(boolean swallowInput){
		this.swallowInput = swallowInput;
	}
	
	public boolean canClose(){
		return canClose;
	}
	
	public void setCanClose(boolean canClose){
		this.canClose = canClose;
	}

	@Override
	public void update(Game game){
		synchronized(components){
			synchronized(addQueue){
				for(WindowComponent add : addQueue){
					components.add(add);
					add.setParent(this);
				}
				addQueue.clear();
			}
			synchronized(removeQueue){
				for(WindowComponent remove : removeQueue)
					components.remove(remove);
				removeQueue.clear();
			}
		}

		if(!visible || !hasFocus)
			return;

		for(WindowComponent component : components)
			component.update(game);
	}

	@Override
	public void draw(Game game, Canvas canvas){
		if(!visible)
			return;

		if(drawBackground)
			RenderUtil.drawBitmapBox(canvas, game, bounds.get(), paint);
		
		for(WindowComponent component : components)
			component.draw(game, canvas);
	}

	@Override
	public int getRenderLayer(){ return RenderLayer.TOP_OVER_ALL; }
	@Override
	public boolean shouldScale(){ return false; }
}
