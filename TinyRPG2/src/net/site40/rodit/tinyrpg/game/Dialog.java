package net.site40.rodit.tinyrpg.game;

import java.util.regex.Pattern;

import net.site40.rodit.util.ArrayUtil;
import net.site40.rodit.util.RenderUtil;

import org.mozilla.javascript.Function;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class Dialog extends GameObject{
	
	public static interface DialogCallback{
		
		public void onSelected(int option);
	}

	public static final int SPEED_NORMAL = 80;
	public static final long INPUT_DELAY_CURSOR = 200L;
	public static final long ARROW_MOVE_DELAY = 500L;
	public static final String STAGE_DELIMETER = Pattern.quote("~");

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

	public void moveUp(){
		if(selected == 0)
			selected = options.length - 1;
		else
			selected--;
	}

	public void moveDown(){
		if(selected == options.length - 1)
			selected = 0;
		else
			selected++;
	}

	public boolean isFinished(){
		return hasFinishedAllStages() && confirmedOption != -1;
	}
	
	public boolean hasFinishedAllStages(){
		return isStageComplete() && stage >= getStages().length - 1;
	}

	public String[] getStages(){
		return text.split(STAGE_DELIMETER);
	}

	public String getStageText(){
		String stageText = getStages()[stage];
		long len = life / SPEED_NORMAL;
		if(len >= stageText.length())
			return stageText;
		return stageText.substring(0, (int)len);
	}

	public boolean isStageComplete(){
		return getStageText().equals(getStages()[stage]);
	}
	
	public void confirm(Game game){
		confirmedOption = selected;
		if(jsCallback != null)
			game.getScripts().executeFunction(game, jsCallback, this, new String[] { "dialog" }, new Object[] { this }, ArrayUtil.concat(Object.class, new Object[] { confirmedOption }, args));
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
		if(input.isDown(Input.KEY_UP))
			keyUpDown += game.getDelta();
		else
			keyUpDown = 0L;
		if(input.isDown(Input.KEY_DOWN))
			keyDownDown += game.getDelta();
		else
			keyDownDown = 0L;
		if(keyUpDown >= INPUT_DELAY_CURSOR || input.isUp(Input.KEY_UP)){
			moveUp();
			keyUpDown = 0L;
		}
		if(keyDownDown >= INPUT_DELAY_CURSOR || input.isUp(Input.KEY_DOWN)){
			moveDown();
			keyDownDown = 0L;
		}

		if(input.isUp(Input.KEY_ACTION)){
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
	
	@Override
	public void draw(Game game, Canvas canvas){
		game.pushTranslate(canvas);
		super.preRender(game, canvas);
		
		paint.setStyle(Style.FILL);
		paint.setTextSize(Values.FONT_SIZE_MEDIUM);
		paint.setTextAlign(Align.LEFT);
		RenderUtil.drawBitmapBox(canvas, game, BOUNDS, paint);

		paint.setColor(Color.WHITE);
		//String[] lines = justStaged ? new String[0] : getStageText().split("\n");
		//for(int i = 0; i < lines.length; i++)
			//canvas.drawText(lines[i], 32f, (720 - 150) + (float)i * paint.getTextSize() + 8f, paint);
		
		canvas.translate(BOUNDS.left + 48f, BOUNDS.top + 32f);
		RenderUtil.drawWrappedText(justStaged ? "" : getStageText(), (int)(BOUNDS.width() - 64f), paint, canvas);
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
				float height = 40 * options.length + 60;
				float width = 0;
				for(String op : options){
					float w = paint.measureText(op);
					if(w > width)width = w;
				}
				width += 80;
				while(width % 16 != 0){
					float fact = width / 16;
					fact = (float)Math.ceil(fact);
					width = 16f * fact;
				}
				while(height % 16 != 0){
					float fact = height / 16;
					fact = (float)Math.ceil(fact);
					height = 16f * fact;
				}
				float xoff = 250 - width;
				float yoff = 275 - height;
				RectF optBounds = new RectF(1030 + xoff, 270 + yoff, 1030 + xoff + width, 270 + yoff + height);
				RenderUtil.drawBitmapBox(canvas, game, optBounds, paint);
				paint.setColor(Color.WHITE);
				for(int i = 0; i < options.length; i++){
					int x = 1080 + (int)xoff;
					int y = 320 + i * 40 + (int)yoff;
					if(i == selected)
						canvas.drawText(">", x - 16, y, paint);
					canvas.drawText(options[i], x, y, paint);
				}
			}
		}else
			dialogFrames = 0L;

		super.postRender(game, canvas);
		game.popTranslate(canvas);
	}
	
	@Override
	public RenderLayer getRenderLayer(){
		return RenderLayer.TOP_ALL;
	}
	
	@Override
	public boolean shouldScale(){
		return false;
	}
}
