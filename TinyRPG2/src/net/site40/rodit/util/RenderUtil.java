package net.site40.rodit.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Html;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import net.site40.rodit.tinyrpg.game.Game;

public class RenderUtil {
	
	public static void drawMultilineText(Game game, Canvas canvas, String text, float x, float y, Paint paint){
		String[] lines = text.split("\n");
		for(int i = 0; i < lines.length; i++)
			canvas.drawText(lines[i], x, y + (i * paint.getTextSize()), paint);
	}
	
	public static int drawWrappedText(Game game, String text, int width, Paint paint, Canvas canvas){
		TextPaint tp = new TextPaint(paint);
		StaticLayout layout = new StaticLayout(Html.fromHtml(text.replace("\n", "<br>")), tp, width, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
		
//		LinearLayout layout = new LinearLayout(game.getContext());
//		TextView tv = new TextView(game.getContext());
//		tv.setVisibility(View.VISIBLE);
//		tv.setText(Html.fromHtml(text));
//		layout.addView(tv);
//		layout.draw(canvas);
		
		layout.draw(canvas);
		return layout.getHeight();
	}

	public static final String RES_BASE = "gui/window/";
	public static final String RES_M = RES_BASE + "m.png";
	public static final String RES_TLC = RES_BASE + "tlc.png";
	public static final String RES_TRC = RES_BASE + "trc.png";
	public static final String RES_BLC = RES_BASE + "blc.png";
	public static final String RES_BRC = RES_BASE + "brc.png";
	public static final String RES_TM = RES_BASE + "tm.png";
	public static final String RES_BM = RES_BASE + "bm.png";
	public static final String RES_LM = RES_BASE + "lm.png";
	public static final String RES_RM = RES_BASE + "rm.png";
	
	public static void drawBitmapBox(Canvas canvas, Game game, RectF bounds, Paint paint){
		canvas.drawBitmap(game.getResources().getBitmap(RES_M), null, new RectF(bounds.left + 128f, bounds.top + 128f, bounds.right - 128f, bounds.bottom - 128f), paint);
		
		canvas.drawBitmap(game.getResources().getBitmap(RES_TLC), null, new RectF(bounds.left, bounds.top, bounds.left + 128f, bounds.top + 128f), paint);
		canvas.drawBitmap(game.getResources().getBitmap(RES_TRC), null, new RectF(bounds.right - 128f, bounds.top, bounds.right, bounds.top + 128f), paint);
		canvas.drawBitmap(game.getResources().getBitmap(RES_BLC), null, new RectF(bounds.left, bounds.bottom - 128f, bounds.left + 128f, bounds.bottom), paint);
		canvas.drawBitmap(game.getResources().getBitmap(RES_BRC), null, new RectF(bounds.right - 128f, bounds.bottom - 128f, bounds.right, bounds.bottom), paint);
		
		int xMiddles = (int)(Math.ceil(bounds.width() / 128f)) - 1;
		int yMiddles = (int)(Math.ceil(bounds.height() / 128f)) - 1;
		
		if(xMiddles > 1){
			for(int i = 1; i < xMiddles; i++){
				canvas.drawBitmap(game.getResources().getBitmap(RES_TM), null, new RectF(bounds.left + i * 128f, bounds.top, bounds.left + i * 128f + 128f, bounds.top + 128f), paint);
				canvas.drawBitmap(game.getResources().getBitmap(RES_BM), null, new RectF(bounds.left + i * 128f, bounds.bottom - 128f, bounds.left + i * 128f + 128f, bounds.bottom), paint);
			}
		}

		if(yMiddles > 1){
			for(int i = 1; i < yMiddles; i++){
				canvas.drawBitmap(game.getResources().getBitmap(RES_LM), null, new RectF(bounds.left, bounds.top + i * 128f, bounds.left + 128f, bounds.top + i * 128f + 128f), paint);
				canvas.drawBitmap(game.getResources().getBitmap(RES_RM), null, new RectF(bounds.right - 128f, bounds.top + i * 128f, bounds.right, bounds.top + i * 128f + 128f), paint);
			}
		}
	}
}
