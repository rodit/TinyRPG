package net.site40.rodit.tinyrpg.game.gui.windows;

import java.util.ArrayList;
import java.util.HashMap;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.item.Inventory.InventoryProvider;
import net.site40.rodit.tinyrpg.game.item.ItemStack;

public class WindowSlotted extends Window{

	protected HashMap<Object, ProviderInfo> providerInfo;
	protected ArrayList<WindowSlot> slots;
	protected ArrayList<WindowEquipmentSlot> equipmentSlots;
	protected ArrayList<WindowTab> tabs;

	public WindowSlotted(Game game){
		super(game);
	}

	@Override
	public void initialize(Game game){
		this.providerInfo = new HashMap<Object, ProviderInfo>();
		this.slots = new ArrayList<WindowSlot>();
		this.equipmentSlots = new ArrayList<WindowEquipmentSlot>();
		this.tabs = new ArrayList<WindowTab>();
	}
	
	public ProviderInfo getProviderInfo(Object providerKey){
		return providerInfo.get(providerKey);
	}

	public int getMaxPages(Object providerKey, int itemsPerPage){
		ProviderInfo info = providerInfo.get(providerKey);
		ArrayList<ItemStack> stacks = info.provider.provide(info.selectedTab);
		return (int)Math.ceil((float)stacks.size() / (float)itemsPerPage);
	}
	
	public InventoryProvider getProvider(Object providerKey){
		return providerInfo.get(providerKey).provider;
	}

	public void registerProvider(Object key, InventoryProvider provider){
		ProviderInfo info = new ProviderInfo(provider);
		info.selectedTab = InventoryProvider.TAB_ALL;
		providerInfo.put(key, info);
	}

	public void addSlot(float x, float y, Object providerKey, String id){
		ProviderInfo info = providerInfo.get(providerKey);
		if(info == null)
			return;
		WindowSlot slot = new WindowSlot(providerKey, InventoryProvider.TAB_ALL, info.slots.size());
		slot.setName("slot_" + id);
		slot.addListener(new WindowListener(){
			public void touchUp(Game game, WindowComponent component){
				onSlotSelected(game, (WindowSlot)component);
			}
		});
		slot.setBounds(x, y, WindowSlot.SLOT_WIDTH, WindowSlot.SLOT_HEIGHT);
		info.slots.add(slot);
		slots.add(slot);
		add(slot);
	}
	
	public void addEquipmentSlot(float x, float y, Object providerKey, int equipId){
		ProviderInfo info = providerInfo.get(providerKey);
		if(info == null)
			return;
		WindowEquipmentSlot slot = new WindowEquipmentSlot(providerKey, equipId);
		slot.setName("slot_equip_" + equipId);
		slot.addListener(new WindowListener(){
			public void touchUp(Game game, WindowComponent component){
				onSlotSelected(game, (WindowSlot)component);
			}
		});
		slot.setBounds(x, y, WindowSlot.SLOT_WIDTH, WindowSlot.SLOT_HEIGHT);
		equipmentSlots.add(slot);
		add(slot);
	}
	
	public void addTab(float x, float y, int index, Object providerKey){
		ProviderInfo info = providerInfo.get(providerKey);
		if(info == null)
			return;
		WindowTab tab = new WindowTab(index, providerKey);
		tab.addListener(new WindowListener(){
			public void touchUp(Game game, WindowComponent component){
				onTabSelected(game, (WindowTab)component);
			}
		});
		tab.setBounds(x, y, WindowTab.TAB_WIDTH, WindowTab.TAB_HEIGHT);
		info.tabs.add(tab);
		tabs.add(tab);
		add(tab);
	}
	
	public int getItemsPerPage(Object providerKey){
		return 0;
	}
	
	public void onTabSelected(Game game, WindowTab tab){
		ProviderInfo info = providerInfo.get(tab.getProviderKey());
		if(info == null)
			return;
		info.selectedTab = tab.getTabIndex();
		for(WindowSlot slot : info.slots)
			slot.setTag(tab.getTabIndex());
	}
	
	public void onSlotSelected(Game game, WindowSlot slot){}

	public void setTab(Object providerKey, int tab){
		ProviderInfo info = providerInfo.get(providerKey);
		if(info != null)
			info.selectedTab = tab;
	}

	public static class ProviderInfo{

		protected InventoryProvider provider;
		protected ArrayList<WindowSlot> slots;
		protected ArrayList<WindowTab> tabs;
		protected int selectedTab;
		protected int page;

		public ProviderInfo(InventoryProvider provider){
			this.provider = provider;
			this.slots = new ArrayList<WindowSlot>();
			this.tabs = new ArrayList<WindowTab>();
			this.selectedTab = 0;
			this.page = 0;
		}
	}
}
