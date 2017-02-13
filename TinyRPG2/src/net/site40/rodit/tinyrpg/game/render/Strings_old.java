package net.site40.rodit.tinyrpg.game.render;

import java.util.regex.Pattern;

public class Strings_old {
	
	public static String DIALOG_DROP_EQUIPPED = "You cannot drop an item which is currently equipped.";
	
	public static String DIALOG_SWIM = "The water is a deep blue colour. Would you like to swim in it?";
	
	public static String DIALOG_TALK_BROKEN = "My dialog is broken for some reason. I can't talk right now.~Sorry about that. Contact the developers immediately for a fix.";

	public static String DIALOG_SELECT_ATTACK = "Select an attack.";
	public static String DIALOG_NO_SP_ATTACK = "You do not have any special attacks.";
	public static String DIALOG_SP_ATTACK_USED = "You used %s.";
	public static String DIALOG_NO_MAGIKA_SP = "You do not have enough magika to use this attack.";
	public static String DIALOG_NO_WEAPON = "You do not have a weapon equipped.";
	public static String DIALOG_WEAPONS_USED = "You used equipped weapon(s).";
	
	public static String DIALOG_INVALID_SELECTION_MODE = "Invalid selection mode.";
	
	public static String DIALOG_ESCAPE_SUCCESSFUL = "You escaped successfully.";
	public static String DIALOG_ESCAPE_FAILURE = "You tried to escape but your attempt failed miserably.";
	
	public static String DIALOG_BATTLE_WIN = "Congratulations! You have won the battle!~You earned %s gold!";
	public static String DIALOG_BATTLE_LOSE = "Oh no! You were killed in the battle!~You forfeited %s gold!";
	
	public static String DIALOG_BATTLE_INPUT_ASK = "What would you like to do?";
	public static String[] DIALOG_BATTLE_INPUT_OPTIONS = new String[] { "Use Weapon", "Sp. Attack", "Inventory", "Run" };
	public static String DIALOG_BATTLE_ASK_TARGET = "Who will you target?";
	public static String DIALOG_BATTLE_NO_OPPOSITION = "There is no opposition to select a target from.";
	
	public static String DIALOG_AI_NOTHING = "%s forgot to take their turn!";
	public static String DIALOG_AI_WEAPONS = "%s used their weapon(s).";
	public static String DIALOG_AI_HEAL = "%s used %s.";
	
	public static String DIALOG_FORGE_SUCCESSFUL = "Forge was successful!";
	public static String DIALOG_FORGE_NO_SKILL = "You do not meet the skill requirements to forge this item.";
	public static String DIALOG_FORGE_NO_GOLD = "You do not have enough gold to forge this item.";
	public static String DIALOG_FORGE_NO_MATERIALS = "You do not have the required materials to forge this item.";
	public static String DIALOG_FORGE_UNKNOWN = "An unknown error occured while processing your forge request.~Please try again later.";
	
	public static String DIALOG_ENTER_NAME = "Please enter your name.";
	public static String DIALOG_CONFIRM_NAME = "Is %s your name?";
	public static String[] DIALOG_YES_NO = new String[] { "Yes", "No" };
	
	public static String DIALOG_CONFIRM_SUMMON = "Would you like to summon %s to your world?";
	
	public static String UI_QUEST_COMPLETED = " (Completed)";
	
	public static String getString(String res, Object... replace){
		String replaced = res;
		for(int i = 0; i < replace.length; i++)
			replaced = replaced.replaceFirst(Pattern.quote("%s"), String.valueOf(replace[i]));
		return replaced;
	}
}
