package com.sakura_apps.sqliteperfcheck;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpener extends SQLiteOpenHelper {
	private static final String TAG = "DbOpener";
	private static final String DATABASE_NAME = "test.db";
	private static final String SCHEDULE_TABLE_NAME = "TestData";
	private static final int DATABASE_VERSION = 1;

	public DbOpener(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		TestData.createTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + SCHEDULE_TABLE_NAME + ";");
		onCreate(db);
	}
}
