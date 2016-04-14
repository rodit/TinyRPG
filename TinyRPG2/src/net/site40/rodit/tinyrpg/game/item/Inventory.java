package net.site40.rodit.tinyrpg.game.item;

import java.io.IOException;
import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.forge.ItemStack;
import net.site40.rodit.tinyrpg.game.item.armour.Armour;
import net.site40.rodit.util.ISavable;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;

public class Inventory implements ISavable{

	private ArrayList<ItemStack> items;

	public Inventory(){
		this.items = new ArrayList<ItemStack>();
	}

	public ArrayList<ItemStack> getItems(){
		return items;
	}

	public boolean isEmpty(){
		for(ItemStack i : items)
			if(i.getAmount() > 0)
				return false;
		return true;
	}

	public boolean isEmptySize(){
		return items.size() == 0;
	}

	public void set(Inventory inventory){
		this.items = new ArrayList<ItemStack>(inventory.getItems());
	}

	public boolean hasItem(Item item){
		for(ItemStack stack : items)
			if(stack.getItem() == item)
				return true;
		return false;
	}
	
	public ItemStack getStack(Item item){
		for(ItemStack stack : items)
			if(stack.getItem() == item)
				return stack;
		ItemStack stack = new ItemStack(item, 0);
		items.add(stack);
		return stack;
	}

	public int getCount(Item item){
		for(ItemStack stack : items)
			if(stack.getItem() == item)
				return stack.getAmount();
		return 0;
	}

	public void setCount(Item item, int count){
		getStack(item).setAmount(count);
	}

	public void add(String itemName, int count){
		add(Item.get(itemName), count);
	}

	public void add(Item item){
		add(item, 1);
	}

	public void add(Item item, int count){
		int nCount = getCount(item) + count;
		setCount(item, nCount);
	}

	public void add(Inventory inventory){
		for(ItemStack stack : inventory.items)
			add(stack.getItem(), stack.getAmount());
	}

	public void remove(Item item){
		remove(item, 1);
	}

	public void remove(Item item, int count){
		int nCount = getCount(item) - count;
		if(nCount <= 0)
			items.remove(item);
		else
			setCount(item, nCount);
	}

	public int getSize(){
		int size = 0;
		for(ItemStack stack : items){
			if(stack != null && stack.getAmount() > 0)
				size++;
		}
		return size;
	}

	public ItemStack getItemStackByIndex(int index){
		return items.get(index);
	}
	
	public InventoryProvider getProvider(){
		return getProvider(null);
	}
	
	public InventoryProvider getProvider(EntityLiving owner){
		return new InventoryProvider(this, owner);
	}

	@Override
	public void serialize(TinyOutputStream out)throws IOException{
		out.write(items.size());
		for(ItemStack stack : items){
			out.writeString(stack.getItem().getName());
			out.write(stack.getAmount());
		}
	}

	@Override
	public void deserialize(TinyInputStream in)throws IOException{
		int itemCount = in.readInt();
		int read = 0;
		while(read < itemCount){
			String name = in.readString();
			Item i = Item.get(name);
			int count = in.readInt();
			items.add(new ItemStack(i, count));
		}
	}

	public static class InventoryProvider{

		public static final int TAB_EQUIPPED = 0;
		public static final int TAB_ALL = 1;
		public static final int TAB_WEAPONS = 2;
		public static final int TAB_ARMOUR = 3;
		public static final int TAB_ACCESSORIES = 4;
		public static final int TAB_POTIONS = 5;
		public static final int TAB_MISC = 6;

		private Inventory inventory;
		private EntityLiving owner;
		
		public InventoryProvider(Inventory inventory, EntityLiving owner){
			this.inventory = inventory;
			this.owner = owner;
		}

		public Inventory getInventory(){
			return inventory;
		}
		
		public ItemStack provide(int type, int index){
			ArrayList<ItemStack> stacks = provide(type);
			if(index > -1 && index < stacks.size())
				return stacks.get(index);
			return null;
		}
		
		public ArrayList<ItemStack> provide(int type){
			ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
			switch(type){
			case TAB_EQUIPPED:
				if(owner == null)
					break;
				for(int i = 0; i < 9; i++){
					Item item = owner.getEquipped(i);
					if(item != null)
						stacks.add(new ItemStack(item, 1));
				}
				break;
			case TAB_ALL:
				for(ItemStack stack : inventory.items){
					if(stack.getAmount() > 0)
						stacks.add(new ItemStack(stack));
				}
				break;
			case TAB_WEAPONS:
				for(ItemStack stack : inventory.items){
					if(stack.getItem() instanceof Weapon && stack.getAmount() > 0)
						stacks.add(new ItemStack(stack));
				}
				break;
			case TAB_ARMOUR:
				for(ItemStack stack : inventory.items){
					if(stack.getItem() instanceof Armour && stack.getAmount() > 0)
						stacks.add(new ItemStack(stack));
				}
				break;
			case TAB_ACCESSORIES:
				for(ItemStack stack : inventory.items){
					if((stack.getItem() instanceof Necklace || stack.getItem() instanceof Ring) && stack.getAmount() > 0)
						stacks.add(new ItemStack(stack));
				}
				break;
			case TAB_POTIONS:
				for(ItemStack stack : inventory.items){
					if(stack.getItem().getName().startsWith("potion") && stack.getAmount() > 0)
						stacks.add(new ItemStack(stack));
				}
				break;
			case TAB_MISC:
				for(ItemStack stack : inventory.items){
					if(!(stack.getItem().getName().startsWith("potion") || stack.getItem() instanceof Weapon || stack.getItem() instanceof Armour || stack.getItem() instanceof Necklace || stack.getItem() instanceof Ring) && stack.getAmount() > 0)
						stacks.add(new ItemStack(stack));
				}
				break;
			}
			return stacks;
		}
	}
}
