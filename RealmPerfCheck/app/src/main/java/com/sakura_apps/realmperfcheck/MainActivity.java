package com.sakura_apps.realmperfcheck;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

	private final String TAG = "MA";

	private TestResult result = new TestResult();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		long t1, t2;
		String resultText;
		Realm realm;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.i("App", "Open DB");
		t1 = System.nanoTime();
		Realm.init(getApplicationContext());
		RealmConfiguration config = new RealmConfiguration.Builder().name("realmPerf.db").build();
		Realm.setDefaultConfiguration(config);
		realm = Realm.getDefaultInstance();
		t2 = System.nanoTime();
		result.loadTimeMsec = (t2-t1)/1000/1000;

		// unmask it to delete database
		//File f = new File(getApplicationContext().getFilesDir().getPath() + "/realmPerf.db");
		//f.delete();


		try{
			long count = realm.where(TestData.class).count();
			Log.i(TAG, String.format(Locale.US, "count:%d", count));
			if(count == 0){
				Log.i(TAG, "insert data");
				// insertData(10000); <== It makes large file (over 9MB). So separate the transaction into small piece
				int insertCount = 10;
				for(int i = 0; i < 10000; i += insertCount){
					insertData(i, insertCount);
				}
				resultText = "Test data is created.\nYou should reboot application\nInsert time:" + result.insertTime + "us";
			}
			else{
				for(int i = 0; i < result.selectTestCount; i++){
					selectData(i);
				}
				for(int i = 0; i < result.selectTestCount; i++) {
					selectLast100(i);
				}
				resultText = result.formatResults(getDbFileSize());
			}
		}
		catch (Exception exp){
			Log.e(TAG, exp.getMessage());
			resultText = ("Failed:" + exp.getMessage());
		}

		TextView t = findViewById(R.id.textview);
		t.setText(resultText);
	}

	private void insertData(int index, int dataCount){
		long t1,t2;
		Realm realm;
		t1 = System.nanoTime();
		realm = Realm.getDefaultInstance();
		try{
			realm.beginTransaction();
			for(int i = 1; i <= dataCount; i++){
				TestData data = new TestData();
				int idx = (index + i);
				data.setId(idx);
				data.setNormalNumber(idx);
				data.setIndexedNumber(idx);
				data.setNormalText(String.format(Locale.US,  "Normal_Text:%d", idx));
				data.setIndexedText(String.format(Locale.US, "IndexedText:%d", idx));
				realm.copyToRealm(data);
			}
			realm.commitTransaction();
		}
		catch (Exception ex){
			Log.e(TAG, "insertData exp:" + ex.getMessage());
			realm.cancelTransaction();
		}
		t2 = System.nanoTime();
		result.insertTime = (t2-t1);
	}

	private long checkPerformance(Realm realm, String property, long value){
		long t1= System.nanoTime();
		TestData data = realm.where(TestData.class).equalTo(property, value).findFirst();
		if(data == null){
			Log.e(TAG, "Data not found");
			return -1;
		}
		result.checkNumData += data.getNormalNumber();
		result.checkNumData += data.getIndexedNumber();
		long t2= System.nanoTime();
		return (t2-t1);
	}

	private long checkPerformance(Realm realm, String property, String value){
		long t1= System.nanoTime();
		TestData data = realm.where(TestData.class).equalTo(property, value).findFirst();
		if(data == null){
			Log.e(TAG, "Data not found");
			return -1;
		}
		result.checkTextData += data.getNormalText().length();
		result.checkTextData += data.getIndexedText().length();
		long t2= System.nanoTime();
		return (t2-t1);
	}


	private void selectData(int testNo){
		long t1, t2;

		t1 = System.nanoTime();
		Realm r = Realm.getDefaultInstance();
		t2 = System.nanoTime();
		result.selectResults.getTime[testNo] = (t2-t1);

		// Normal number
		result.selectResults.nnTime1[testNo] = checkPerformance(r, "normalNumber", 1);
		result.selectResults.nnTime5K[testNo] = checkPerformance(r, "normalNumber", 5000);
		result.selectResults.nnTime10K[testNo] = checkPerformance(r, "normalNumber", 10000);

		// Indexed number
		result.selectResults.inTime1[testNo] = checkPerformance(r, "indexedNumber", 1);
		result.selectResults.inTime5K[testNo] = checkPerformance(r, "indexedNumber", 5000);
		result.selectResults.inTime10K[testNo] = checkPerformance(r, "indexedNumber", 10000);

		// Normal text
		result.selectResults.ntTime1[testNo] = checkPerformance(r, "normalText", "Normal_Text:1");
		result.selectResults.ntTime5K[testNo] = checkPerformance(r, "normalText", "Normal_Text:5000");
		result.selectResults.ntTime10K[testNo] = checkPerformance(r, "normalText", "Normal_Text:10000");

		// Indexed text
		result.selectResults.itTime1[testNo] = checkPerformance(r, "indexedText", "IndexedText:1");
		result.selectResults.itTime5K[testNo] = checkPerformance(r, "indexedText", "IndexedText:5000");
		result.selectResults.itTime10K[testNo] = checkPerformance(r, "indexedText", "IndexedText:10000");
	}

	private void selectLast100(int testNo){
		long t1,t2,t3;

		t1 = System.nanoTime();
		Realm r = Realm.getDefaultInstance();
		RealmResults<TestData> list = r.where(TestData.class).greaterThan("normalNumber", 9900).findAll();
		t2 = System.nanoTime();
		for(TestData data : list){
			result.checkNumData += data.getNormalNumber();
			result.checkNumData += data.getIndexedNumber();
			result.checkTextData += data.getNormalText().length();
			result.checkTextData += data.getIndexedText().length();
		}
		t3 = System.nanoTime();

		result.last100Results.queryTime[testNo] = (t2-t1);
		result.last100Results.retrieveTime[testNo] = (t3-t2);
		result.last100Results.totalTime[testNo] = (t3-t1);
	}

	private long getDbFileSize(){
		File f = new File(getApplicationContext().getFilesDir().getPath() + "/realmPerf.db");
		return f.length();
	}
}
