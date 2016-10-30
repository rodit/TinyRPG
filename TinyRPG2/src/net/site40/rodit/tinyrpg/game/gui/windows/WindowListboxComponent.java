package net.site40.rodit.tinyrpg.game.gui.windows;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.item.ItemStack;

public class WindowListboxComponent<T> extends WindowComponent{

	public static final float DEFAULT_OBJECT_HEIGHT = 0f;

	private float objectHeight = DEFAULT_OBJECT_HEIGHT;
	private float scrollOffsetY = 0f;
	private int selectedIndex = -1;

	private ListboxComponentRenderer<T> renderer;
	private ArrayList<T> addQueue;
	private ArrayList<T> removeQueue;
	private ArrayList<T> objects;

	private float lastPointerY = 0f;

	public WindowListboxComponent(){
		this.addQueue = new ArrayList<T>();
		this.removeQueue = new ArrayList<T>();
		this.objects = new ArrayList<T>();
	}

	public ListboxComponentRenderer<T> getRenderer(){
		return renderer;
	}

	public void setRenderer(ListboxComponentRenderer<T> renderer){
		this.renderer = renderer;
	}

	public ArrayList<T> getObjects(){
		return objects;
	}

	public void add(T object){
		synchronized(addQueue){
			addQueue.add(object);
		}
	}

	public void remove(T object){
		synchronized(removeQueue){
			removeQueue.remove(object);
		}
	}

	public float getMaxScrollOffsetY(){
		return objects.size() * (objectHeight / getHeight());
	}

	public int getSelectedIndex(){
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex){
		this.selectedIndex = selectedIndex;
	}

	public T getSelectedObject(){
		if(selectedIndex < 0 || selectedIndex >= objects.size())
			return null;
		return objects.get(selectedIndex);
	}

	public void scroll(float diff){
		float nScroll = scrollOffsetY + diff;
		float min = 0f;
		float max = getMaxScrollOffsetY();
		if(nScroll < min)
			scrollOffsetY = 0f;
		else if(nScroll > max)
			scrollOffsetY = max;
		else
			scrollOffsetY = nScroll;
	}

	public void select(Game game, float y){
		float relativeY = y - getY() - scrollOffsetY;
		int relativeIndex = (int)((relativeY / getHeight()) * (int)(getHeight() / renderer.getObjectHeight(game, this, null)));
		if(relativeIndex >= objects.size())
			setSelectedIndex(-1);
		else
			setSelectedIndex(relativeIndex);
		onSelected(game);
	}
	
	public void clear(){
		synchronized(objects){
			objects.clear();
		}
		synchronized(addQueue){
			addQueue.clear();
		}
		synchronized(removeQueue){
			removeQueue.clear();
		}
	}

	@SuppressWarnings("unchecked")
	public void onSelected(Game game){
		for(WindowListener listener : getListeners()){
			if(listener instanceof ItemSelectedListener)
				((ItemSelectedListener<T>)listener).selected(game, this);
		}
	}
	
	@Override
	public WindowEventStatus touchInput(Game game, MotionEvent event){
		if(getState() == STATE_DISABLED || getFlag(FLAG_INVISIBLE))
			return WindowEventStatus.UNHANDLED;

		if(!this.getScreenBoundsF().contains(event.getX(), event.getY())){
			this.setState(STATE_IDLE);
			this.setFocus(false, game);
			return WindowEventStatus.UNHANDLED;
		}

		boolean wasZero = lastPointerY == -1;
		float pointerY = event.getY();
		float diff = pointerY - lastPointerY;
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			lastPointerY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			if(!wasZero)
			scroll(diff);
			lastPointerY = pointerY;
			break;
		case MotionEvent.ACTION_UP:
			if(diff == 0)
				select(game, pointerY);
			lastPointerY = -1;
			break;
		}
		return WindowEventStatus.HANDLED;
	}

	@Override
	public void draw(Game game, Canvas canvas){
		canvas.save();
		canvas.clipRect(getScreenBoundsF());
		canvas.translate(getScreenX(), getScreenY() - scrollOffsetY);
		for(int i = 0; i < objects.size(); i++){
			T object = objects.get(i);
			if(i == selectedIndex){
				int alpha = paint.getAlpha();
				int color = paint.getColor();
				paint.setAlpha(80);
				paint.setColor(Color.BLACK);
				canvas.drawRect(new RectF(0, 0, getWidth(), renderer.getObjectHeight(game, this, object)), paint);
				paint.setAlpha(alpha);
				paint.setColor(color);
			}
			renderer.render(game, canvas, paint, this, object);
			canvas.translate(0f, renderer.getObjectHeight(game, this, object));
		}
		canvas.restore();
	}
	
	@Override
	public void update(Game game){
		super.update(game);
		
		synchronized(objects){
			synchronized(addQueue){
				for(T object : addQueue)
					objects.add(object);
				addQueue.clear();
			}
			synchronized(removeQueue){
				for(T object : removeQueue)
					objects.remove(object);
				removeQueue.clear();
			}
		}
	}

	public static interface ListboxComponentRenderer<T>{

		public float getObjectHeight(Game game, WindowListboxComponent<T> listbox, T object);		
		public void render(Game game, Canvas canvas, Paint paint, WindowListboxComponent<T> listbox, T object);

		public static class StringRenderer implements ListboxComponentRenderer<String>{

			@Override
			public float getObjectHeight(Game game, WindowListboxComponent<String> listbox, String object){
				return 92f;
			}

			@Override
			public void render(Game game, Canvas canvas, Paint paint, WindowListboxComponent<String> listbox, String object){
				canvas.drawText(object, 8f, 64f, paint);
			}
		}

		public static class ItemStackRenderer implements ListboxComponentRenderer<ItemStack>{

			@Override
			public float getObjectHeight(Game game, WindowListboxComponent<ItemStack> listbox, ItemStack object){
				return 92f;
			}

			@Override
			public void render(Game game, Canvas canvas, Paint paint, WindowListboxComponent<ItemStack> listbox, ItemStack stack){
				Item item = stack.getItem();
				if(item == null)
					return;
				int count = stack.getAmount();
				canvas.drawBitmap(game.getResources().getBitmap(item.getResource()), null, new RectF(8, 8, 8 + 48, 8 + 48), paint);
				canvas.drawText(item.getShowName() + " x " + count, 72f, 24f, paint);
			}
		}
	}
	
	public static class ItemSelectedListener<T> extends WindowListener{
		public void selected(Game game, WindowListboxComponent<T> listbox){}
	}
}
