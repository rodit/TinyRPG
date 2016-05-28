package net.site40.rodit.tinyrpg.game.gui;

import java.io.IOException;

import net.site40.rodit.tinyrpg.game.Dialog;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Input;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowInventory;
import net.site40.rodit.tinyrpg.game.saves.SaveGame;
import net.site40.rodit.util.RenderUtil;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.RectF;

public class GuiIngameMenu extends Gui{

	private int selected = 0;

	public GuiIngameMenu(){
		super("");
	}

	@Override
	public void onShown(){
		selected = 0;
	}

	@Override
	public void init(){
		Component bg = new Component(){
			private final RectF BOUNDS_MENU = new RectF(32, 32, 32 + 384, 32 + 512);
			private final String[] OPTIONS = new String[] { "Inventory", "Map", "Combat", "Save", "Options", "Menu", "Back" };
			private long keyUpDown = 0L;
			private long keyDownDown = 0L;

			private void select(Game game){
				switch(selected){
				case 0:
					game.getGuis().hide(GuiIngameMenu.class);
					game.getWindows().get(WindowInventory.class).show();
					//game.getGuis().show(GuiPlayerInventory.class);
					break;
				case 3:
					boolean success = false;
					SaveGame newSave = game.getSaves().newSave(game, true);
					try{
						newSave.save(game);
						success = true;
					}catch(IOException e){
						e.printStackTrace();
						game.getHelper().dialog("There was an error while saving your game. " + e.getMessage() + ".");
					}
					game.getGuis().hide(GuiIngameMenu.class);
					game.getInput().allowMovement(true);
					if(success)
						game.getHelper().dialog("Save complete!");
					break;
				case 4:
					game.getGuis().hide(GuiIngameMenu.class);
					game.getGuis().show(GuiOptions.class);
					break;
				case 5:
					game.removeObject(game.getMap());
					game.setMap(null);
					game.getGuis().hide(GuiIngameMenu.class);
					game.getGuis().hide(GuiIngame.class);
					game.getGuis().show(GuiMenu.class);
					game.getInput().allowMovement(true);
					break;
				case 6:
					game.getGuis().hide(GuiIngameMenu.class);
					game.getInput().allowMovement(true);
					break;
				}
			}

			public void moveUp(){
				if(selected == 0)
					selected = OPTIONS.length - 1;
				else
					selected--;
			}

			public void moveDown(){
				if(selected == OPTIONS.length - 1)
					selected = 0;
				else
					selected++;
			}

			@Override
			public void update(Game game){
				super.update(game);
				Input input = game.getInput();
				input.allowMovement(false);
				if(input.isDown(Input.KEY_UP))
					keyUpDown += game.getDelta();
				else
					keyUpDown = 0L;
				if(input.isDown(Input.KEY_DOWN))
					keyDownDown += game.getDelta();
				else
					keyDownDown = 0L;
				if(keyUpDown >= Dialog.INPUT_DELAY_CURSOR || input.isUp(Input.KEY_UP)){
					moveUp();
					keyUpDown = 0L;
				}
				if(keyDownDown >= Dialog.INPUT_DELAY_CURSOR || input.isUp(Input.KEY_DOWN)){
					moveDown();
					keyDownDown = 0L;
				}
				if(input.isUp(Input.KEY_ACTION))
					select(game);
			}

			@Override
			public void draw(Game game, Canvas canvas){
				RenderUtil.drawBitmapBox(canvas, game, BOUNDS_MENU, getPaint());

				getPaint().setColor(Color.WHITE);
				getPaint().setTextAlign(Align.LEFT);
				getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
				float offX = 92f;
				float offY = 104f;
				for(int i = 0; i < OPTIONS.length; i++){
					canvas.drawText(OPTIONS[i], offX, offY, getPaint());
					if(i == selected)
						canvas.drawText(">", offX - 32, offY, getPaint());
					offY += getPaint().getTextSize() + 15f;
				}
			}
		};
		add(bg);
	}
}
