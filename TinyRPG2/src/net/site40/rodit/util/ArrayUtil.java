package net.site40.rodit.util;

import java.lang.reflect.Array;



public class ArrayUtil {

	@SuppressWarnings("unchecked")
	public static <T>T[] concat(Class<?> T, T[]... args){
		int len = 0;
		for(int i = 0; i < args.length; i++)
			len += args[i].length;
		T[] ret = (T[])Array.newInstance(T, len);
		int done = 0;
		for(int i = 0; i < args.length; i++){
			T[] arg = args[i];
			System.arraycopy(arg, 0, ret, done, arg.length);
			done += arg.length;
		}
		return ret;
	}
}
