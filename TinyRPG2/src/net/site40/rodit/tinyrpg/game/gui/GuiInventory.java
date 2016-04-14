package net.site40.rodit.tinyrpg.game.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.site40.rodit.tinyrpg.game.Dialog;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Input;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.forge.ItemStack;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.item.ItemEquippable;
import net.site40.rodit.tinyrpg.game.item.Necklace;
import net.site40.rodit.tinyrpg.game.item.Ring;
import net.site40.rodit.tinyrpg.game.item.Weapon;
import net.site40.rodit.tinyrpg.game.item.armour.Armour;
import net.site40.rodit.tinyrpg.game.render.Animation;
import net.site40.rodit.util.RenderUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.RectF;

public class GuiInventory extends Gui{

	public static final int PAGE_ALL = 0;
	public static final int PAGE_WEAPONS = 1;
	public static final int PAGE_ARMOUR = 2;
	public static final int PAGE_ACCESSORIES = 3;
	public static final int PAGE_POTIONS = 4;
	public static final int PAGE_MISC = 5;

	private static final int MAX_PER_SCROLL = 10;

	private int selected = 0;
	private int scrollOffset = 0;
	private Item selectedItem = null;

	private int page;

	private ArrayList<Item> currentView = new ArrayList<Item>();

	private static final Comparator<Item> alphabeticalItemSorter = new Comparator<Item>(){

		@Override
		public int compare(Item i0, Item i1){
			return i0.getName().compareTo(i1.getName());
		}
	};

	public GuiInventory(){
		super("");
	}

	@Override
	public void onShown(){
		selected = 0;
	}

	@Override
	public void init(){
		Component bg = new Component(){
			private final RectF BOUNDS_MENU = new RectF(512, 32, 512 + 768, 32 + 512);
			private final RectF BOUNDS_ITEM_INFO = new RectF(0, 32, 448, 32 + 512);

			private long keyUpDown = 0L;
			private long keyDownDown = 0L;

			private int optionCount = 0;

			private void select(Game game){
				ArrayList<Item> cViewCopy;
				synchronized(currentView){
					cViewCopy = new ArrayList<Item>(currentView);
				}

				if(selectedItem == null){
					if(selected >= cViewCopy.size())
						return;
					selectedItem = cViewCopy.get(selected);
				}else{
					if(selectedItem instanceof ItemEquippable){
						ItemEquippable ie = (ItemEquippable)selectedItem;
						int slot = game.getPlayer().getSlot(selectedItem);
						if(slot > -1){
							game.getPlayer().setEquipped(slot, null);
							ie.onUnEquip(game, game.getPlayer());
						}else{
							boolean wasEquipped = false;
							for(int i = 0; i < ie.getEquipSlots().length; i++){
								if(game.getPlayer().getEquippedItem(ie.getEquipSlots()[i]) == null){
									game.getPlayer().setEquipped(ie.getEquipSlots()[i], ie);
									ie.onEquip(game, game.getPlayer());
									wasEquipped = true;
								}
							}
							if(!wasEquipped){
								game.getPlayer().setEquipped(ie.getEquipSlots()[0], ie);
								ie.onEquip(game, game.getPlayer());
							}
						}
					}else{
						if(selectedItem.canUse()){
							selectedItem.onEquip(game, game.getPlayer());
							if(selectedItem.isConsumed())
								game.getPlayer().getInventory().remove(selectedItem);
						}
					}
				}
			}

			private void prevPage(){
				if(page == 0)
					page = 5;
				else
					page--;
				currentView.clear();
			}

			private void nextPage(){
				if(page == 5)
					page = 0;
				else
					page++;
				currentView.clear();
			}

			public void moveUp(){
				if(selected == 0)
					selected = optionCount - 1;
				else
					selected--;
				scroll();
				selectedItem = null;
			}

			public void moveDown(){
				if(selected == optionCount - 1)
					selected = 0;
				else
					selected++;
				scroll();
				selectedItem = null;
			}

			public void scroll(){
				if(selected < scrollOffset)
					scrollOffset--;
				if(selected > scrollOffset + MAX_PER_SCROLL)
					scrollOffset++;
			}

			@Override
			public void update(Game game){
				super.update(game);

				synchronized(currentView){
					if(currentView.isEmpty()){
						for(ItemStack stack : game.getPlayer().getInventory().getItems()){
							Item i = stack.getItem();
							switch(page){
							case PAGE_ALL:
								currentView.add(i);
								break;
							case PAGE_WEAPONS:
								if(i instanceof Weapon)
									currentView.add(i);
								break;
							case PAGE_ARMOUR:
								if(i instanceof Armour)
									currentView.add(i);
								break;
							case PAGE_ACCESSORIES:
								if(i instanceof Necklace || i instanceof Ring)
									currentView.add(i);
								break;
							case PAGE_POTIONS:
								if(i.getName().contains("potion") && i.isConsumed())
									currentView.add(i);
								break;
							case PAGE_MISC:
								if(!(i instanceof Weapon || i instanceof Armour || i instanceof Necklace || i instanceof Ring || i.getName().contains("potion")))
									currentView.add(i);
								break;
							}
						}
						Collections.sort(currentView, alphabeticalItemSorter);
					}
					optionCount = currentView.size();
					if(selected >= optionCount)
						selected = optionCount - 1;
					if(selected == -1)
						selected = 0;
				}

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
				if(input.isUp(Input.KEY_MENU)){
					if(selectedItem != null)
						selectedItem = null;
					else{
						game.getGuis().show(GuiIngameMenu.class);
						game.getGuis().hide(GuiInventory.class);
					}
				}
				if(input.isUp(Input.KEY_LEFT))
					prevPage();
				if(input.isUp(Input.KEY_RIGHT))
					nextPage();
			}

			@Override
			public void draw(Game game, Canvas canvas){
				ArrayList<Item> cViewCopy = null;
				synchronized(currentView){
					cViewCopy = new ArrayList<Item>(currentView);
				}

				RenderUtil.drawBitmapBox(canvas, game, BOUNDS_MENU, getPaint());

				getPaint().setColor(Color.BLACK);
				getPaint().setTextAlign(Align.CENTER);
				getPaint().setTextSize(28f);
				String titleTxt = "Inventory";
				if(page == PAGE_ALL)
					titleTxt += " - All";
				else if(page == PAGE_WEAPONS)
					titleTxt += " - Weapons";
				else if(page == PAGE_ARMOUR)
					titleTxt += " - Armour";
				else if(page == PAGE_ACCESSORIES)
					titleTxt += " - Accessories";
				else if(page == PAGE_POTIONS)
					titleTxt += " - Potions";
				else if(page == PAGE_MISC)
					titleTxt += " - Misc";
				canvas.drawText(titleTxt, BOUNDS_MENU.centerX(), 72f, getPaint());
				getPaint().setTextAlign(Align.LEFT);
				getPaint().setTextSize(Values.FONT_SIZE_SMALL);
				float offX = BOUNDS_MENU.left + 48f;
				float offY = 108f;
				int i = 0;
				for(Item key : cViewCopy){
					if(i < scrollOffset || i > scrollOffset + MAX_PER_SCROLL){
						i++;
						continue;
					}
					int count = game.getPlayer().getInventory().getCount(key);
					canvas.drawText(key.getShowName() + " x" + count, offX, offY, getPaint());
					if(i == selected)
						canvas.drawText(">", offX - 24, offY, getPaint());
					else if(game.getPlayer().isEquipped(key))
						canvas.drawText("X", offX - 24, offY, getPaint());
					offY += getPaint().getTextSize() + 15f;
					i++;
				}

				if(selectedItem != null){
					RenderUtil.drawBitmapBox(canvas, game, BOUNDS_ITEM_INFO, getPaint());
					getPaint().setColor(Color.BLACK);
					getPaint().setTextAlign(Align.LEFT);
					canvas.save();
					canvas.translate(BOUNDS_ITEM_INFO.left + 24f, BOUNDS_ITEM_INFO.top + 24f);
					getPaint().setTextSize(Values.FONT_SIZE_SMALL);
					RenderUtil.drawWrappedText(selectedItem.getShowName(), (int)(BOUNDS_ITEM_INFO.width() - 40f), getPaint(), canvas);
					getPaint().setTextSize(Values.FONT_SIZE_TINY);
					String fullTxt = "\n\n\n\n\n\n\n\n\n\n\n" + selectedItem.getDescription().replace("\\n", "\n") + "\n\n" + selectedItem.getRarity().toString() + "\n\nValue: " + selectedItem.getValue() + "G";
					if(selectedItem.canUse() && !(selectedItem instanceof ItemEquippable))
						fullTxt += "\n\nPress [A] to use.";
					else if(selectedItem instanceof ItemEquippable){
						if(selectedItem instanceof Weapon)
							fullTxt += "\n\nDamage: " + ((Weapon)selectedItem).getDamage();
						if(selectedItem instanceof Armour)
							fullTxt += "\n\nDefence: " + ((Armour)selectedItem).getArmourValue();
						if(game.getPlayer().isEquipped(selectedItem))
							fullTxt += "\n\nPress [A] to unequip.";
						else
							fullTxt += "\n\nPress [A] to equip.";
					}
					RenderUtil.drawWrappedText(fullTxt, (int)(BOUNDS_ITEM_INFO.width() - 40f), getPaint(), canvas);
					canvas.restore();
					Bitmap icon = null;
					Object res = game.getResources().getObject(selectedItem.getResource());
					if(res instanceof Bitmap)
						icon = (Bitmap)res;
					else if(res instanceof Animation)
						icon = ((Animation)res).getFrame(game.getTime());
					if(icon != null)
						canvas.drawBitmap(icon, null, new RectF(BOUNDS_ITEM_INFO.centerX() - 64f, BOUNDS_ITEM_INFO.centerY() - 192f, BOUNDS_ITEM_INFO.centerX() + 64f, BOUNDS_ITEM_INFO.centerY() - 64f), null);
				}
			};
		};
		add(bg);
	}
}
