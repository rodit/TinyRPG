package net.site40.rodit.tinyrpg.game.render.gl;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;

public class GenericManager {

	private AssetManager assets;

	public GenericManager(AssetManager assets){
		this.assets = assets;
	}

	public byte[] read(String file){
		try{
			InputStream in = open(file);
			if(in != null){
				byte[] buffer = new byte[in.available()];
				in.read(buffer);
				in.close();
				return buffer;
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}

	public InputStream open(String file){
		try{
			return assets.open(file);
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public void close(InputStream stream){
		try{
			stream.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
