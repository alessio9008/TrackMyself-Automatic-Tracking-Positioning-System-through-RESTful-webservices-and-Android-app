package it.unict.dieei.semm.trackmyself.db;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract.Contacts.Data;

public class DataBaseAdapter {
	
	private SQLiteDatabase db=null;
		
	public SQLiteDatabase getDb() {
		return db;
	}

	public DataBaseAdapter(Context context) {
		db=new DataBaseHelper(context).getWritableDatabase();
	}
	
	private ContentValues insertValuePointer(long TimeStampInsert,double Latitude,double Longitude){
		ContentValues value=new ContentValues();
		value.put("timestampinsert", TimeStampInsert);
		value.put("latitude", Latitude);
		value.put("longitude", Longitude);
		return value;
	}
	
	/*
	 Metodi per l'inserimento dei punti geografici 
	 */
	
	public synchronized long insertPoint(long TimeStampInsert,double Latitude,double Longitude){
		return db.insert("pointer", null, insertValuePointer(TimeStampInsert,Latitude,Longitude));
	}
	
	public synchronized int updatePoint(long TimeStampInsert,double Latitude,double Longitude){
		return db.update("pointer", insertValuePointer(TimeStampInsert,Latitude,Longitude), "timestampinsert"+"=?" , new String[]{String.valueOf(TimeStampInsert)});
	}
	
	public synchronized int deletePoint(long TimeStampInsert){
		return db.delete("pointer", "timestampinsert"+"=?", new String[]{String.valueOf(TimeStampInsert)});
	}
	
	public synchronized int deleteAll(){
		return db.delete("pointer", null, null);
	}
	
	public synchronized Cursor returnAllPoints(){
		return db.query("pointer", null, null, null, null, null, null);
	}
	
	public void close(){
		db.close();
	}

}
