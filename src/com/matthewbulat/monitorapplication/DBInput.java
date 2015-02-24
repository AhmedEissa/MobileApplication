package com.matthewbulat.monitorapplication;

public class DBInput {
	//Variables
	String _email;
	String _token;
	
	// Empty constructor
	public DBInput(){
		
	}
	//Constructor
	public DBInput(String email, String token){
		this._email=email;
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
	//getting token
	public String getToken(){
		return this._token;
	}
	//Setting token
	public void setToken(String token){
		this._token=token;
	}
	
}
