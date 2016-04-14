package net.site40.rodit.util;

public class ByteArrayWriter {

	private byte[] buffer;
	private int position;
	
	public ByteArrayWriter(byte[] buffer){
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
	
	public void write(byte[] data){
		ByteUtil.write(buffer, position, data);
		seek(data.length);
	}
	
	public void write(int value){
		write(ByteUtil.getBytes(value));
	}
	
	public void write(long value){
		write(ByteUtil.getBytes(value));
	}
	
	public void write(float value){
		write(ByteUtil.getBytes(value));
	}
	
	public void write(boolean value){
		write(value ? new byte[] { 1 } : new byte[] { 0 });
	}
	
	public void write(String value){
		write(value.getBytes());
	}
}
