package net.site40.rodit.tinyrpg.game;

public class Ticker {

	private long interval;
	private long lastUpdate;
	
	public Ticker(long interval){
		this.interval = interval;
		this.lastUpdate = 0l;
	}
	
	public long getInvterval(){
		return interval;
	}
	
	public void setInterval(long interval){
		this.interval = interval;
	}
	
	public long getLastUpdate(){
		return lastUpdate;
	}
	
	public void setLastUpdate(long lastUpdate){
		this.lastUpdate = lastUpdate;
	}
	
	private volatile long now;
	public boolean shouldRun(Game game){
		now = game.getTime();
		if(now - lastUpdate >= interval){
			lastUpdate = now;
			return true;
		}
		return false;
	}
}
