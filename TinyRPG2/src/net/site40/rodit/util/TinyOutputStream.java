package net.site40.rodit.util;

import java.io.IOException;
import java.io.OutputStream;

public class TinyOutputStream {

	private OutputStream out;
	
	public TinyOutputStream(OutputStream out){
		this.out = out;
	}
	
	public OutputStream getOutputStream(){
		return out;
	}
	
	public void write(byte[] data)throws IOException{
		write(data, 0, data.length);
	}
	
	public void write(byte[] data, int offset, int length)throws IOException{
		out.write(data, offset, length);
	}
	
	public void write(byte b)throws IOException{
		write(new byte[] { b });
	}
	
	public void write(int i)throws IOException{
		write(ByteUtil.getBytes(i));
	}
	
	public void write(float f)throws IOException{
		write(ByteUtil.getBytes(f));
	}
	
	public void write(long l)throws IOException{
		write(ByteUtil.getBytes(l));
	}
	
	public void write(double d)throws IOException{
		write(ByteUtil.getBytes(d));
	}
	
	public void write(boolean b)throws IOException{
		write((byte)(b ? 1 : 0));
	}
	
	public void writeString(String s)throws IOException{
		if(s == null)
			s = "";
		write(s.length());
		write(s.getBytes());
	}
	
	public void writeStringNoHead(String s)throws IOException{
		if(s == null)
			s = "";
		write(s.getBytes());
	}
	
	public void flush()throws IOException{
		out.flush();
	}
	
	public void close()throws IOException{
		out.close();
	}
}
