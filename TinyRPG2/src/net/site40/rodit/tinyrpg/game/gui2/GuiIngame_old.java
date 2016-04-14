package net.site40.rodit.tinyrpg.game.gui2;

import net.site40.rodit.tinyrpg.game.Dialog;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.IGameObject;
import net.site40.rodit.tinyrpg.game.Input;
import net.site40.rodit.tinyrpg.game.gui2.IComponentListener.ComponentListener;
import net.site40.rodit.util.RenderUtil;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

public class GuiIngame_old extends LinkedGui {

	public GuiIngame_old(){

	}
	
	@Override
	public void postInit(){
		for(Component component : components){
			String inputId = component.getTag(String.class);
			if(inputId.startsWith("KEY_")){
				component.attach(new ComponentListener(){				
					public void update(Game game, Component component){
						controlButtonUpdate(game, component);
					}
				});
			}
		};
		get("playerPanel").attach(new ComponentListener(){
			public void draw(Game game, Canvas canvas, Component component){
				for(IGameObject obj : game.getObjects()){
					if(game.getBattle() != null)
						break;
					if(obj instanceof Dialog)
						return;
				}
				if(game.getGuis().isShowing("ingameMenu") || game.getGuis().isShowing("inventory") || game.getGuis().isShowing("options") || game.getGuis().isShowing("quest"))
					return;
				
				Log.i("GuiIngame", "TextSize=" + getPaint().getTextSize());

				RenderUtil.drawBitmapBox(canvas, Game.cornerDefault, Game.sideVDefault, Game.sideHDefault, Game.fillDefault, component.getBoundsF(), getPaint());
				//PLAYER NAME
				getPaint().setColor(Color.BLACK);
				getPaint().setTextAlign(Align.LEFT);
				canvas.drawText(game.getPlayer().getUsername(), component.getX() + 24, component.getY() + 32, getPaint());
				//PLAYER HEALTH
				int darkRed = Color.RED;
				int red = Color.GREEN;
				getPaint().setColor(darkRed);
				RectF healthBounds = new RectF(component.getX() + 24, component.getY() + 40, component.getX() + 232, component.getY() + 60);
				float hcx = healthBounds.centerX();
				float hcy = healthBounds.centerY() + 4;
				canvas.drawRect(healthBounds, getPaint());
				getPaint().setColor(red);
				float healthVal = game.getPlayer().getHealthRatio() * healthBounds.width();
				healthBounds.right = healthBounds.left + healthVal;
				canvas.drawRect(healthBounds, getPaint());
				getPaint().setColor(Color.WHITE);
				getPaint().setTextAlign(Align.CENTER);
				canvas.drawText(game.getPlayer().getHealth() + "/" + game.getPlayer().getMaxHealth(), hcx, hcy, getPaint());
				//PLAYER MAGIKA
				int darkBlue = Color.BLUE;
				int blue = Color.CYAN;
				getPaint().setColor(darkBlue);
				RectF magikaBounds = new RectF(component.getX() + 24, component.getY() + 72, component.getX() + 232, component.getY() + 92);
				float mcx = magikaBounds.centerX();
				float mcy = magikaBounds.centerY() + 4;
				canvas.drawRect(magikaBounds, getPaint());
				getPaint().setColor(blue);
				float magikaVal = game.getPlayer().getMagikaRatio() * magikaBounds.width();
				magikaBounds.right = magikaBounds.left + magikaVal;
				canvas.drawRect(magikaBounds, getPaint());
				getPaint().setColor(Color.WHITE);
				getPaint().setTextAlign(Align.CENTER);
				canvas.drawText(game.getPlayer().getMagika() + "/" + game.getPlayer().getMaxMagika(), mcx, mcy, getPaint());

				getPaint().setTextAlign(Align.LEFT);
				getPaint().setColor(Color.rgb(255, 215, 0));
				canvas.drawText(game.getPlayer().getMoney() + " Gold", 24, magikaBounds.bottom + 14, getPaint());
			}
		});
	}

	public void controlButtonDown(Game game, MotionEvent event, Component component){
		String inputAction = component.getTag(String.class);
		int inputId = -1;
		if(inputAction.equals("KEY_UP"))
			inputId = Input.KEY_UP;
		else if(inputAction.equals("KEY_DOWN"))
			inputId = Input.KEY_DOWN;
		else if(inputAction.equals("KEY_LEFT"))
			inputId = Input.KEY_LEFT;
		else if(inputAction.equals("KEY_RIGHT"))
			inputId = Input.KEY_RIGHT;
		else if(inputAction.equals("KEY_ACTION"))
			inputId = Input.KEY_ACTION;
		else if(inputAction.equals("KEY_MENU"))
			inputId = Input.KEY_MENU;
		else
			return;
		
		game.getInput().setKeyState(inputId, true);
	}

	public void controlButtonUp(Game game, MotionEvent event, Component component){
		String inputAction = component.getTag(String.class);
		int inputId = -1;
		if(inputAction.equals("KEY_UP"))
			inputId = Input.KEY_UP;
		else if(inputAction.equals("KEY_DOWN"))
			inputId = Input.KEY_DOWN;
		else if(inputAction.equals("KEY_LEFT"))
			inputId = Input.KEY_LEFT;
		else if(inputAction.equals("KEY_RIGHT"))
			inputId = Input.KEY_RIGHT;
		else if(inputAction.equals("KEY_ACTION"))
			inputId = Input.KEY_ACTION;
		else if(inputAction.equals("KEY_MENU"))
			inputId = Input.KEY_MENU;
		else
			return;
		
		game.getInput().setKeyState(inputId, false);
		game.getInput().setKeyUpState(inputId, true, game.getTime());
	}

	public void controlButtonUpdate(Game game, Component component){
		String inputAction = component.getTag(String.class);
		int inputId = -1;
		if(inputAction.equals("KEY_UP"))
			inputId = Input.KEY_UP;
		else if(inputAction.equals("KEY_DOWN"))
			inputId = Input.KEY_DOWN;
		else if(inputAction.equals("KEY_LEFT"))
			inputId = Input.KEY_LEFT;
		else if(inputAction.equals("KEY_RIGHT"))
			inputId = Input.KEY_RIGHT;
		else if(inputAction.equals("KEY_ACTION"))
			inputId = Input.KEY_ACTION;
		else if(inputAction.equals("KEY_MENU"))
			inputId = Input.KEY_MENU;
		else
			return;
		
		if(component.getState() != Component.STATE_TOUCH && game.getInput().isUp(inputId) && game.getInput().getUpTime(inputId) != game.getTime())
			game.getInput().setKeyUpState(inputId, false, game.getTime());
	}
	
	@Override
	public void draw(Canvas canvas, Game game){
		super.draw(canvas, game);
	}
}
