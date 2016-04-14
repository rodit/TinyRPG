package net.site40.rodit.tinyrpg.game.forge;

import net.site40.rodit.tinyrpg.game.item.Item;

public class ItemStack {

	private Item item;
	private int amount;

	public ItemStack(ItemStack stack){
		this(stack.getItem(), stack.getAmount());
	}
	
	public ItemStack(Item item, int amount){
		this.item = item;
		this.amount = amount;
	}
	
	public Item getItem(){
		return item;
	}
	
	public void setItem(Item item){
		this.item = item;
	}
	
	public int getAmount(){
		return amount;
	}
	
	public void setAmount(int amount){
		this.amount = amount;
	}
}
