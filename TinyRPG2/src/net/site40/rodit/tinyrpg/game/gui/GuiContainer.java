package net.site40.rodit.tinyrpg.game.gui;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.IGameObject;
import net.site40.rodit.tinyrpg.game.IPaintMixer;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.gui.ComponentListener.ComponentListenerImpl;
import net.site40.rodit.tinyrpg.game.item.Inventory;
import net.site40.rodit.tinyrpg.game.item.Inventory.InventoryProvider;
import net.site40.rodit.tinyrpg.game.item.ItemStack;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

public class GuiContainer extends GuiPlayerInventory{

	private static final int COUNT_ROWS = 3;
	private static final int COUNT_COLUMNS = 3;
	private static final int ITEMS_PER_PAGE = COUNT_ROWS * COUNT_COLUMNS;
	private static final float OFFSET_X_PLAYER = 128f;
	private static final float OFFSET_Y_PLAYER = OFFSET_Y;
	private static final float OFFSET_X_CONTAINER = OFFSET_X + 128f;
	private static final float OFFSET_Y_CONTAINER = OFFSET_Y;
	private static final float X_SUB = 512f;

	private int pagePlayer = 0;
	private int pageContainer = 0;

	protected WindowComponent window;
	protected Component scrollDownPlayer;
	protected Component scrollUpPlayer;
	protected Component scrollDownContainer;
	protected Component scrollUpContainer;
	protected Component txtPageNoPlayer;
	protected Component txtPageNoContainer;

	protected Component btnMove;

	protected InventoryProvider playerProvider;
	protected InventoryProvider containerProvider;
	protected InventoryProvider currentProvider;

	protected boolean closing = true;
	
	protected boolean openedWindow = false;

	public GuiContainer(){
		super();
	}

	@Override
	public void onShown(){
		pagePlayer = 0;
		pageContainer = 0;
		playerProvider = null;
		containerProvider = null;
		closing = false;
		selected = -1;
		tab = InventoryProvider.TAB_ALL;
	}

	@Override
	public void onHidden(){
		closing = true;
	}

	@Override
	public void init(){
		this.tabComponents = new ArrayList<Component>();
		this.slotComponents = new ArrayList<Component>();

		this.window = new WindowComponent();
		window.setX(64);
		window.setY(32);
		window.setWidth(1152);
		window.setHeight(640);
		add(window);

		Component title = new Component("txtTitle", "Inventory Transfer");
		title.setX(window.getX() + window.getWidth() / 2);
		title.setY(window.getY() + 48);
		title.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		add(title);

		int k = 0;
		for(int y = 0; y < COUNT_ROWS; y++){
			for(int x = 0; x < COUNT_COLUMNS; x++){
				addSlotPlayer("gui/inventory/slot.png", "gui/inventory/slot_hover.png", OFFSET_X_PLAYER + x * SLOT_WIDTH, OFFSET_Y_PLAYER + y * SLOT_HEIGHT, SLOT_WIDTH, SLOT_HEIGHT, k++);
			}
		}

		k = 0;
		for(int y = 0; y < COUNT_ROWS; y++){
			for(int x = 0; x < COUNT_COLUMNS; x++){
				addSlotContainer("gui/inventory/slot.png", "gui/inventory/slot_hover.png", OFFSET_X_CONTAINER + x * SLOT_WIDTH, OFFSET_Y_CONTAINER + y * SLOT_HEIGHT, SLOT_WIDTH, SLOT_HEIGHT, k++);
			}
		}

		scrollDownContainer = new Component("scrollDownContainer");
		scrollUpContainer = new Component("scrollUpContainer");
		scrollUpContainer.setBackground("gui/scroll_up.png");
		scrollUpContainer.setBackgroundSelected("gui/scroll_up_selected.png");
		scrollUpContainer.setX(1116);
		scrollUpContainer.setY(248);
		scrollUpContainer.setWidth(72);
		scrollUpContainer.setHeight(72);
		scrollUpContainer.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				int maxPages = getMaxPages(game, containerProvider);
				pageContainer--;
				if(pageContainer == -1)
					pageContainer = maxPages - 1;
				txtPageNoContainer.setText((pageContainer + 1) + "/" + maxPages);
			}
		});
		add(scrollUpContainer);

		scrollDownContainer.setBackground("gui/scroll_down.png");
		scrollDownContainer.setBackgroundSelected("gui/scroll_down_selected.png");
		scrollDownContainer.setX(1116);
		scrollDownContainer.setY(512);
		scrollDownContainer.setWidth(72);
		scrollDownContainer.setHeight(72);
		scrollDownContainer.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				int maxPages = getMaxPages(game, containerProvider);
				pageContainer++;
				if(pageContainer == maxPages)
					pageContainer = 0;
				txtPageNoContainer.setText((pageContainer + 1) + "/" + maxPages);
			}
		});
		add(scrollDownContainer);

		scrollDownPlayer = new Component("scrollDownPlayer");
		scrollUpPlayer = new Component("scrollUpPlayer");
		scrollUpPlayer.setBackground("gui/scroll_up.png");
		scrollUpPlayer.setBackgroundSelected("gui/scroll_up_selected.png");
		scrollUpPlayer.setX(OFFSET_X_PLAYER + COUNT_COLUMNS * SLOT_WIDTH + 16f);
		scrollUpPlayer.setY(248);
		scrollUpPlayer.setWidth(72);
		scrollUpPlayer.setHeight(72);
		scrollUpPlayer.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				int maxPages = getMaxPages(game, playerProvider);
				pagePlayer--;
				if(pagePlayer == -1)
					pagePlayer = maxPages - 1;
				txtPageNoPlayer.setText((pagePlayer + 1) + "/" + maxPages);
			}
		});
		add(scrollUpPlayer);

		scrollDownPlayer.setBackground("gui/scroll_down.png");
		scrollDownPlayer.setBackgroundSelected("gui/scroll_down_selected.png");
		scrollDownPlayer.setX(OFFSET_X_PLAYER + COUNT_COLUMNS * SLOT_WIDTH + 16f);
		scrollDownPlayer.setY(512);
		scrollDownPlayer.setWidth(72);
		scrollDownPlayer.setHeight(72);
		scrollDownPlayer.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				int maxPages = getMaxPages(game, playerProvider);
				pagePlayer++;
				if(pagePlayer == maxPages)
					pagePlayer = 0;
				txtPageNoPlayer.setText((pagePlayer + 1) + "/" + maxPages);
			}
		});
		add(scrollDownPlayer);

		txtPageNoContainer = new Component("txtPageNoContainer");
		txtPageNoContainer.setX(1148);
		txtPageNoContainer.setY(412);
		txtPageNoContainer.setFlag(Component.IGNORE_CUSTOM_CENTERING);
		txtPageNoContainer.setText("1/1");
		txtPageNoContainer.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		txtPageNoContainer.addListener(new ComponentListenerImpl(){
			boolean first = true;
			public void update(Component component, Game game){
				if(first){
					component.setText("1/" + getMaxPages(game, containerProvider));
					first = false;
				}
			}
		});
		add(txtPageNoContainer);

		txtPageNoPlayer = new Component("txtPageNoPlayer");
		txtPageNoPlayer.setX(OFFSET_X_PLAYER + COUNT_COLUMNS * SLOT_WIDTH + 48f);
		txtPageNoPlayer.setY(412);
		txtPageNoPlayer.setFlag(Component.IGNORE_CUSTOM_CENTERING);
		txtPageNoPlayer.setText("1/1");
		txtPageNoPlayer.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		txtPageNoPlayer.addListener(new ComponentListenerImpl(){
			boolean first = true;
			public void update(Component component, Game game){
				if(first){
					component.setText("1/" + getMaxPages(game, playerProvider));
					first = false;
				}
			}
		});
		add(txtPageNoPlayer);
	}

	@Override
	public void onSlotSelected(Game game){
		this.provider = currentProvider;
		this.page = currentProvider == containerProvider ? pageContainer : pagePlayer;

		super.onSlotSelected(game, ITEMS_PER_PAGE);

		if(selected == -1 || selectedItem == null){
			selected = -1;
			if(btnBack != null)
				btnBack.simulateInput(game, MotionEvent.ACTION_UP);
			return;
		}

		if(btnBack.getListeners().size() == 1){
			btnBack.addListener(new ComponentListenerImpl(){
				public void touchUp(Component component, Game game){
					btnMove.setFlag(Component.INACTIVE);
				}
			});
		}
		
		if(btnMove == null){
			btnMove = new Component("btnMove");
			btnMove.setBackground("gui/button.png");
			btnMove.setBackgroundSelected("gui/button_selected.png");
			btnMove.setText("Move");
			btnMove.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM - 8f);
			btnMove.setWidth(164);
			btnMove.setHeight(72);
			btnMove.setX(btnDispose.getX() + btnDispose.getWidth() + 16f);
			btnMove.setY(btnDispose.getY());
			btnMove.addListener(new ComponentListenerImpl(){
				public void touchUp(Component component, Game game){
					if(currentProvider == playerProvider){
						playerProvider.getInventory().getItems().remove(selectedItem);
						containerProvider.getInventory().add(selectedItem);
					}else if(currentProvider == containerProvider){
						containerProvider.getInventory().getItems().remove(selectedItem);
						playerProvider.getInventory().add(selectedItem);
					}
					btnBack.simulateInput(game, MotionEvent.ACTION_UP);
				}
			});
			add(btnMove);
		}

		remove(btnDispose);
		remove(btnEquipUse);
		
		openedWindow = true;
		if(txtItemTitle.getListeners().size() == 1){
			txtItemTitle.addListener(new ComponentListenerImpl(){
				int updateLast = 0;
				int updateTotal = 0;
				public void update(Component component, Game game){					
					updateTotal++;
					if(updateTotal > updateLast && openedWindow){
						openedWindow = false;
						updateLast = 0;
						btnMove.setFlag(0);
					}
					if(openedWindow)
						updateLast = updateTotal;

					if(currentProvider == playerProvider)
						btnMove.setText("Store");
					else if(currentProvider == containerProvider)
						btnMove.setText("Take");
					else
						btnMove.setText("Hmm...");
				}
			});
		}

		if(currentProvider == playerProvider)
			itemWindow.setX(OFFSET_X - 16 - X_SUB);
		else
			itemWindow.setX(OFFSET_X - 16);

		btnBack.setX(itemWindow.getX() + 52);
		btnDispose.setX(btnBack.getX() + btnBack.getWidth() + 16f);
		btnMove.setX(btnDispose.getX() + btnDispose.getWidth() + 16f);
		txtItemTitle.setX(itemWindow.getX() + 32f);
		txtItemDescription.setX(txtItemTitle.getX());
		txtItemRarity.setX(txtItemDescription.getX());
		txtItemValue.setX(txtItemRarity.getX());
		txtItemStats.setX(itemWindow.getX() + itemWindow.getWidth() - 172);
		imgItemPreview.setX(itemWindow.getX() + itemWindow.getWidth() - 172);
	}

	public int getMaxPages(Game game, InventoryProvider provider){
		return Math.max((int)Math.ceil((float)provider.provide(InventoryProvider.TAB_ALL).size() / (float)ITEMS_PER_PAGE), 1);
	}

	public Component addSlotPlayer(String resource, String selectedResource, float x, float y, float width, float height, int index){
		Component slotComp = new Component("slot_player" + index);
		slotComp.tag = index;
		slotComp.setBackground(resource);
		slotComp.setBackgroundSelected(selectedResource);
		slotComp.setBounds(x, y, width, height);
		slotComp.attachPaintMixer(new ItemOverlayMixer(this));
		slotComp.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				currentProvider = playerProvider;
				selected = component.tag;
				onSlotSelected(game);
			}
		});
		add(slotComp);
		slotComponents.add(slotComp);
		return slotComp;
	}

	public Component addSlotContainer(String resource, String selectedResource, float x, float y, float width, float height, int index){
		Component slotComp = new Component("slot_container" + index);
		slotComp.tag = index;
		slotComp.setBackground(resource);
		slotComp.setBackgroundSelected(selectedResource);
		slotComp.setBounds(x, y, width, height);
		slotComp.attachPaintMixer(new ItemOverlayMixer(this));
		slotComp.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				currentProvider = containerProvider;
				selected = component.tag;
				onSlotSelected(game);
			}
		});
		add(slotComp);
		slotComponents.add(slotComp);
		return slotComp;
	}

	@Override
	public void update(Game game){
		if(playerProvider == null)
			playerProvider = game.getPlayer().getInventory().getProvider(game.getPlayer());
		if(containerProvider == null){
			Entity currentContainer = (Entity)game.getGlobal("current_container");
			if(currentContainer == null){
				containerProvider = new InventoryProvider(new Inventory(), null);
				Log.w("GuiContainer", "Game global current_container was not set so new, empty inventory provider had to be created.");
			}else
				containerProvider = currentContainer.getInventory().getProvider(currentContainer);
		}
		
		if(this.active){
			if(closing)
				game.getInput().allowMovement(true);
			else
				game.getInput().allowMovement(false);
		}

		super.update(game);
	}

	public static class ItemOverlayMixer implements IPaintMixer{

		private GuiContainer gui;
		private ItemStack item;

		public ItemOverlayMixer(GuiContainer gui){
			this.gui = gui;
		}

		public void preRender(Game game, Canvas canvas, IGameObject object){
			Component component = (Component)object;
			ItemStack stack = null;
			boolean player = component.getName().contains("player");
			int invIndex = (player ? gui.pagePlayer : gui.pageContainer) * ITEMS_PER_PAGE + component.tag;
			InventoryProvider provider = player ? gui.playerProvider : gui.containerProvider;
			stack = provider.provide(InventoryProvider.TAB_ALL, invIndex);
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
