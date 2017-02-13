package net.site40.rodit.tinyrpg.game.item;


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
		if(this.amount < 0)
			this.amount = 0;
	}
	
	public void consume(){
		consume(1);
	}
	
	public void consume(int amount){
		this.amount -= amount;
		if(this.amount <= 0)
			item = null;
	}
	
	public void fill(){
		fill(1);
	}
	
	public void fill(int amount){
		this.amount += amount;
	}
}
