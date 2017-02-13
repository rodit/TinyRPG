package net.site40.rodit.tinyrpg.game.render.gl;

public class Texture {

	private int id;
	private int width;
	private int height;
	
	public Texture(int id, int width, int height){
		this.id = id;
		this.width = width;
		this.height = height;
	}
	
	public int getId(){
		return id;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
}
