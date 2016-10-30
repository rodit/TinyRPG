package net.site40.rodit.tinyrpg.game.render;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import net.site40.rodit.rlib.util.io.StreamUtils;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.mod.TinyMod;

public abstract class ResourceStreamProvider {

	public abstract boolean hasResource(String path)throws IOException;
	public abstract InputStream openResource(String path)throws IOException;

	public byte[] readResource(String path)throws IOException{
		InputStream in = openResource(path);
		if(in == null)
			return null;
		byte[] read = StreamUtils.readAll(in);
		in.close();
		return read;
	}

	public static class AssetStreamProvider extends ResourceStreamProvider{

		private AssetManager assets;

		public AssetStreamProvider(AssetManager assets){
			this.assets = assets;
		}

		@Override
		public boolean hasResource(String path){
			InputStream testIn = openResource(path);
			if(testIn == null)
				return false;
			try{
				testIn.close();
			}catch(IOException e){
				e.printStackTrace();
			}
			return true;
		}

		@Override
		public InputStream openResource(String path){
			try{
				return assets.open(path);
			}catch(IOException e){
				return null;
			}
		}
	}

	public static class ModStreamProvider extends ResourceStreamProvider{

		private Game game;

		public ModStreamProvider(Game game){
			this.game = game;
		}

		@Override
		public boolean hasResource(String path){
			InputStream testIn = openResource(path);
			if(testIn == null)
				return false;
			try{
				testIn.close();
			}catch(IOException e){
				e.printStackTrace();
			}
			return true;
		}

		@Override
		public InputStream openResource(String path){
			try{
				InputStream in = null;
				for(TinyMod mod : game.getMods().listEnabledMods(game)){
					try{
						in = mod.getArchive().openFile("assets/" + path);
						if(in != null)
							break;
					}catch(IOException e){
						continue;
					}
				}
				if(in == null)
					throw new IOException("Failed to find asset in mods.");
				else
					return in;
			}catch(IOException e){
				return null;
			}
		}
	}
}
