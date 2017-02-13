package net.site40.rodit.tinyrpg.game.entity;

import net.site40.rodit.tinyrpg.game.battle.Battle;

public class Damage {

	public static enum DamageType{
		PHYSICAL_ATTACK, MAGIC_ATTACK, EFFECT, ITEM, OTHER;
	}
	
	public static class SourceStack{
		
		private Object[] stack;
		
		public SourceStack(Object... stack){
			if(stack == null)
				stack = new Object[0];
			this.stack = stack;
		}
		
		public void push(Object object){
			Object[] nStack = new Object[stack.length + 1];
			System.arraycopy(stack, 0, nStack, 0, stack.length);
			nStack[stack.length] = object;
			stack = nStack;
		}
		
		public Object pop(){
			if(stack.length == 0)
				return null;
			Object popped = stack[stack.length - 1];
			Object[] nStack = new Object[stack.length - 2];
			System.arraycopy(stack, 0, nStack, 0, nStack.length);
			stack = nStack;
			return popped;
		}
		
		public Object get(int index){
			if(index < 0 || index >= stack.length)
				return null;
			return stack[index];
		}
	}
	
	private SourceStack source;
	private DamageType type;
	private float damage;
	
	public Damage(float damage){
		this(DamageType.OTHER, damage);
	}
	
	public Damage(DamageType type, float damage){
		this(new SourceStack(), type, damage);
	}
	
	public Damage(SourceStack source, DamageType type, float damage){
		this.source = source;
		this.type = type;
		this.damage = damage;
	}
	
	public SourceStack getSource(){
		return source;
	}
	
	public DamageType getType(){
		return type;
	}
	
	public float getDamage(){
		return damage;
	}
	
	public boolean isFromBattle(){
		return source.get(0) instanceof Battle;
	}
}
