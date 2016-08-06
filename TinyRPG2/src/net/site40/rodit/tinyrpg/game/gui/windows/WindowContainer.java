package net.site40.rodit.tinyrpg.game.gui.windows;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.item.Inventory.InventoryProvider;
import net.site40.rodit.tinyrpg.game.item.ItemStack;

public class WindowContainer extends WindowSlotted{
	
	public static final String PLAYER_KEY = "player_inventory";
	public static final String CONTAINER_KEY = "container_inventory";
	
	public static final int COUNT_ROWS = 3;
	public static final int COUNT_COLUMNS = 3;
	public static final int ITEMS_PER_PAGE = COUNT_ROWS * COUNT_COLUMNS;
	public static final float OFFSET_X = 528f + 128f;
	public static final float OFFSET_Y = 192f;
	public static final float X_SUB = 512f;
	
	private Entity container;
	
	private WindowComponent txtTitle;
	
	private WindowComponent txtPageNoPlayer;
	private WindowComponent btnPageUpPlayer;
	private WindowComponent btnPageDownPlayer;

	private WindowComponent txtPageNoContainer;
	private WindowComponent btnPageUpContainer;
	private WindowComponent btnPageDownContainer;

	public WindowContainer(Game game, Entity container){
		super(game);
		this.container = container;
		initialize(game);
	}
	
	@Override
	public void initialize(Game game){		
		if(container == null)
			return;
		
		super.initialize(game);
		
		registerProvider(PLAYER_KEY, game.getPlayer().getInventory().getProvider(game.getPlayer()));
		registerProvider(CONTAINER_KEY, container.getInventory().getProvider(container));
		
		this.setBounds(64, 32, 1152, 640);

		this.txtTitle = new WindowComponent("txtTitle");
		txtTitle.setText("Container");
		txtTitle.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		txtTitle.setX(this.getWidth() / 2f);
		txtTitle.setY(92f);
		add(txtTitle);
		
		int k = 0;
		for(int y = 0; y < COUNT_ROWS; y++)
			for(int x = 0; x < COUNT_COLUMNS; x++)
				addSlot(OFFSET_X + x * WindowSlot.SLOT_WIDTH - X_SUB, OFFSET_Y + y * WindowSlot.SLOT_HEIGHT, PLAYER_KEY, "player_inv_" + k++);
		
		txtPageNoPlayer = new WindowComponent("txtPageNoPlayer");
		txtPageNoPlayer.setX(1148 - getX() - X_SUB);
		txtPageNoPlayer.setY(412 - getY());
		txtPageNoPlayer.setText("1/1");
		txtPageNoPlayer.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		txtPageNoPlayer.addListener(new WindowListener(){
			public void update(Game game, WindowComponent component){
				ProviderInfo info = getProviderInfo(PLAYER_KEY);
				if(info == null)
					return;
				String page = String.valueOf(info.page[info.selectedTab] + 1);
				component.setText(page + "/" + getMaxPages(PLAYER_KEY, ITEMS_PER_PAGE));
			}
		});
		add(txtPageNoPlayer);

		btnPageDownPlayer = new WindowComponent("btnPageDownPlayer");
		btnPageUpPlayer = new WindowComponent("btnPageUpPlayer");
		btnPageUpPlayer.setBackground(WindowComponent.STATE_IDLE, "gui/scroll_up.png");
		btnPageUpPlayer.setBackground(WindowComponent.STATE_DOWN, "gui/scroll_up_selected.png");
		btnPageUpPlayer.setX(1116 - getX() - X_SUB);
		btnPageUpPlayer.setY(248 - getY());
		btnPageUpPlayer.setWidth(72);
		btnPageUpPlayer.setHeight(72);
		btnPageUpPlayer.addListener(new WindowListener(){
			public void touchUp(Game game, WindowComponent component){
				int maxPages = getMaxPages(PLAYER_KEY, ITEMS_PER_PAGE);
				ProviderInfo info = getProviderInfo(PLAYER_KEY);
				info.page[info.selectedTab]--;
				if(info.page[info.selectedTab] == -1)
					info.page[info.selectedTab] = (maxPages > 0 ? maxPages : 1) - 1;
				txtPageNoPlayer.setText((info.page[info.selectedTab] + 1) + "/" + maxPages);
			}
		});
		add(btnPageUpPlayer);

		btnPageDownPlayer.setBackground(WindowComponent.STATE_IDLE, "gui/scroll_down.png");
		btnPageDownPlayer.setBackground(WindowComponent.STATE_DOWN, "gui/scroll_down_selected.png");
		btnPageDownPlayer.setX(1116 - getX() - X_SUB);
		btnPageDownPlayer.setY(512 - getY());
		btnPageDownPlayer.setWidth(72);
		btnPageDownPlayer.setHeight(72);
		btnPageDownPlayer.addListener(new WindowListener(){
			public void touchUp(Game game, WindowComponent component){
				int maxPages = getMaxPages(PLAYER_KEY, ITEMS_PER_PAGE);
				ProviderInfo info = getProviderInfo(PLAYER_KEY);
				info.page[info.selectedTab]++;
				if(info.page[info.selectedTab] >= maxPages)
					info.page[info.selectedTab] = 0;
				txtPageNoPlayer.setText((info.page[info.selectedTab] + 1) + "/" + maxPages);
			}
		});
		add(btnPageDownPlayer);
		
		k = 0;
		for(int y = 0; y < COUNT_ROWS; y++)
			for(int x = 0; x < COUNT_COLUMNS; x++)
				addSlot(OFFSET_X + x * WindowSlot.SLOT_WIDTH, OFFSET_Y + y * WindowSlot.SLOT_HEIGHT, CONTAINER_KEY, "container_inv_" + k++);

		txtPageNoContainer = new WindowComponent("txtPageNoContainer");
		txtPageNoContainer.setX(1148 - getX());
		txtPageNoContainer.setY(412 - getY());
		txtPageNoContainer.setText("1/1");
		txtPageNoContainer.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		txtPageNoContainer.addListener(new WindowListener(){
			public void update(Game game, WindowComponent component){
				ProviderInfo info = getProviderInfo(CONTAINER_KEY);
				if(info == null)
					return;
				String page = String.valueOf(info.page[info.selectedTab] + 1);
				component.setText(page + "/" + getMaxPages(CONTAINER_KEY, ITEMS_PER_PAGE));
			}
		});
		add(txtPageNoContainer);

		btnPageDownContainer = new WindowComponent("btnPageDownContainer");
		btnPageUpContainer = new WindowComponent("btnPageUpContainer");
		btnPageUpContainer.setBackground(WindowComponent.STATE_IDLE, "gui/scroll_up.png");
		btnPageUpContainer.setBackground(WindowComponent.STATE_DOWN, "gui/scroll_up_selected.png");
		btnPageUpContainer.setX(1116 - getX());
		btnPageUpContainer.setY(248 - getY());
		btnPageUpContainer.setWidth(72);
		btnPageUpContainer.setHeight(72);
		btnPageUpContainer.addListener(new WindowListener(){
			public void touchUp(Game game, WindowComponent component){
				int maxPages = getMaxPages(CONTAINER_KEY, ITEMS_PER_PAGE);
				ProviderInfo info = getProviderInfo(CONTAINER_KEY);
				info.page[info.selectedTab]--;
				if(info.page[info.selectedTab] == -1)
					info.page[info.selectedTab] = (maxPages > 0 ? maxPages : 1) - 1;
				txtPageNoContainer.setText((info.page[info.selectedTab] + 1) + "/" + maxPages);
			}
		});
		add(btnPageUpContainer);

		btnPageDownContainer.setBackground(WindowComponent.STATE_IDLE, "gui/scroll_down.png");
		btnPageDownContainer.setBackground(WindowComponent.STATE_DOWN, "gui/scroll_down_selected.png");
		btnPageDownContainer.setX(1116 - getX());
		btnPageDownContainer.setY(512 - getY());
		btnPageDownContainer.setWidth(72);
		btnPageDownContainer.setHeight(72);
		btnPageDownContainer.addListener(new WindowListener(){
			public void touchUp(Game game, WindowComponent component){
				int maxPages = getMaxPages(CONTAINER_KEY, ITEMS_PER_PAGE);
				ProviderInfo info = getProviderInfo(CONTAINER_KEY);
				info.page[info.selectedTab]++;
				if(info.page[info.selectedTab] >= maxPages)
					info.page[info.selectedTab] = 0;
				txtPageNoContainer.setText((info.page[info.selectedTab] + 1) + "/" + maxPages);
			}
		});
		add(btnPageDownContainer);
	}
	
	@Override
	public int getItemsPerPage(Object providerKey){
		return ITEMS_PER_PAGE;
	}
	
	@Override
	public void onSlotSelected(Game game, WindowSlot slot){
		ProviderInfo info = getProviderInfo(slot.getProviderKey());
		if(info == null)
			return;
		ItemStack stack = info.provider.provide(InventoryProvider.TAB_ALL, info.page[info.selectedTab] * getItemsPerPage(slot.getProviderKey()) + slot.getIndex());
		if(stack == null)
			return;
		WindowContainerItemInfo itemWindow = new WindowContainerItemInfo(game, stack, info);
		itemWindow.zIndex = 1;
		game.getWindows().register(itemWindow);
		itemWindow.show();
	}
}
