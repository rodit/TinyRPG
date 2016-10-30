package net.site40.rodit.tinyrpg.mp;

import java.io.IOException;

import net.site40.rodit.rlib.sockets.tcp.DisconnectReason;
import net.site40.rodit.rlib.sockets.tcp.client.Client;
import net.site40.rodit.rlib.sockets.tcp.client.IClientListener;
import net.site40.rodit.rlib.util.ByteArrayReader;
import net.site40.rodit.rlib.util.ByteArrayWriter;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.mp.api.API;
import net.site40.rodit.tinyrpg.mp.api.API.ApiCallback;
import net.site40.rodit.tinyrpg.mp.api.API.ApiRequest;
import net.site40.rodit.tinyrpg.mp.api.API.ApiResponse;
import net.site40.rodit.util.GenericCallback.ObjectCallback;
import net.site40.rodit.util.Util;

public class TinyClient implements IClientListener{
	
	private Game gameRef;

	protected String username;
	protected String password;
	protected String sessionId;
	protected AuthStatus status;
	protected ServerData server;
	
	protected MPController controller;

	private Client client;

	public TinyClient(Game game, String username, String password){
		this.gameRef = game;
		
		this.username = username;
		this.password = password;
		this.status = AuthStatus.NONE;

		this.client = new Client(25L, 256);
		client.addListener(this);
	}

	public Client getClient(){
		return client;
	}

	public AuthStatus getAuthStatus(){
		return status;
	}

	public boolean requestSessionIdSync(){
		try{
			ApiResponse response = ApiRequest.Builder.create().setUrl(API.API_HOST + API.API_AUTH + API.API_LOGIN)
			.put("username", username)
			.put("password", password)
			.build().requestSync();
			int status = Util.tryGetInt(response.get("status"));
			if(status == API.STATUS_OK){
				sessionId = response.get("session_id");
				return true;
			}else
				System.out.println("Failed to get session (" + status + "): " + response.get("message"));
		}catch(Exception e){
			System.err.println("Failed to get session.");
			e.printStackTrace();
		}
		return false;
	}

	public void requestSessionId(){
		ApiRequest.Builder.create().setUrl(API.API_HOST + API.API_AUTH + API.API_LOGIN)
		.put("username", username)
		.put("password", password)
		.build().request(new ApiCallback(){
			public void response(ApiResponse response){
				int status = Util.tryGetInt(response.get("status"));
				if(status == API.STATUS_OK)
					sessionId = response.get("session_id");
				else
					System.out.println("Failed to get session (" + status + "): " + response.get("message"));
			}
		});
	}

	public void matchmakeAndConnect(final ObjectCallback<ServerData> callback){
		ApiRequest.Builder.create().setUrl(API.API_HOST + API.API_MATCHMAKING)
		.put("username", username)
		.put("session_id", sessionId)
		.build().request(new ApiCallback(){
			public void response(ApiResponse response){
				int status = Util.tryGetInt(response.get("status"));
				if(status == API.STATUS_OK){
					ServerData server = new ServerData(response.get("server"), 1337);
					try{
						client.connect(server.getHost(), server.getPort());
						System.out.println("Connected to matchmade host successfully (" + server + ").");
						if(callback != null)
							callback.callback(server);
					}catch(IOException e){
						if(callback != null)
							callback.callback(null);
					}
				}else{
					System.out.println("Failed to get a matchmaking server (" + status + "): " + response.get("message"));
					if(callback != null)
						callback.callback(null);
				}
			}
		});
	}

	public void tryAuthenticate(){
		if(sessionId == null || sessionId.length() < API.LENGTH_SESSION_ID){
			System.out.println("Failed to authenticate as session id has not been set or is invalid.");
			return;
		}
		status = AuthStatus.AUTHENTICATING;
		byte[] encryptedAuthData = Proto.encrypt((username + ":" + sessionId).getBytes(), Proto.CLIENT_KEY);
		sendPacket(Proto.Client.AUTH_SESSION, encryptedAuthData);
		encryptedAuthData = null;
		System.out.println("Encrypted authentication packet sent.");
	}

	public void sendPacket(byte id, byte[] payload){
		try{
			ByteArrayWriter writer = new ByteArrayWriter();
			writer.write(id);
			writer.write(payload);
			client.write(writer.getBuffer());
			writer.dispose();
			writer = null;
		}catch(IOException e){
			System.out.println("Exception while sending packet id=" + id + " length=" + payload.length + ".");
			e.printStackTrace();
		}
	}

	@Override
	public void onConnected(Client client){
		System.out.println("Connected to server.");
		System.out.println("Sending authentication...");
		tryAuthenticate();
	}

	@Override
	public void onDisconnected(Client client, DisconnectReason reason){
		System.out.println("Disconnected from server (" + reason + ").");
	}

	@Override
	public void onDataReceived(Client client, byte[] data){
		ByteArrayReader reader = new ByteArrayReader(data);
		byte id = reader.read();
		switch(id){
		case Proto.Server.AUTH_FAILED:
			System.out.println("Authentication denied by server.");
			status = AuthStatus.FAILED;
			break;
		case Proto.Server.AUTH_TIMEOUT:
			System.out.println("Authentication timed out.");
			status = AuthStatus.FAILED;
			break;
		case Proto.Server.AUTH_SUCCESS:
			System.out.println("Authentication successful.");
			status = AuthStatus.AUTHENTICATED;
			break;
		case Proto.Server.PROXY_DATA:
			if(controller != null)
				controller.handleProxyData(gameRef, reader.read(reader.remaining()));
			break;
		}
		reader.dispose();
		reader = null;
	}

	@Override
	public void onClientException(Client client, Exception e){
		System.out.println("Warn/Client Exception: " + e.toString());
	}
}
