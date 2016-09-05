package net.site40.rodit.tinyrpg.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;

public class GlobalSerializer {

	public static final byte TYPE_STRING = 0;
	public static final byte TYPE_INT = 1;
	public static final byte TYPE_BOOL = 2;

	public static void deserialize(HashMap<String, Object> globals, TinyInputStream in)throws IOException{
		int count = in.readInt();
		for(int i = 0; i < count; i++){
			byte type = in.read();
			switch(type){
			case TYPE_STRING:
				globals.put(in.readString(), in.readString());
				break;
			case TYPE_INT:
				globals.put(in.readString(), in.readInt());
				break;
			case TYPE_BOOL:
				globals.put(in.readString(), in.readBoolean());
				break;
			default:
				throw new RuntimeException("Invalid global type id=" + type + ".");
			}
		}
	}

	public static void serialize(HashMap<String, Object> globals, TinyOutputStream out)throws IOException{
		ArrayList<String> globalKeys = new ArrayList<String>();
		for(String key : globals.keySet()){
			Object val = globals.get(key);
			if(getType(val) != -1)
				globalKeys.add(key);
		}
		out.write(globalKeys.size());
		String key = null;
		for(int i = 0; i < globalKeys.size(); i++){
			key = globalKeys.get(i);
			Object val = globals.get(key);
			byte type = getType(val);
			out.write(type);
			out.writeString(key);
			switch(type){
			case TYPE_STRING:
				out.writeString((String)val);
				break;
			case TYPE_INT:
				out.write((Integer)val);
				break;
			case TYPE_BOOL:
				out.write((Boolean)val);
				break;
			default:
				throw new RuntimeException("Invalid global type id=" + type + ".");
			}
		}
	}

	public static byte getType(Object obj){
		if(obj instanceof String)
			return TYPE_STRING;
		if(obj instanceof Integer)
			return TYPE_INT;
		if(obj instanceof Boolean)
			return TYPE_BOOL;
		return -1;
	}
}
