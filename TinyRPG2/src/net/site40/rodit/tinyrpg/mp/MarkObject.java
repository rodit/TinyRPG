package net.site40.rodit.tinyrpg.mp;

import static net.site40.rodit.tinyrpg.game.render.Strings.getString;
import net.site40.rodit.tinyrpg.game.Dialog.DialogCallback;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Input;
import net.site40.rodit.tinyrpg.game.object.GameObject;
import net.site40.rodit.tinyrpg.game.render.Animation;
import net.site40.rodit.tinyrpg.game.render.Strings;
import android.graphics.Canvas;
import android.graphics.RectF;

public class MarkObject extends GameObject{
	
	public static final String MARK_LOCATION = "mp/mark.anm";
	public static final float WIDTH = 72f;
	public static final float HEIGHT = 72f;
	
	private static Animation resourceCache = null;
	
	private String mapFile;
	private String remoteUsername;
	
	private volatile boolean dialogOpen = false;
	
	public MarkObject(String mapFile, String remoteUsername, float x, float y){
		this.mapFile = mapFile;
		this.remoteUsername = remoteUsername;
		this.bounds.set(x, y, WIDTH, HEIGHT);
	}
	
	public String getMapFile(){
		return mapFile;
	}
	
	public String getRemoteUsername(){
		return remoteUsername;
	}

	@Override
	public void update(final Game game){
		if(!dialogOpen && game.getInput().isUp(Input.KEY_ACTION) && RectF.intersects(bounds.get(), game.getPlayer().getCollisionBounds())){
			game.getHelper().dialog(getString(Strings.Dialog.CONFIRM_SUMMON, remoteUsername), new String[] { "Yes", "No" }, new DialogCallback(){
				@Override
				public void onSelected(int option){
					if(option == 0){
						game.getMP().sendMarkAccepted(mapFile, remoteUsername, MarkObject.this.bounds.getX(), MarkObject.this.bounds.getY());
						game.getHelper().dialog("Attempting to summon " + remoteUsername + " to your world...");
					}
					dialogOpen = false;
				}
			});
			dialogOpen = true;
		}
	}

	@Override
	public void draw(Game game, Canvas canvas){
		if(resourceCache == null)
			resourceCache = game.getResources().getAnimation(MARK_LOCATION);
		if(!game.getMap().getMap().getFile().equals(mapFile)){
			game.removeObject(this);
			return;
		}
		canvas.drawBitmap(resourceCache.getFrame(game.getTime()), null, bounds.get(), paint);
	}

	@Override
	public int getRenderLayer(){
		return RenderLayer.MIDDLE;
	}

	@Override
	public boolean shouldScale(){
		return true;
	}
	
	@Override
	public boolean equals(Object object){
		if(!(object instanceof MarkObject))
			return false;
		MarkObject obj = (MarkObject)object;
		return obj.getMapFile().equals(mapFile) && obj.getRemoteUsername().equals(remoteUsername) && obj.getBounds().getX() == bounds.getX() && obj.getBounds().getY() == bounds.getY();
	}
	
	@Override
	public int hashCode(){
		return (mapFile + "/" + remoteUsername).hashCode();
	}
}
