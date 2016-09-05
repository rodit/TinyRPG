package net.site40.rodit.tinyrpg.game.gui;

import java.io.IOException;

import net.site40.rodit.tinyrpg.game.Dialog;
import net.site40.rodit.tinyrpg.game.Dialog.DialogCallback;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Input;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowInventory;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowTextBoxComponent;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowUserInput;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowUserInput.InputCallback;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowUserInput.InputResult;
import net.site40.rodit.tinyrpg.game.render.effects.FadeOutEffect;
import net.site40.rodit.tinyrpg.game.render.effects.Weather.Lightning;
import net.site40.rodit.tinyrpg.game.render.effects.Weather.Rain;
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
			private final String[] OPTIONS = new String[] { "Inventory", "Map", "Combat", "Save", "Options", "Menu", "Back", "Debug" };
			private long keyUpDown = 0L;
			private long keyDownDown = 0L;

			private void select(final Game game){
				switch(selected){
				case 0:
					game.getGuis().hide(GuiIngameMenu.class);
					game.getWindows().get(WindowInventory.class).show();
					//game.getGuis().show(GuiPlayerInventory.class);
					break;
				case 3:
					boolean success = false;
					try{
						game.getSaves().save(game);
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
				case 7:
					game.getGuis().hide(GuiIngameMenu.class);
					game.getInput().allowMovement(true);
					game.getHelper().dialog("Choose a debug operation.", new String[] { "Fade Out", "Start Lightning", "Stop Lightning", "Start Rain", "Load Map", "Cancel" }, new DialogCallback(){
						public void onSelected(int option){
							switch(option){
							case 0:
								game.getPostProcessor().add(new FadeOutEffect());
								break;
							case 1:
								Object globalLightning = game.getGlobal("debug_lightning");
								if(globalLightning == null){
									game.setGlobal("debug_lightning", globalLightning = new Lightning());
									game.getWeather().add((Lightning)globalLightning);
								}
								break;
							case 2:
								Object globalLightning0 = game.getGlobal("debug_lightning");
								if(globalLightning0 != null)
									game.getWeather().remove((Lightning)globalLightning0);
								break;
							case 3:
								game.getWeather().add(new Rain());
								break;
							case 4:
								WindowUserInput input = new WindowUserInput(game, "Map File:", "map/world.tmx", new InputCallback(){
									public boolean onResult(WindowUserInput window, Object result){
										if(result == InputResult.CANCELLED)
											return true;
										game.getHelper().setMap(result.toString());
										return true;
									}
								});
								input.getInputBox().setInputType(WindowTextBoxComponent.INPUT_TYPE_ALPHA_NUMERIC);
								game.getWindows().register(input);
								input.zIndex = 0;
								input.show();
								break;
							}
						}
					});
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
