package net.site40.rodit.tinyrpg.game.gui.windows;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.entity.EntityItemDrop;
import net.site40.rodit.tinyrpg.game.event.EventReceiver.EventType;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowSlotted.ProviderInfo;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowUserInput.InputCallback;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowUserInput.InputResult;
import net.site40.rodit.tinyrpg.game.item.Inventory.InventoryProvider;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.item.ItemEquippable;
import net.site40.rodit.tinyrpg.game.item.ItemStack;
import net.site40.rodit.tinyrpg.game.item.Weapon;
import net.site40.rodit.tinyrpg.game.item.armour.Armour;
import net.site40.rodit.util.RenderUtil;
import net.site40.rodit.util.Util;
import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.util.Log;
import android.view.MotionEvent;

public class WindowItemInfo extends Window{

	protected ItemStack stack;
	protected ProviderInfo info;

	protected WindowComponent txtItemName;
	protected WindowComponent txtItemDescription;
	protected WindowComponent txtItemRarity;
	protected WindowComponent txtItemValue;
	protected WindowComponent txtItemStats;
	protected WindowComponent imgItemPreview;

	protected WindowComponent btnBack;
	protected WindowComponent btnDispose;
	protected WindowComponent btnEquipUse;

	public WindowItemInfo(Game game, ItemStack stack, ProviderInfo info){
		super(game);
		this.stack = stack;
		this.info = info;
	}

	@Override
	public void update(Game game){
		super.update(game);

		initAfterInit(game);
	}

	@Override
	public void initialize(Game game){
		this.setBounds(576, 152, WindowSlot.SLOT_WIDTH * WindowInventory.COUNT_COLUMNS + 128, WindowSlot.SLOT_HEIGHT * WindowInventory.COUNT_ROWS + 128);

		txtItemName = new WindowComponent("txtItemName");
		txtItemName.setX(32f);
		txtItemName.setY(64f);
		txtItemName.setText("Null Title");
		txtItemName.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM - 8f);
		txtItemName.getPaint().setTextAlign(Align.LEFT);
		add(txtItemName);

		txtItemDescription = new WindowComponent("txtItemDescription"){
			@Override
			public void draw(Game game, Canvas canvas){
				if(getFlag(WindowComponent.FLAG_INVISIBLE))
					return;
				if(stack != null){
					int s = canvas.save();
					canvas.translate(getScreenX(), getScreenY());
					setHeight(RenderUtil.drawWrappedText(game, stack.getItem().getDescription().replace("\\n", "\n") + "\n" + getText(), (int)(WindowItemInfo.this.getWidth() / 16f * 10f), paint, canvas));
					canvas.restoreToCount(s);
				}
			}
		};
		txtItemDescription.setX(txtItemName.getX());
		txtItemDescription.setY(txtItemName.getY() + txtItemName.getPaint().getTextSize() - 8f);
		txtItemDescription.setText("");
		txtItemDescription.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		txtItemDescription.getPaint().setTextAlign(Align.LEFT);
		add(txtItemDescription);

		txtItemRarity = new WindowComponent("txtItemRarity");
		txtItemRarity.setX(txtItemDescription.getX());
		txtItemRarity.setY(txtItemDescription.getY() + txtItemDescription.getPaint().getTextSize() * 4 + 64f);
		txtItemRarity.getPaint().setTextAlign(Align.LEFT);
		txtItemRarity.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		add(txtItemRarity);

		txtItemValue = new WindowComponent("txtItemValue");
		txtItemValue.setX(txtItemRarity.getX());
		txtItemValue.setY(txtItemRarity.getY() + txtItemRarity.getPaint().getTextSize() + 8f);
		txtItemValue.getPaint().setTextAlign(Align.LEFT);
		txtItemValue.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		add(txtItemValue);

		txtItemStats = new WindowComponent("txtItemStats");
		txtItemStats.setX(getWidth() - 192);
		txtItemStats.setY(txtItemName.getY() + 164);
		txtItemStats.getPaint().setTextAlign(Align.LEFT);
		txtItemStats.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		add(txtItemStats);

		imgItemPreview = new WindowComponent("imgItemPreview");
		imgItemPreview.setX(getWidth() - 192f);
		imgItemPreview.setY(txtItemName.getY());
		imgItemPreview.setWidth(128f);
		imgItemPreview.setHeight(128f);
		add(imgItemPreview);

		if(btnBack == null){
			btnBack = new WindowComponent("btnBack");
			btnBack.setBackground(WindowComponent.STATE_IDLE, "gui/button.png");
			btnBack.setBackground(WindowComponent.STATE_DOWN, "gui/button_selected.png");
			btnBack.setText("Back");
			btnBack.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM - 8f);
			btnBack.setWidth(164);
			btnBack.setHeight(72);
			btnBack.setX(52);
			btnBack.setY(getHeight() - btnBack.getHeight() - 48);
			btnBack.addListener(new WindowListener(){
				public void touchUp(Game game, WindowComponent component){
					WindowItemInfo.this.hide();
				}
			});
			add(btnBack);
		}

		if(btnDispose == null){
			btnDispose = new WindowComponent("btnDispose");
			btnDispose.setBackground(WindowComponent.STATE_IDLE, "gui/button.png");
			btnDispose.setBackground(WindowComponent.STATE_DOWN, "gui/button_selected.png");
			btnDispose.setText("Drop");
			btnDispose.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM - 8f);
			btnDispose.setWidth(164);
			btnDispose.setHeight(72);
			btnDispose.setX(btnBack.getX() + btnBack.getWidth() + 16f);
			btnDispose.setY(btnBack.getY());
			btnDispose.addListener(new WindowListener(){
				public void touchUp(final Game game, WindowComponent component){
					if(stack != null){
						final ItemStack toDrop = new ItemStack(stack);
						if(stack.getAmount() > 1){
							WindowUserInput input = new WindowUserInput(game, "How Many", String.valueOf(stack.getAmount()), new InputCallback(){
								public boolean onResult(WindowUserInput window, Object result){
									if(result == InputResult.CANCELLED)
										return true;
									int count = Util.tryGetInt(String.valueOf(result));
									if(count > stack.getAmount())
										window.get("input").setText(String.valueOf(stack.getAmount()));
									else if(count < 1)
										window.get("input").setText("1");
									else{
										stack.setAmount(stack.getAmount() - count);
										toDrop.setItem(stack.getItem());
										toDrop.setAmount(count);
										hide();
										return true;
									}
									return false;
								}
							});
							game.getWindows().register(input);
							input.zIndex = 0;
							input.show();
						}else
							info.provider.getInventory().getItems().remove(stack);

						EntityItemDrop drop = null;
						ArrayList<Entity> ents = game.getMap().getCollidingEntities(game.getPlayer().getBounds(), game.getPlayer());
						for(Entity e : ents)
							if(e instanceof EntityItemDrop)
								drop = (EntityItemDrop)e;
						if(drop == null){
							drop = new EntityItemDrop(null);
							game.getMap().spawn(game, drop);
							drop.setX(game.getPlayer().getCenterX() - drop.getWidth() / 2f);
							drop.setY(game.getPlayer().getCenterY() - drop.getHeight() / 2f);
						}
						drop.getInventory().add(toDrop);
						game.getEvents().onEvent(game, EventType.ITEM_DROP, toDrop, game.getPlayer());
					}
				}
			});
			add(btnDispose);
		}

		if(btnEquipUse == null){
			btnEquipUse = new WindowComponent("btnEquipUse");
			btnEquipUse.setBackground(WindowComponent.STATE_IDLE, "gui/button.png");
			btnEquipUse.setBackground(WindowComponent.STATE_DOWN, "gui/button_selected.png");
			btnEquipUse.setText("Use");
			btnEquipUse.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM - 8f);
			btnEquipUse.setWidth(164);
			btnEquipUse.setHeight(72);
			btnEquipUse.setX(btnDispose.getX() + btnDispose.getWidth() + 16f);
			btnEquipUse.setY(btnDispose.getY());
			btnEquipUse.addListener(new WindowListener(){
				public void touchUp(Game game, WindowComponent component){
					if(stack != null){
						if(stack.getItem() instanceof ItemEquippable){
							ItemEquippable ie = (ItemEquippable)stack.getItem();
							int slot = game.getPlayer().getSlot(stack.getItem());
							if(slot > -1){
								game.getPlayer().setEquipped(slot, null);
								ie.onUnEquip(game, game.getPlayer());
								game.getEvents().onEvent(game, EventType.ITEM_UNEQUIP, ie, game.getPlayer(), slot);
							}else{
								boolean wasEquipped = false;
								for(int i = 0; i < ie.getEquipSlots().length; i++){
									if(game.getPlayer().getEquippedItem(ie.getEquipSlots()[i]) == null){
										game.getPlayer().setEquipped(ie.getEquipSlots()[i], ie);
										ie.onEquip(game, game.getPlayer());
										game.getEvents().onEvent(game, EventType.ITEM_EQUIP, ie, game.getPlayer(), ie.getEquipSlots()[i]);
										wasEquipped = true;
										break;
									}
								}
								if(!wasEquipped){
									Item item = game.getPlayer().getEquipped(ie.getEquipSlots()[0]);
									if(item != null){
										item.onUnEquip(game, game.getPlayer());
										game.getEvents().onEvent(game, EventType.ITEM_UNEQUIP, item, game.getPlayer(), ie.getEquipSlots()[0]);
									}
									game.getPlayer().setEquipped(ie.getEquipSlots()[0], ie);
									game.getEvents().onEvent(game, EventType.ITEM_EQUIP, ie, game.getPlayer(), ie.getEquipSlots()[0]);
									ie.onEquip(game, game.getPlayer());
								}
							}
						}else if(stack.getItem().canUse()){
							stack.getItem().onEquip(game, game.getPlayer());
							game.getEvents().onEvent(game, EventType.ITEM_EQUIP, stack.getItem(), game.getPlayer(), game.getPlayer().getSlot(stack.getItem()));
							if(stack.getItem().isConsumed())
								stack.consume();
						}else
							game.getHelper().dialog("This item cannot be used or equipped.");
					}
				}
			});
			add(btnEquipUse);
		}
	}
	
	public boolean isVanillaWindow(){
		return true;
	}

	public void initAfterInit(Game game){
		if(stack == null)
			return;
		
		imgItemPreview.setBackgroundDefault(stack.getItem().getResource());
		txtItemName.setText(stack.getItem().getShowName() + "  (" + stack.getAmount() + ")");
		txtItemRarity.setText("Rarity: " + stack.getItem().getRarity().toString());
		txtItemValue.setText("Value: " + stack.getItem().getValue() + " Gold");
		if(stack.getItem() instanceof Weapon)
			txtItemStats.setText("Damage: " + ((Weapon)stack.getItem()).getDamage());
		else if(stack.getItem() instanceof Armour)
			txtItemStats.setText("Armour: " + ((Armour)stack.getItem()).getArmourValue());
		
		if(isVanillaWindow()){
			if(stack.getItem() instanceof ItemEquippable){
				txtItemName.setText(stack.getItem().getShowName());
				if(game.getPlayer().getSlot(stack.getItem()) > -1){
					if(!(this instanceof WindowContainerItemInfo))
						btnEquipUse.setText("Unequip");
					txtItemName.setText(stack.getItem().getShowName() + "  (Equipped)");
				}else{
					if(!(this instanceof WindowContainerItemInfo))
						btnEquipUse.setText("Equip");
					if(info.selectedTab == InventoryProvider.TAB_EQUIPPED)
						btnBack.simulateInput(game, MotionEvent.ACTION_UP);
				}
			}else if(stack.getItem().canUse() && !(this instanceof WindowContainerItemInfo))
				btnEquipUse.setText("Use");
			else if(!(this instanceof WindowContainerItemInfo))
				btnEquipUse.setFlag(WindowComponent.FLAG_INVISIBLE, true);
		}
	}
}
