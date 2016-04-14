package net.site40.rodit.tinyrpg.game.gui;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Input;
import net.site40.rodit.util.RenderUtil;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.RectF;

public class GuiIngame extends Gui{

	public GuiIngame(){
		super("");
	}

	@Override
	public void init(){
		ControlButton btnUp = new ControlButton(Input.KEY_UP, "btnUp", true);
		btnUp.setBackgroundDefault("controls/up.png");
		btnUp.setX(92);
		btnUp.setY(720 - 92 * 3);
		btnUp.setWidth(92);
		btnUp.setHeight(92);
		add(btnUp);

		ControlButton btnDown = new ControlButton(Input.KEY_DOWN, "btnDown", true);
		btnDown.setBackgroundDefault("controls/down.png");
		btnDown.setX(92);
		btnDown.setY(720 - 92);
		btnDown.setWidth(92);
		btnDown.setHeight(92);
		add(btnDown);

		ControlButton btnLeft = new ControlButton(Input.KEY_LEFT, "btnLeft", true);
		btnLeft.setBackgroundDefault("controls/left.png");
		btnLeft.setX(0);
		btnLeft.setY(720 - 92 * 2);
		btnLeft.setWidth(92);
		btnLeft.setHeight(92);
		add(btnLeft);

		ControlButton btnRight = new ControlButton(Input.KEY_RIGHT, "btnRight", true);
		btnRight.setBackgroundDefault("controls/right.png");
		btnRight.setX(92 * 2);
		btnRight.setY(720 - 92 * 2);
		btnRight.setWidth(92);
		btnRight.setHeight(92);
		add(btnRight);

		ControlButton btnMenu = new ControlButton(Input.KEY_MENU, "btnMenu", false);
		btnMenu.setBackgroundDefault("controls/menu.png");
		btnMenu.setX(1280 - 92 * 4);
		btnMenu.setY(720 - 128);
		btnMenu.setWidth(92);
		btnMenu.setHeight(92);
		add(btnMenu);
		
		ControlButton btnAction = new ControlButton(Input.KEY_ACTION, "btnAction", false);
		btnAction.setBackgroundDefault("controls/action.png");
		btnAction.setX(1280 - 92 * 2);
		btnAction.setY(720 - 128);
		btnAction.setWidth(92);
		btnAction.setHeight(92);
		add(btnAction);
		
		Component playerPanel = new Component(){
			@Override
			public void draw(Game game, Canvas canvas){
				if(game.getBattle() == null && game.isShowingDialog())
					return;
				for(Gui gui : game.getGuis().list()){
					if(game.getBattle() != null)
						break;
					if(gui.isActive() && (gui instanceof GuiIngameMenu || gui instanceof GuiInventory || gui instanceof GuiOptions || gui instanceof GuiQuest))
						return;
				}
				
				RenderUtil.drawBitmapBox(canvas, game, getBoundsF(), getPaint());
				//PLAYER NAME
				getPaint().setColor(Color.BLACK);
				getPaint().setTextAlign(Align.LEFT);
				canvas.drawText(game.getPlayer().getUsername(), getX() + 24, getY() + 32, getPaint());
				//PLAYER HEALTH
				int darkRed = Color.RED;
				int red = Color.GREEN;
				getPaint().setColor(darkRed);
				RectF healthBounds = new RectF(getX() + 24, getY() + 40, getX() + 232, getY() + 60);
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
				RectF magikaBounds = new RectF(getX() + 24, getY() + 72, getX() + 232, getY() + 92);
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
		};
		playerPanel.setName("playerPanel");
		playerPanel.getPaint().setTextSize(14f);
		playerPanel.setX(0f);
		playerPanel.setY(0f);
		playerPanel.setWidth(256f);
		playerPanel.setHeight(128f);
		add(playerPanel);
	}
	
	@Override
	public boolean shouldDrawOverDialog(){
		return true;
	}
}
