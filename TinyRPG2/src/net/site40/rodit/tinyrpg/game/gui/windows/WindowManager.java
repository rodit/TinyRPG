package net.site40.rodit.tinyrpg.game.gui.windows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Input;
import net.site40.rodit.tinyrpg.game.event.EventReceiver.EventType;
import net.site40.rodit.util.Util;
import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class WindowManager {

	public static Comparator<Window> windowComparator = new Comparator<Window>(){
		public int compare(Window w0, Window w1){
			return w1.zIndex - w0.zIndex;
		}
	};

	private LinkedHashMap<Class<? extends Window>, Window> windows;
	private int modCount;

	public WindowManager(){
		this.windows = new LinkedHashMap<Class<? extends Window>, Window>();
		this.modCount = 0;
	}

	public void register(Window window){
		windows.put(window.getClass(), window);
		modCount++;
	}

	public void initialize(Game game){

	}

	public Window get(Class<? extends Window> cls){
		return windows.get(cls);
	}

	public boolean anyVisibleInstancesOf(Class<? extends Window> cls){
		for(Window window : windows.values())
			if(cls.isInstance(window) && window.isVisible())
				return true;
		return false;
	}

	private ArrayList<Window> tWindowList = new ArrayList<Window>();
	public void touchInput(Game game, MotionEvent event){
		tWindowList.clear();
		tWindowList.addAll(windows.values());
		Collections.reverse(tWindowList);
		for(Window window : tWindowList)
			if(window.touchInput(game, event) == WindowEventStatus.HANDLED)
				break;
	}

	private ArrayList<Window> kWindowList = new ArrayList<Window>();
	public void keyInput(Game game, KeyEvent event){
		kWindowList.clear();
		kWindowList.addAll(windows.values());
		Collections.reverse(kWindowList);
		for(Window window : kWindowList)
			window.keyInput(game, event);
	}

	private boolean uAllowInput;
	private ArrayList<Window> uReversed = new ArrayList<Window>();
	private ArrayList<Window> uCopy = new ArrayList<Window>();
	public void update(Game game){
		if(modCount > 0)
			Util.sortValuesInMap(windows, windowComparator);

		if(!game.getGlobalb("transitioning")){
			uAllowInput = true;
			for(Window window : windows.values()){
				if(window.isVisible() && window.swallowsInput())
					uAllowInput = false;
			}
			game.getInput().allowMovement(uAllowInput && !game.isShowingDialog());
		}

		if(game.getInput().isUp(Input.KEY_MENU)){
			uReversed.clear();
			uReversed.addAll(windows.values());
			Collections.reverse(uReversed);
			for(Window window : uReversed){
				if(window.canClose() && window.isVisible()){
					window.close();
					game.getEvents().onEvent(game, EventType.WINDOW_CLOSED_BACK, window);
					break;
				}
			}
		}

		uCopy.clear();
		uCopy.addAll(windows.values());
		for(Window window : uCopy)
			if(window.isClosed())
				windows.remove(window.getClass());

		for(Window window : windows.values())
			window.update(game);

		modCount = 0;
	}

	public void draw(Game game, Canvas canvas){
		for(Window window : windows.values())
			window.draw(game, canvas);
	}
}
