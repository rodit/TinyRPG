package net.site40.rodit.tinyrpg.game.entity;

import java.io.IOException;

import net.site40.rodit.util.ISavable;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;

public class EntityStats implements ISavable{

	private int level;
	private int xp;
	private float speed;
	private float strength;
	private float defence;
	private float luck;
	private float magika;
	private float forge;
	
	public EntityStats(){
		this.level = 1;
		this.xp = 0;
		this.speed = this.strength = this.defence = this.luck = this.magika = this.forge = 0f;
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
	
	public float getMoveSpeed(){
		return fPointF(1f, speed);
	}
	
	public float getSpeed(){
		return speed;
	}
	
	public void setSpeed(float speed){
		this.speed = speed;
	}
	
	public float getStrength(){
		return strength;
	}
	
	public void setStrength(float strength){
		this.strength = strength;
	}
	
	public float getDefence(){
		return defence;
	}
	
	public void setDefence(float defence){
		this.defence = defence;
	}
	
	public float getLuck(){
		return luck;
	}
	
	public void setLuck(float stamina){
		this.luck = stamina;
	}
	
	public float getMagika(){
		return magika;
	}
	
	public void setMagika(float magika){
		this.magika = magika;
	}
	
	public float getForge(){
		return forge;
	}
	
	public void setForge(float forge){
		this.forge = forge;
	}
	
	public float getLevelFactor(){
		return 1f + (float)level / 100f;
	}
	
	@Override
	public void serialize(TinyOutputStream out)throws IOException{
		out.write(level);
		out.write(xp);
		out.write(speed);
		out.write(strength);
		out.write(defence);
		out.write(luck);
		out.write(magika);
		out.write(forge);
	}
	
	@Override
	public void deserialize(TinyInputStream in)throws IOException{
		level = in.readInt();
		xp = in.readInt();
		speed = in.readFloat();
		strength = in.readFloat();
		defence = in.readFloat();
		luck = in.readFloat();
		magika = in.readFloat();
		forge = in.readFloat();
	}
	
	public static float fPointF(float f0, float f1){
		String f0s = (int)f0 + "";
		String f1s = (int)f1 + "";
		return Float.valueOf(f0s + "." + f1s);
	}
}
