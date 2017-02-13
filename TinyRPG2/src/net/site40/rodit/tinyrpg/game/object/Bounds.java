package net.site40.rodit.tinyrpg.game.object;

import java.io.IOException;
import java.util.ArrayList;

import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;
import android.graphics.RectF;

public class Bounds {

	private RectF cache;
	private RectF zeroed;
	
	private RectF pool0;
	private RectF pool1;
	
	private float x;
	private float y;
	private float width;
	private float height;
	
	private float centerX;
	private float centerY;
	
	private ArrayList<Bounds> linked;
	
	public Bounds(){
		this(0f, 0f, 0f, 0f);
	}
	
	public Bounds(float x, float y, float width, float height){
		init(x, y, width, height);
	}
	
	private void init(float x, float y, float width, float height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		this.cache = new RectF(x, y, x + width, y + height);
		this.zeroed = new RectF(0, 0, width, height);
		
		this.pool0 = new RectF();
		this.pool1 = new RectF();
		
		this.linked = new ArrayList<Bounds>();
	}
	
	public float getX(){
		return x;
	}
	
	public void setX(float x){
		this.x = x;
		cache.set(x, y, x + width, y + height);
		centerX = cache.centerX();
		for(int i = 0; i < linked.size(); i++)
			linked.get(i).setX(x);
	}
	
	public float getY(){
		return y;
	}
	
	public void setY(float y){
		this.y = y;
		cache.set(x, y, x + width, y + height);
		centerY = cache.centerY();
		for(int i = 0; i < linked.size(); i++)
			linked.get(i).setY(y);
	}
	
	public float getCenterX(){
		return centerX;
	}
	
	public float getCenterY(){
		return centerY;
	}
	
	public float getWidth(){
		return width;
	}
	
	public void setWidth(float width){
		this.width = width;
		cache.set(x, y, x + width, y + height);
		zeroed.set(0, 0, width, height);
		centerX = cache.centerX();
		for(int i = 0; i < linked.size(); i++)
			linked.get(i).setWidth(width);
	}
	
	public float getHeight(){
		return height;
	}
	
	public void setHeight(float height){
		this.height = height;
		cache.set(x, y, x + width, y + height);
		zeroed.set(0, 0, width, height);
		centerY = cache.centerY();
		for(int i = 0; i < linked.size(); i++)
			linked.get(i).setHeight(height);
	}
	
	public RectF get(){
		return cache;
	}
	
	public RectF getZeroed(){
		return zeroed;
	}
	
	public void set(float x, float y, float width, float height){
		setX(x);
		setY(y);
		setWidth(width);
		setHeight(height);
	}
	
	public void set(RectF rect){
		set(rect.left, rect.top, rect.width(), rect.height());
	}
	
	public void link(Bounds bounds){
		if(!linked.contains(bounds))
			linked.add(bounds);
		bounds.set(x, y, width, height);
	}
	
	public void unlink(Bounds bounds){
		linked.remove(bounds);
	}
	
	public RectF getPooled0(){
		return pool0;
	}
	
	public RectF getPooled1(){
		return pool1;
	}
	
	public void load(TinyInputStream in)throws IOException{
		init(in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat());
		pool0.set(in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat());
		pool1.set(in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat());
	}
	
	public void save(TinyOutputStream out)throws IOException{
		out.write(x);
		out.write(y);
		out.write(width);
		out.write(height);
		out.write(pool0.left);
		out.write(pool0.top);
		out.write(pool0.right);
		out.write(pool0.bottom);
		out.write(pool1.left);
		out.write(pool1.top);
		out.write(pool1.right);
		out.write(pool1.bottom);
	}
}
