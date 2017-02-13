package net.site40.rodit.tinyrpg.game.gui.windows;

import static net.site40.rodit.tinyrpg.game.gui.windows.WindowSlot.SLOT_HEIGHT;
import static net.site40.rodit.tinyrpg.game.gui.windows.WindowSlot.SLOT_WIDTH;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.item.Inventory.InventoryProvider;
import net.site40.rodit.tinyrpg.game.item.ItemEquippable;
import net.site40.rodit.tinyrpg.game.item.ItemStack;

public class WindowInventory extends WindowSlotted{

	public static final String PLAYER_KEY = "player_inventory";

	public static final int COUNT_ROWS = 3;
	public static final int COUNT_COLUMNS = 4;
	public static final int ITEMS_PER_PAGE = COUNT_ROWS * COUNT_COLUMNS;
	public static final float OFFSET_X = 528f;
	public static final float OFFSET_Y = 192f;

	private WindowComponent txtTitle;
	private WindowComponent txtPageNo;
	private WindowComponent btnPageUp;
	private WindowComponent btnPageDown;

	public WindowInventory(Game game){
		super(game);
	}

	@Override
	public void initialize(Game game){
		super.initialize(game);

		registerProvider(PLAYER_KEY, game.getPlayer().getInventory().getProvider(game.getPlayer()));

		this.setBounds(64, 32, 1152, 640);

		this.txtTitle = new WindowComponent("txtTitle");
		txtTitle.setText("Inventory");
		txtTitle.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		txtTitle.setX(bounds.getWidth() / 2f);
		txtTitle.setY(78f);
		add(txtTitle);

		int k = 0;
		for(int y = 0; y < COUNT_ROWS; y++)
			for(int x = 0; x < COUNT_COLUMNS; x++)
				addSlot(OFFSET_X + x * WindowSlot.SLOT_WIDTH, OFFSET_Y + y * WindowSlot.SLOT_HEIGHT, PLAYER_KEY, "inv_" + k++);

		for(int i = 0; i < 7; i++)
			addTab(OFFSET_X + i * WindowTab.TAB_WIDTH, OFFSET_Y - WindowTab.TAB_HEIGHT, i, PLAYER_KEY);

		addEquipmentSlot((OFFSET_X - bounds.getX()) / 2f + 92f - SLOT_WIDTH / 2f - bounds.getX(), 64f + SLOT_HEIGHT / 2f, PLAYER_KEY, ItemEquippable.SLOT_HELMET);
		addEquipmentSlot((OFFSET_X - bounds.getX()) / 2f + 92f - SLOT_WIDTH / 2f - bounds.getX(), 64f + SLOT_HEIGHT + SLOT_HEIGHT / 2f, PLAYER_KEY, ItemEquippable.SLOT_CHEST);
		addEquipmentSlot((OFFSET_X - bounds.getX()) / 2f + 92f - SLOT_WIDTH / 2f + SLOT_WIDTH - bounds.getX(), 64f + SLOT_HEIGHT, PLAYER_KEY, ItemEquippable.SLOT_SHOULDERS);
		addEquipmentSlot((OFFSET_X - bounds.getX()) / 2f + 92f - SLOT_WIDTH / 2f - SLOT_WIDTH - bounds.getX(), 64f + SLOT_HEIGHT, PLAYER_KEY, ItemEquippable.SLOT_NECK);
		addEquipmentSlot((OFFSET_X - bounds.getX()) / 2f + 92f - SLOT_WIDTH / 2f - SLOT_WIDTH / 2f - bounds.getX(), 64f + SLOT_HEIGHT * 3f, PLAYER_KEY, ItemEquippable.SLOT_FINGER_0);
		addEquipmentSlot((OFFSET_X - bounds.getX()) / 2f + 92f - SLOT_WIDTH / 2f + SLOT_WIDTH / 2f - bounds.getX(), 64f + SLOT_HEIGHT * 3f, PLAYER_KEY, ItemEquippable.SLOT_FINGER_1);
		addEquipmentSlot((OFFSET_X - bounds.getX()) / 2f + 92f - SLOT_WIDTH / 2f - SLOT_WIDTH - bounds.getX(), 64f + SLOT_HEIGHT * 2f, PLAYER_KEY, ItemEquippable.SLOT_HAND_0);
		addEquipmentSlot((OFFSET_X - bounds.getX()) / 2f + 92f - SLOT_WIDTH / 2f + SLOT_WIDTH - bounds.getX(), 64f + SLOT_HEIGHT * 2f, PLAYER_KEY, ItemEquippable.SLOT_HAND_1);
		
		txtPageNo = new WindowComponent("txtPageNo");
		txtPageNo.setX(1148 - bounds.getX());
		txtPageNo.setY(412 - bounds.getY());
		txtPageNo.setText("1/1");
		txtPageNo.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		txtPageNo.addListener(new WindowListener(){
			private String uPage;
			public void update(Game game, WindowComponent component){
				ProviderInfo info = getProviderInfo(PLAYER_KEY);
				if(info == null)
					return;
				uPage = String.valueOf(info.page[info.selectedTab] + 1);
				component.setText(uPage + "/" + getMaxPages(PLAYER_KEY, ITEMS_PER_PAGE));
			}
		});
		add(txtPageNo);

		btnPageDown = new WindowComponent("btnPageDown");
		btnPageUp = new WindowComponent("btnPageUp");
		btnPageUp.setBackground(WindowComponent.STATE_IDLE, "gui/scroll_up.png");
		btnPageUp.setBackground(WindowComponent.STATE_DOWN, "gui/scroll_up_selected.png");
		btnPageUp.setX(1116 - bounds.getX());
		btnPageUp.setY(248 - bounds.getY());
		btnPageUp.setWidth(72);
		btnPageUp.setHeight(72);
		btnPageUp.addListener(new WindowListener(){
			private int uMaxPages;
			public void touchUp(Game game, WindowComponent component){
				uMaxPages = getMaxPages(PLAYER_KEY, ITEMS_PER_PAGE);
				ProviderInfo info = getProviderInfo(PLAYER_KEY);
				info.page[info.selectedTab]--;
				if(info.page[info.selectedTab] == -1)
					info.page[info.selectedTab] = (uMaxPages > 0 ? uMaxPages : 1) - 1;
				txtPageNo.setText((info.page[info.selectedTab] + 1) + "/" + uMaxPages);
			}
		});
		add(btnPageUp);

		btnPageDown.setBackground(WindowComponent.STATE_IDLE, "gui/scroll_down.png");
		btnPageDown.setBackground(WindowComponent.STATE_DOWN, "gui/scroll_down_selected.png");
		btnPageDown.setX(1116 - bounds.getX());
		btnPageDown.setY(512 - bounds.getY());
		btnPageDown.setWidth(72);
		btnPageDown.setHeight(72);
		btnPageDown.addListener(new WindowListener(){
			private int uMaxPages;
			public void touchUp(Game game, WindowComponent component){
				uMaxPages = getMaxPages(PLAYER_KEY, ITEMS_PER_PAGE);
				ProviderInfo info = getProviderInfo(PLAYER_KEY);
				info.page[info.selectedTab]++;
				if(info.page[info.selectedTab] >= uMaxPages)
					info.page[info.selectedTab] = 0;
				txtPageNo.setText((info.page[info.selectedTab] + 1) + "/" + uMaxPages);
			}
		});
		add(btnPageDown);
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
		int tab = info.selectedTab;
		if(slot instanceof WindowEquipmentSlot)
			tab = InventoryProvider.TAB_EQUIPPED;
		int page = info.page[tab];
		if(slot instanceof WindowEquipmentSlot)
			page = 0;
		ItemStack stack = info.provider.provide(tab, page * getItemsPerPage(slot.getProviderKey()) + slot.getIndex());
		if(stack == null)
			return;
		WindowItemInfo itemWindow = new WindowItemInfo(game, stack, info);
		itemWindow.zIndex = 1;
		game.getWindows().register(itemWindow);
		itemWindow.show();
	}
}
