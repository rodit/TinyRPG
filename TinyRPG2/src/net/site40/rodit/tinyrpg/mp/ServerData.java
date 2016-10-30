package net.site40.rodit.tinyrpg.mp;

public class ServerData {

	private String host;
	private int port;
	
	public ServerData(String host, int port){
		this.host = host;
		this.port = port;
	}
	
	public String getHost(){
		return host;
	}
	
	public int getPort(){
		return port;
	}
	
	@Override
	public String toString(){
		return host + ":" + port;
	}
}
