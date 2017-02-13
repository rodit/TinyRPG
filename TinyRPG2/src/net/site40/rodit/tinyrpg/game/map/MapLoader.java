package net.site40.rodit.tinyrpg.game.map;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import net.site40.rodit.rlib.util.io.LimitedStream;
import net.site40.rodit.tinyrpg.game.render.ResourceManager;
import net.site40.rodit.util.TinyInputStream;
import android.graphics.BitmapFactory;

public class MapLoader {
	
	public static RPGMap loadMap(ResourceManager resources, String fileName){
		RPGMap map = new RPGMap(fileName, true);
		try{
			String binFileName = "map/bin/" + new File(fileName).getName().replace(".tmx", ".dat");
			TinyInputStream in = new TinyInputStream(resources.openAsset(binFileName));
			byte[] sig = in.read(RPGMap.MAP_SIG.length);
			if(!Arrays.equals(RPGMap.MAP_SIG, sig))
				throw new IOException("Invalid map file signature.");
			LimitedStream imgStream = new LimitedStream(in.getStream(), in.readInt());
			map.setBackground(BitmapFactory.decodeStream(imgStream));
			imgStream.readAvailable();
			map.setHasRot(in.readBoolean());
			if(map.hasRot()){
				imgStream = new LimitedStream(in.getStream(), in.readInt());
				map.setRenderOnTop(BitmapFactory.decodeStream(imgStream));
				imgStream.readAvailable();
			}
			map.setHasLightMap(in.readBoolean());
			if(map.hasLightMap()){
				imgStream = new LimitedStream(in.getStream(), in.readInt());
				map.setLightMap(BitmapFactory.decodeStream(imgStream));
				imgStream.readAvailable();
			}
			imgStream = null;
			int mapPropCount = in.readInt();
			for(int i = 0; i < mapPropCount; i++)
				map.getProperties().setString(in.readString(), in.readString());
			int groupCount = in.readInt();
			for(int i = 0; i < groupCount; i++){
				String groupName = in.readString();
				int groupObjCount = in.readInt();
				for(int j = 0; j < groupObjCount; j++){
					String objName = in.readString();
					MapObject mob = new MapObject(groupName, in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat());
					mob.setString("name", objName);
					int mobPropCount = in.readInt();
					for(int k = 0; k < mobPropCount; k++)
						mob.getProperties().put(in.readString(), in.readString());
					map.getObjects().add(mob);
				}
			}
			in.close();
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
		resources.putObject(fileName, map);
		return map;
	}
}
