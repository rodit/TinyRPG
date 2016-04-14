package net.site40.rodit.tinyrpg.game.render;

public class SpriteNonScale extends Sprite{

	public SpriteNonScale(float x, float y, float width, float height, String resource, String name){
		super(x, y, width, height, resource, name);
	}
	
	@Override
	public boolean shouldScale(){
		return false;
	}
}
