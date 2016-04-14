package net.site40.rodit.tinyrpg.game;

import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.item.Weapon;

public class SuperCalc {
	
	public static final float BASE_MAGIKA = 10f;
	public static final float BASE_HEALTH = 25f;
	
	public static void attack(EntityLiving user, EntityLiving receiver, Weapon weapon){
		receiver.removeHealth(getDamage(user, receiver, weapon));
	}

	public static float getDamage(EntityLiving user, EntityLiving receiver, Weapon weapon){
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
		return (int)(BASE_HEALTH + (float)Math.pow(1.13f, ent.getStats().getLevel()));
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
}
