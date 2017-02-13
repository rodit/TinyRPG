package net.site40.rodit.tinyrpg.game.faction;

import java.io.IOException;

import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;

public class FactionStats {

	private Faction faction;
	private int level;
	
	public FactionStats(){
		this(Faction.NONE);
	}
	
	public FactionStats(Faction faction){
		this(faction, 0);
	}
	
	public FactionStats(Faction faction, int level){
		this.faction = faction;
		this.level = level;
	}
	
	public Faction getFaction(){
		return faction;
	}
	
	public void setFaction(Faction faction){
		this.faction = faction;
	}
	
	public int getLevel(){
		return level;
	}
	
	public void setLevel(int level){
		this.level = level;
	}
	
	public void addLevel(int add){
		level += add;
	}
	
	public void load(TinyInputStream in)throws IOException{
		this.faction = Faction.get(in.readString());
		this.level = in.readInt();
	}
	
	public void save(TinyOutputStream out)throws IOException{
		out.writeString(faction.getName());
		out.write(level);
	}
}
