package net.site40.rodit.util;

public class ByteArrayReader {
	
	private byte[] buffer;
	private int position;
	
	public ByteArrayReader(byte[] buffer){
		this.buffer = buffer;
		this.position = 0;
	}
	
	public void position(int position){
		this.position = position;
	}
	
	public void seek(int amount){
		position += amount;
		if(position >= buffer.length)
			position = 0;
	}
	
	public int available(){
		return buffer.length - position;
	}
	
	public byte[] read(int length){
		byte[] data = ByteUtil.read(buffer, position, length);
		seek(length);
		return data;
	}
	
	public int readInt(){
		return ByteUtil.getInt(read(4));
	}
	
	public long readLong(){
		return ByteUtil.getLong(read(8));
	}
	
	public float readFloat(){
		return ByteUtil.getFloat(read(4));
	}
	
	public boolean readBool(){
		return read(1)[0] == 1;
	}
	
	public String readString(int length){
		return new String(read(length));
	}
}
