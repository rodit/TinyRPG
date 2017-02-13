package net.site40.rodit.util;

import android.annotation.SuppressLint;

public class GrammarUtil {

	@SuppressLint("DefaultLocale")
	public static String capitalise(String str){
		return str.substring(0, 1).toUpperCase() + str.toLowerCase().substring(1);
	}
}
