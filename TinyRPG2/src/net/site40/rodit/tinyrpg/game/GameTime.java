package net.site40.rodit.tinyrpg.game;

import java.io.IOException;

import net.site40.rodit.tinyrpg.game.render.Strings.GameDates;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;

public class GameTime {
	
	private long startTime;
	
	public GameTime(){
		this(System.currentTimeMillis());
	}
	
	public GameTime(long startTime){
		this.startTime = startTime;
	}
	
	public void setStart(long startTime){
		this.startTime = startTime;
	}
	
	public long getGameTime(Game game){
		return game.getRealTime() - startTime;
	}
	
	public void load(TinyInputStream in)throws IOException{
		startTime = in.readLong();
	}
	
	public void save(TinyOutputStream out)throws IOException{
		out.write(startTime);
	}
	
	public static class GameDate{
		
		public static final long START_YEAR = 1192;
		
		public static final long MINUTE_MS = 100;
		public static final long HOUR_MS = MINUTE_MS * 60;
		public static final long DAY_MS = HOUR_MS * 14;
		public static final long NIGHT_MS = HOUR_MS * 10;
		
		public static final long DAY_NIGHT_CYCLE_MS = DAY_MS + NIGHT_MS;
		public static final long MONTH_MS = DAY_NIGHT_CYCLE_MS * 30;
		public static final long YEAR_MS = MONTH_MS * 12;
		
		private long excess;
		private long minutes;
		private long hours;
		private long days;
		private long months;
		private long years;
		
		private float nightFactor = 0f;
		
		public GameDate(long time){
			init(time);
		}
		
		public void init(long time){
			excess = minutes = hours = days = months = years = 0l;
			long remain = time;
			while(remain > 0){
				if(remain >= YEAR_MS){
					years++;
					remain -= YEAR_MS;
				}else if(remain >= MONTH_MS){
					months++;
					remain -= MONTH_MS;
				}else if(remain >= DAY_NIGHT_CYCLE_MS){
					days++;
					remain -= DAY_NIGHT_CYCLE_MS;
				}else if(remain >= HOUR_MS){
					hours++;
					remain -= HOUR_MS;
				}else if(remain >= MINUTE_MS){
					minutes++;
					remain -= MINUTE_MS;
				}else{
					excess++;
					remain = 0;
				}
			}
			if(hours <= 5)
				nightFactor = 1f - (float)(hours * HOUR_MS + minutes * MINUTE_MS) / (float)(5 * HOUR_MS);
			else if(hours >= 19)
				nightFactor = (float)((hours - 19) * HOUR_MS + minutes * MINUTE_MS) / (float)(5 * HOUR_MS);
			nightFactor = Math.min(nightFactor, 0.8f);
		}
		
		public long getExcess(){
			return excess;
		}
		
		public long getMinutes(){
			return minutes;
		}
		
		public long getHours(){
			return hours;
		}
		
		public long getDays(){
			return days;
		}
		
		public long getMonths(){
			return months;
		}
		
		public long getYears(){
			return years;
		}
		
		public float getNightFactor(){
			return nightFactor;
		}
		
		public String format(){
			String format = "";
			String hStr = String.valueOf(hours);
			if(hStr.length() == 1)
				hStr = "0" + hStr;
			String mStr = String.valueOf(minutes);
			if(mStr.length() == 1)
				mStr = "0" + mStr;
			format = GameDates.DAY_NAMES[(int)(days % 7)] + " " + hStr + ":" + mStr;
			format += "\n" + GameDates.MONTH_NAMES[(int)months] + " " + (days + 1) + ", " + (START_YEAR + years);
			return format;
		}
	}
}
