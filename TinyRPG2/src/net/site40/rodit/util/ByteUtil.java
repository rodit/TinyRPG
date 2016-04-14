package net.site40.rodit.util;

import java.nio.ByteBuffer;

public class ByteUtil {

	public static int getInt(byte[] data){
		if(data.length < 4)
			return 0;
		ByteBuffer buff = ByteBuffer.allocate(4).put(data);
		buff.position(0);
		return buff.getInt();
	}

	public static byte[] getBytes(int value){
		ByteBuffer buff = ByteBuffer.allocate(4).putInt(value);
		buff.position(0);
		byte[] data = new byte[4];
		buff.get(data);
		return data;
	}

	public static long getLong(byte[] data){
		if(data.length < 8)
			return 0L;
		ByteBuffer buff = ByteBuffer.allocate(8).put(data);
		buff.position(0);
		return buff.getLong();
	}

	public static byte[] getBytes(long value){
		ByteBuffer buff = ByteBuffer.allocate(8).putLong(value);
		buff.position(0);
		byte[] data = new byte[8];
		buff.get(data);
		return data;
	}
	
	public static float getFloat(byte[] data){
		if(data.length < 4)
			return 0f;
		ByteBuffer buff = ByteBuffer.allocate(4).put(data);
		buff.position(0);
		return buff.getFloat();
	}
	
	public static byte[] getBytes(float value){
		ByteBuffer buff = ByteBuffer.allocate(4).putFloat(value);
		buff.position(0);
		byte[] data = new byte[4];
		buff.get(data);
		return data;
	}
	
	public static double getDouble(byte[] data){
		if(data.length < 8)
			return 0d;
		ByteBuffer buff = ByteBuffer.allocate(8).put(data);
		buff.position(0);
		return buff.getDouble();
	}
	
	public static byte[] getBytes(double value){
		ByteBuffer buff = ByteBuffer.allocate(8).putDouble(value);
		buff.position(0);
		byte[] data = new byte[8];
		buff.get(data);
		return data;
	}

	public static byte[] read(byte[] data, int offset, int length){
		byte[] sdata = new byte[length];
		System.arraycopy(data, offset, sdata, 0, length);
		return sdata;
	}
	
	public static void write(byte[] out, int offset, byte[] data){
		for(int i = offset; i < out.length && i < offset + data.length; i++)
			out[i] = data[i - offset];
	}

	public static byte[] concat(byte[] ... data){
		int len = 0;
		for(byte[] d : data)
			len += d.length;
		byte[] fdat = new byte[len];
		int offset = 0;
		for(int i = 0; i < data.length; i++){
			System.arraycopy(data[i], 0, fdat, offset, data[i].length);
			offset += data[i].length;
		}
		return fdat;
	}
}
