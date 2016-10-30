package net.site40.rodit.tinyrpg.game.gui;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.GameObject;
import net.site40.rodit.tinyrpg.game.render.Animation;

public class Component extends GameObject{

	public static final int IGNORE_CUSTOM_CENTERING = 1;
	public static final int INACTIVE = 2;
	
	private Gui parent;
	
	private String name;
	private String text;
	private String background;
	private String backgroundSelected;
	private float x, y, width, height;
	private Gui gui;
	private ArrayList<ComponentListener> listeners;
	private boolean selected;
	protected int flag;

	public int tag;

	public Component(){
		this("", "", "", "", null);
	}
	
	public Component(String name){
		this(name, "");
	}
	
	public Component(String name, String text){
		this(name, text, "", "", null);
	}

	public Component(String name, String text, String background, String backgroundSelected, Gui gui){
		this.name = name;
		this.text = text;
		this.background = background;
		this.backgroundSelected = backgroundSelected;
		this.gui = gui;
		this.listeners = new ArrayList<ComponentListener>();
		this.paint = Game.getDefaultPaint();
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(12f);
	}
	
	public Gui getParent(){
		return parent;
	}
	
	public void setParent(Gui parent){
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}
	
	public void setBackgroundDefault(String background){
		setBackground(background);
		setBackgroundSelected(background);
	}

	public String getBackgroundSelected(){
		return this.backgroundSelected;
	}

	public void setBackgroundSelected(String backgroundSelected){
		this.backgroundSelected = backgroundSelected;
	}

	public void setBounds(float x, float y, float width, float height){
		this.setX(x);
		this.setY(y);
		this.setWidth(width);
		this.setHeight(height);
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

	public RectF getBoundsF(){
		return new RectF(x, y, x + width, y + height);
	}

	public Rect getBounds(){
		if(width == 0 || height == 0){
			height = paint.getTextSize();
			width = paint.measureText(text);
			x -= width / 2;
		}
		return new Rect((int)x, (int)y, (int)(x + width), (int)(y + height));
	}

	public Gui getGui() {
		return gui;
	}

	public void setGui(Gui gui) {
		this.gui = gui;
	}

	public ArrayList<ComponentListener> getListeners(){
		return listeners;
	}

	public void addListener(ComponentListener listener){
		listeners.add(listener);
	}

	public void removeListener(ComponentListener listener){
		listeners.remove(listener);
	}

	public Paint getPaint(){
		return paint;
	}

	public void setPaint(Paint paint){
		this.paint = paint;
	}

	public boolean isSelected(){
		return selected;
	}
	
	public void setSelected(boolean selected){
		this.selected = selected;
	}

	public void setFlag(int flag){
		this.flag = flag;
	}
	
	public void input(MotionEvent event, Game game){
		if(flag == INACTIVE)
			return;
		
		int action = event.getAction();
		switch(action){
		case MotionEvent.ACTION_DOWN:
			selected = true;
			for(ComponentListener listener : listeners)
				listener.touchDown(this, game);
			break;
		case MotionEvent.ACTION_UP:
			selected = false;
			for(ComponentListener listener : listeners)
				listener.touchUp(this, game);
			break;
		case MotionEvent.ACTION_MOVE:
			if(!getBoundsF().contains(event.getX(), event.getY()))
				selected = false;
			else if(!selected){
				selected = true;
				for(ComponentListener listener : listeners)
					listener.touchDown(this, game);
			}
			break;
		}
	}

	public void update(Game game){
		if(flag == INACTIVE)
			return;
		for(ComponentListener listener : listeners)
			listener.update(this, game);
	}
	
	public void simulateInput(Game game, int type){
		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis() + 100;
		int metaState = 0;
		MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, type, x, y, metaState);
		input(motionEvent, game);
		motionEvent.recycle();
	}

	@Override
	public void draw(Game game, Canvas canvas){
		if(flag == INACTIVE)
			return;
		
		super.preRender(game, canvas);
		
		String toUse = (selected && !TextUtils.isEmpty(backgroundSelected)) ? backgroundSelected : background;
		if(toUse != null && !TextUtils.isEmpty(toUse)){
			Object o = game.getResources().getObject(toUse);
			if(o instanceof Bitmap)
				canvas.drawBitmap((Bitmap)o, null, new RectF(x, y, x + width, y + height), paint);
			else if(o instanceof Animation){
				canvas.drawBitmap(((Animation)o).getFrame(game.getTime()), null, new RectF(x, y, x + width, y + height), paint);
			}
		}
		if(!TextUtils.isEmpty(text)){
			String[] lines = text.split("\n");
			for(int i = 0; i < lines.length; i++){
				if(paint.getTextAlign() == Align.CENTER && flag != IGNORE_CUSTOM_CENTERING)
					drawCenteredText(canvas, lines[i], getBounds());
				else
					canvas.drawText(lines[i], x, y + (float)i * paint.getTextSize(), paint);
			}
		}
		
		super.postRender(game, canvas);
	}
	
	@Override
	public RenderLayer getRenderLayer(){
		return RenderLayer.TOP_ALL;
	}
	
	@Override
	public boolean shouldScale(){
		return false;
	}

	protected void drawCenteredText(Canvas canvas, String text, Rect r){
		int width = r.width();
		int numOfChars = paint.breakText(text, true, width, null);
		int start = (text.length() - numOfChars) / 2;
		canvas.drawText(text, start, start + numOfChars, r.exactCenterX(), r.exactCenterY() + paint.getTextSize() / 4, paint);
	}
}
