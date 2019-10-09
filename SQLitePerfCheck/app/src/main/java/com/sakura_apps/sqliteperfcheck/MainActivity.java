package com.sakura_apps.sqliteperfcheck;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

	private final String TAG = "MA";
	private SQLiteDatabase db;
	private TestResult result = new TestResult();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String resultText;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try{
			long t1, t2, count;

			// Uncomment if you want to delete DB file
			//File f = getApplicationContext().getDatabasePath("test.db");
			//f.delete();

			t1 = System.nanoTime();
			DbOpener opener = new DbOpener(getApplicationContext());
			db = opener.getWritableDatabase();
			t2 = System.nanoTime();
			result.loadTimeMsec = (t2 - t1);

			count = TestData.getCount(db);
			Log.i(TAG, String.format(Locale.US, "count:%d", count));
			if(count == 0){
				Log.i(TAG, "insert data");
				insertData(db);
				resultText = "Test data is created.\nYou should reboot application\nInsert time:" + result.insertTime / 1000 + "us";
			}
			else{
				for(int i = 0; i < result.selectTestCount; i++){
					selectData(db, i);
				}
				for(int i = 0; i < result.selectTestCount; i++) {
					selectLast100(db, i);
				}
				resultText = result.formatResults(getDbSize());
			}
		}
		catch (Exception exp){
			Log.e(TAG, exp.getMessage());
			resultText = ("Failed:" + exp.getMessage());
		}

		TextView t = findViewById(R.id.textview);
		t.setText(resultText);

		db.close();
	}

	private void insertData(SQLiteDatabase db){
		long t1 = System.nanoTime();
		try{
			db.beginTransaction();
			int dataCount = 10000;
			for(int i = 1; i <= dataCount; i++){
				TestData data = new TestData();
				data.id = i;
				data.normalNumber = i;
				data.indexedNumber = i;
				data.normalText = String.format(Locale.US,  "Normal_Text_%d", i);
				data.indexedText = String.format(Locale.US, "IndexedText_%d", i);
				TestData.insert(db, data);
			}
			db.setTransactionSuccessful();
		}
		catch (Exception ex){
			Log.e(TAG, "insertData exp:" + ex.getMessage());
		}
		finally {
			db.endTransaction();
		}
		long t2 = System.nanoTime();
		result.insertTime = (t2 - t1);
	}


	private long checkPerformance(SQLiteDatabase db, String property, long value) {
		long t1 = System.nanoTime();
		TestData data = TestData.selectByNumber(db, property, value);
		if (data == null) {
			Log.e(TAG, "Data not found");
			return -1;
		}
		result.checkNumData += data.normalNumber;
		result.checkNumData += data.indexedNumber;
		long t2 = System.nanoTime();
		return (t2 - t1);
	}

	private long checkPerformance(SQLiteDatabase db, String property, String value) {
		long t1 = System.nanoTime();
		TestData data = TestData.selectByText(db, property, value);
		if (data == null) {
			Log.e(TAG, "Data not found");
			return -1;
		}
		result.checkTextData += data.normalText.length();
		result.checkTextData += data.indexedText.length();
		long t2 = System.nanoTime();
		return (t2 - t1);
	}

	private void selectData(SQLiteDatabase db, int testNo){

		result.selectResults.getTime[testNo] = 0;   // No instance get time needed for SQLite

		// Normal number
		result.selectResults.nnTime1[testNo] = checkPerformance(db, "normalNumber", 1);
		result.selectResults.nnTime5K[testNo] = checkPerformance(db, "normalNumber", 5000);
		result.selectResults.nnTime10K[testNo] = checkPerformance(db, "normalNumber", 10000);

		// Indexed number
		result.selectResults.inTime1[testNo] = checkPerformance(db, "indexedNumber", 1);
		result.selectResults.inTime5K[testNo] = checkPerformance(db, "indexedNumber", 5000);
		result.selectResults.inTime10K[testNo] = checkPerformance(db, "indexedNumber", 10000);

		// Normal text
		result.selectResults.ntTime1[testNo] = checkPerformance(db, "normalText", "Normal_Text_1");
		result.selectResults.ntTime5K[testNo] = checkPerformance(db, "normalText", "Normal_Text_5000");
		result.selectResults.ntTime10K[testNo] = checkPerformance(db, "normalText", "Normal_Text_10000");

		// Indexed text
		result.selectResults.itTime1[testNo] = checkPerformance(db, "indexedText", "IndexedText_1");
		result.selectResults.itTime5K[testNo] = checkPerformance(db, "indexedText", "IndexedText_5000");
		result.selectResults.itTime10K[testNo] = checkPerformance(db, "indexedText", "IndexedText_10000");
	}

	private void selectLast100(SQLiteDatabase db, int testNo){
		long t1, t2, t3;

		t1 = System.nanoTime();
		ArrayList<TestData> list = TestData.selectGraterThanByNN(db, 9900);
		t2 = System.nanoTime();
		for(TestData data : list){
			result.checkNumData += data.normalNumber;
			result.checkNumData += data.indexedNumber;
			result.checkTextData += data.normalText.length();
			result.checkTextData += data.indexedText.length();
		}
		t3 = System.nanoTime();

		result.last100Results.queryTime[testNo] = (t2-t1);
		result.last100Results.retrieveTime[testNo] = (t3-t2);
		result.last100Results.totalTime[testNo] = (t3-t1);
	}

	private long getDbSize(){
		try{
			File f = getApplicationContext().getDatabasePath("test.db");
			return f.length();
		}
		catch (Exception ex){
			Log.e(TAG, "File exp:" + ex.getMessage());
		}
		return -1;
	}

}
