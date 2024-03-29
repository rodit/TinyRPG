package net.site40.rodit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.battle.AIBattleProvider.AIDifficulty;
import net.site40.rodit.tinyrpg.game.entity.Damage.DamageType;
import net.site40.rodit.tinyrpg.game.forge.ForgeRegistry.ForgeRecipy.ForgeType;
import net.site40.rodit.tinyrpg.game.item.ItemEquippable;
import net.site40.rodit.tinyrpg.game.quest.Quest.QuestImportance;
import net.site40.rodit.tinyrpg.game.render.SpriteSheet.MovementState;
import net.site40.rodit.tinyrpg.game.util.Direction;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.text.TextUtils;

public class Util {

	public static String[] join(String[]... arrays){
		int totalLength = 0;
		for(int i = 0; i < arrays.length; i++)
			totalLength += arrays[i].length;
		String[] total = new String[totalLength];
		int k = 0;
		for(int i = 0; i < arrays.length; i++)
			for(int j = 0; j < arrays[i].length; j++)
				total[k++] = arrays[i][j];
		return total;
	}

	public static <K, V> void sortValuesInMap(LinkedHashMap<K, V> map, final Comparator<? super V> comparator){
		List<Map.Entry<K, V>> entries = new ArrayList<Entry<K, V>>(map.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<K, V>>(){
			@Override
			public int compare(Map.Entry<K, V> lhs, Map.Entry<K, V> rhs){
				return comparator.compare(lhs.getValue(), rhs.getValue());
			}
		});

		map.clear();
		for(Map.Entry<K, V> e : entries)
			map.put(e.getKey(), e.getValue());
	}
	
	public static float getDistance(float x0, float y0, float x1, float y1){
		return (float)Math.sqrt(Math.pow(x0 - x1, 2f) + Math.pow(y0 - y1, 2f));
	}

	@SuppressLint("DefaultLocale")
	public static String format(double d){
		if(d == (long)d)
			return String.format("%d", (long)d);
		else
			return new DecimalFormat("#.##").format(d);
	}
	
	public static int getStatInt(float stat){
		return (int)(stat * 10);
	}

	public static boolean arrayContains(int[] array, int val){
		for(int i = 0; i < array.length; i++)
			if(array[i] == val)
				return true;
		return false;
	}

	public static <T>boolean arrayContains(T[] array, T object, Class<?> T){
		for(int i = 0; i < array.length; i++)
			if(array[i] == object)
				return true;
		return false;
	}

	public static int tryGetInt(String key){
		return tryGetInt(key, -1);
	}

	public static int tryGetInt(String key, int ret){
		try{
			return Integer.valueOf(key);
		}catch(Exception e){}
		return ret;
	}

	public static float tryGetFloat(String key){
		return tryGetFloat(key, 0f);
	}

	public static float tryGetFloat(String key, float ret){
		try{
			return Float.valueOf(key);
		}catch(Exception e){}
		return ret;
	}

	public static long tryGetLong(String key){
		return tryGetLong(key, -1l);
	}

	public static long tryGetLong(String key, long ret){
		try{
			return Long.valueOf(key);
		}catch(Exception e){}
		return ret;
	}

	public static AIDifficulty tryGetAIDifficulty(String key){
		return tryGetAIDifficulty(key, AIDifficulty.MEDIUM);
	}

	@SuppressLint("DefaultLocale")
	public static AIDifficulty tryGetAIDifficulty(String key, AIDifficulty ret){
		try{
			AIDifficulty diff = AIDifficulty.valueOf(key.toUpperCase());
			return diff == null ? ret : diff;
		}catch(Exception e){}
		return ret;
	}

	public static Direction tryGetDirection(String key){
		return tryGetDirection(key, Direction.D_DOWN);
	}

	@SuppressLint("DefaultLocale")
	public static Direction tryGetDirection(String key, Direction ret){
		try{
			return Direction.valueOf(key.toUpperCase());
		}catch(Exception e){}
		try{
			return Direction.valueOf("D_" + key.toUpperCase());
		}catch(Exception e){}
		return ret;
	}

	public static MovementState tryGetMoveState(String key){
		return tryGetMoveState(key, MovementState.IDLE);
	}

	@SuppressLint("DefaultLocale")
	public static MovementState tryGetMoveState(String key, MovementState ret){
		try{
			return MovementState.valueOf(key.toUpperCase());
		}catch(Exception e){}
		return ret;
	}
	
	public static QuestImportance tryGetQuestImportance(String key){
		return tryGetQuestImportance(key, QuestImportance.OPTIONAL);
	}

	@SuppressLint("DefaultLocale")
	public static QuestImportance tryGetQuestImportance(String key, QuestImportance ret){
		try{
			return QuestImportance.valueOf(key.toUpperCase());
		}catch(Exception e){}
		return ret;
	}

	public static boolean tryGetBool(String key){
		return tryGetBool(key, false);
	}

	public static boolean tryGetBool(String key, boolean ret){
		if(TextUtils.isEmpty(key))
			return ret;
		try{
			return Boolean.valueOf(key);
		}catch(Exception e){}
		return ret;
	}

	public static int tryGetColor(String key, int ret){
		try{
			Field f = Color.class.getDeclaredField(key);
			return f.getInt(null);
		}catch(Exception e){}
		try{
			return Integer.decode(key);
		}catch(Exception e){}
		return ret;
	}

	public static Align tryGetAlign(String key, Align ret){
		try{
			Field f = Align.class.getDeclaredField(key);
			return (Align)f.get(null);
		}catch(Exception e){}
		return ret;
	}

	private static HashMap<String, Float> textSizeCache = new HashMap<String, Float>();
	public static float tryGetTextSize(String key, float ret){
		Float val = textSizeCache.get(key);
		if(val == null){
			try{
				textSizeCache.put(key, val = Float.valueOf(key));
				return val;
			}catch(Exception e){}
			try{
				Field f = Values.class.getDeclaredField(key);
				textSizeCache.put(key, val = f.getFloat(null));
				return val;
			}catch(Exception e){}
		}else
			return val;
		return ret;
	}

	public static int tryGetSlot(String key){
		return tryGetSlot(key, -1);
	}

	public static int tryGetSlot(String key, int ret){
		try{
			return Integer.valueOf(key);
		}catch(Exception e){}
		try{
			Field f = ItemEquippable.class.getDeclaredField(key);
			return f.getInt(null);
		}catch(Exception e){}
		return ret;
	}

	public static ForgeType tryGetForgeType(String key){
		return tryGetForgeType(key, ForgeType.CRAFT);
	}

	public static ForgeType tryGetForgeType(String key, ForgeType ret){
		try{
			return ForgeType.valueOf(key);
		}catch(Exception e){}
		return ret;
	}
	
	public static DamageType tryGetDamageType(String key){
		return tryGetDamageType(key, DamageType.OTHER);
	}
	
	public static DamageType tryGetDamageType(String key, DamageType ret){
		try{
			return DamageType.valueOf(key);
		}catch(Exception e){}
		return ret;
	}

	public static byte[] readAll(InputStream in){
		try{
			byte[] toRead = new byte[in.available()];
			in.read(toRead);
			return toRead;
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}

	public static InputStream openFile(String file){
		return openFile(new File(file));
	}

	public static InputStream openFile(File file){
		try{
			return new FileInputStream(file);
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static void writeFile(File file, byte[] contents){
		try{
			FileOutputStream fout = new FileOutputStream(file);
			fout.write(contents);
			fout.flush();
			fout.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void deleteDir(File dir){
		for(File f : dir.listFiles()){
			if(f.isDirectory())
				deleteDir(f);
			else
				f.delete();
		}
		dir.delete();
	}
}
