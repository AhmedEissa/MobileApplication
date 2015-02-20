package com.matthewbulat.monitorapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper{
	
	
	// All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String LOGGIN_DETAILS = "loggin_details";
    // Contacts table name
    private static final String DETAILS = "details";
    // Contacts Table Columns names
    private static final String EMAIL_ID = "id";
    private static final String PASSWORD = "password";
    private static final String TOKEN = "token";
    
    public Database(Context context) {
        super(context, LOGGIN_DETAILS, null, DATABASE_VERSION);
    }
    
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DETAILS_TABLE = "CREATE TABLE " + DETAILS + "("
                + EMAIL_ID + " TEXT PRIMARY KEY," + PASSWORD + " BLOB,"
                + TOKEN + " TEXT" + ")";
        db.execSQL(CREATE_DETAILS_TABLE);
    }
    // Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DETAILS);
 
        // Create tables again
        onCreate(db);
		
	}
	  // Adding new user
	public void addUser(DBInput dbinput) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(EMAIL_ID,dbinput.getEmail());
	    values.put(PASSWORD, dbinput.getPass()); // user password
	    values.put(TOKEN, dbinput.getToken()); // user token
	 
	    // Inserting Row
	    db.insert(DETAILS, null, values);
	    db.close(); // Closing database connection
	}
	 // Getting single user
	public DBInput getUser(String email) {
	    SQLiteDatabase db = this.getReadableDatabase();
	 
	    Cursor cursor = db.query(DETAILS, new String[] { EMAIL_ID,
	            PASSWORD, TOKEN }, EMAIL_ID + "=?",
	            new String[] { String.valueOf(email) }, null, null, null, null);
	    if (cursor != null)
	        cursor.moveToFirst();
	 
	    DBInput user = new DBInput(cursor.getString(0), cursor.getBlob(1), cursor.getString(2));
	    cursor.close();
	    db.close();
	    // return user
	    return user;
	}
	public DBInput TopRow(){
		SQLiteDatabase db = this.getReadableDatabase();
		String query="SELECT * FROM " + DETAILS + " LIMIT 1";
		Cursor cursor = db.rawQuery(query, null);
		 if (cursor != null)
		        cursor.moveToFirst();
		DBInput user = new DBInput(cursor.getString(0), cursor.getBlob(1), cursor.getString(2));
		cursor.close();
		db.close();
		return user;
		
	}
	// Getting contacts Count
    public int getCount() {
        String countQuery = "SELECT  * FROM " + DETAILS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count =cursor.getCount();
        cursor.close();
        db.close();
        // return count
        return count;
    }
 // Updating single user
    public void updateUser(DBInput user) {
        SQLiteDatabase db = this.getWritableDatabase();
     
        ContentValues values = new ContentValues();
        values.put(PASSWORD, user.getPass());
        values.put(TOKEN, user.getToken());
        
        // updating row
        db.update(DETAILS, values, EMAIL_ID + " = ?",
                new String[] { String.valueOf(user.getEmail()) });
        db.close();
        
        
    }
    // Deleting single user
    public void deleteUser(DBInput user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DETAILS, EMAIL_ID + " = ?",
                new String[] { String.valueOf(user.getEmail()) });
        db.close();
    }
    public void deleteToken(DBInput user){
    	SQLiteDatabase db = this.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put(TOKEN, "null");
    	db.update(DETAILS, values, EMAIL_ID + " = ?",
                new String[] { String.valueOf(user.getEmail()) });
    	db.close();
    }
    public void updateEmail(String newUser,DBInput user){
    	SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EMAIL_ID, newUser);
        db.update(DETAILS, values, EMAIL_ID + " = ?",
                new String[] { String.valueOf(user.getEmail()) });
        db.close();
    }
    public void updatePassword(String newPassword,DBInput user){
    	SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PASSWORD, newPassword);
        db.update(DETAILS, values, EMAIL_ID + " = ?",
                new String[] { String.valueOf(user.getEmail()) });
        db.close();
    }
			
	
}
