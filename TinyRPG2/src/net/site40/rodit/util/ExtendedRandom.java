package net.site40.rodit.util;

import java.security.SecureRandom;

import android.annotation.SuppressLint;

@SuppressWarnings("serial")
public class ExtendedRandom extends SecureRandom{

	public static final String CHARS_ALL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

	@SuppressLint("TrulyRandom")
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
		float nFloat = nextFloat() + 0.5f;
		float threshMin = 0.5f;
		float threshMax = 0.5f + threshhold;
		return threshMin <= nFloat && nFloat <= threshMax;
	}
}
