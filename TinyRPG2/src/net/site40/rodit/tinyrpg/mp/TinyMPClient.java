package net.site40.rodit.tinyrpg.mp;

import java.io.IOException;

import net.site40.rodit.sockets.Packet;
import net.site40.rodit.sockets.PacketReader;
import net.site40.rodit.sockets.PacketWriter;
import net.site40.rodit.sockets.client.Client;
import net.site40.rodit.sockets.client.ClientListener;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.mp.common.Packets;
import android.util.Log;

public class TinyMPClient implements ClientListener{

	public static final String MP_HOST = "192.168.1.88";
	public static final int MP_PORT = 7173;

	private Game game;
	private Client client;

	public TinyMPClient(Game game){
		this.game = game;
		this.client = new Client(10L);
	}

	public void init(){
		new Thread(){
			@Override
			public void run(){
				Log.i("TinyMP", "Connecting to server @ " + MP_HOST + ":" + MP_PORT + "...");
				client.connect(MP_HOST, MP_PORT);
				client.setCallback(TinyMPClient.this);
				Log.i("TinyMP", "Connected to server @ " + MP_HOST + ":" + MP_PORT + ".");
			}
		}.start();
	}
	
	public void send(byte id){
		send(id, new byte[0]);
	}

	public void send(final byte id, final byte[] payload){
		new Thread(){
			@Override
			public void run(){
				PacketWriter writer = new PacketWriter(new Packet());
				writer.write(new byte[] { id });
				writer.write(payload);
				client.send(writer.getPacket());
			}
		}.start();
	}

	public void onConnect(String host, int port){
		Log.i("TinyMP", "Successfully connected to server.");
	}

	public void onDisconnect(IOException reason){
		Log.i("TinyMP", "Connection to server closed. Reason: " + reason.getMessage());
	}

	public void onReceived(PacketReader payload){
		byte id = payload.read();
		switch(id){
		case Packets.Server.DISCONNECT:
			Log.i("TinyMP", "Disconnected from server.");
			break;
		case Packets.Server.KICK:
			Log.i("TinyMP", "Kicked from server. Reason: " + new String(payload.readAll()));
			break;
		case Packets.Server.MATCH_FOUND:
			Log.i("TinyMP", "Found MP match.");
			break;
		case Packets.Server.MATCH_TIMEOUT:
			Log.i("TinyMP", "Match search timed out.");
			break;
		default:
			Log.i("TinyMP", "Received packet with unknown id=" + id + ".");
			break;
		}
	}
}
