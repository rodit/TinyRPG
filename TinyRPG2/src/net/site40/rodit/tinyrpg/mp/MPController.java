package net.site40.rodit.tinyrpg.mp;

import static net.site40.rodit.tinyrpg.mp.Proxy.Common.ENTITY;
import static net.site40.rodit.tinyrpg.mp.Proxy.Common.ENTITY_LIVING;
import static net.site40.rodit.tinyrpg.mp.Proxy.Common.ENTITY_PLAYER;
import static net.site40.rodit.tinyrpg.mp.Proxy.Common.INPUT_EVENT;
import static net.site40.rodit.tinyrpg.mp.Proxy.Common.MAP_UPDATE;
import static net.site40.rodit.tinyrpg.mp.Proxy.Common.MARK_ACCEPTED;
import static net.site40.rodit.tinyrpg.mp.Proxy.Common.MARK_PLACE;
import static net.site40.rodit.tinyrpg.mp.Proxy.Common.MARK_PLACED;
import static net.site40.rodit.tinyrpg.mp.Proxy.Common.MARK_REMOVE;

import java.io.IOException;

import net.site40.rodit.rlib.util.ByteArrayReader;
import net.site40.rodit.rlib.util.ByteArrayWriter;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.entity.EntityPlayer;
import net.site40.rodit.tinyrpg.mp.Proxy.RemoteInputEvent;
import net.site40.rodit.util.GenericCallback.ObjectCallback;


public class MPController {

	private TinyClient client;
	private NetEntityCache entityCache;
	private GameStatus status;
	
	public MPController(Game game){
		this.client = new TinyClient(game, "", "");
		client.controller = this;
		this.entityCache = new NetEntityCache();
		this.status = GameStatus.LOCAL;
	}

	public TinyClient getClient(){
		return client;
	}

	public NetEntityCache getEntityCache(){
		return entityCache;
	}
	
	public GameStatus getStatus(){
		return status;
	}

	public void authenticate(final ObjectCallback<MPController> callbackSuccess, final ObjectCallback<MPController> callbackFailed){
		new Thread(){
			public void run(){
				if(client.requestSessionIdSync()){
					if(callbackSuccess != null)
						callbackSuccess.callback(MPController.this);
				}else{
					if(callbackFailed != null)
						callbackFailed.callback(MPController.this);
				}
			}
		}.start();
	}

	public void connect(final ObjectCallback<MPController> callbackSuccess, final ObjectCallback<MPController> callbackFailed){
		client.matchmakeAndConnect(new ObjectCallback<ServerData>(){
			public void callback(ServerData server){
				if(server == null){
					if(callbackFailed != null)
						callbackFailed.callback(MPController.this);
				}else if(callbackSuccess != null)
					callbackSuccess.callback(MPController.this);
			}
		});
	}

	public void handleProxyData(Game game, byte[] buffer){
		ByteArrayReader reader = new ByteArrayReader(buffer);
		byte proxyId = reader.read();
		switch(proxyId){
		case ENTITY:		
			Entity readEntity0 = new Entity();
			Proxy.Read.readEntityState(readEntity0, reader);
			handleEntityUpdate(readEntity0);
			break;
		case ENTITY_LIVING:
			EntityLiving readLiving0 = new EntityLiving();
			Proxy.Read.readEntityLivingState(game, readLiving0, reader);
			handleEntityUpdate(readLiving0);
			break;
		case ENTITY_PLAYER:
			EntityPlayer readPlayer0 = new EntityPlayer();
			Proxy.Read.readEntityPlayerState(game, readPlayer0, reader);
			handleEntityUpdate(readPlayer0);
			break;
		case MARK_REMOVE:
			MarkObject readMark0 = Proxy.Read.readMark(reader);
			handleMarkRemoved(game, readMark0);
			break;
		case MARK_PLACED:
			MarkObject readMark1 = Proxy.Read.readMark(reader);
			handleMarkPlaced(game, readMark1);
			break;
		case MARK_ACCEPTED:
			MarkObject readMark2 = Proxy.Read.readMark(reader);
			handleMarkAccepted(game, readMark2);
			break;
		}
		reader.dispose();
		reader = null;
	}
	
	public void handleEntityUpdate(Entity e){
		entityCache.update(e);
	}
	
	public void handleMarkRemoved(Game game, MarkObject mark){
		game.removeObject(mark);
	}
	
	public void handleMarkPlaced(Game game, MarkObject mark){
		if(mark.getMapFile().equals(game.getMap().getMap().getFile()))
			game.addObject(mark);
	}
	
	public void handleMarkAccepted(Game game, MarkObject mark){
		if(mark.getMapFile().equals(game.getMap().getMap().getFile()))
			game.removeObject(mark);
		//TODO: Change this to another form of display (e.g. in feed)
		game.getHelper().dialog("You are being summoned to another world...");
	}
	
	public void sendRawProxy(byte[] data)throws IOException{
		client.sendPacket(Proto.Client.PROXY_DATA, data);
	}

	public void sendMapUpdate(String mapName){
		try{
			ByteArrayWriter writer = new ByteArrayWriter();
			writer.write(MAP_UPDATE);
			Proxy.Write.writeMapUpdate(mapName, writer);
			sendRawProxy(writer.getBuffer());
			writer.dispose();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public void sendEntityUpdate(Entity e){
		try{
			ByteArrayWriter writer = new ByteArrayWriter();
			writer.write(ENTITY);
			Proxy.Write.writeEntityState(e, writer);
			sendRawProxy(writer.getBuffer());
			writer.dispose();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public void sendEntityUpdate(EntityLiving e){
		try{
			ByteArrayWriter writer = new ByteArrayWriter();
			writer.write(ENTITY_LIVING);
			Proxy.Write.writeEntityLivingState(e, writer);
			sendRawProxy(writer.getBuffer());
			writer.dispose();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public void sendEntityUpdate(EntityPlayer e){
		try{
			ByteArrayWriter writer = new ByteArrayWriter();
			writer.write(ENTITY_PLAYER);
			Proxy.Write.writeEntityPlayerState(e, writer);
			sendRawProxy(writer.getBuffer());
			writer.dispose();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public void sendInputEvent(int key, boolean up){
		try{
			ByteArrayWriter writer = new ByteArrayWriter();
			writer.write(INPUT_EVENT);
			Proxy.Write.writeInputEvent(new RemoteInputEvent(key, up), writer);
			sendRawProxy(writer.getBuffer());
			writer.dispose();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public void sendMarkPlace(String mapFile, String username, float x, float y){
		try{
			ByteArrayWriter writer = new ByteArrayWriter();
			writer.write(MARK_PLACE);
			Proxy.Write.writeMark(mapFile, username, x, y, writer);
			sendRawProxy(writer.getBuffer());
			writer.dispose();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public void sendMarkRemove(){
		try{
			sendRawProxy(new byte[] { MARK_REMOVE });
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public void sendMarkAccepted(String mapFile, String username, float x, float y){
		try{
			ByteArrayWriter writer = new ByteArrayWriter();
			writer.write(MARK_ACCEPTED);
			Proxy.Write.writeMark(mapFile, username, x, y, writer);
			sendRawProxy(writer.getBuffer());
			writer.dispose();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
}
