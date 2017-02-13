package net.site40.rodit.tinyrpg.game;

import java.util.regex.Pattern;

import net.site40.rodit.tinyrpg.game.object.GameObject;
import net.site40.rodit.tinyrpg.game.script.ScriptManager.KVP;
import net.site40.rodit.util.ArrayUtil;
import net.site40.rodit.util.RenderUtil;

import org.mozilla.javascript.Function;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.text.StaticLayout;
import android.text.TextPaint;

public class Dialog extends GameObject{

	public static interface DialogCallback{

		public void onSelected(int option);
	}

	public static final int SPEED_NORMAL = 80;
	public static final long INPUT_DELAY_CURSOR = 200L;
	public static final long ARROW_MOVE_DELAY = 500L;
	public static final String STAGE_DELIMETER = Pattern.quote("~");
	
	public static final String SFX_CONFIRM = "sound/menu/menu_confirm.ogg";
	public static final String SFX_SELECT = "sound/menu/menu_select.ogg";

	private String text;
	private String[] options;
	private Function jsCallback;
	private DialogCallback callback;

	private Object[] args;

	private int selected = 0;
	private int stage = 0;
	private long startTime = 0L;
	private long life = 0L;

	private int confirmedOption = -1;

	public Dialog(String text){
		this(text, new String[0]);
	}

	public Dialog(String text, String[] options){
		this(text, options, (Function)null);
	}

	public Dialog(String text, String[] options, Function jsCallback){
		super();
		this.text = text;
		this.options = options == null ? new String[0] : options;
		this.jsCallback = jsCallback;
		this.paint = Game.getDefaultPaint();
	}

	public Dialog(String text, String[] options, DialogCallback callback){
		super();
		this.text = text;
		this.options = options == null ? new String[0] : options;
		this.callback = callback;
		this.paint = Game.getDefaultPaint();
	}

	public void setArgs(Object... args){
		this.args = args;
	}

	public String getText(){
		return text;
	}

	public void setText(String text){
		this.text = text;
	}

	public String[] getOptions(){
		return options;
	}

	public void setOptions(String[] options){
		this.options = options;
	}

	public void moveUp(Game game){
		if(selected == 0)
			selected = options.length - 1;
		else
			selected--;
		game.getAudio().playEffect(SFX_SELECT);
	}

	public void moveDown(Game game){
		if(selected == options.length - 1)
			selected = 0;
		else
			selected++;
		game.getAudio().playEffect(SFX_SELECT);
	}

	public boolean isFinished(){
		return hasFinishedAllStages() && confirmedOption != -1;
	}

	public boolean hasFinishedAllStages(){
		return isStageComplete() && stage >= getStages().length - 1;
	}

	private String[] stageCache;
	public String[] getStages(){
		return stageCache == null ? stageCache = text.split(STAGE_DELIMETER) : stageCache;
	}

	private long stLen;
	public String getStageText(){
		String stageText = getStages()[stage];
		stLen = life / SPEED_NORMAL;
		if(stLen >= stageText.length())
			return stageText;
		return stageText.substring(0, (int)stLen);
	}

	public boolean isStageComplete(){
		return getStageText().equals(getStages()[stage]);
	}

	public void confirm(Game game){
		game.getAudio().playEffect(SFX_CONFIRM);
		confirmedOption = selected;
		if(jsCallback != null)
			game.getScript().runFunction(game, jsCallback, this, new KVP<?>[] { new KVP<Dialog>("dialog", this) }, ArrayUtil.concat(Object.class, new Object[] { confirmedOption }, args));
		if(callback != null)
			callback.onSelected(confirmedOption);
		game.removeObject(this);
		game.getInput().allowMovement(true);
	}

	private long keyUpDown = 0L;
	private long keyDownDown = 0L;
	private boolean justStaged = false;

	@Override
	public void update(Game game){		
		if(startTime == 0L || justStaged){
			startTime = game.getTime();
			justStaged = false;
		}
		life = game.getTime() - startTime;

		Input input = game.getInput();
		input.allowMovement(false);
		if(hasFinishedAllStages() && options.length > 0){
			if(input.isDown(Input.KEY_UP))
				keyUpDown += game.getDelta();
			else
				keyUpDown = 0L;
			if(input.isDown(Input.KEY_DOWN))
				keyDownDown += game.getDelta();
			else
				keyDownDown = 0L;
			if(keyUpDown >= INPUT_DELAY_CURSOR || (input.isUp(Input.KEY_UP) && keyUpDown == 0)){
				moveUp(game);
				keyUpDown = 0L;
			}
			if(keyDownDown >= INPUT_DELAY_CURSOR || (input.isUp(Input.KEY_DOWN) && keyDownDown == 0)){
				moveDown(game);
				keyDownDown = 0L;
			}
		}

		if(input.isUp(Input.KEY_ACTION)){
			input.setIdle(game, Input.KEY_ACTION);
			if(hasFinishedAllStages() && options.length != 0)
				confirm(game);
			else{
				if(hasFinishedAllStages()){
					confirm(game);
					return;
				}
				if(isStageComplete()){
					startTime = game.getTime();
					stage++;
					justStaged = true;
				}else
					startTime = 1L;
			}
		}
	}

	private float arrowOffset = 0f;
	private long lastArrowChange = 0L;
	private long dialogFrames = 0L;

	public static final RectF BOUNDS = new RectF(0, 720 - 192, 1280, 720);

	private int lastStageTextLen = -1;
	private String dStageText;
	private TextPaint tp;
	private StaticLayout multiLineDrawCache;
	private float uWidth, uHeight, uW, uFact, uXoff, uYoff, uLongestTextWidth, uX, uY;
	private int uAlpha, uColor;
	@Override
	public void draw(Game game, Canvas canvas){
		game.pushTranslate(canvas);
		super.preRender(game, canvas);

		paint.setStyle(Style.FILL);
		paint.setTextSize(Values.FONT_SIZE_MEDIUM);
		paint.setTextAlign(Align.LEFT);
		RenderUtil.drawBitmapBox(canvas, game, BOUNDS, paint);

		//String[] lines = justStaged ? new String[0] : getStageText().split("\n");
		//for(int i = 0; i < lines.length; i++)
		//canvas.drawText(lines[i], 32f, (720 - 150) + (float)i * paint.getTextSize() + 8f, paint);

		dStageText = justStaged ? "" : getStageText();
		if(lastStageTextLen != dStageText.length()){
			if(tp == null)
				tp = new TextPaint(paint);
			multiLineDrawCache = RenderUtil.getStaticLayout(dStageText, (int)(BOUNDS.width() - 64f), tp);
			lastStageTextLen = dStageText.length();
		}
		canvas.translate(BOUNDS.left + 48f, BOUNDS.top + 32f);
		RenderUtil.drawWrappedTextMemorySafe(game, multiLineDrawCache, canvas);
		canvas.translate(-(BOUNDS.left + 48f), -(BOUNDS.top + 32f));

		if(isStageComplete() && !hasFinishedAllStages()){
			if(game.getTime() - lastArrowChange >= ARROW_MOVE_DELAY){
				arrowOffset = arrowOffset == 5f ? 0f : 5f;
				lastArrowChange = game.getTime();
			}

			if(!hasFinishedAllStages())
				canvas.drawText("V", 1180, 665 + arrowOffset, paint);
		}

		if(options != null && options.length > 0 && hasFinishedAllStages()){
			dialogFrames++;
			if(dialogFrames > 2){
				uHeight = 40 * options.length + 60;
				uWidth = 0;
				for(String op : options){
					uW = paint.measureText(op);
					if(uW > uWidth)
						uWidth = uW;
				}
				uLongestTextWidth = uWidth;
				uWidth += 80;
				while(uWidth % 16 != 0){
					uFact = uWidth / 16;
					uFact = (float)Math.ceil(uFact);
					uWidth = 16f * uFact;
				}
				while(uHeight % 16 != 0){
					uFact = uHeight / 16;
					uFact = (float)Math.ceil(uFact);
					uHeight = 16f * uFact;
				}
				uXoff = 250 - uWidth;
				uYoff = 275 - uHeight;
				bounds.set(1030 + uXoff, 270 + uYoff, uWidth, uHeight);
				RenderUtil.drawBitmapBox(canvas, game, bounds.get(), paint);
				for(int i = 0; i < options.length; i++){
					uX = 1080 + (int)uXoff;
					uY = 340 + i * 40 + (int)uYoff;
					if(i == selected){
						uAlpha = paint.getAlpha();
						uColor = paint.getColor();
						paint.setAlpha(120);
						paint.setColor(Color.GRAY);
						bounds.set(uX - 8, uY - 34f, uLongestTextWidth + 8, 36f);
						canvas.drawRect(bounds.get(), paint);
						paint.setAlpha(uAlpha);
						paint.setColor(uColor);
					}
					canvas.drawText(options[i], uX, uY, paint);
				}
			}
		}else
			dialogFrames = 0L;

		super.postRender(game, canvas);
		game.popTranslate(canvas);
	}

	@Override
	public int getRenderLayer(){
		return RenderLayer.DIALOG;
	}

	@Override
	public boolean shouldScale(){
		return false;
	}
}
