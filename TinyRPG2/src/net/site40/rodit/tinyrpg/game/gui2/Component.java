package net.site40.rodit.tinyrpg.game.gui2;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.render.ResourceWrapper;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class Component {

	public static final int FLAG_IGNORE_CUSTOM_CENTERING = 1;
	public static final int FLAG_DONT_DRAW = 2;
	public static final int FLAG_DONT_NOTIFY_LISTENERS = 3;

	public static int STATE_ALL = -1;
	public static int STATE_IDLE = 0;
	public static int STATE_TOUCH = 1;

	private Gui parent;
	private String name;
	private String text;
	private SparseArray<String> states;
	private int state;
	private float x, y, width, height;
	private boolean active;
	private boolean visible;
	private Paint paint;

	private ArrayList<IComponentListener> listeners;

	private Object tag;

	public int flag;

	public Component(){
		this("");
	}

	public Component(String name){
		this(name, "");
	}

	public Component(String name, String text){
		this(name, text, 0f, 0f, 1f, 1f);
	}

	public Component(String name, String text, float x, float y, float width, float height){
		this.name = name;
		this.text = text;
		this.state = 0;
		this.states = new SparseArray<String>();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.active = this.visible = true;
		this.listeners = new ArrayList<IComponentListener>();
		this.paint = Game.getDefaultPaint();
	}

	public Gui getParent(){
		return parent;
	}

	public void setParent(Gui parent){
		this.parent = parent;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getText(){
		return text;
	}

	public void setText(String text){
		this.text = text;
	}

	public SparseArray<String> getStates(){
		return states;
	}

	public int getState(){
		return state;
	}

	public void setState(int state){
		this.state = state;
	}

	public String getBackground(){
		return getBackground(state);
	}

	public String getBackground(int state){
		return states.get(state);
	}

	public void setBackground(String background){
		setBackground(STATE_IDLE, background);
		setBackground(STATE_TOUCH, background);
	}

	public void setBackground(int state, String background){
		if(state == STATE_ALL)
			setBackground(background);
		else
			states.put(state, background);
	}

	public float getX(){
		return x;
	}

	public void setX(float x){
		this.x = x;
	}

	public float getY(){
		return y;
	}

	public void setY(float y){
		this.y = y;
	}

	public float getWidth(){
		return width;
	}

	public void setWidth(float width){
		this.width = width;
	}

	public float getHeight(){
		return height;
	}

	public void setHeight(float height){
		this.height = height;
	}

	public Rect getBounds(){
		if(width == 0 || height == 0){
			height = paint.getTextSize();
			width = paint.measureText(text);
			x -= width / 2;
		}
		return new Rect((int)x, (int)y, (int)(x + width), (int)(y + height));
	}

	public RectF getBoundsF(){
		return new RectF(x, y, x + width, y + height);
	}

	public void setBounds(Rect bounds){
		this.x = bounds.left;
		this.y = bounds.top;
		this.width = bounds.width();
		this.height = bounds.height();
	}

	public void setBounds(RectF bounds){
		this.x = bounds.left;
		this.y = bounds.top;
		this.width = bounds.width();
		this.height = bounds.height();
	}

	public void setBounds(float x, float y, float width, float height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
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

	public ArrayList<IComponentListener> getListeners(){
		return listeners;
	}

	public void attach(IComponentListener listener){
		if(!listeners.contains(listener))
			listeners.add(listener);
	}

	public void detach(IComponentListener listener){
		listeners.remove(listener);
	}

	public Object getTag(){
		return tag;
	}

	public void setTag(Object tag){
		this.tag = tag;
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

	@SuppressWarnings("unchecked")
	public <T>T getTag(Class<?> T){
		return (T)tag;
	}

	public void handleTouchInput(Game game, MotionEvent event){
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			setState(STATE_TOUCH);
			if(flag != FLAG_DONT_NOTIFY_LISTENERS)
				for(IComponentListener listener : listeners)
					listener.touchDown(game, event, this);
			break;
		case MotionEvent.ACTION_UP:
			if(state == STATE_TOUCH){
				setState(STATE_IDLE);
				if(flag != FLAG_DONT_NOTIFY_LISTENERS)
					for(IComponentListener listener : listeners)
						listener.touchUp(game, event, this);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if(!getBoundsF().contains(event.getX(), event.getY())){
				String tag = getTag(String.class);
				if(tag.startsWith("KEY_"))
					if(flag != FLAG_DONT_NOTIFY_LISTENERS)
						for(IComponentListener listener : listeners)
							listener.touchUp(game, event, this);
				setState(STATE_IDLE);
			}
			break;
		}
	}
	
	public void handleKeyInput(Game game, KeyEvent event){
		switch(event.getAction()){
		case KeyEvent.ACTION_DOWN:
			if(flag != FLAG_DONT_NOTIFY_LISTENERS)
				for(IComponentListener listener : listeners)
					listener.keyUp(game, event, this);
			break;
		case KeyEvent.ACTION_UP:
			if(flag != FLAG_DONT_NOTIFY_LISTENERS)
				for(IComponentListener listener : listeners)
					listener.keyUp(game, event, this);
			break;
		}
	}

	public void update(Game game){
		if(flag != FLAG_DONT_NOTIFY_LISTENERS)
			for(IComponentListener listener : listeners)
				listener.update(game, this);
	}

	public void draw(Canvas canvas, Game game){
		if(flag != FLAG_DONT_DRAW){
			String currentBackground = getBackground();
			String background = (visible && currentBackground != null && !TextUtils.isEmpty(currentBackground)) ? currentBackground : null;
			if(background != null && !TextUtils.isEmpty(background)){
				ResourceWrapper wrapper = new ResourceWrapper(game.getResources().getObject(background));
				wrapper.draw(game, canvas, x, y, width, height, paint);
			}

			if(text != null && !TextUtils.isEmpty(text)){
				String[] lines = text.split("\n");
				for(int i = 0; i < lines.length; i++)
					if(paint.getTextAlign() == Align.CENTER && flag != FLAG_IGNORE_CUSTOM_CENTERING)
						drawCenteredText(canvas, lines[i], getBounds());
					else
						canvas.drawText(lines[i], x, y + (float)i * paint.getTextSize(), paint);
			}
		}

		if(flag != FLAG_DONT_NOTIFY_LISTENERS)
			for(IComponentListener listener : listeners)
				listener.draw(game, canvas, this);
	}

	protected void drawCenteredText(Canvas canvas, String text, Rect r){
		int width = r.width();
		int numOfChars = paint.breakText(text, true, width, null);
		int start = (text.length() - numOfChars) / 2;
		canvas.drawText(text, start, start + numOfChars, r.exactCenterX(), r.exactCenterY() + paint.getTextSize() / 4, paint);
	}
}
