package net.site40.rodit.tinyrpg.game.gui2;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.render.ResourceWrapper;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class Gui {

	protected String name;
	protected String background;
	protected ArrayList<Component> components;
	protected boolean active;
	protected boolean visible;
	protected Paint paint;

	public Gui(){
		this("");
	}

	public Gui(String name){
		this(name, "");
	}

	public Gui(String name, String background, Component... components){
		this.name = name;
		this.background = background;
		this.components = new ArrayList<Component>();
		for(int i = 0; components != null && i < components.length; i++)
			this.components.add(components[i]);
		this.active = this.visible = true;
		this.paint = Game.getDefaultPaint();
	}
	
	public void onShow(){
		
	}
	
	public void onHide(){
		
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

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
		for(Component component : components)
			if(component.getName().equals(name))
				return component;
		return null;
	}

	public void add(Component component){
		if(!components.contains(component))
			components.add(component);
		component.setParent(this);
	}

	public void remove(Component component){
		components.remove(component);
	}

	public boolean isActive(){
		return active;
	}

	public void setActive(boolean active){
		this.active = active;
	}

	public boolean isVisible(){
		return visible;
	}

	public void setVisible(boolean visible){
		this.visible = visible;
	}

	public Paint getPaint(){
		return paint;
	}

	public void setPaint(Paint paint){
		setPaint(paint, false);
	}

	public void setPaint(Paint paint, boolean ref){
		this.paint = ref ? paint : new Paint(paint);
	}

	public void handleTouchInput(Game game, MotionEvent event){
		for(Component c : components)
			if(c.isActive() && (event.getAction() == MotionEvent.ACTION_MOVE || c.getBoundsF().contains(event.getX(), event.getY())))
				c.handleTouchInput(game, event);
	}

	public void handleKeyInput(Game game, KeyEvent event){
		for(Component c : components)
			if(c.isActive())
				c.handleKeyInput(game, event);
	}

	public void update(Game game){
		for(Component c : components)
			if(c.isActive())
				c.update(game);
	}
	
	public void draw(Canvas canvas, Game game){
		ResourceWrapper bgWrapper = new ResourceWrapper(game.getResources().getObject(this.background));
		bgWrapper.draw(game, canvas, 0, 0, 1280, 720, paint);
		
		for(Component c : components)
			if(c.isVisible())
				c.draw(canvas, game);
	}
}
