package net.site40.rodit.tinyrpg.game.item;

import net.site40.rodit.tinyrpg.game.entity.EntityLiving;

public class InventoryUtil {
	
	public static boolean hasWeaponEquipped(EntityLiving entity){
		for(int i = 0; i < entity.getEquipped().length; i++)
			if(entity.getEquipped(i) instanceof Weapon)
				return true;
		return false;
	}
	
	public static boolean containsWeapon(Inventory inventory){
		for(ItemStack stack : inventory.getItems()){
			if(stack == null)
				continue;
			if(stack.getItem() instanceof Weapon)
				return true;
		}
		return false;
	}

	public static boolean containsHealingItem(Inventory inventory){
		for(ItemStack stack : inventory.getItems()){
			if(stack == null)
				continue;
			Item item = stack.getItem();
			if(item == null)
				continue;
			if(item.getName().contains("potion_health"))
				return true;
		}
		return false;
	}

	public static Item getBestHealingItem(Inventory inventory){
		Item best = null;
		for(ItemStack stack : inventory.getItems()){
			if(stack == null)
				continue;
			Item item = stack.getItem();
			if(item == null)
				continue;
			if(item.getName().contains("potion_health")){
				if(best == null || item.getLevel() > best.getLevel())
					best = item;
			}
		}
		return best;
	}
	
	static String[] vowels = new String[] { "a", "e", "i", "o", "u" };
	public static String grammer(Item item){
		boolean isVowel = false;
		for(String vowel : vowels){
			if(item.getShowName().startsWith(vowel)){
				isVowel = true;
				break;
			}
		}
		return (isVowel ? "an" : "a") + " " + item.getShowName();
	}
}
