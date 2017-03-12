package net.site40.rodit.tinyrpg.game.saves;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import net.site40.rodit.rlib.util.io.LimitedStream;
import net.site40.rodit.rlib.util.io.StreamUtils;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.map.MapState;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.util.Log;

public class SaveSlot {

	private static final long SECOND_MS = 1000;
	private static final long MINUTE_MS = 60 * SECOND_MS;
	private static final long HOUR_MS = 60 * MINUTE_MS;
	private static final long DAY_MS = 24 * HOUR_MS;

	protected static final String BACKUP_DIR = ".backup";
	protected static final String MAP_DIR = "map";
	protected static final String GAME_FILE = "game.dat";
	protected static final String INFO_FILE = "save.dat";

	private File root;
	private long playTime;
	private Bitmap screenshot;

	public SaveSlot(File root){
		this.root = root;
		getSaveFile(MAP_DIR).mkdir();
	}

	public File getRoot(){
		return root;
	}

	public boolean loadInfo(){
		try{
			TinyInputStream in = new TinyInputStream(new FileInputStream(getSaveFile(INFO_FILE)));
			playTime = in.readLong();
			in.close();
			return true;
		}catch(IOException e){
			return false;
		}
	}

	public long getPlayTime(){
		return playTime;
	}

	public Bitmap getScreenshot(){
		if(screenshot == null){
			try{
				TinyInputStream in = new TinyInputStream(new FileInputStream(getSaveFile(INFO_FILE)));
				in.readLong();
				int bmpLen = in.readInt();
				LimitedStream inStr = new LimitedStream(in.getStream(), bmpLen);
				screenshot = BitmapFactory.decodeStream(inStr);
				in.close();
			}catch(IOException e){}
		}
		return screenshot;
	}

	public void releaseScreenshot(){
		if(screenshot != null)
			screenshot.recycle();
		screenshot = null;
	}

	public File getSaveFile(String name){
		return new File(root, name);
	}

	public void load(Game game)throws IOException{
		load(game, false);
	}

	public void load(final Game game, boolean isBackup)throws IOException{
		TinyInputStream gameIn = new TinyInputStream(new FileInputStream(getSaveFile(GAME_FILE)));
		try{
			game.load(gameIn);
		}catch(IOException e){
			if(isBackup){
				new AlertDialog.Builder(game.getContext()).setMessage("The backup for this save is corrupt.").show();
				return;
			}
			OnClickListener dialogClickListener = new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int selected){
					if(selected == AlertDialog.BUTTON_POSITIVE){
						File backupDir = getSaveFile(BACKUP_DIR);
						if(backupDir.isDirectory()){
							File backupGame = new File(backupDir, GAME_FILE);
							File backupMap = new File(backupDir, MAP_DIR);
							File backupInfo = new File(backupDir, INFO_FILE);
							if(backupGame.exists())
								copyFile(backupGame, getSaveFile(GAME_FILE));
							if(backupMap.exists())
								copyRecursive(backupMap, getSaveFile(MAP_DIR));
							if(backupInfo.exists())
								copyFile(backupInfo, getSaveFile(INFO_FILE));
							try{
								load(game, true);
							}catch(IOException e){}
						}else
							new AlertDialog.Builder(game.getContext()).setMessage("No backup was found for this save.").show();
					}
				}
			};
			AlertDialog.Builder builder = new AlertDialog.Builder(game.getContext());
			builder.setMessage("Your save data appears to be corrupt. Would you like to try and load a backup instead?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
		}finally{
			gameIn.close();
		}
	}

	public void save(Game game)throws IOException{
		backup();
		TinyOutputStream gameOut = new TinyOutputStream(new FileOutputStream(getSaveFile(GAME_FILE)));
		game.save(gameOut);
		gameOut.close();
		TinyOutputStream infoOut = new TinyOutputStream(new FileOutputStream(getSaveFile(INFO_FILE)));
		infoOut.write(game.getTime());
		Bitmap screenshot = Bitmap.createBitmap(640, 360, Config.ARGB_8888);//TODO: actually screenshot game.
		screenshot.compress(CompressFormat.PNG, 100, infoOut.getOutputStream());
		screenshot.recycle();
		screenshot = null;
		infoOut.close();
	}

	public MapState loadMap(Game game, String name)throws IOException{
		File mapFile = getSaveFile(MAP_DIR + "/" + new File(name).getName() + ".dat");
		if(mapFile.exists()){
			TinyInputStream in = new TinyInputStream(new FileInputStream(mapFile));
			MapState state = new MapState(null);
			state.load(game, in);
			in.close();
			return state;
		}
		return null;
	}

	public void saveMap(Game game, MapState map)throws IOException{
		if(map == null || map.getMap() == null || map.getMap().getFile() == null)
			return;
		File mapFile = getSaveFile(MAP_DIR + "/" + new File(map.getMap().getFile()).getName() + ".dat");
		TinyOutputStream out = new TinyOutputStream(new FileOutputStream(mapFile));
		map.save(game, out);
		out.close();
	}

	private void backup(){
		File backupDir = new File(root, BACKUP_DIR);
		if(backupDir.exists())
			deleteRecursive(backupDir);
		backupDir.mkdir();
		File gameFile = getSaveFile(GAME_FILE);
		File mapDir = getSaveFile(MAP_DIR);
		File infoFile = getSaveFile(INFO_FILE);
		if(gameFile.exists())
			copyRecursive(gameFile, backupDir);
		if(mapDir.exists())
			copyRecursive(mapDir, backupDir);
		if(infoFile.exists())
			copyRecursive(infoFile, backupDir);
	}

	public boolean destroy(){
		return deleteRecursive(root);
	}

	private boolean deleteRecursive(File file){
		if(file.isFile())
			return file.delete();
		else if(file.isDirectory()){
			for(File sFile : file.listFiles())
				deleteRecursive(sFile);
			return file.delete();
		}
		return false;
	}

	private void copyRecursive(File src, File dst){
		if(src.isDirectory()){
			File dstDir = new File(dst, src.getName());
			dstDir.mkdir();
			for(File file : src.listFiles()){
				if(file.isFile())
					copyFile(file, new File(dstDir, file.getName()));
				else
					copyRecursive(file, new File(dstDir, file.getName()));
			}
		}else
			copyFile(src, new File(dst, src.getName()));
	}

	private void copyFile(File src, File dst){
		try{
			StreamUtils.copyAndClose(new FileInputStream(src), new FileOutputStream(dst));
		}catch(IOException e){
			Log.e("SaveSlot", "Error copying file " + src.getName() + " to " + dst.getName() + ".");
			if(Game.DEBUG)
				e.printStackTrace();
		}
	}

	private String hrptCache;
	public String getHumanReadablePlayTime(){
		if(hrptCache == null){
			int s = 0, m = 0, h = 0, d = 0;
			long remainTime = playTime;
			while(remainTime > 0){
				if(remainTime >= DAY_MS){
					d++;
					remainTime -= DAY_MS;
				}else if(remainTime >= HOUR_MS){
					h++;
					remainTime -= HOUR_MS;
				}else if(remainTime >= MINUTE_MS){
					m++;
					remainTime -= MINUTE_MS;
				}else if(remainTime >= SECOND_MS){
					s++;
					remainTime -= SECOND_MS;
				}else if(remainTime >= SECOND_MS / 2){
					s++;
					remainTime = 0;
				}else
					remainTime = 0;
			}
			String sStr = String.valueOf(s);
			if(sStr.length() == 1)
				sStr = "0" + sStr;
			String mStr = String.valueOf(m);
			if(mStr.length() == 1)
				mStr = "0" + mStr;
			String hStr = String.valueOf(h);
			if(hStr.length() == 1)
				hStr = "0" + hStr;
			hrptCache = "";
			if(s > 0)
				hrptCache = sStr;
			if(m > 0)
				hrptCache = mStr + ":" + hrptCache;
			if(h > 0)
				hrptCache = hStr + ":" + hrptCache;
			if(d > 0)
				hrptCache = d + ":" + hrptCache;
		}
		return hrptCache;
	}
}
