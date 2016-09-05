package net.site40.rodit.tinyrpg.game;

import java.util.Random;

public class Protect {
	
	public static Protect instance = new Protect();
	
	public static final int MAX_PROTECT = 32;
	//TODO: All protected integer values.
	public static final int PLAYER_HEALTH = 0;
	public static final int PLAYER_MAX_HEALTH = 1;
	public static final int PLAYER_XP = 2;
	public static final int PLAYER_MAGIKA = 3;
	
	//TODO: All protected long values.
	public static final int PLAYER_MONEY = 0;
	
	private static Random random = new Random();
	
	private int[] seeds;
	private int[] iprotect;
	
	private long[] lseeds;
	private long[] lprotect;
	
	public Protect(){
		this.seeds = new int[MAX_PROTECT];
		this.iprotect = new int[MAX_PROTECT];
		this.lseeds = new long[MAX_PROTECT];
		this.lprotect = new long[MAX_PROTECT];
	}
	
	private int genSeed(){
		long rfact = random.nextLong();
		while(rfact == 0)
			rfact = random.nextLong();
		return (int)(System.nanoTime() / rfact);
	}
	
	private long lgenSeed(){
		long rfact = random.nextLong();
		while(rfact == 0)
			rfact = random.nextLong();
		return System.nanoTime() / rfact;
	}
	
	private int convert(int i, int seed){
		return (~i) ^ seed;
	}
	
	private long convertLong(long l, long seed){
		return (~l) ^ seed;
	}
	
	public int get(int index){
		return convert(iprotect[index], seeds[index]);
	}
	
	public long getLong(int index){
		return convertLong(lprotect[index], lseeds[index]);
	}
	
	public void set(int index, int value){
		seeds[index] = genSeed();
		iprotect[index] = convert(value, seeds[index]);
	}
	
	public void setLong(int index, long value){
		lseeds[index] = lgenSeed();
		lprotect[index] = convertLong(value, lseeds[index]);
	}
	
	public boolean check(int index, int value){
		return get(index) == value;
	}
	
	public boolean checkLong(int index, long value){
		return getLong(index) == value;
	}
}
