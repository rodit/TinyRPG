package net.site40.rodit.tinyrpg.game.gui;

import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.GameObject;
import net.site40.rodit.tinyrpg.game.render.Animation;

public abstract class Gui extends GameObject{
	
	private ArrayList<Component> addQueue = new ArrayList<Component>();
	private ArrayList<Component> removeQueue = new ArrayList<Component>();

	private String background;
	private ArrayList<Component> components;
	protected boolean active;

	public Gui(){
		this("");
	}

	public Gui(String background){
		this.background = background;
		this.addQueue = new ArrayList<Component>();
		this.removeQueue = new ArrayList<Component>();
		this.components = new ArrayList<Component>();
		this.init();
	}

	public abstract void init();

	public void onShown(){}
	public void onHidden(){}

	public String getBackground(){
		return background;
	}

	public void setBackground(String background){
		this.background = background;
	}

	public ArrayList<Component> getComponents(){
		return components;
	}

	public Component get(String name){
		for(Component c : components)
			if(c.getName().equals(name))
				return c;
		return null;
	}

	public void add(Component c){
		synchronized(addQueue){
			addQueue.add(c);
		}
	}
	
	public void remove(Component c){
		synchronized(removeQueue){
			removeQueue.add(c);
		}
	}

	public boolean isActive(){
		return active;
	}

	public void setActive(boolean active){
		this.active = active;
	}

	public void input(MotionEvent event, Game game){
		if(!active)
			return;
		if(!shouldDrawOverDialog() && game.isShowingDialog())
			return;
		synchronized(components){
			for(Iterator<Component> iterator = components.iterator(); iterator.hasNext();){
				Component c = iterator.next();
				if(c.getBoundsF().contains(event.getX(), event.getY()) || event.getAction() == MotionEvent.ACTION_MOVE)
					c.input(event, game);
			}
		}
	}

	public void keyInput(KeyEvent event, Game game){
		if(!active)
			return;
		if(!shouldDrawOverDialog() && game.isShowingDialog())
			return;
		for(Component c : components){
			if(c instanceof TextboxComponent)
				((TextboxComponent)c).keyEvent(event, game);
		}
	}
	
	public synchronized ArrayList<Component> getQueue(){
		return addQueue;
	}

	public void update(Game game){
		if(!active)
			return;
		if(!shouldDrawOverDialog() && game.isShowingDialog())
			return;
		synchronized(addQueue){
			for(Component c : addQueue){
				components.add(c);
				c.setGui(this);
			}
			addQueue.clear();
		}
		synchronized(removeQueue){
			components.removeAll(removeQueue);
			removeQueue.clear();
		}
		
		for(Component c : components)
			c.update(game);
	}

	@Override
	public void draw(Game game, Canvas canvas){
		if(!shouldDrawOverDialog() && game.isShowingDialog())
			return;
		
		super.preRender(game, canvas);

		if(!active)
			return;
		if(!TextUtils.isEmpty(background)){
			Object o = game.getResources().getObject(background);
			if(o instanceof Bitmap)
				canvas.drawBitmap((Bitmap)o, null, new RectF(0, 0, 1280, 720), null);
			else if(o instanceof Animation){
				canvas.drawBitmap(((Animation)o).getFrame(game.getTime()), null, new RectF(0, 0, 1280, 720), null);
			}
		}
		for(Component c : components)
			c.draw(game, canvas);

		super.postRender(game, canvas);
	}
	
	public boolean shouldDrawOverDialog(){
		return false;
	}

	@Override
	public RenderLayer getRenderLayer(){
		return RenderLayer.TOP_ALL;
	}

	@Override
	public boolean shouldScale(){
		return false;
	}
}
