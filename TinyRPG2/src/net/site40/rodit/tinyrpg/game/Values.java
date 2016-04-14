package net.site40.rodit.tinyrpg.game;

public class Values {

	public static final float FONT_SIZE_HUGE = 128f;
	public static final float FONT_SIZE_LARGE = 92f;
	public static final float FONT_SIZE_BIG = 64f;
	public static final float FONT_SIZE_MEDIUM = 48f;
	public static final float FONT_SIZE_SMALL = 32f;
	public static final float FONT_SIZE_TINY = 24f;
	
	public static String readableFloat(float f){
		String fs = f + "";
		if(fs.endsWith(".0"))
			return (int)f + "";
		return fs;
	}
}
