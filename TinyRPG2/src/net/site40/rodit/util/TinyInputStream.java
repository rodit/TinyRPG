package net.site40.rodit.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TinyInputStream {
	
	public static final int DEFAULT_BUFFER_SIZE = 2048;

	private InputStream in;
	
	public TinyInputStream(InputStream in){
		this.in = in;
	}
	
	public byte[] read(int length)throws IOException{
		byte[] data = new byte[length];
		in.read(data);
		return data;
	}
	
	public void copy(OutputStream out)throws IOException{
		copy(out, DEFAULT_BUFFER_SIZE);
	}
	
	public void copy(OutputStream out, int bufferSize)throws IOException{
		byte[] buffer = new byte[bufferSize];
		int read = 0;
		while((read = in.read(buffer)) > 0)
			out.write(buffer, 0, read);
	}
	
	public byte[] readAll()throws IOException{
		return readAll(DEFAULT_BUFFER_SIZE);
	}
	
	public byte[] readAll(int bufferSize)throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copy(out, bufferSize);
		byte[] data = out.toByteArray();
		out.close();
		return data;
	}
	
	public byte read()throws IOException{
		return read(1)[0];
	}
	
	public int readInt()throws IOException{
		return ByteUtil.getInt(read(4));
	}
	
	public float readFloat()throws IOException{
		return ByteUtil.getFloat(read(4));
	}
	
	public long readLong()throws IOException{
		return ByteUtil.getLong(read(8));
	}
	
	public double readDouble()throws IOException{
		return ByteUtil.getDouble(read(8));
	}
	
	public boolean readBoolean()throws IOException{
		return read(1)[0] == 1;
	}
	
	public String readString()throws IOException{
		return readString(readInt());
	}
	
	public String readString(int length)throws IOException{
		return new String(read(length));
	}
}
