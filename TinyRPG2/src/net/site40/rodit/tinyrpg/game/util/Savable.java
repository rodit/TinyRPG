package net.site40.rodit.tinyrpg.game.util;

import java.io.IOException;

import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;

public interface Savable {

	public void load(TinyInputStream in)throws IOException;
	public void save(TinyOutputStream out)throws IOException;
}
