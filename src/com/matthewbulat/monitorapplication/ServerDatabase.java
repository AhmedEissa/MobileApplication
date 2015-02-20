package com.matthewbulat.monitorapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ServerDatabase extends SQLiteOpenHelper{
	
	// All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String LOGGIN_DETAILS = "Server";
    // Contacts table name
    private static final String DETAILS = "server_table";
    // Contacts Table Columns names
    private static final String SERVER = "server";
    
    public ServerDatabase(Context context) {
        super(context, LOGGIN_DETAILS, null, DATABASE_VERSION);
    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DETAILS_TABLE = "CREATE TABLE " + DETAILS + "("
                + SERVER + " TEXT PRIMARY KEY" + ")";
        db.execSQL(CREATE_DETAILS_TABLE);
    }
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DETAILS);
 
        // Create tables again
        onCreate(db);
	}
	public void addServer(String server) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(SERVER,server);
		db.insert(DETAILS, null, values);
	    db.close(); // Closing database connection
		
	}
	public DatabaseServ getServer(String server) {
	    SQLiteDatabase db = this.getReadableDatabase();
	 
	    Cursor cursor = db.query(DETAILS, new String[] { SERVER}, SERVER + "=?",
	            new String[] { String.valueOf(server) }, null, null, null, null);
	    if (cursor != null)
	        cursor.moveToFirst();
	 
	    DatabaseServ serverData = new DatabaseServ(cursor.getString(0));
	    // return user
	    return serverData;
	}
	public String TopRow(){
		SQLiteDatabase db = this.getReadableDatabase();
		String query="SELECT * FROM " + DETAILS + " LIMIT 1";
		Cursor cursor = db.rawQuery(query, null);
		 if (cursor != null)
		        cursor.moveToFirst();
		 String address = cursor.getString(0);
		cursor.close();
		return address;
		
	}
	public int getCount() {
        String countQuery = "SELECT  * FROM " + DETAILS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count =cursor.getCount();
        cursor.close();
 
        // return count
        return count;
    }
	public int updateServer(String server, String currentAddress) {
        SQLiteDatabase db = this.getWritableDatabase();
     
        ContentValues values = new ContentValues();
        values.put(SERVER, server);
     
        // updating row
        return db.update(DETAILS, values, SERVER + " = ?",
                new String[] { currentAddress });
        
    }
}
