package net.site40.rodit.tinyrpg.game.entity.npc;

import java.io.IOException;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.util.Direction;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;
import net.site40.rodit.util.Util;
import android.graphics.Canvas;

public class EntityNPC extends EntityAI{
	
	protected Nametag nametag;
	
	public EntityNPC(){
		this("New Entity");
	}
	
	public EntityNPC(String displayName){
		super();
		this.displayName = displayName;
	}
	
	@Override
	public boolean showName(){
		return true;
	}
	
	@Override
	public void onAction(Game game, Entity actor){
		String optDontChangeDirection = getRuntimeProperty("opt_dont_change_direction");
		if(!Util.tryGetBool(optDontChangeDirection, false))
			this.setDirection(Direction.opposite(actor.getDirection()));
		super.onAction(game, actor);
	}
	
	@Override
	public void serialize(TinyOutputStream out)throws IOException{
		super.serialize(out);
		out.writeString(displayName);
	}
	
	@Override
	public void deserialize(TinyInputStream in)throws IOException{
		super.deserialize(in);
		this.displayName = in.readString();
	}
	
	@Override
	public void onSpawn(Game game){
		super.onSpawn(game);
		if(nametag == null){
			nametag = new Nametag(this);
			game.getObjects().add(nametag);
		}
	}
	
	@Override
	public void onDespawn(Game game){
		super.onDespawn(game);
		game.getObjects().remove(nametag);
	}
	
	@Override
	public void draw(Game game, Canvas canvas){
		super.draw(game, canvas);
	}
}
