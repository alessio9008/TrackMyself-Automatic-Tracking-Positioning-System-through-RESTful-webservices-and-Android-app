package it.unict.dieei.semm.trackmyself.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "traceability";
	public static final int DB_VERSION = 1;

	private static final String CREATE_TABLE_POINTER = "CREATE TABLE `pointer` ("
			+ "  `timestampinsert` bigint(20) NOT NULL,"
			+ "`latitude` double NOT NULL,"
			+ "`longitude` double NOT NULL,"
			+ "PRIMARY KEY (`timestampinsert`) );";


	
	public DataBaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		arg0.execSQL(CREATE_TABLE_POINTER);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
