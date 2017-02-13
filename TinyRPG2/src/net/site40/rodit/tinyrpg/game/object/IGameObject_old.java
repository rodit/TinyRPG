package net.site40.rodit.tinyrpg.game.object;

import java.util.Comparator;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.IPaintMixer;
import net.site40.rodit.tinyrpg.game.entity.EntityPlayer;
import android.graphics.Canvas;
import android.graphics.Paint;


public interface IGameObject_old {
	
	public static enum RenderLayer{
		POST_PROCESS, DIALOG, TOP_OVERRIDE_PLAYER, TOP_ALL, TOP, MIDDLE, BOTTOM, BELOW_MAP
	}
	
	public static final Comparator<IGameObject> RENDER_COMPARATOR = new Comparator<IGameObject>(){
		
		private int index0;
		private int index1;
		@Override
		public int compare(IGameObject ig0, IGameObject ig1){
			if((ig0.getRenderLayer() == RenderLayer.TOP_OVERRIDE_PLAYER && ig1.getRenderLayer() != RenderLayer.POST_PROCESS && ig1.getRenderLayer() != RenderLayer.DIALOG) || ig0.getRenderLayer() == RenderLayer.POST_PROCESS || (ig0.getRenderLayer() == RenderLayer.DIALOG && ig1.getRenderLayer() != RenderLayer.POST_PROCESS))
				return 1;
			if(ig1.getRenderLayer() == RenderLayer.TOP_OVERRIDE_PLAYER || ig1.getRenderLayer() == RenderLayer.POST_PROCESS || ig1.getRenderLayer() == RenderLayer.DIALOG)
				return -1;
			if(ig0 instanceof EntityPlayer)
				return 1;
			if(ig1 instanceof EntityPlayer)
				return -1;
			RenderLayer p0 = ig0.getRenderLayer();
			RenderLayer p1 = ig1.getRenderLayer();
			RenderLayer[] layers = RenderLayer.values();
			index0 = 0;
			index1 = 0;
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
	public void dispose(Game game);
}
