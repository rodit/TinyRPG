package net.site40.rodit.util;

import net.site40.rodit.tinyrpg.game.Game;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Html;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

public class RenderUtil {

	public static int drawWrappedText(Game game, String text, int width, Paint paint, Canvas canvas){
		TextPaint tp = new TextPaint(paint);
		StaticLayout layout = new StaticLayout(Html.fromHtml(text), tp, width, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
		
//		LinearLayout layout = new LinearLayout(game.getContext());
//		TextView tv = new TextView(game.getContext());
//		tv.setVisibility(View.VISIBLE);
//		tv.setText(Html.fromHtml(text));
//		layout.addView(tv);
//		layout.draw(canvas);
		
		layout.draw(canvas);
		return layout.getHeight();
	}

	public static void drawBitmapBox(Canvas canvas, Game game, RectF bounds, Paint paint){
		canvas.drawBitmap(game.getResources().getBitmap("gui/window/m.png"), null, new RectF(bounds.left + 128f, bounds.top + 128f, bounds.right - 128f, bounds.bottom - 128f), paint);

		canvas.drawBitmap(game.getResources().getBitmap("gui/window/tlc.png"), null, new RectF(bounds.left, bounds.top, bounds.left + 128f, bounds.top + 128f), paint);
		canvas.drawBitmap(game.getResources().getBitmap("gui/window/trc.png"), null, new RectF(bounds.right - 128f, bounds.top, bounds.right, bounds.top + 128f), paint);
		canvas.drawBitmap(game.getResources().getBitmap("gui/window/blc.png"), null, new RectF(bounds.left, bounds.bottom - 128f, bounds.left + 128f, bounds.bottom), paint);
		canvas.drawBitmap(game.getResources().getBitmap("gui/window/brc.png"), null, new RectF(bounds.right - 128f, bounds.bottom - 128f, bounds.right, bounds.bottom), paint);
		
		int xMiddles = (int)(Math.ceil(bounds.width() / 128f)) - 1;
		int yMiddles = (int)(Math.ceil(bounds.height() / 128f)) - 1;

		if(xMiddles > 1){
			for(int i = 1; i < xMiddles; i++){
				canvas.drawBitmap(game.getResources().getBitmap("gui/window/tm.png"), null, new RectF(bounds.left + i * 128f, bounds.top, bounds.left + i * 128f + 128f, bounds.top + 128f), paint);
				canvas.drawBitmap(game.getResources().getBitmap("gui/window/bm.png"), null, new RectF(bounds.left + i * 128f, bounds.bottom - 128f, bounds.left + i * 128f + 128f, bounds.bottom), paint);
			}
		}

		if(yMiddles > 1){
			for(int i = 1; i < yMiddles; i++){
				canvas.drawBitmap(game.getResources().getBitmap("gui/window/lm.png"), null, new RectF(bounds.left, bounds.top + i * 128f, bounds.left + 128f, bounds.top + i * 128f + 128f), paint);
				canvas.drawBitmap(game.getResources().getBitmap("gui/window/rm.png"), null, new RectF(bounds.right - 128f, bounds.top + i * 128f, bounds.right, bounds.top + i * 128f + 128f), paint);
			}
		}
	}
}
