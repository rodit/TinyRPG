package net.site40.rodit.tinyrpg.game.map;

import android.annotation.SuppressLint;

public enum Region {

	GRASS, FOREST, LAKE, SEA, BEACH, DESERT, SNOW, MOUNTAIN, PEAK, UNKNOWN;
	
	public static class RegionLocal{
		
		@SuppressLint("DefaultLocale")
		public static String getString(Region region){
			return region.toString().substring(0, 1).toUpperCase() + region.toString().substring(1).toLowerCase();
		}
		
		@SuppressLint("DefaultLocale")
		public static String getResource(Region region){
			return "region/" + region.toString().toLowerCase() + ".png";
		}
	}
}