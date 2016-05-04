package net.site40.rodit.tinyrpg.game.util;

public enum Direction {

	D_UP, D_DOWN, D_LEFT, D_RIGHT;

	public static Direction opposite(Direction d){
		switch(d){
		case D_UP:
			return D_DOWN;
		case D_DOWN:
			return D_UP;
		case D_LEFT:
			return D_RIGHT;
		case D_RIGHT:
			return D_LEFT;
		default:
			return d;
		}
	}
}
