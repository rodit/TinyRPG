package net.site40.rodit.tinyrpg.game.gui.windows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Input;
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
		register(new WindowInventory(game));
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

	public void touchInput(Game game, MotionEvent event){
		if(game.isShowingDialog())
			return;

		ArrayList<Window> windowList = new ArrayList<Window>(windows.values());
		Collections.reverse(windowList);
		for(Window window : windowList)
			if(window.touchInput(game, event) == WindowEventStatus.HANDLED)
				break;
	}

	public void keyInput(Game game, KeyEvent event){
		if(game.isShowingDialog())
			return;

		ArrayList<Window> windowList = new ArrayList<Window>(windows.values());
		Collections.reverse(windowList);
		for(Window window : windowList)
			window.keyInput(game, event);
	}

	public void update(Game game){
		if(modCount > 0){
			Util.sortValuesInMap(windows, windowComparator);
			modCount = 0;
		}

		boolean allowInput = true;
		for(Window window : windows.values()){
			if(window.isVisible() && window.swallowsInput())
				allowInput = false;
		}
		game.getInput().allowMovement(allowInput);

		if(game.getInput().isUp(Input.KEY_MENU)){
			ArrayList<Window> reversed = new ArrayList<Window>(windows.values());
			Collections.reverse(reversed);
			for(Window window : reversed){
				if(window.isVisible()){
					window.setVisible(false);
					break;
				}
			}
		}

		for(Window window : windows.values())
			window.update(game);
	}

	public void draw(Game game, Canvas canvas){
		if(game.isShowingDialog())
			return;

		for(Window window : windows.values())
			window.draw(game, canvas);
	}
}