package net.site40.rodit.tinyrpg.game.forge;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.item.ItemStack;

public class ForgeRegistry {
	
	private ArrayList<ForgeRecipy> recipes;
	
	public ForgeRegistry(){
		this.recipes = new ArrayList<ForgeRecipy>();
		
		initRecipes();
	}
	
	private void initRecipes(){
		//TODO ADD ALL RECIPES
	}
	
	public void register(ItemStack[] inputs, ItemStack[] outputs, float minForge){
		register(new ForgeRecipy(inputs, outputs, minForge));
	}
	
	public void register(ForgeRecipy recipy){
		if(!recipes.contains(recipy))
			recipes.add(recipy);
	}
	
	public void unregister(ForgeRecipy recipy){
		recipes.remove(recipy);
	}
	
	public static class ForgeRecipy{
		
		private ArrayList<ItemStack> input;
		private ArrayList<ItemStack> output;
		private float minForge;
		
		public ForgeRecipy(ItemStack[] inputs, ItemStack[] outputs, float minForge){
			this.input = new ArrayList<ItemStack>();
			this.output = new ArrayList<ItemStack>();
			for(int i = 0; i < inputs.length; i++)
				input.add(inputs[i]);
			for(int i = 0; i < outputs.length; i++)
				output.add(outputs[i]);
			this.minForge = minForge;
		}
		
		public ArrayList<ItemStack> getInput(){
			return input;
		}
		
		public ArrayList<ItemStack> getOutput(){
			return output;
		}
		
		public float getMinForge(){
			return minForge;
		}
		
		public void setMinForge(float minForge){
			this.minForge = minForge;
		}
	}
}
