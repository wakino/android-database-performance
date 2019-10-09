package com.sakura_apps.sqliteperfcheck;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;


class TestData {
	long id;
	int normalNumber;
	int indexedNumber;
	String normalText;
	String indexedText;

	static long getCount(SQLiteDatabase db){
		return DatabaseUtils.queryNumEntries(db, "TestData");
	}

	static void createTable(SQLiteDatabase db){
		String sql = "CREATE TABLE TestData ("
				+ " id INTEGER PRIMARY KEY,"
				+ " normalNumber INTEGER,"
				+ " indexedNumber INTEGER,"
				+ " normalText TEXT,"
				+ " indexedText TEXT"
				+ ");";
		db.execSQL(sql);

		sql = "CREATE INDEX numIndex ON TestData(indexedNumber)";
		db.execSQL(sql);

		sql = "CREATE INDEX textIndex ON TestData(indexedText)";
		db.execSQL(sql);
	}

	static void insert(SQLiteDatabase db, TestData d){
		Object[] values = {d.id, d.normalNumber, d.indexedNumber, d.normalText, d.indexedText};
		db.execSQL("INSERT INTO TestData VALUES(?,?,?,?,?)", values );

		/*
		String sql = String.format(Locale.US, "INSERT INTO TestData VALUES(%d,%d,%d,'%s','%s')", d.id, d.normalNumber, d.indexedNumber, d.normalText, d.indexedText);
		db.execSQL(sql);
		*/
	}

	static TestData selectByNumber(SQLiteDatabase db, String property, long value){
		Cursor c = null;
		try {
			String q = "SELECT * FROM TestData WHERE " + property + " = ?;";
			c = db.rawQuery(q, new String[]{Long.toString(value)});
			c.moveToFirst();
			return getData(c);
		}
		catch (Exception ex){
			Log.e("TD", "selectByNumber failed" +  ex.getMessage());
			return null;
		}
		finally {
			if(c != null){
				c.close();
			}
		}
	}

	static TestData selectByText(SQLiteDatabase db, String property, String value){
		Cursor c = null;
		try {
			String q = "SELECT * FROM TestData WHERE " + property + " = ?;";
			c = db.rawQuery(q, new String[]{value});
			c.moveToFirst();
			return getData(c);
		}
		catch (Exception ex){
			Log.e("TD", "selectByText failed" +  ex.getMessage());
			return null;
		}
		finally {
			if(c != null){
				c.close();
			}
		}
	}

	static ArrayList<TestData> selectGraterThanByNN(SQLiteDatabase db, int number){
		Cursor c = null;
		try{
			c = db.rawQuery("SELECT * FROM TestData WHERE normalNumber > ?;", new String[]{Integer.toString(number) });
			c.moveToFirst();
			ArrayList<TestData> list = new ArrayList<>();
			for (int i = 0; i < c.getCount(); i++) {
				TestData d = getData(c);
				list.add(d);
				c.moveToNext();
			}
			return list;
		}
		catch (Exception ex){
			Log.e("TD", "selectGraterThanByNN failed" +  ex.getMessage());
			return null;
		}
		finally {
			if(c != null){
				c.close();
			}
		}
	}

	private static TestData getData(Cursor c){
		TestData data = new TestData();
		if(c.getCount() > 0){
			data.id = c.getLong(0);
			data.normalNumber = c.getInt(1);
			data.indexedNumber = c.getInt(2);
			data.normalText = c.getString(3);
			data.indexedText = c.getString(4);
			return data;
		}
		else{
			Log.e("TD", "Not found");
			return null;
		}
	}
}
