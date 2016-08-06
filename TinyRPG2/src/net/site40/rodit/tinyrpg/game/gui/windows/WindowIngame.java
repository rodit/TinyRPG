package net.site40.rodit.tinyrpg.game.gui.windows;

import java.util.ArrayList;
import java.util.Collections;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Input;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.chat.Chat.ChatMessage;
import net.site40.rodit.tinyrpg.game.effect.Effect;
import net.site40.rodit.tinyrpg.game.gui.Gui;
import net.site40.rodit.tinyrpg.game.gui.GuiIngameMenu;
import net.site40.rodit.tinyrpg.game.gui.GuiOptions;
import net.site40.rodit.tinyrpg.game.gui.GuiPlayerInventory;
import net.site40.rodit.tinyrpg.game.gui.GuiQuest;
import net.site40.rodit.util.ColorGradient;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class WindowIngame extends Window{

	public WindowIngame(Game game){
		super(game);
	}
	
	public WindowControlComponent getControlComponent(int key){
		for(WindowComponent comp : getComponents())
			if(comp instanceof WindowControlComponent && ((WindowControlComponent)comp).inputKey == key)
				return (WindowControlComponent)comp;
		return null;
	}
	
	@Override
	public void initialize(Game game){
		this.zIndex = 1;
		this.drawBackground = false;
		
		WindowControlComponent btnUp = new WindowControlComponent(Input.KEY_UP, true, "controls/up.png");
		btnUp.setX(92);
		btnUp.setY(720 - 92 * 3);
		btnUp.setWidth(92);
		btnUp.setHeight(92);
		add(btnUp);

		WindowControlComponent btnDown = new WindowControlComponent(Input.KEY_DOWN, true, "controls/down.png");
		btnDown.setX(92);
		btnDown.setY(720 - 92);
		btnDown.setWidth(92);
		btnDown.setHeight(92);
		add(btnDown);

		WindowControlComponent btnLeft = new WindowControlComponent(Input.KEY_LEFT, true, "controls/left.png");
		btnLeft.setX(0);
		btnLeft.setY(720 - 92 * 2);
		btnLeft.setWidth(92);
		btnLeft.setHeight(92);
		add(btnLeft);

		WindowControlComponent btnRight = new WindowControlComponent(Input.KEY_RIGHT, true, "controls/right.png");
		btnRight.setX(92 * 2);
		btnRight.setY(720 - 92 * 2);
		btnRight.setWidth(92);
		btnRight.setHeight(92);
		add(btnRight);

		WindowControlComponent btnMenu = new WindowControlComponent(Input.KEY_MENU, false, "controls/menu.png");
		btnMenu.setBackgroundDefault("controls/menu.png");
		btnMenu.setX(1280 - 92 * 4);
		btnMenu.setY(720 - 128);
		btnMenu.setWidth(92);
		btnMenu.setHeight(92);
		add(btnMenu);
		
		WindowControlComponent btnAction = new WindowControlComponent(Input.KEY_ACTION, false, "controls/action.png");
		btnAction.setX(1280 - 92 * 2);
		btnAction.setY(720 - 128);
		btnAction.setWidth(92);
		btnAction.setHeight(92);
		add(btnAction);
		
		final ColorGradient healthTextGradient = new ColorGradient(255, 255, 255, 255, 0, 0);
		final ColorGradient magikaTextGradient = healthTextGradient;
		WindowComponent playerPanel = new WindowComponent(){
			@Override
			public void draw(Game game, Canvas canvas){
				if(game.getBattle() == null && game.isShowingDialog())
					return;
				if(game.getWindows().anyVisibleInstancesOf(WindowInventory.class))
					return;
				for(Gui gui : game.getGuis().list()){
					if(game.getBattle() != null)
						break;
					if(gui.isActive() && (gui instanceof GuiIngameMenu || gui instanceof GuiPlayerInventory || gui instanceof GuiOptions || gui instanceof GuiQuest))
						return;
				}

				paint.setTextAlign(Align.CENTER);
				paint.setTextSize(Values.FONT_SIZE_SMALL - 4f);
				paint.setStyle(Style.FILL);

				canvas.drawBitmap(game.getResources().getBitmap("gui/pp/bg.png"), null, getBoundsF(), paint);

				float healthRatio = game.getPlayer().getHealthRatio();
				RectF healthBounds = new RectF(getX() + 32f, getY() + 40f, getX() + 232f, getY() + 68f);
				paint.setColor(Color.rgb(180, 0, 0));
				canvas.drawRect(healthBounds, paint);

				float[] healthCenter = new float[] { healthBounds.centerX(), healthBounds.centerY() + 10 };

				float healthBarWidth = healthBounds.width() * healthRatio;
				healthBounds.right = healthBounds.left + healthBarWidth;
				paint.setColor(Color.rgb(0, 170, 0));
				canvas.drawRect(healthBounds, paint);
				paint.setColor(healthTextGradient.getColor(healthRatio));
				canvas.drawText(game.getPlayer().getHealth() + "/" + game.getPlayer().getMaxHealth(), healthCenter[0], healthCenter[1], paint);

				float magikaRatio = game.getPlayer().getMagikaRatio();
				RectF magikaBounds = new RectF(getX() + 32f, getY() + 80f, getX() + 232f, getY() + 108f);
				paint.setColor(Color.rgb(0, 0, 120));
				canvas.drawRect(magikaBounds, paint);
				
				float[] magikaCenter = new float[] { magikaBounds.centerX(), magikaBounds.centerY() + 10 };

				RectF magikaBoundsFull = new RectF(magikaBounds.left, magikaBounds.top, magikaBounds.left + magikaBounds.width() * magikaRatio, magikaBounds.bottom);
				paint.setColor(Color.BLUE);
				canvas.drawRect(magikaBoundsFull, paint);
				paint.setColor(magikaTextGradient.getColor(magikaRatio));
				canvas.drawText(game.getPlayer().getMagika() + "/" + game.getPlayer().getMaxMagika(), magikaCenter[0], magikaCenter[1], paint);

				ArrayList<String> drawn = new ArrayList<String>();
				for(Effect e : game.getPlayer().getEffects()){
					if(drawn.contains(e.getName()))
						continue;
					canvas.drawBitmap(game.getResources().getBitmap(e.getResource()), null, new RectF(32f + drawn.size() * 32f, getY() + 120f, 32f + drawn.size() * 32f + 32f, getY() + 120f + 32f), paint);
					drawn.add(e.getName());
				}
			}
		};
		playerPanel.setName("playerPanel");
		playerPanel.getPaint().setTextSize(14f);
		playerPanel.setX(0f);
		playerPanel.setY(0f);
		playerPanel.setWidth(320f);
		playerPanel.setHeight(160f);
		add(playerPanel);
		
		WindowComponent chatTxt = new WindowComponent(){
			public static final int MAX_MESSAGE_COUNT = 5;
			
			@Override
			public void draw(Game game, Canvas canvas){
				if(game.isShowingDialog())
					return;
				
				paint.setAlpha(game.getGlobali("chat_bg_alpha"));
				paint.setColor(Color.rgb(51, 51, 51));
				canvas.drawRect(getBoundsF(), paint);
				
				paint.setAlpha(game.getGlobali("chat_text_alpha"));
				paint.setColor(Color.WHITE);
				paint.setTextSize(Values.FONT_SIZE_SMALL);
				paint.setTextAlign(Align.LEFT);
				ArrayList<ChatMessage> messages = game.getChat().getMessages();
				Collections.reverse(messages);
				ArrayList<ChatMessage> toDraw = new ArrayList<ChatMessage>();
				
				for(int i = 0; i < messages.size() && i < MAX_MESSAGE_COUNT; i++)
					toDraw.add(messages.get(i));
				
				Collections.reverse(toDraw);
				
				for(int i = 0; i < toDraw.size() && i < MAX_MESSAGE_COUNT; i++)
					canvas.drawText(toDraw.get(i).getFullMessage(), getX(), getY() + i * paint.getTextSize(), paint);
			}
		};
		chatTxt.setName("chatTxt");
		chatTxt.setBounds(0f, 360f, 192f, 256f);
		add(chatTxt);
	}
}
