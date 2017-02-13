package net.site40.rodit.util;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.render.Strings.Resource;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Html;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

public class RenderUtil {

	public static void drawMultilineText(Game game, Canvas canvas, String[] lines, float x, float y, Paint paint){
		for(int i = 0; i < lines.length; i++)
			canvas.drawText(lines[i], x, y + (i * paint.getTextSize()), paint);
	}

	public static StaticLayout getStaticLayout(String text, int width, Paint paint){
		TextPaint tp = new TextPaint(paint);
		return getStaticLayout(text, width, tp);
	}

	public static StaticLayout getStaticLayout(String text, int width, TextPaint tp){
		StaticLayout layout = new StaticLayout(Html.fromHtml(text.replace("\n", "<br>")), tp, width, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
		return layout;
	}

	public static int drawWrappedText(Game game, String text, int width, Paint paint, Canvas canvas){
		return drawWrappedTextMemorySafe(game, getStaticLayout(text, width, paint), canvas);
	}

	public static int drawWrappedTextMemorySafe(Game game, StaticLayout layout, Canvas canvas){
		layout.draw(canvas);
		return layout.getHeight();
	}

	private static int xMiddles = 0;
	private static int yMiddles = 0;
	private static RectF rect = new RectF();
	public static void drawBitmapBox(Canvas canvas, Game game, RectF bounds, Paint paint){
		rect.set(bounds.left + 128f, bounds.top + 128f, bounds.right - 128f, bounds.bottom - 128f);
		canvas.drawBitmap(game.getResources().getBitmap(Resource.BOX_M), null, rect, paint);

		rect.set(bounds.left, bounds.top, bounds.left + 128f, bounds.top + 128f);
		canvas.drawBitmap(game.getResources().getBitmap(Resource.BOX_TLC), null, rect, paint);
		rect.set(bounds.right - 128f, bounds.top, bounds.right, bounds.top + 128f);
		canvas.drawBitmap(game.getResources().getBitmap(Resource.BOX_TRC), null, rect, paint);
		rect.set(bounds.left, bounds.bottom - 128f, bounds.left + 128f, bounds.bottom);
		canvas.drawBitmap(game.getResources().getBitmap(Resource.BOX_BLC), null, rect, paint);
		rect.set(bounds.right - 128f, bounds.bottom - 128f, bounds.right, bounds.bottom);
		canvas.drawBitmap(game.getResources().getBitmap(Resource.BOX_BRC), null, rect, paint);

		xMiddles = (int)(Math.ceil(bounds.width() / 128f)) - 1;
		yMiddles = (int)(Math.ceil(bounds.height() / 128f)) - 1;

		if(xMiddles > 1){
			for(int i = 1; i < xMiddles; i++){
				rect.set(bounds.left + i * 128f, bounds.top, bounds.left + i * 128f + 128f, bounds.top + 128f);
				canvas.drawBitmap(game.getResources().getBitmap(Resource.BOX_TM), null, rect, paint);
				rect.set(bounds.left + i * 128f, bounds.bottom - 128f, bounds.left + i * 128f + 128f, bounds.bottom);
				canvas.drawBitmap(game.getResources().getBitmap(Resource.BOX_BM), null, rect, paint);
			}
		}

		if(yMiddles > 1){
			for(int i = 1; i < yMiddles; i++){
				rect.set(bounds.left, bounds.top + i * 128f, bounds.left + 128f, bounds.top + i * 128f + 128f);
				canvas.drawBitmap(game.getResources().getBitmap(Resource.BOX_LM), null, rect, paint);
				rect.set(bounds.right - 128f, bounds.top + i * 128f, bounds.right, bounds.top + i * 128f + 128f);
				canvas.drawBitmap(game.getResources().getBitmap(Resource.BOX_RM), null, rect, paint);
			}
		}
	}
}
