package net.site40.rodit.util;

import java.security.SecureRandom;

@SuppressWarnings("serial")
public class ExtendedRandom extends SecureRandom{

	public static final String CHARS_ALL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	
	public ExtendedRandom(){
		super();
	}
	
	public int nextInt(int min, int max){
		return nextInt(max - min) + min;
	}
	
	public String nextString(){
		return nextString(4);
	}
	
	public String nextString(int length){
		String str = "";
		for(int i = 0; i < length; i++)
			str += CHARS_ALL.charAt(nextInt(CHARS_ALL.length() - 1));
		return str;
	}
	
	public boolean should(float threshhold){
		return nextFloat() <= threshhold;
	}
}
