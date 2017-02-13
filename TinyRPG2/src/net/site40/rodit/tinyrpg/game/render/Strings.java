package net.site40.rodit.tinyrpg.game.render;

import java.util.regex.Pattern;

import android.graphics.Rect;
import android.graphics.RectF;

public class Strings {
	
	public static class GameData{
		
		public static final byte[] FILE_SIG = new byte[] { 7, 1, 5, 7 };
		public static final String VERSION = "TinyRPG0";
		
		public static final Object[] EMPTY_ARRAY = new Object[0];
		public static final String[] EMPTY_STRING_ARRAY = new String[0];
		
		public static final String EMPTY_STRING = "";
		
		public static final Rect EMPTY_RECT = new Rect();
		public static final RectF EMPTY_RECT_F = new RectF();
	}

	public static class Benchmarks{
		
		public static final String INIT = "init";
		public static final String UPDATE = "update";
		public static final String DRAW = "draw";
		public static final String DRAW_OBJ = "draw_obj";
		public static final String DRAW_GUI = "draw_gui";
		public static final String DRAW_PP = "draw_pp";
		
		public static final String SCRIPT_EXEC = "sc_";
		public static final String SCRIPT_FUNCTION = "sf_";
	}
	
	public static class Dialog{
		
		public static String DROP_EQUIPPED = "You cannot drop an item which is currently equipped.";
		
		public static String SWIM = "The water is a deep blue colour. Would you like to swim in it?";
		
		public static String TALK_BROKEN = "My dialog is broken for some reason. I can't talk right now.~Sorry about that. Contact the developers immediately for a fix.";
		
		public static String SELECT_ATTACK = "Select an attack.";
		public static String NO_SP_ATTACK = "You do not have any special attacks.";
		public static String SP_ATTACK_USED = "You used %s.";
		public static String NO_MAGIKA_SP = "You do not have enough magika to use this attack.";
		public static String NO_WEAPON = "You do not have a weapon equipped.";
		public static String WEAPONS_USED = "You used equipped weapon(s).";
		
		public static String INVALID_SELECTION_MODE = "Invalid selection mode.";
		
		public static String ESCAPE_SUCCESSFUL = "You escaped successfully.";
		public static String ESCAPE_FAILURE = "You tried to escape but your attempt failed miserably.";
		
		public static String BATTLE_WIN = "Congratulations! You have won the battle!~You earned %s gold!";
		public static String BATTLE_LOSE = "Oh no! You were killed in the battle!~You forfeited %s gold!";
		
		public static String BATTLE_INPUT_ASK = "What would you like to do?";
		public static String[] BATTLE_INPUT_OPTIONS = new String[] { "Use Weapon", "Sp. Attack", "Inventory", "Run" };
		public static String BATTLE_ASK_TARGET = "Who will you target?";
		public static String BATTLE_NO_OPPOSITION = "There is no opposition to select a target from.";
		
		public static String AI_NOTHING = "%s forgot to take their turn!";
		public static String AI_WEAPONS = "%s used their weapon(s).";
		public static String AI_HEAL = "%s used %s.";
		
		public static String FORGE_SUCCESSFUL = "Forge was successful!";
		public static String FORGE_NO_SKILL = "You do not meet the skill requirements to forge this item.";
		public static String FORGE_NO_GOLD = "You do not have enough gold to forge this item.";
		public static String FORGE_NO_MATERIALS = "You do not have the required materials to forge this item.";
		public static String FORGE_UNKNOWN = "An unknown error occured while processing your forge request.~Please try again later.";
		
		public static String ENTER_NAME = "Please enter your name.";
		public static String CONFIRM_NAME = "Is %s your name?";
		public static String[] YES_NO = new String[] { "Yes", "No" };
		
		public static String CONFIRM_SUMMON = "Would you like to summon %s to your world?";
		
		public static String getString(String res, Object... replace){
			String replaced = res;
			for(int i = 0; i < replace.length; i++)
				replaced = replaced.replaceFirst(Pattern.quote("%s"), String.valueOf(replace[i]));
			return replaced;
		}
	}
	
	public static class UI{
		
		public static String QUEST_COMPLETED = " (Completed)";
	}
	
	public static class SFX{
		
		public static final String DIALOG_CONFIRM = "sound/menu/menu_confirm.ogg";
		public static final String DIALOG_SELECT = "sound/menu/menu_select.ogg";
	}
	
	public static class Resource{
		
		public static final String FILE_CONFIG = "config.dat";
		
		public static final String BOX_BASE = "gui/window/";
		public static final String BOX_M = BOX_BASE + "m.png";
		public static final String BOX_TLC = BOX_BASE + "tlc.png";
		public static final String BOX_TRC = BOX_BASE + "trc.png";
		public static final String BOX_BLC = BOX_BASE + "blc.png";
		public static final String BOX_BRC = BOX_BASE + "brc.png";
		public static final String BOX_TM = BOX_BASE + "tm.png";
		public static final String BOX_BM = BOX_BASE + "bm.png";
		public static final String BOX_LM = BOX_BASE + "lm.png";
		public static final String BOX_RM = BOX_BASE + "rm.png";
	}
	
	public static class Config{
		
		public static final String USE_HARDWARE_RENDER = "hardware_render";
		public static final String BACKGROUND_MUSIC = "music_bg";
		public static final String CONTROLS_ALPHA = "alpha_controls";
		public static final String TRANSITION_SPEED = "transition_speed";
		
		public static class Values{
			
			public static final String TRANSITION_SPEED_SLOW = "slow";
			public static final String TRANSITION_SPEED_NORMAL = "normal";
			public static final String TRANSITION_SPEED_FAST = "fast";
		}
	}
	
	public static class Tags{
		
		public static final String GAME = "Game";
		public static final String SCRIPT_MANAGER = "ScriptManager";
	}
	
	public static class Values{
		
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
	
	public static String getString(String res, Object... replace){
		String replaced = res;
		for(int i = 0; i < replace.length; i++)
			replaced = replaced.replaceFirst(Pattern.quote("%s"), String.valueOf(replace[i]));
		return replaced;
	}
}
