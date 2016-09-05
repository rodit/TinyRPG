package net.site40.rodit.tinyrpg.game.gui;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Benchmark;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Input;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.effect.Effect;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowInventory;
import net.site40.rodit.tinyrpg.game.render.SpriteSheet.MovementState;
import net.site40.rodit.tinyrpg.game.util.Direction;
import net.site40.rodit.util.ColorGradient;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class GuiIngame extends Gui{

	public static final float HEALTH_BAR_OFFSET_X = 132;
	public static final float HEALTH_BAR_OFFSET_Y = 10;
	public static final float HEALTH_BAR_WIDTH = 16;
	public static final float HEALTH_BAR_HEIGHT = 27;

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

		final ColorGradient healthTextGradient = new ColorGradient(255, 255, 255, 255, 0, 0);
		final ColorGradient magikaTextGradient = healthTextGradient;
		Component playerPanel = new Component(){
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
				if(Game.DEBUG)
					Benchmark.start("draw_pp");

				paint.setTextAlign(Align.CENTER);
				paint.setTextSize(Values.FONT_SIZE_SMALL - 4f);
				paint.setStyle(Style.FILL);

				canvas.drawBitmap(game.getResources().getBitmap("gui/pp/icon.png"), null, new RectF(getX(), getY(), getX() + 128f, getY() + 128f), paint);
				int state = canvas.save();
				RectF oBounds = game.getPlayer().getBounds();
				Direction d = game.getPlayer().getDirection();
				MovementState ms = game.getPlayer().getMoveState();

				float clipFact = 24f / 32f;
				game.getPlayer().setBounds(new RectF(18, 42, 18 + 92, 42 + 92));
				game.getPlayer().setDirection(Direction.D_DOWN);
				game.getPlayer().setMoveState(MovementState.IDLE);
				canvas.clipRect(new RectF(18, 42, 18 + 92, 42 + 92f * clipFact));
				game.getPlayer().draw(game, canvas);

				game.getPlayer().setMoveState(ms);
				game.getPlayer().setDirection(d);
				game.getPlayer().setBounds(oBounds);
				canvas.restoreToCount(state);

				float healthRatio = game.getPlayer().getHealthRatio();
				RectF healthBounds = new RectF(getX() + 128f, getY() + 16f, getX() + 384f, getY() + 44f);
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
				RectF magikaBounds = new RectF(getX() + 128f, getY() + 44f, getX() + 328f, getY() + 72f);
				paint.setColor(Color.rgb(0, 0, 120));
				canvas.drawRect(magikaBounds, paint);

				float[] magikaCenter = new float[] { magikaBounds.centerX(), magikaBounds.centerY() + 10 };

				RectF magikaBoundsFull = new RectF(magikaBounds.left, magikaBounds.top, magikaBounds.left + magikaBounds.width() * magikaRatio, magikaBounds.bottom);
				paint.setColor(Color.BLUE);
				canvas.drawRect(magikaBoundsFull, paint);
				paint.setColor(magikaTextGradient.getColor(magikaRatio));
				canvas.drawText(game.getPlayer().getMagika() + "/" + game.getPlayer().getMaxMagika(), magikaCenter[0], magikaCenter[1], paint);

				paint.setColor(Color.YELLOW);
				paint.setTextAlign(Align.LEFT);
				canvas.drawText(game.getPlayer().getMoney() + " Gold", getX() + 132f, getY() + 94f, paint);

				ArrayList<String> drawn = new ArrayList<String>();
				for(Effect e : game.getPlayer().getEffects()){
					if(drawn.contains(e.getName()))
						continue;
					canvas.drawBitmap(game.getResources().getBitmap(e.getResource()), null, new RectF(getX() + 128f + drawn.size() * 32f, getY() + 120f, getX() + 128f + drawn.size() * 32f + 32f, getY() + 120f + 32f), paint);
					drawn.add(e.getName());
				}
				if(Game.DEBUG)
					Benchmark.stop("draw_pp");
			}
		};
		playerPanel.setName("playerPanel");
		playerPanel.getPaint().setTextSize(14f);
		playerPanel.setX(0f);
		playerPanel.setY(0f);
		playerPanel.setWidth(320f);
		playerPanel.setHeight(160f);
		add(playerPanel);
	}

	@Override
	public boolean shouldDrawOverDialog(){
		return true;
	}
}
