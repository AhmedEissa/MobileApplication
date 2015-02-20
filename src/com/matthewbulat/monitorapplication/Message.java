package com.matthewbulat.monitorapplication;
import java.io.Serializable;

public class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	
	String request;
	String data;
	String email;
	String token;
	static byte[] serverdata;
	static String serverAddress;//stores websocket address of the server so any mathod can access it
	String[] localServerData;
	public void setEmail(String data){
		email=data;
	}
	public String getEmail(){
		return email;
	}
	public String getAddress(){
		return serverAddress;
	}
	public void setAddress(String data){
		serverAddress=data;
	}
	
	public static byte[] getServerData(){
		return Message.serverdata;
	}
	public static void setServerData(byte[] data){
		serverdata=data;
	}
	public void setRequest(String request){
		this.request=request;
	}
	public void setData(String data){
		this.data=data;
	}
	public void setToken(String token){
		this.token=token;
	}
	public String getlocalRequest(){
		return this.request;
	}
	public String getlocalData(){
		return this.data;
	}
	public String getlocalToken(){
		return this.token;
	}
	public byte[] getBytes(int a){
		String stringed=null;
		if(a==1){
			stringed=request +"!"+email+"!"+token;
		}else if(a==2){
			stringed=request +"!"+data;
		}else if(a==3){
			stringed=request +"!"+data+"!"+email+"!"+token;
		}
		return stringed.getBytes(); 
	}
	public String getRequest(byte[] data1){
		String getText = new String(data1);
		String[] splitter=getText.split("!");
		this.request = splitter[0];
		return request;
	}
	public String getData(byte[] data1){
		String getText = new String(data1);
		String[] splitter=getText.split("!");
		this.data = splitter[1];
		return data;
	}
	public String getToken(byte[] data1){
		String getText = new String(data1);
		String[] splitter=getText.split("!");
		this.token = splitter[2];
		return token;
	}
	public String[] getLocalServerData(byte[] data1){
		String getText = new String(data1);
		String[] splitter=getText.split("!");
		return this.localServerData = splitter;
	}
}
