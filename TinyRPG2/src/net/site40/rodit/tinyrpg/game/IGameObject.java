package net.site40.rodit.tinyrpg.game;

import java.util.Comparator;

import android.graphics.Canvas;
import android.graphics.Paint;


public interface IGameObject {
	
	public static enum RenderLayer{
		TOP_ALL, TOP, MIDDLE, BOTTOM
	}
	
	public static final Comparator<IGameObject> RENDER_COMPARATOR = new Comparator<IGameObject>(){

		@Override
		public int compare(IGameObject ig0, IGameObject ig1){
			RenderLayer p0 = ig0.getRenderLayer();
			RenderLayer p1 = ig1.getRenderLayer();
			RenderLayer[] layers = RenderLayer.values();
			int index0 = 0;
			int index1 = 0;
			for(; index0 < layers.length; index0++)
				if(layers[index0] == p0)
					break;
			for(; index1 < layers.length; index1++)
				if(layers[index1] == p1)
					break;
			return index1 - index0;
		}
	};
	
	public void update(Game game);
	public void draw(Game game, Canvas canvas);
	public Paint getPaint();
	public void setPaint(Paint paint);
	public void attachPaintMixer(IPaintMixer mixer);
	public void detachPaintMixer(IPaintMixer mixer);
	public RenderLayer getRenderLayer();
	public boolean shouldScale();
}
