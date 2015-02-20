package com.matthewbulat.monitorapplication;

public class DBInput {
	//Variables
	String _email;
	byte[] _password;
	String _token;
	
	// Empty constructor
	public DBInput(){
		
	}
	//Constructor
	public DBInput(String email,byte[] pass, String token){
		this._email=email;
		this._password=pass;
		this._token=token;
		
	}
	//getting email
	public String getEmail(){
		return this._email;
	}
	//Setting email
	public void setEmail(String email){
		this._email=email;
	}
	//getting password
	public byte[] getPass(){
		return this._password;
	}
	//Setting password
	public void setPass(byte[] pass){
		this._password=pass;
	}
	//getting token
	public String getToken(){
		return this._token;
	}
	//Setting token
	public void setToken(String token){
		this._token=token;
	}
	
}
