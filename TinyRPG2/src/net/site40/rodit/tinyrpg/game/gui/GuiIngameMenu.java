package net.site40.rodit.tinyrpg.game.gui;

import java.io.IOException;

import org.w3c.dom.Document;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import net.site40.rodit.tinyrpg.game.Dialog;
import net.site40.rodit.tinyrpg.game.Dialog.DialogCallback;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Input;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.battle.Battle;
import net.site40.rodit.tinyrpg.game.battle.Team;
import net.site40.rodit.tinyrpg.game.entity.mob.EntityMob;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowInventory;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowOptions;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowQuests;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowTextBoxComponent;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowUserInput;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowUserInput.InputCallback;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowUserInput.InputResult;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.map.Region;
import net.site40.rodit.tinyrpg.game.render.effects.FadeOutEffect;
import net.site40.rodit.tinyrpg.game.render.effects.Weather.Lightning;
import net.site40.rodit.tinyrpg.game.render.effects.Weather.Rain;
import net.site40.rodit.util.RenderUtil;

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
			private final String[] OPTIONS = new String[] { "Inventory", "Quests", "Map", "Combat", "Save", "Options", "Menu", "Back", "Debug" };
			private long keyUpDown = 0L;
			private long keyDownDown = 0L;

			private void select(final Game game){
				switch(selected){
				case 0:
					game.getGuis().hide(GuiIngameMenu.class);
					WindowInventory windowInv = new WindowInventory(game);
					windowInv.show();
					game.getWindows().register(windowInv);
					break;
				case 1:
					game.getGuis().hide(GuiIngameMenu.class);
					WindowQuests questsWindow = new WindowQuests(game);
					game.getWindows().register(questsWindow);
					questsWindow.show();
					break;
				case 4:
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
				case 5:
					game.getGuis().hide(GuiIngameMenu.class);
					WindowOptions optionsWindow = new WindowOptions(game);
					game.getWindows().register(optionsWindow);
					optionsWindow.show();
					break;
				case 6:
					game.setPlayer(null);
					game.releaseMap(true);
					//game.setMap(null);
					game.getGuis().hide(GuiIngameMenu.class);
					game.getGuis().hide(GuiIngame.class);
					game.getGuis().show(GuiMenu.class);
					game.getInput().allowMovement(true);
					break;
				case 7:
					game.getGuis().hide(GuiIngameMenu.class);
					game.getInput().allowMovement(true);
					break;
				case 8:
					game.getGuis().hide(GuiIngameMenu.class);
					game.getInput().allowMovement(true);
					game.getHelper().dialog("Choose a debug operation.", new String[] { "Fade Out", "Start Lightning", "Stop Lightning", "Start Rain", "Load Map", "Test Battle", "Give Item", "Cancel" }, new DialogCallback(){
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
							case 5:
								EntityMob rat0 = new EntityMob();
								Document ratDoc = game.getResources().readDocument("config/entity/rat.xml");
								rat0.linkConfig(ratDoc);
								rat0.setDisplayName("Rat 0");
								EntityMob mimic0 = new EntityMob();
								Document mimicDoc = game.getResources().readDocument("config/entity/mimic.xml");
								mimic0.linkConfig(mimicDoc);
								mimic0.setDisplayName("Mimic 0");
								EntityMob rat1 = new EntityMob();
								rat1.linkConfig(ratDoc);
								rat1.setDisplayName("Rat 1");
								EntityMob mimic1 = new EntityMob();
								mimic1.linkConfig(mimicDoc);
								mimic1.setDisplayName("Mimic 1");
								Battle battle = new Battle(Region.GRASS, new Team(rat0, mimic0, rat1, mimic1), new Team(game.getPlayer()));
								game.setBattle(battle);
								break;
							case 6:
								WindowUserInput inputWindow = new WindowUserInput(game, "Item Name:", "", new InputCallback(){
									@Override
									public boolean onResult(WindowUserInput window, Object result){
										if(result == InputResult.CANCELLED)
											return true;
										Item item = Item.get(String.valueOf(result));
										if(item == null)
											return false;
										game.getPlayer().getInventory().add(item, 1);
										return true;
									}
								});
								inputWindow.getInputBox().setInputType(WindowTextBoxComponent.INPUT_TYPE_ALPHA_NUMERIC);
								inputWindow.show();
								game.getWindows().register(inputWindow);
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
