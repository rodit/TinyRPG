package net.site40.rodit.tinyrpg.game;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class GameObject implements IGameObject{

	private ArrayList<IPaintMixer> addQueue;
	private ArrayList<IPaintMixer> removeQueue;

	protected Paint paint;
	private ArrayList<IPaintMixer> mixers;

	public GameObject(){
		this.addQueue = new ArrayList<IPaintMixer>();
		this.removeQueue = new ArrayList<IPaintMixer>();

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
		synchronized(addQueue){
			addQueue.add(mixer);
		}
	}

	public void detachPaintMixer(IPaintMixer mixer){
		synchronized(removeQueue){
			removeQueue.add(mixer);
		}
	}

	public void preRender(Game game, Canvas canvas){
		synchronized(addQueue){
			for(IPaintMixer mixer : addQueue)
				if(!mixers.contains(mixer))
					mixers.add(mixer);
			addQueue.clear();
		}
		synchronized(removeQueue){
			for(IPaintMixer mixer : removeQueue)
				mixers.remove(mixer);
			removeQueue.clear();
		}

		for(IPaintMixer mixer : mixers)
			mixer.preRender(game, canvas, this);
	}

	public void postRender(Game game, Canvas canvas){
		for(IPaintMixer mixer : mixers)
			mixer.postRender(game, canvas, this);
	}
}
