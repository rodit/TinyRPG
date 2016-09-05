package net.site40.rodit.tinyrpg.game.map;

import java.util.ArrayList;
import java.util.HashMap;

import net.site40.rodit.tinyrpg.game.render.ResourceManager;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;

public class RPGMap {

	private String file;
	private Bitmap background;
	private Bitmap renderOnTop;
	private MapProperties properties;
	private ArrayList<MapObject> objects;
	private HashMap<String, ArrayList<MapObject>> objectGroupCache;
	private HashMap<Region, ArrayList<RectF>> regions;
	private HashMap<String, ArrayList<RectF>> mobSpawnAreaKeys;

	private boolean loaded = false;

	public RPGMap(String file, boolean loaded){
		this.file = file;
		this.loaded = loaded;
		this.background = null;
		this.renderOnTop = null;
		this.properties = new MapProperties();
		this.objects = new ArrayList<MapObject>();
		this.objectGroupCache = new HashMap<String, ArrayList<MapObject>>();
		this.regions = new HashMap<Region, ArrayList<RectF>>();
		this.mobSpawnAreaKeys = new HashMap<String, ArrayList<RectF>>();
	}

	public boolean isLoaded(){
		return loaded;
	}

	public String getFile(){
		return file;
	}

	public Bitmap getBackground(){
		return background;
	}

	public void setBackground(Bitmap background){
		this.background = background;
	}

	public Bitmap getRenderOnTop(){
		return renderOnTop;
	}

	public void setRenderOnTop(Bitmap renderOnTop){
		this.renderOnTop = renderOnTop;
	}

	public MapProperties getProperties(){
		return properties;
	}

	public ArrayList<MapObject> getObjects(){
		return objects;
	}

	public ArrayList<MapObject> getObjects(String group){
		if(objectGroupCache == null)
			objectGroupCache = new HashMap<String, ArrayList<MapObject>>();
		ArrayList<MapObject> grouped = objectGroupCache.get(group);
		if(grouped == null){
			grouped = new ArrayList<MapObject>();
			for(MapObject obj : objects)
				if(obj.getGroup().equals(group))
					grouped.add(obj);
			objectGroupCache.put(group, grouped);
		}
		return grouped;
	}

	public HashMap<Region, ArrayList<RectF>> getRegions(){
		return regions;
	}
	
	public Region getRegion(float x, float y){
		for(Region region : regions.keySet()){
			ArrayList<RectF> allBounds = regions.get(region);
			if(allBounds == null)
				continue;
			for(RectF rect : allBounds)
				if(rect.contains(x, y))
					return region;
		}
		return Region.GRASS;
	}

	public void addRegionLocation(Region region, RectF location){
		ArrayList<RectF> locations = regions.get(region);
		if(locations == null)
			locations = new ArrayList<RectF>();
		if(!locations.contains(location))
			locations.add(location);
		regions.put(region, locations);
	}
	
	public ArrayList<RectF> getMobSpawnAreas(String key){
		return mobSpawnAreaKeys.get(key);
	}
	
	public String getMobSpawnAreaKeys(float x, float y){
		for(String key : this.mobSpawnAreaKeys.keySet()){
			ArrayList<RectF> allBounds = this.mobSpawnAreaKeys.get(key);
			if(allBounds == null)
				continue;
			for(RectF rect : allBounds)
				if(rect.contains(x, y))
					return key;
		}
		return "null_mobspawn";
	}
	
	public void addMobSpawnArea(String key, RectF area){
		ArrayList<RectF> locations = mobSpawnAreaKeys.get(key);
		if(locations == null)
			locations = new ArrayList<RectF>();
		if(!locations.contains(area))
			locations.add(area);
		mobSpawnAreaKeys.put(key, locations);
		Log.i("RPGMap", "Registered mob spawn area key=" + key + " area=" + area.toString());
	}

	public void dispose(ResourceManager resources){
		Log.i("RPGMap", "Disposing of map " + file + ".");
		if(background != null)
			background.recycle();
		if(renderOnTop != null)
			renderOnTop.recycle();
		background = renderOnTop = null;
		objects.clear();
		objects = null;
		regions.clear();
		regions = null;
		objectGroupCache.clear();
		objectGroupCache = null;
		resources.getResources().remove(file);
	}
}
