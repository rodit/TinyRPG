package net.site40.rodit.util;

import java.io.IOException;

public interface ISavable {
	
	public void serialize(TinyOutputStream out)throws IOException;
	public void deserialize(TinyInputStream in)throws IOException;
}
