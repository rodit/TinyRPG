package net.site40.rodit.util;

import android.graphics.Color;


public class ColorGradient {

	private int r0;
	private int g0;
	private int b0;
	private int r1;
	private int g1;
	private int b1;
	
	public ColorGradient(){
		this(0, 0, 0, 255, 255, 255);
	}
	
	public ColorGradient(int r0, int g0, int b0, int r1, int g1, int b1){
		this.r0 = r0;
		this.g0 = g0;
		this.b0 = b0;
		this.r1 = r1;
		this.g1 = g1;
		this.b1 = b1;
	}
	
	public int getColor(float p){
		int r = (int)((float)r0 * p + (float)r1 * (1 - p));
		int g = (int)((float)g0 * p + (float)g1 * (1 - p));
		int b = (int)((float)b0 * p + (float)b1 * (1 - p));
		return Color.rgb(r, g, b);
	}
}
