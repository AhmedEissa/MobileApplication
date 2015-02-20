package com.matthewbulat.monitorapplication;

public class DatabaseServ {
	
	String _server;
	
	public DatabaseServ(){
		
	}
	public DatabaseServ(String server){
		this._server=server;		
	}

	
	
	public void setServer(String server){
		this._server=server;
	}
	
	
	
	public String getServer(){
		return this._server;
	}

}
