package net.site40.rodit.tinyrpg.game;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class GameObject implements IGameObject{

	protected Paint paint;
	private ArrayList<IPaintMixer> mixers;
	
	public GameObject(){
		this.mixers = new ArrayList<IPaintMixer>();
		this.paint = Game.getDefaultPaint();
	}
	
	public Paint getPaint(){
		return paint;
	}
	
	public void setPaint(Paint paint){
		this.paint = paint;
	}
	
	public ArrayList<IPaintMixer> getMixers(){
		return mixers;
	}
	
	public void attachPaintMixer(IPaintMixer mixer){
		if(!mixers.contains(mixer))
			mixers.add(mixer);
	}
	
	public void detachPaintMixer(IPaintMixer mixer){
		mixers.remove(mixer);
	}
	
	public void preRender(Game game, Canvas canvas){
		for(IPaintMixer mixer : mixers)
			mixer.preRender(game, canvas, this);
	}
	
	public void postRender(Game game, Canvas canvas){
		for(IPaintMixer mixer : mixers)
			mixer.postRender(game, canvas, this);
	}
}
