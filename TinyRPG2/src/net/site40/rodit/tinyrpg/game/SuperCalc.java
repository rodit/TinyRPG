package net.site40.rodit.tinyrpg.game;

import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.entity.EntityPlayer;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.item.ItemStack;
import net.site40.rodit.tinyrpg.game.item.Weapon;
import net.site40.rodit.tinyrpg.game.item.armour.Armour;

public class SuperCalc {
	
	public static final float BASE_MAGIKA = 10f;
	public static final float BASE_HEALTH = 25f;
	
	public static void attack(Game game, EntityLiving user, EntityLiving receiver, Weapon weapon){
		weapon.onHit(game, user, receiver);
		receiver.removeHealth(getDamage(game, user, receiver, weapon));
		for(int i = 0; i < receiver.getEquipped().length; i++){
			Item item = receiver.getEquipped(i);
			if(item instanceof Armour)
				((Armour)item).onHit(game, user, receiver);
		}
	}
	
	public static float getDamage(Game game, EntityLiving user, EntityLiving receiver, Weapon weapon){
		float rand = game.getRandom().nextFloat() - 0.5f;
		rand /= 0.5f;
		return getDirectDamage(game, user, receiver, weapon) * (1f + rand);
	}
	
	public static float getDirectDamage(Game game, EntityLiving user, EntityLiving receiver, Weapon weapon){
		float dmgMulti = weapon.isMagic() ? getMagikaDamageMulti(user, receiver) : getDamageMulti(user, receiver);
		float def = getDefenceMulti(user, receiver) * receiver.getTotalDefence();
		float damage = dmgMulti * weapon.getDamage() - def;
		damage = damage < 0f ? 0f : damage;
		return (int)damage;
	}
	
	public static float getDamageMulti(EntityLiving user, EntityLiving receiver){
		return receiver.getStats().getLevel() - user.getStats().getLevel() < 3 ? 0.88f : 1f;
	}
	
	public static int getLevelDifference(EntityLiving user, EntityLiving receiver){
		return receiver.getStats().getLevel() - user.getStats().getLevel();
	}
	
	public static float getMagikaDamageMulti(EntityLiving user, EntityLiving receiver){
		return getMagikaDifference(user, receiver) < 3 ? 1 : 0.9f;
	}
	
	public static float getMagikaDifference(EntityLiving user, EntityLiving receiver){
		return receiver.getStats().getMagika() - user.getStats().getMagika();
	}
	
	public static float getDefenceMulti(EntityLiving user, EntityLiving receiver){
		return getDefenceAttackDifference(user, receiver) < 1.5f ? 1f : 0.75f;
	}
	
	public static float getDefenceAttackDifference(EntityLiving user, EntityLiving receiver){
		return receiver.getStats().getDefence() - user.getStats().getStrength();
	}
	
	public static int getMaxMagika(EntityLiving ent){
		return (int)(BASE_MAGIKA + 30f * ((float)Math.pow(1.13f, ent.getStats().getMagika())));
	}
	
	public static int getMaxHealth(EntityLiving ent){
		return (int)(BASE_HEALTH + (float)Math.pow(1.13f, ent.getStats().getLevel())) + getHealthBonus(ent);
	}
	
	private static int getHealthBonus(EntityLiving ent){
		int strMulti = (int)(ent.getStats().getStrength() / 2f);
		return strMulti * 10;
	}
	
	public static int getXpForLevel(int level){
		return (int)((6f / 5f) * Math.pow(level, 3) - 15 * Math.pow(level, 2) + 100 * level - 140);
	}
	
	public static int getXpForLevelDifficult(int level){
		return (int)(Math.pow(level, 3) * (((float)level / 2f) + 32) / 50);
	}
	
	public static int getXpForLevelExp(int level){
		return (int)(Math.pow(2, (float)level / 4f) * 300f - Math.pow(2, 1f / 4f) * 300f);
	}
	
	public static int getXpForLevelLog(int level){
		return log(level, 1.2f) * 300;
	}
	
	public static int log(float x, float base){
		return (int)(Math.log(x) / Math.log(base));
	}
	
	public static long getItemValue(Item item, EntityLiving seller){
		float luckVal = seller == null ? 0f : seller.getStats().getLuck();
		float value = (float)item.getValue();
		if(seller != null)
			value /= 8 * (1 + luckVal);	
		value = Math.min(value, item.getValue());
		return (long)value;
	}
	
	public static long getStackValue(ItemStack stack, EntityLiving seller){
		return getItemValue(stack.getItem(), seller) * stack.getAmount();
	}
	
	public static long getItemValueFromShop(Item item, EntityPlayer player){
		return item.getValue();
	}
	
	public static long getStackValueFromShop(ItemStack stack, EntityPlayer player){
		return getItemValueFromShop(stack.getItem(), player) * stack.getAmount();
	}
}
