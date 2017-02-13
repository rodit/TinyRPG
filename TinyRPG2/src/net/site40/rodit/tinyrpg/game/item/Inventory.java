package net.site40.rodit.tinyrpg.game.item;

import java.io.IOException;
import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.item.armour.Armour;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;
import net.site40.rodit.util.Util;

public class Inventory {

	private ArrayList<ItemStack> items;

	public Inventory(){
		this.items = new ArrayList<ItemStack>();
	}

	public Inventory(Inventory inv){
		this();
		for(ItemStack stack : inv.getItems())
			items.add(new ItemStack(stack));
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
			if(stack.getItem() == item && stack.getAmount() > 0)
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

	public ItemStack getExistingStack(Item item, ItemStack... ignore){
		for(ItemStack stack : items)
			if(stack.getItem() == item && (ignore != null && !Util.arrayContains(ignore, stack, ItemStack.class)))
				return stack;
		return null;
	}

	public ItemStack getStackableStack(Item item){
		for(ItemStack stack : items)
			if(stack.getItem() == item && stack.getAmount() < item.stackSize)
				return stack;
		return null;
	}

	public int getCount(Item item){
		int count = 0;
		for(ItemStack stack : items)
			if(stack.getItem() == item)
				count += stack.getAmount();
		return count;
	}

	public void setCountRemove(Item item, int count){
		if(item.isStackable()){
			int oldCount = getCount(item);
			int toRemove = oldCount - count;
			while(toRemove > 0){
				ItemStack stack = getStack(item);
				if(stack == null)
					break;
				if(stack.getAmount() >= toRemove){
					stack.setAmount(stack.getAmount() - toRemove);
					if(stack.getAmount() == 0)
						items.remove(stack);
					break;
				}else if(stack.getAmount() < toRemove){
					toRemove -= stack.getAmount();
					items.remove(stack);
				}
			}
		}else
			for(int i = 0; i < count; i++)
				items.remove(this.getStack(item));
	}
	
	public void setCountAdd(Item item, int count){
		count -= getCount(item);
		if(item.isStackable()){
			ItemStack stack = null;
			int remain = count;
			while((stack = getStackableStack(item)) != null && remain > 0){
				int space = item.getStackSize() - stack.getAmount();
				stack.setAmount(stack.getAmount() + Math.min(space, remain));
				remain -= space;
			}
			if(remain > 0)
				items.add(new ItemStack(item, remain));
		}else{
			for(int i = 0; i < count; i++)
				items.add(new ItemStack(item, 1));
		}
	}

	public void add(String itemName, int count){
		add(Item.get(itemName), count);
	}

	public void add(Item item){
		add(item, 1);
	}

	public void add(Item item, int count){
		int nCount = getCount(item) + count;
		if(count > 0)
			setCountAdd(item, nCount);
		else if(count < 0)
			setCountRemove(item, Math.abs(nCount));
	}

	public void add(ItemStack stack){
		add(stack.getItem(), stack.getAmount());
	}

	public void add(Inventory inventory){
		for(ItemStack stack : inventory.items)
			add(stack.getItem(), stack.getAmount());
	}
	
	public void remove(Item item, int count){
		add(item, -count);
	}

	public int getSize(){
		int size = 0;
		for(ItemStack stack : items){
			if(stack != null && stack.getAmount() > 0)
				size++;
		}
		return size;
	}

	public int getIndexByItemStack(ItemStack stack){
		return items.indexOf(stack);
	}

	public ItemStack getItemStackByIndex(int index){
		return items.get(index);
	}

	public InventoryProvider getProvider(){
		return getProvider(null);
	}

	public InventoryProvider getProvider(Entity owner){
		return new InventoryProvider(this, owner);
	}
	
	public void load(TinyInputStream in)throws IOException{
		int count = in.readInt();
		while(items.size() < count){
			String itemName = in.readString();
			Item i = Item.get(itemName);
			items.add(new ItemStack(i, in.readInt()));
		}
	}
	
	public void save(TinyOutputStream out)throws IOException{
		out.write(items.size());
		for(ItemStack stack : items){
			if(stack == null)
				continue;
			out.writeString(stack.getItem().getName());
			out.write(stack.getAmount());
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
		private Entity owner;

		public InventoryProvider(Inventory inventory, Entity owner){
			this.inventory = inventory;
			this.owner = owner;
		}

		public Inventory getInventory(){
			return inventory;
		}

		public Entity getOwner(){
			return owner;
		}

		public EntityLiving getOwnerLiving(){
			return (EntityLiving)getOwner();
		}

		public ItemStack provide(int type, int index){
			if(type == TAB_EQUIPPED){
				if(owner == null)
					return null;
				ItemStack stack = null;
				if((stack = getOwnerLiving().getEquipped(index)) == null)
					return null;
				else
					return stack;
			}
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
					ItemStack stack = getOwnerLiving().getEquipped(i);
					if(stack != null)
						stacks.add(stack);
				}
				break;
			case TAB_ALL:
				for(ItemStack stack : inventory.items){
					if(stack.getAmount() > 0)
						stacks.add(stack);
				}
				break;
			case TAB_WEAPONS:
				for(ItemStack stack : inventory.items){
					if(stack.getItem() instanceof Weapon && stack.getAmount() > 0)
						stacks.add(stack);
				}
				break;
			case TAB_ARMOUR:
				for(ItemStack stack : inventory.items){
					if(stack.getItem() instanceof Armour && stack.getAmount() > 0)
						stacks.add(stack);
				}
				break;
			case TAB_ACCESSORIES:
				for(ItemStack stack : inventory.items){
					if((stack.getItem() instanceof Necklace || stack.getItem() instanceof Ring) && stack.getAmount() > 0)
						stacks.add(stack);
				}
				break;
			case TAB_POTIONS:
				for(ItemStack stack : inventory.items){
					if(stack.getItem().getName().startsWith("potion") && stack.getAmount() > 0)
						stacks.add(stack);
				}
				break;
			case TAB_MISC:
				for(ItemStack stack : inventory.items){
					if(!(stack.getItem().getName().startsWith("potion") || stack.getItem() instanceof Weapon || stack.getItem() instanceof Armour || stack.getItem() instanceof Necklace || stack.getItem() instanceof Ring) && stack.getAmount() > 0)
						stacks.add(stack);
				}
				break;
			}
			return stacks;
		}
	}
}
