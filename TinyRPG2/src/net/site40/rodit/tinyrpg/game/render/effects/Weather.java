package net.site40.rodit.tinyrpg.game.render.effects;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.gui.GuiMenu;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

public class Weather extends PPEffect{

	private ArrayList<WeatherEffect> addQueue;
	private ArrayList<WeatherEffect> removeQueue;
	private ArrayList<WeatherEffect> effects;

	public Weather(){
		this.addQueue = new ArrayList<WeatherEffect>();
		this.removeQueue = new ArrayList<WeatherEffect>();
		this.effects = new ArrayList<WeatherEffect>();
	}

	public void add(WeatherEffect weather){
		synchronized(addQueue){
			if(!addQueue.contains(weather))
				addQueue.add(weather);
		}
	}

	public void remove(WeatherEffect weather){
		synchronized(removeQueue){
			removeQueue.add(weather);
		}
	}

	public void update(Game game){
		synchronized(effects){
			synchronized(addQueue){
				for(WeatherEffect weather : addQueue){
					if(!effects.contains(weather)){
						effects.add(weather);
						weather.onAdded(game);
					}
				}
				addQueue.clear();
			}
			synchronized(removeQueue){
				for(WeatherEffect effect : removeQueue){
					effects.remove(effect);
					effect.onRemoved(game);
				}
				removeQueue.clear();
			}
		}
	}

	public void preDraw(Game game, Canvas canvas){}

	public void draw(Game game, Canvas canvas){
		if(game.getMap() != null && !game.getGuis().isVisible(GuiMenu.class)){
			for(WeatherEffect effect : effects)
				effect.draw(game, canvas);
		}
	}

	public void postDraw(Game game, Canvas canvas){}

	public static class WeatherEffect{

		protected Paint paint = new Paint();

		public void draw(Game game, Canvas canvas){}

		public void onAdded(Game game){}
		public void onRemoved(Game game){}

		private RectF screenBoundsCache = new RectF();
		public RectF getScreenBounds(Game game){
			screenBoundsCache.set(game.getPlayer().getBounds().getX() - 640, game.getPlayer().getBounds().getY() - 360, game.getPlayer().getBounds().getX() + 640, game.getPlayer().getBounds().getY() + 360);
			return screenBoundsCache;
		}
	}

	public static class Rain extends WeatherEffect{

		static final int MAX_RAIN_PARTICLES = 100;
		static final float HEAVY_MULTI = 0.35f;

		private PointF[] rainParticleLocations;
		private int[] rainParticleLengths;
		private int cRainParticles;
		private float heaviness;

		private int audioStreamId;
		
		public Rain(){
			this.rainParticleLocations = new PointF[MAX_RAIN_PARTICLES];
			this.rainParticleLengths = new int[MAX_RAIN_PARTICLES];
			paint.setColor(Color.WHITE);
			paint.setStrokeWidth(0.1f);
			this.heaviness = 0.2f;
		}

		public void onAdded(Game game){
			audioStreamId = game.getAudio().playEffect("sound/weather/rain.ogg", true);
		}

		public void onRemoved(Game game){
			game.getAudio().stopEffect(audioStreamId);
		}

		private float getHeaviness(){
			return heaviness * HEAVY_MULTI;
		}

		private int nextFreeLocation(){
			for(int i = 0; i < rainParticleLocations.length; i++)
				if(rainParticleLocations[i] == null)
					return i;
			return -1;
		}

		public void draw(Game game, Canvas canvas){
			if(cRainParticles < MAX_RAIN_PARTICLES){
				int diff = MAX_RAIN_PARTICLES - cRainParticles;
				for(int i = 0; i < diff; i++){
					if(game.getRandom().nextFloat() > getHeaviness())
						continue;
					int nLoc = nextFreeLocation();
					rainParticleLocations[nLoc] = new PointF(game.getRandom().nextInt(1280), 0);
					rainParticleLengths[nLoc] = game.getRandom().nextInt(8, 16);
					cRainParticles++;
				}
			}
			
			RectF pBounds = getScreenBounds(game);
			for(int i = 0; i < rainParticleLocations.length; i++){
				PointF point = rainParticleLocations[i];
				if(point == null)
					continue;
				int size = rainParticleLengths[i];
				canvas.drawLine(pBounds.left + point.x, pBounds.top + point.y - size / 2, pBounds.left + point.x, pBounds.top + point.y + size / 2, paint);
				point.x -= 0.5f;
				point.y += 12f;
				if(point.y >= 480){
					rainParticleLocations[i] = null;
					cRainParticles--;
				}
			}
		}
	}

	public static class Lightning extends WeatherEffect{

		static final int OP_RESET = -1;
		static final int OP_WHITE = 0;
		static final int OP_BLACK = 1;

		private long interval;
		private float chance;

		private long lastStrike;
		private boolean running;
		private int sequenceCount;
		private int nextOp = -1;

		private ArrayList<Integer> audioStreamIds;

		public Lightning(){
			this(10000L);
			paint.setAlpha(160);
		}

		public Lightning(long interval){
			this(interval, 1f);
		}

		public Lightning(long interval, float chance){
			this.interval = interval;
			this.chance = chance;
			this.audioStreamIds = new ArrayList<Integer>();
		}

		public void onRemoved(Game game){
			for(Integer i : audioStreamIds)
				game.getAudio().stopEffect(i);
		}

		public void draw(Game game, Canvas canvas){
			if(game.getTime() - lastStrike >= interval && !running && game.getRandom().nextFloat() <= chance){
				running = true;
				sequenceCount = 0;
				game.getAudio().playEffect("sound/weather/lightning.ogg");
				lastStrike = game.getTime();
			}
			if(running){
				if(sequenceCount >= 8)
					running = false;
				else{
					switch(nextOp){
					case OP_RESET:
						nextOp = OP_WHITE;
						break;
					case OP_WHITE:
						paint.setColor(Color.WHITE);
						canvas.drawRect(getScreenBounds(game), paint);
						if(sequenceCount % 2 == 0)
							nextOp = OP_BLACK;
						break;
					case OP_BLACK:
						paint.setColor(Color.BLACK);
						canvas.drawRect(getScreenBounds(game), paint);
						if(sequenceCount % 2 != 0)
							nextOp = OP_WHITE;
						break;
					}
					sequenceCount++;
				}
			}
		}
	}
}
