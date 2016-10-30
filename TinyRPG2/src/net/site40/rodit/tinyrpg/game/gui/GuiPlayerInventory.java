package net.site40.rodit.tinyrpg.game.gui;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.view.MotionEvent;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.IGameObject;
import net.site40.rodit.tinyrpg.game.IPaintMixer;
import net.site40.rodit.tinyrpg.game.Input;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.event.EventReceiver.EventType;
import net.site40.rodit.tinyrpg.game.gui.ComponentListener.ComponentListenerImpl;
import net.site40.rodit.tinyrpg.game.item.Inventory.InventoryProvider;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.item.ItemEquippable;
import net.site40.rodit.tinyrpg.game.item.ItemStack;
import net.site40.rodit.tinyrpg.game.item.Weapon;
import net.site40.rodit.tinyrpg.game.item.armour.Armour;
import net.site40.rodit.util.RenderUtil;
import net.site40.rodit.util.Util;



public class GuiPlayerInventory extends Gui{

	private static final int COUNT_ROWS = 3;
	private static final int COUNT_COLUMNS = 4;
	private static final int ITEMS_PER_PAGE = COUNT_ROWS * COUNT_COLUMNS;
	protected static final int OFFSET_X = 592;
	protected static final int OFFSET_Y = 224;
	protected static final int SLOT_WIDTH = 128;
	protected static final int SLOT_HEIGHT = 128;
	private static final int TAB_WIDTH = 72;
	private static final int TAB_HEIGHT = 72;

	private int tabHover;
	protected int tab;
	private int selectedHover;
	protected int selected;
	protected int page;

	protected ItemStack selectedItem;

	protected ArrayList<Component> tabComponents;
	protected ArrayList<Component> slotComponents;
	private Component scrollUp;
	private Component scrollDown;

	protected WindowComponent itemWindow;
	protected Component btnBack;
	protected Component btnDispose;
	protected Component btnEquipUse;
	protected Component txtItemTitle;
	protected Component txtItemDescription;
	protected Component txtItemRarity;
	protected Component txtItemValue;
	protected Component txtItemStats;
	protected Component imgItemPreview;

	protected boolean openedWindow = false;

	public InventoryProvider provider;

	public GuiPlayerInventory(){
		super("");
	}

	@Override
	public void onShown(){
		tabHover = InventoryProvider.TAB_ALL;
		tab = InventoryProvider.TAB_ALL;
		selectedHover = -1;
		selected = -1;
		page = 0;
	}

	@Override
	public void init(){
		this.tabComponents = new ArrayList<Component>();
		this.slotComponents = new ArrayList<Component>();

		WindowComponent bgWindow = new WindowComponent();
		bgWindow.setX(64);
		bgWindow.setY(32);
		bgWindow.setWidth(1152);
		bgWindow.setHeight(640);
		add(bgWindow);

		Component title = new Component("txtTitle", "Inventory");
		title.setX(bgWindow.getX() + bgWindow.getWidth() / 2);
		title.setY(bgWindow.getY() + 48);
		title.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		add(title);

		int k = 0;
		for(int y = 0; y < COUNT_ROWS; y++){
			for(int x = 0; x < COUNT_COLUMNS; x++){
				addSlot("gui/inventory/slot.png", "gui/inventory/slot_hover.png", OFFSET_X + x * SLOT_WIDTH, OFFSET_Y + y * SLOT_HEIGHT, SLOT_WIDTH, SLOT_HEIGHT, k++);
			}
		}
		for(int i = 0; i < 7; i++)
			addTab("gui/inventory/tab.png", "gui/inventory/tab_selected.png", OFFSET_X + i * TAB_WIDTH, OFFSET_Y - TAB_HEIGHT, TAB_WIDTH, TAB_HEIGHT, i);

		addEquipmentSlot("gui/inventory/slot.png", "gui/inventory/slot_hover.png", (OFFSET_X - bgWindow.getX()) / 2 + 92 - SLOT_WIDTH / 2, bgWindow.getY() + 64 + SLOT_HEIGHT / 2, SLOT_WIDTH, SLOT_HEIGHT, ItemEquippable.SLOT_HELMET);
		addEquipmentSlot("gui/inventory/slot.png", "gui/inventory/slot_hover.png", (OFFSET_X - bgWindow.getX()) / 2 + 92 - SLOT_WIDTH / 2, bgWindow.getY() + 64 + SLOT_HEIGHT + SLOT_HEIGHT / 2, SLOT_WIDTH, SLOT_HEIGHT, ItemEquippable.SLOT_CHEST);
		addEquipmentSlot("gui/inventory/slot.png", "gui/inventory/slot_hover.png", (OFFSET_X - bgWindow.getX()) / 2 + 92 - SLOT_WIDTH / 2 + SLOT_WIDTH, bgWindow.getY() + 64 + SLOT_HEIGHT, SLOT_WIDTH, SLOT_HEIGHT, ItemEquippable.SLOT_SHOULDERS);
		addEquipmentSlot("gui/inventory/slot.png", "gui/inventory/slot_hover.png", (OFFSET_X - bgWindow.getX()) / 2 + 92 - SLOT_WIDTH / 2 - SLOT_WIDTH, bgWindow.getY() + 64 + SLOT_HEIGHT, SLOT_WIDTH, SLOT_HEIGHT, ItemEquippable.SLOT_NECK);
		addEquipmentSlot("gui/inventory/slot.png", "gui/inventory/slot_hover.png", (OFFSET_X - bgWindow.getX()) / 2 + 92 - SLOT_WIDTH / 2 - SLOT_WIDTH / 2, bgWindow.getY() + 64 + SLOT_HEIGHT * 3, SLOT_WIDTH, SLOT_HEIGHT, ItemEquippable.SLOT_FINGER_0);
		addEquipmentSlot("gui/inventory/slot.png", "gui/inventory/slot_hover.png", (OFFSET_X - bgWindow.getX()) / 2 + 92 - SLOT_WIDTH / 2 + SLOT_WIDTH / 2, bgWindow.getY() + 64 + SLOT_HEIGHT * 3, SLOT_WIDTH, SLOT_HEIGHT, ItemEquippable.SLOT_FINGER_1);
		addEquipmentSlot("gui/inventory/slot.png", "gui/inventory/slot_hover.png", (OFFSET_X - bgWindow.getX()) / 2 + 92 - SLOT_WIDTH / 2 - SLOT_WIDTH, bgWindow.getY() + 64 + SLOT_HEIGHT * 2, SLOT_WIDTH, SLOT_HEIGHT, ItemEquippable.SLOT_HAND_0);
		addEquipmentSlot("gui/inventory/slot.png", "gui/inventory/slot_hover.png", (OFFSET_X - bgWindow.getX()) / 2 + 92 - SLOT_WIDTH / 2 + SLOT_WIDTH, bgWindow.getY() + 64 + SLOT_HEIGHT * 2, SLOT_WIDTH, SLOT_HEIGHT, ItemEquippable.SLOT_HAND_1);

		final Component txtPageNo = new Component("txtPageNo");
		txtPageNo.setX(1148);
		txtPageNo.setY(412);
		txtPageNo.setFlag(Component.IGNORE_CUSTOM_CENTERING);
		txtPageNo.setText("1/1");
		txtPageNo.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		txtPageNo.addListener(new ComponentListenerImpl(){
			boolean first = true;
			public void update(Component component, Game game){
				if(first){
					component.setText("1/" + getMaxPages(game));
					first = false;
				}
			}
		});
		add(txtPageNo);

		scrollDown = new Component("scrollDown");
		scrollUp = new Component("scrollUp");
		scrollUp.setBackground("gui/scroll_up.png");
		scrollUp.setBackgroundSelected("gui/scroll_up_selected.png");
		scrollUp.setX(1116);
		scrollUp.setY(248);
		scrollUp.setWidth(72);
		scrollUp.setHeight(72);
		scrollUp.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				int maxPages = getMaxPages(game);
				page--;
				if(page == -1)
					page = maxPages - 1;
				txtPageNo.setText((page + 1) + "/" + maxPages);
			}
		});
		add(scrollUp);

		scrollDown.setBackground("gui/scroll_down.png");
		scrollDown.setBackgroundSelected("gui/scroll_down_selected.png");
		scrollDown.setX(1116);
		scrollDown.setY(512);
		scrollDown.setWidth(72);
		scrollDown.setHeight(72);
		scrollDown.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				int maxPages = getMaxPages(game);
				page++;
				if(page == maxPages)
					page = 0;
				txtPageNo.setText((page + 1) + "/" + maxPages);
			}
		});
		add(scrollDown);
	}

	@Override
	public void update(Game game){
		if(this.provider == null)
			this.provider = new InventoryProvider(game.getPlayer().getInventory(), game.getPlayer());

		super.update(game);
		if(!active)
			return;

		if(game.getInput().isDown(Input.KEY_MENU) && (itemWindow == null || itemWindow.flag == Component.INACTIVE)){
			game.getGuis().hide(GuiPlayerInventory.class);
			game.getGuis().show(GuiIngameMenu.class);
		}
	}

	public int getMaxPages(Game game){
		return Math.max((int)Math.ceil((float)provider.provide(tab).size() / (float)ITEMS_PER_PAGE), 1);
	}

	public void onTabSelected(Game game){
		selectedHover = 0;
		selected = -1;
		page = 0;

		get("txtPageNo").setText("1/" + getMaxPages(game));

		Component sTab = get("tab" + tab);
		if(sTab != null)
			sTab.attachPaintMixer(new SelectedMixer(this));
	}
	
	public void onSlotSelected(Game game){
		onSlotSelected(game, ITEMS_PER_PAGE);
	}

	public void onSlotSelected(Game game, final int itemsPerPage){
		selectedItem = provider.provide(tab, itemsPerPage * page + selected);
		if(selectedItem != null){
			Component sSlot = get("slot" + selected);
			if(sSlot != null)
				sSlot.attachPaintMixer(new SelectedMixer(this));
		}else{
			this.selected = -1;
			return;
		}

		if(itemWindow == null){
			itemWindow = new WindowComponent();
			itemWindow.setX(OFFSET_X - 16);
			itemWindow.setY(OFFSET_Y - 72);
			itemWindow.setWidth(SLOT_WIDTH * COUNT_COLUMNS + 128);
			itemWindow.setHeight(SLOT_HEIGHT * COUNT_ROWS + 128);
			add(itemWindow);
		}
		itemWindow.setFlag(0);

		if(btnBack == null){
			btnBack = new Component("btnBack");
			btnBack.setBackground("gui/button.png");
			btnBack.setBackgroundSelected("gui/button_selected.png");
			btnBack.setText("Back");
			btnBack.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM - 8f);
			btnBack.setWidth(164);
			btnBack.setHeight(72);
			btnBack.setX(itemWindow.getX() + 52);
			btnBack.setY(itemWindow.getY() + itemWindow.getHeight() - btnBack.getHeight() - 48);
			btnBack.addListener(new ComponentListenerImpl(){
				public void touchUp(Component component, Game game){
					selected = -1;
					selectedItem = null;

					itemWindow.setFlag(Component.INACTIVE);
					btnBack.setFlag(Component.INACTIVE);
					btnDispose.setFlag(Component.INACTIVE);
					btnEquipUse.setFlag(Component.INACTIVE);
					txtItemTitle.setFlag(Component.INACTIVE);
					txtItemDescription.setFlag(Component.INACTIVE);
					txtItemRarity.setFlag(Component.INACTIVE);
					txtItemValue.setFlag(Component.INACTIVE);
					txtItemStats.setFlag(Component.INACTIVE);
					imgItemPreview.setFlag(Component.INACTIVE);

					for(Component comp : tabComponents)
						comp.setFlag(0);
					for(Component comp : slotComponents)
						comp.setFlag(0);
					
					if(scrollUp != null){
						scrollUp.setFlag(0);
						scrollDown.setFlag(0);
					}
				}
			});
			add(btnBack);
		}

		if(btnDispose == null){
			btnDispose = new Component("btnDispose");
			btnDispose.setBackground("gui/button.png");
			btnDispose.setBackgroundSelected("gui/button_selected.png");
			btnDispose.setText("Drop");
			btnDispose.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM - 8f);
			btnDispose.setWidth(164);
			btnDispose.setHeight(72);
			btnDispose.setX(btnBack.getX() + btnBack.getWidth() + 16f);
			btnDispose.setY(btnBack.getY());
			btnDispose.addListener(new ComponentListenerImpl(){
				public void touchUp(Component component, Game game){
					if(selectedItem != null){
						//TODO: Entity pickup for item.
						//game.getEvents().onEvent(game, EventType.ITEM_DROP, selectedItem.getItem(), game.getPlayer());
						game.getHelper().dialog("This feature is currently not available.");
					}
				}
			});
			add(btnDispose);
		}

		if(btnEquipUse == null){
			btnEquipUse = new Component("btnEquipUse");
			btnEquipUse.setBackground("gui/button.png");
			btnEquipUse.setBackgroundSelected("gui/button_selected.png");
			btnEquipUse.setText("Use");
			btnEquipUse.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM - 8f);
			btnEquipUse.setWidth(164);
			btnEquipUse.setHeight(72);
			btnEquipUse.setX(btnDispose.getX() + btnDispose.getWidth() + 16f);
			btnEquipUse.setY(btnDispose.getY());
			btnEquipUse.addListener(new ComponentListenerImpl(){
				public void touchUp(Component component, Game game){
					if(selectedItem != null){
						if(selectedItem.getItem() instanceof ItemEquippable){
							ItemEquippable ie = (ItemEquippable)selectedItem.getItem();
							int slot = game.getPlayer().getSlot(selectedItem.getItem());
							if(slot > -1){
								game.getPlayer().setEquipped(slot, null);
								ie.onUnEquip(game, game.getPlayer());
								//game.getEvents().onEvent(game, EventType.ITEM_UNEQUIP, ie, game.getPlayer(), slot);
							}else{
								boolean wasEquipped = false;
								for(int i = 0; i < ie.getEquipSlots().length; i++){
									if(game.getPlayer().getEquippedItem(ie.getEquipSlots()[i]) == null){
										game.getPlayer().setEquipped(ie.getEquipSlots()[i], ie);
										ie.onEquip(game, game.getPlayer());
										//game.getEvents().onEvent(game, EventType.ITEM_EQUIP, ie, game.getPlayer(), ie.getEquipSlots()[i]);
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
									//game.getEvents().onEvent(game, EventType.ITEM_EQUIP, ie, game.getPlayer(), ie.getEquipSlots()[0]);
									ie.onEquip(game, game.getPlayer());
								}
							}
						}else if(selectedItem.getItem().canUse()){
							selectedItem.getItem().onEquip(game, game.getPlayer());
							//game.getEvents().onEvent(game, EventType.ITEM_EQUIP, selectedItem.getItem(), game.getPlayer(), game.getPlayer().getSlot(selectedItem.getItem()));
							if(selectedItem.getItem().isConsumed())
								selectedItem.consume();
						}else
							game.getHelper().dialog("This item cannot be used or equipped.");
					}
				}
			});
			add(btnEquipUse);
		}

		openedWindow = true;
		if(txtItemTitle == null){
			txtItemTitle = new Component("txtItemTitle");
			txtItemTitle.setX(itemWindow.getX() + 32f);
			txtItemTitle.setY(itemWindow.getY() + 64f);
			txtItemTitle.setText("Null Title");
			txtItemTitle.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM - 8f);
			txtItemTitle.getPaint().setTextAlign(Align.LEFT);
			txtItemTitle.addListener(new ComponentListenerImpl(){
				int updateLast = 0;
				int updateTotal = 0;
				public void update(Component component, Game game){					
					updateTotal++;
					if(updateTotal > updateLast && openedWindow){
						openedWindow = false;
						updateLast = 0;
						btnBack.setFlag(0);
						btnDispose.setFlag(0);
						btnEquipUse.setFlag(0);
					}
					if(openedWindow)
						updateLast = updateTotal;
					selectedItem = provider.provide(tab, itemsPerPage * page + selected);
					if(selectedItem != null){
						txtItemTitle.setText(selectedItem.getItem().getShowName() + "  (" + selectedItem.getAmount() + ")");
						if(selectedItem.getItem() instanceof ItemEquippable){
							txtItemTitle.setText(selectedItem.getItem().getShowName());
							if(game.getPlayer().getSlot(selectedItem.getItem()) > -1){
								btnEquipUse.setText("Unequip");
								txtItemTitle.setText(selectedItem.getItem().getShowName() + "  (Equipped)");
							}else{
								btnEquipUse.setText("Equip");
								if(tab == InventoryProvider.TAB_EQUIPPED)
									btnBack.simulateInput(game, MotionEvent.ACTION_UP);
							}
						}else if(selectedItem.getItem().canUse())
							btnEquipUse.setText("Use");
						else
							btnEquipUse.setFlag(Component.INACTIVE);
						txtItemRarity.setText("Rarity: " + selectedItem.getItem().getRarity().toString());
						txtItemValue.setText("Value: " + selectedItem.getItem().getValue() + " Gold");
						if(selectedItem.getItem() instanceof Weapon){
							Weapon w = (Weapon)selectedItem.getItem();
							txtItemStats.setText("Damage: " + Util.format(w.getDamage()));
						}else if(selectedItem.getItem() instanceof Armour){
							Armour a = (Armour)selectedItem.getItem();
							txtItemStats.setText("Armour: " + Util.format(a.getArmourValue()));
						}else
							txtItemStats.setText("");
						imgItemPreview.setBackgroundDefault(selectedItem.getItem().getResource());

						if(game.getInput().isDown(Input.KEY_MENU) || selectedItem == null)
							btnBack.simulateInput(game, MotionEvent.ACTION_UP);
					}
				}
			});
			add(txtItemTitle);
		}
		txtItemTitle.setFlag(0);

		if(txtItemDescription == null){
			txtItemDescription = new Component("txtItemDescription"){
				@Override
				public void draw(Game game, Canvas canvas){
					if(flag == Component.INACTIVE)
						return;
					if(selectedItem != null){
						int s = canvas.save();
						canvas.translate(getX(), getY());
						setHeight(RenderUtil.drawWrappedText(game, selectedItem.getItem().getDescription().replace("\\n", "\n"), (int)(itemWindow.getWidth() / 16f * 11f), paint, canvas));
						canvas.restoreToCount(s);
					}
				}
			};
			txtItemDescription.setX(txtItemTitle.getX());
			txtItemDescription.setY(txtItemTitle.getY() + txtItemTitle.getPaint().getTextSize() + 8f);
			txtItemDescription.setText("Null");
			txtItemDescription.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
			txtItemDescription.getPaint().setTextAlign(Align.LEFT);
			add(txtItemDescription);
		}
		txtItemDescription.setFlag(0);

		if(txtItemRarity == null){
			txtItemRarity = new Component("txtItemRarity");
			txtItemRarity.setX(txtItemDescription.getX());
			txtItemRarity.setY(txtItemDescription.getY() + txtItemDescription.getPaint().getTextSize() * 4 + 64f);
			txtItemRarity.getPaint().setTextAlign(Align.LEFT);
			txtItemRarity.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
			add(txtItemRarity);
		}
		txtItemRarity.setFlag(0);

		if(txtItemValue == null){
			txtItemValue = new Component("txtItemValue");
			txtItemValue.setX(txtItemRarity.getX());
			txtItemValue.setY(txtItemRarity.getY() + txtItemRarity.getPaint().getTextSize() + 8f);
			txtItemValue.getPaint().setTextAlign(Align.LEFT);
			txtItemValue.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
			add(txtItemValue);
		}
		txtItemValue.setFlag(0);

		if(txtItemStats == null){
			txtItemStats = new Component("txtItemStats");
			txtItemStats.setX(itemWindow.getX() + itemWindow.getWidth() - 172);
			txtItemStats.setY(txtItemTitle.getY() + 164);
			txtItemStats.getPaint().setTextAlign(Align.LEFT);
			txtItemStats.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
			add(txtItemStats);
		}
		txtItemStats.setFlag(0);

		if(imgItemPreview == null){
			imgItemPreview = new Component("imgItemPreview");
			imgItemPreview.setX(itemWindow.getX() + itemWindow.getWidth() - 172);
			imgItemPreview.setY(txtItemTitle.getY());
			imgItemPreview.setWidth(128f);
			imgItemPreview.setHeight(128f);
			add(imgItemPreview);
		}
		imgItemPreview.setFlag(0);
	}

	public void addTab(String resource, String selectedResource, float x, float y, float width, float height, int index){
		Component tabComp = new Component("tab" + index);
		tabComp.tag = index;
		tabComp.setBackground(resource);
		tabComp.setBackgroundSelected(selectedResource);
		tabComp.setBounds(x, y, width, height);
		tabComp.attachPaintMixer(new TabOverlayMixer());
		tabComp.addListener(new ComponentListenerImpl(){
			public void touchDown(Component component, Game game){
				tabHover = component.tag;
			}
			public void touchUp(Component component, Game game){
				tab = component.tag;
				onTabSelected(game);
			}
		});
		tabComponents.add(tabComp);
		add(tabComp);
	}

	public Component addSlot(String resource, String selectedResource, float x, float y, float width, float height, int index){
		Component slotComp = new Component("slot" + index);
		slotComp.tag = index;
		slotComp.setBackground(resource);
		slotComp.setBackgroundSelected(selectedResource);
		slotComp.setBounds(x, y, width, height);
		slotComp.attachPaintMixer(new ItemOverlayMixer(this));
		slotComp.addListener(new ComponentListenerImpl(){
			public void touchDown(Component component, Game game){
				selectedHover = component.tag;
			}
			public void touchUp(Component component, Game game){
				if(selected == -1){
					selected = component.tag;
					onSlotSelected(game);
				}
			}
		});
		slotComponents.add(slotComp);
		add(slotComp);
		return slotComp;
	}

	public Component addEquipmentSlot(String resource, String selectedResource, float x, float y, float width, float height, int equipIndex){
		Component slotComp = new Component("equip_slot" + equipIndex);
		slotComp.tag = equipIndex;
		slotComp.setBackground(resource);
		slotComp.setBackgroundSelected(selectedResource);
		slotComp.setBounds(x, y, width, height);
		slotComp.attachPaintMixer(new ItemOverlayMixer(this));
		slotComp.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				tab = InventoryProvider.TAB_EQUIPPED;
				selected = component.tag;
				onSlotSelected(game);
			}
		});
		add(slotComp);
		return slotComp;
	}

	public static class TabOverlayMixer implements IPaintMixer{

		public TabOverlayMixer(){}

		public void preRender(Game game, Canvas canvas, IGameObject object){}

		public void postRender(Game game, Canvas canvas, IGameObject object){
			Component c = (Component)object;
			String overlayResource = "gui/inventory/tab/" + c.tag + ".png";
			canvas.drawBitmap(game.getResources().getBitmap(overlayResource), null, c.getBoundsF(), object.getPaint());
		}
	}

	public static class SelectedMixer implements IPaintMixer{

		private GuiPlayerInventory gui;

		public SelectedMixer(GuiPlayerInventory gui){
			this.gui = gui;
		}

		public void preRender(Game game, Canvas canvas, IGameObject object){
			Component comp = (Component)object;
			if(comp.tag != gui.selected || gui.tab == -1)
				comp.detachPaintMixer(this);
		}

		public void postRender(Game game, Canvas canvas, IGameObject object){
			canvas.drawBitmap(game.getResources().getBitmap("gui/inventory/slot_overlay.png"), null, ((Component)object).getBoundsF(), object.getPaint());
		}
	}

	public static class ItemOverlayMixer implements IPaintMixer{

		private GuiPlayerInventory gui;
		private ItemStack item;

		public ItemOverlayMixer(GuiPlayerInventory gui){
			this.gui = gui;
		}

		public void preRender(Game game, Canvas canvas, IGameObject object){
			Component component = (Component)object;
			ItemStack stack = null;
			if(component.getName().startsWith("equip_slot")){
				stack = gui.provider.provide(InventoryProvider.TAB_EQUIPPED, component.tag);
			}else{
				int invIndex = gui.page * ITEMS_PER_PAGE + component.tag;
				stack = gui.provider.provide(gui.tab, invIndex);
			}
			item = stack != null && stack.getAmount() > 0 ? stack : null;
		}

		public void postRender(Game game, Canvas canvas, IGameObject object){
			Component component = (Component)object;
			if(item != null){
				canvas.drawBitmap(game.getResources().getBitmap(item.getItem().getResource()), null, new RectF(component.getX() + 8f, component.getY() + 8f, component.getX() + component.getWidth() - 8f, component.getY() + component.getHeight() - 8f), null);
				float size = object.getPaint().getTextSize();
				object.getPaint().setTextSize(24f);
				canvas.drawText(String.valueOf(item.getAmount()), component.getX() + component.getWidth() - 24f, component.getY() + component.getHeight() - 16f, object.getPaint());
				object.getPaint().setTextSize(size);
			}
		}
	}
}
