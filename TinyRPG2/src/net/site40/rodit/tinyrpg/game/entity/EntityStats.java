package net.site40.rodit.tinyrpg.game.entity;

import java.io.IOException;

import net.site40.rodit.tinyrpg.game.SuperCalc;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;

public class EntityStats {
	
	public static final int UPGRADE_POINTS_LEVEL = 3;
	public static final float DEFAULT_STAT = 0.5f;

	private int level;
	private int xp;
	private int upgradePoints;
	private float speed;
	private float strength;
	private float defence;
	private float luck;
	private float magika;
	private float forge;
	protected float hpMulti;
	protected float speedMulti;
	protected float strengthMulti;
	protected float defenceMulti;
	protected float luckMulti;
	protected float magikaMulti;
	protected float forgeMulti;
	
	public EntityStats(){
		this.level = 1;
		this.xp = 0;
		this.upgradePoints = 0;
		this.speed = this.strength = this.defence = this.luck = this.magika = this.forge = 0f;
		this.hpMulti = this.speedMulti = this.strengthMulti = this.defenceMulti = this.luckMulti = this.magikaMulti = this.forgeMulti = 1f;
	}
	
	public EntityStats(EntityStats stats){
		level = stats.level;
		xp = stats.xp;
		upgradePoints = stats.upgradePoints;
		speed = stats.speed;
		strength = stats.strength;
		defence = stats.defence;
		luck = stats.luck;
		magika = stats.magika;
		forge = stats.forge;
		hpMulti = stats.hpMulti;
		speedMulti = stats.speedMulti;
		strengthMulti = stats.strengthMulti;
		defenceMulti = stats.defenceMulti;
		luckMulti = stats.luckMulti;
		magikaMulti = stats.magikaMulti;
		forgeMulti = stats.forgeMulti;
	}
	
	public int getLevel(){
		return level;
	}
	
	public void setLevel(int level){
		this.level = level;
	}
	
	public int getXp(){
		return xp;
	}
	
	public void setXp(int xp){
		this.xp = xp;
	}
	
	public void addXp(int xp){
		this.xp += xp;
	}
	
	public int getXpForLevelUp(){
		return SuperCalc.getXpForLevel(level);
	}
	
	public int getUpgradePoints(){
		return upgradePoints;
	}
	
	public void setUpgradePoints(int upgradePoints){
		this.upgradePoints = upgradePoints;
	}
	
	public void addUpgradePoints(int upgradePoints){
		this.upgradePoints += upgradePoints;
	}
	
	public void removeUpgradePoints(int upgradePoints){
		this.upgradePoints -= upgradePoints;
	}
	
	public float getMoveSpeed(){
		return fPointF(1f, getSpeed());
	}
	
	public float getSpeed(){
		return speed * speedMulti;
	}
	
	public void setSpeed(float speed){
		this.speed = speed;
	}
	
	public float getStrength(){
		return strength * strengthMulti;
	}
	
	public void setStrength(float strength){
		this.strength = strength;
	}
	
	public float getDefence(){
		return defence * defenceMulti;
	}
	
	public void setDefence(float defence){
		this.defence = defence;
	}
	
	public float getLuck(){
		return luck * luckMulti;
	}
	
	public void setLuck(float stamina){
		this.luck = stamina;
	}
	
	public float getMagika(){
		return magika * magikaMulti;
	}
	
	public void setMagika(float magika){
		this.magika = magika;
	}
	
	public float getForge(){
		return forge * forgeMulti;
	}
	
	public void setForge(float forge){
		this.forge = forge;
	}
	
	public float getLevelFactor(){
		return 1f + (float)level / 100f;
	}
	
	public int getMaxHealth(int maxHealth){
		return (int)((float)maxHealth * hpMulti);
	}
	
	public void addHpMulti(float amount){
		hpMulti += amount;
	}
	
	public void addSpeedMulti(float amount){
		speedMulti += amount;
	}
	
	public void addStrengthMulti(float amount){
		strengthMulti += amount;
	}
	
	public void addDefenceMulti(float amount){
		defenceMulti += amount;
	}
	
	public void addLuckMulti(float amount){
		luckMulti += amount;
	}
	
	public void addMagikaMulti(float amount){
		magikaMulti += amount;
	}
	
	public void addForgeMulti(float amount){
		forgeMulti += amount;
	}
	
	public float getHpMulti() {
		return hpMulti;
	}

	public void setHpMulti(float hpMulti) {
		this.hpMulti = hpMulti;
	}

	public float getSpeedMulti() {
		return speedMulti;
	}

	public void setSpeedMulti(float speedMulti) {
		this.speedMulti = speedMulti;
	}

	public float getStrengthMulti() {
		return strengthMulti;
	}

	public void setStrengthMulti(float strengthMulti) {
		this.strengthMulti = strengthMulti;
	}

	public float getDefenceMulti() {
		return defenceMulti;
	}

	public void setDefenceMulti(float defenceMulti) {
		this.defenceMulti = defenceMulti;
	}

	public float getLuckMulti() {
		return luckMulti;
	}

	public void setLuckMulti(float luckMulti) {
		this.luckMulti = luckMulti;
	}

	public float getMagikaMulti() {
		return magikaMulti;
	}

	public void setMagikaMulti(float magikaMulti) {
		this.magikaMulti = magikaMulti;
	}

	public float getForgeMulti() {
		return forgeMulti;
	}

	public void setForgeMulti(float forgeMulti) {
		this.forgeMulti = forgeMulti;
	}
	
	public int compare(EntityStats stats){
		if(stats.level > level && stats.speed > speed && stats.strength > strength && stats.defence > defence && stats.luck > luck && stats.magika > magika && stats.forge > forge)
			return 1;
		if(stats.level >= level && stats.speed >= speed && stats.strength >= strength && stats.defence >= defence && stats.luck >= luck && stats.magika >= magika && stats.forge >= forge)
			return 0;
		return -1;
	}
	
	public void load(TinyInputStream in)throws IOException{
		this.level = in.readInt();
		this.xp = in.readInt();
		this.upgradePoints = in.readInt();
		this.speed = in.readFloat();
		this.strength = in.readFloat();
		this.defence = in.readFloat();
		this.luck = in.readFloat();
		this.magika = in.readFloat();
		this.forge = in.readFloat();
		//TODO: Multipliers are re-applied by re-equipping inventory items.
	}
	
	public void save(TinyOutputStream out)throws IOException{
		out.write(level);
		out.write(xp);
		out.write(upgradePoints);
		out.write(speed);
		out.write(strength);
		out.write(defence);
		out.write(luck);
		out.write(magika);
		out.write(forge);
	}
	
	public static float fPointF(float f0, float f1){
		String f0s = (int)f0 + "";
		String f1s = (int)f1 + "";
		return Float.valueOf(f0s + "." + f1s);
	}
	
	@Override
	public boolean equals(Object object){
		if(!(object instanceof EntityStats))
			return false;
		EntityStats s = (EntityStats)object;
		return s.speed == speed && s.strength == strength && s.defence == defence && s.luck == luck && s.magika == magika && s.forge == forge;
	}
}
