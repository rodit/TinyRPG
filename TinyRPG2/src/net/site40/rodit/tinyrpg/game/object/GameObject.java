package net.site40.rodit.tinyrpg.game.object;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.IPaintMixer;
import net.site40.rodit.tinyrpg.game.render.ResourceWrapper;
import net.site40.rodit.tinyrpg.game.render.SpriteSheet.MovementState;
import net.site40.rodit.tinyrpg.game.render.Strings.GameData;
import net.site40.rodit.tinyrpg.game.util.Direction;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;
import net.site40.rodit.util.Util;
import android.graphics.Canvas;
import android.graphics.Paint;

public class GameObject {

	public static class RenderLayer{

		public static final int POST_PROCESS = 100;
		public static final int DIALOG = 80;
		public static final int TOP_OVER_PLAYER = 60;
		public static final int TOP_OVER_ALL = 40;
		public static final int TOP = 20;
		public static final int MIDDLE = 0;
		public static final int BOTTOM = -20;
		public static final int BELOW_MAP = -40;

		public static Comparator<GameObject> RENDER_COMPARATOR = new Comparator<GameObject>(){

			@Override
			public int compare(GameObject g0, GameObject g1){
				return g0.getRenderLayer() - g1.getRenderLayer();
			}
		};
	}

	private ArrayList<IPaintMixer> addQueue;
	private ArrayList<IPaintMixer> removeQueue;

	protected String name;
	protected String resource;
	protected Bounds bounds;
	protected MovementState moveState;
	protected Direction direction;

	protected ResourceWrapper resourceCache;
	protected boolean dirtyResource = false;
	protected boolean dirtyDraw = false;

	protected Paint paint;
	protected ArrayList<IPaintMixer> mixers;

	public boolean ignoreScroll = false;

	public GameObject(){
		this.addQueue = new ArrayList<IPaintMixer>();
		this.removeQueue = new ArrayList<IPaintMixer>();

		this.bounds = new Bounds();
		this.moveState = MovementState.IDLE;
		this.direction = Direction.D_DOWN;
		this.mixers = new ArrayList<IPaintMixer>();
		this.paint = Game.getDefaultPaint();
		
		this.resourceCache = new ResourceWrapper(GameData.EMPTY_STRING);
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getResource(){
		return resource;
	}

	public void setResource(String resource){
		if(resource == null && this.resource != null)
			dirtyResource = true;
		else if(resource != null && !resource.equals(this.resource))
			dirtyResource = true;
		this.resource = resource;
	}
	
	public void setX(float x){
		bounds.setX(x);
	}
	
	public void setY(float y){
		bounds.setY(y);
	}
	
	public void setWidth(float width){
		bounds.setWidth(width);
	}
	
	public void setHeight(float height){
		bounds.setHeight(height);
	}
	
	public void setBounds(float x, float y, float width, float height){
		bounds.set(x, y, width, height);
	}

	public Bounds getBounds(){
		return bounds;
	}

	public MovementState getMoveState(){
		return moveState;
	}

	public void setMoveState(MovementState moveState){
		this.moveState = moveState;
	}

	public Direction getDirection(){
		return direction;
	}

	public void setDirection(Direction direction){
		this.direction = direction;
	}

	public boolean shouldScale(){
		return true;
	}

	public int getRenderLayer(){
		return RenderLayer.MIDDLE;
	}
	
	public void invalidate(){
		dirtyDraw = true;
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

	public void update(Game game){
		dirtyDraw = true;
	}

	public void draw(Game game, Canvas canvas){
		if(dirtyDraw){
			if(dirtyResource){
				dirtyResource = false;
				resourceCache.setResource(getResource());
				resourceCache.cacheResource(game);
			}
			
			dirtyDraw = false;
			if(ignoreScroll)
				game.pushTranslate(canvas);
			
			preRender(game, canvas);
			resourceCache.draw(game, canvas, this);
			postRender(game, canvas);
			
			if(ignoreScroll)
				game.popTranslate(canvas);
		}
	}

	public void dispose(Game game){}
	
	public GameObject copy(){
		GameObject obj = new GameObject();
		return copy(obj);
	}
	
	public GameObject copy(GameObject object){
		object.name = name;
		object.resource = resource;
		object.bounds.set(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
		object.bounds.getPooled0().set(bounds.getPooled0());
		object.bounds.getPooled1().set(bounds.getPooled1());
		object.moveState = moveState;
		object.direction = direction;
		object.ignoreScroll = ignoreScroll;
		return object;
	}
	
	public void load(Game game, TinyInputStream in)throws IOException{
		name = in.readString();
		setResource(in.readString());
		bounds.load(in);
		moveState = Util.tryGetMoveState(in.readString(), moveState);
		direction = Util.tryGetDirection(in.readString(), direction);
		ignoreScroll = in.readBoolean();
	}
	
	public void save(TinyOutputStream out)throws IOException{
		out.writeString(name);
		out.writeString(resource);
		bounds.save(out);
		out.writeString(moveState.toString());
		out.writeString(direction.toString());
		out.write(ignoreScroll);
	}
}
