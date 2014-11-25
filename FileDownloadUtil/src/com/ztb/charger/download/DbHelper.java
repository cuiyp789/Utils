package com.ztb.charger.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "download_file.db";

	public DbHelper(Context context) {
		super(context, DB_NAME, null, 1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table "+NetFile.TABLE+" ("+
				"id integer primary key autoincrement,"+
				NetFile.COL_url+" varchar(128),"+
				NetFile.COL_progress+" integer,"+
				NetFile.COL_state+" integer)");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion!=newVersion){
		    db.execSQL("DROP TABLE IF EXISTS "+NetFile.TABLE);
		    onCreate(db);
		}
	}
}