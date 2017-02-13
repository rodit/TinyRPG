package net.site40.rodit.tinyrpg.game.gui.windows;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.forge.ForgeProvider;
import net.site40.rodit.tinyrpg.game.forge.ForgeRegistry.ForgeRecipy.ForgeType;
import net.site40.rodit.tinyrpg.game.item.ItemStack;

public class WindowForge extends WindowSlotted{
	
	public static final String FORGE_KEY = "forge";
	
	public static final int COUNT_ROWS = 3;
	public static final int COUNT_COLUMNS = 7;
	public static final int ITEMS_PER_PAGE = COUNT_ROWS * COUNT_COLUMNS;
	public static final float OFFSET_X = 64f;
	public static final float OFFSET_Y = 192f;
	
	protected ForgeType type;
	
	protected WindowComponent txtTitle;
	protected WindowComponent txtPageNo;
	protected WindowComponent btnPageDown;
	protected WindowComponent btnPageUp;
	protected WindowCheckboxComponent btnShowAll;

	public WindowForge(Game game){
		super(game);
	}
	
	@Override
	public void initialize(Game game){
		super.initialize(game);
		
		registerProvider(FORGE_KEY, new ForgeProvider(game.getPlayer().getInventory(), game.getPlayer(), game.getForge(), type));
		getProviderInfo(FORGE_KEY).selectedTab = -1;
		
		this.setBounds(64, 32, 1152, 640);

		this.txtTitle = new WindowComponent("txtTitle");
		txtTitle.setText("Forge");
		txtTitle.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		txtTitle.setX(bounds.getWidth() / 2f);
		txtTitle.setY(78f);
		add(txtTitle);
		
		int k = 0;
		for(int y = 0; y < COUNT_ROWS; y++)
			for(int x = 0; x < COUNT_COLUMNS; x++)
				addSlot(OFFSET_X + x * WindowSlot.SLOT_WIDTH, OFFSET_Y + y * WindowSlot.SLOT_HEIGHT, FORGE_KEY, "frg_inv_" + k++);
		
		txtPageNo = new WindowComponent("txtPageNo");
		txtPageNo.setX(1148 - bounds.getX());
		txtPageNo.setY(412 - bounds.getY());
		txtPageNo.setText("1/1");
		txtPageNo.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		txtPageNo.addListener(new WindowListener(){
			private String uPage;
			public void update(Game game, WindowComponent component){
				ProviderInfo info = getProviderInfo(FORGE_KEY);
				if(info == null)
					return;
				uPage = String.valueOf(info.page[0] + 1);
				component.setText(uPage + "/" + getMaxPages(FORGE_KEY, ITEMS_PER_PAGE));
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
				uMaxPages = getMaxPages(FORGE_KEY, ITEMS_PER_PAGE);
				ProviderInfo info = getProviderInfo(FORGE_KEY);
				info.page[0]--;
				if(info.page[0] == -1)
					info.page[0] = (uMaxPages > 0 ? uMaxPages : 1) - 1;
				txtPageNo.setText((info.page[0] + 1) + "/" + uMaxPages);
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
				uMaxPages = getMaxPages(FORGE_KEY, ITEMS_PER_PAGE);
				ProviderInfo info = getProviderInfo(FORGE_KEY);
				info.page[0]++;
				if(info.page[0] >= uMaxPages)
					info.page[0] = 0;
				txtPageNo.setText((info.page[0] + 1) + "/" + uMaxPages);
			}
		});
		add(btnPageDown);
		
		btnShowAll = new WindowCheckboxComponent();
		btnShowAll.setBackgroundDefault("gui/checkbox.png");
		btnShowAll.setBackgroundChecked("gui/checkbox_checked.png");
		btnShowAll.setText("Show All");
		btnShowAll.setX(btnPageDown.getBounds().getX() - 128f);
		btnShowAll.setY(txtTitle.getBounds().getY());
		btnShowAll.setWidth(48f);
		btnShowAll.setHeight(48f);
		btnShowAll.addListener(new WindowListener(){
			public void touchUp(Game game, WindowComponent component){
				((ForgeProvider)getProvider(FORGE_KEY)).setShowAll(component.getFlag(WindowCheckboxComponent.FLAG_CHECKED));
			}
		});
		add(btnShowAll);
	}
	
	@Override
	public int getItemsPerPage(Object providerKey){
		return ITEMS_PER_PAGE;
	}
	
	private int sIndex = 0;
	@Override
	public void onSlotSelected(Game game, WindowSlot slot){
		sIndex = slot.getIndex();
		ProviderInfo info = getProviderInfo(FORGE_KEY);
		ForgeProvider provider = (ForgeProvider)info.provider;
		ItemStack stack = provider.provide(-1, info.page[0] * getItemsPerPage(slot.getProviderKey()) + slot.getIndex());
		if(stack == null || stack.getItem() == null)
			return;
		provider.setSelectedRecipy(game.getForge().getAvailable(provider).get(sIndex));
		WindowItemForgeInfo itemWindow = new WindowItemForgeInfo(game, provider.getSelectedRecipy());
		itemWindow.zIndex = 1;
		game.getWindows().register(itemWindow);
		itemWindow.show();
	}
}
