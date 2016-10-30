package net.site40.rodit.tinyrpg.mp;

public class Proto {
	
	public static final byte[] CLIENT_KEY = "ZPtnxCxrGGx1mo6QWdZkBnQTMx7tHTi6TG4jQzXfntCzlrKNmiXWF7PtwMaXpBhASVzKx2nXMuVAznRR".getBytes();

	public static class Client{

		public static final byte AUTH_SESSION = 5;
		
		public static final byte PROXY_DATA = 10;

		public static final byte KEEP_ALIVE = 92;
	}

	public static class Server{

		public static final byte AUTH_SUCCESS = 5;
		public static final byte AUTH_FAILED = 6;
		public static final byte AUTH_TIMEOUT = 7;
		
		public static final byte PROXY_DATA = 10;

		public static final byte KEEP_ALIVE = 92;
	}
	
	public static class Game{
		
		public static final byte ENTITY_STATE = 0;
		public static final byte ENTITY_STATE_STATS = 1;
		public static final byte ENTITY_STATE_POSITION = 2;
		public static final byte ENTITY_STATE_INVENTORY = 3;
		
		public static final byte ENTITY_LIVING_STATE = 10;
		
		public static final byte SUMMON_SIGN_PLACE = 20;
		public static final byte SUMMON_SIGN_PLACED = 21;
		public static final byte SUMMON_SIGN_REMOVED = 22;
		public static final byte SUMMON_SIGN_ACCEPTED = 23;
	}

	public static byte[] encrypt(byte[] data, byte[] key){
		return xor(data, key);
	}

	public static byte[] decrypt(byte[] data, byte[] key){
		return xor(data, key);
	}

	private static byte[] xor(byte[] input, byte[] key){
		final byte[] output = new byte[input.length];
		int spos = 0;
		for(int pos = 0; pos < input.length; pos++){
			output[pos] = (byte)(input[pos] ^ key[spos]);
			spos += 1;
			if(spos >= key.length)
				spos = 0;
		}
		return output;
	}
}
