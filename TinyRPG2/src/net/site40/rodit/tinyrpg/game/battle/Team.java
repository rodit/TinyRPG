package net.site40.rodit.tinyrpg.game.battle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.site40.rodit.tinyrpg.game.entity.EntityLiving;

public class Team {
	
	public static class Attribs{
		public static final int TEAM_ATTACK = 1;
		public static final int TEAM_DEFENCE = 2;
		
		public static final int BATTLE_INDEX = 1;
		public static final int BATTLE_HAS_FOUGHT = 2;
	}
	
	public static Comparator<EntityLiving> speedComparator = new Comparator<EntityLiving>(){
		public int compare(EntityLiving e0, EntityLiving e1){
			return (int)((e0.getStats().getSpeed() - e1.getStats().getSpeed()) * 100000f);
		}
	};

	private ArrayList<EntityLiving> members;
	private boolean[] attribs;
	private int[] battleAttribs;
	
	public Team(){
		this(new EntityLiving[0]);
	}
	
	public Team(EntityLiving... members){
		this.members = new ArrayList<EntityLiving>();
		if(members != null)
			for(int i = 0; i < members.length; i++)
				this.members.add(members[i]);
		this.attribs = new boolean[3];
		this.battleAttribs = new int[3];
	}
	
	public EntityLiving getLeader(){
		return members.get(0);
	}
	
	public ArrayList<EntityLiving> getMembers(){
		return members;
	}
	
	public void add(EntityLiving member){
		members.add(member);
	}
	
	public void remove(EntityLiving member){
		members.add(member);
	}
	
	public boolean getAttribute(int key){
		return attribs[key];
	}
	
	public void setAttribute(int key, boolean value){
		attribs[key] = value;
	}
	
	public int getBattleAttribute(int key){
		return battleAttribs[key];
	}
	
	public void setBattleAttribute(int key, int value){
		battleAttribs[key] = value;
	}
	
	public boolean hasExhaustedMembers(){
		return getBattleAttribute(Attribs.BATTLE_INDEX) >= members.size() - 1;
	}
	
	public EntityLiving getNext(){
		ArrayList<EntityLiving> membersSorted = new ArrayList<EntityLiving>(members);
		Collections.sort(membersSorted, speedComparator);
		int current = getBattleAttribute(Attribs.BATTLE_INDEX);
		if(getBattleAttribute(Attribs.BATTLE_HAS_FOUGHT) != 0)
			current++;
		else
			setBattleAttribute(Attribs.BATTLE_HAS_FOUGHT, 1);
		if(current >= membersSorted.size())
			current = 0;
		setBattleAttribute(Attribs.BATTLE_INDEX, current);
		return membersSorted.get(current);
	}
	
	public boolean isDefeated(){
		for(EntityLiving member : members)
			if(!member.isDead())
				return false;
		return true;
	}
	
	public float getSpeed(){
		float speed = 0;
		for(EntityLiving member : members)
			speed += member.getStats().getSpeed();
		return speed;
	}
}
