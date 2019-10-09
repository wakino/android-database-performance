package com.sakura_apps.obxperfcheck;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.util.List;
import java.util.Locale;
import io.objectbox.Box;
import io.objectbox.Property;


public class MainActivity extends AppCompatActivity {

	private final String TAG = "MA";
	private TestResult result = new TestResult();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String resultText;
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		try {
			long t1, t2, count;
			t1 = System.nanoTime();
			ObjectBox.init(this);
			t2 = System.nanoTime();
			result.loadTimeMsec = (t2 - t1) / 1000 / 1000;

			// Uncomment if you want to delete DB file
			//ObjectBox.deleteAll();

			count = ObjectBox.get().boxFor(TestData.class).count();
			Log.i(TAG, String.format(Locale.US, "count:%d", count));
			if (count == 0) {
				Log.i(TAG, "insert data");
				insertData();
				resultText = "Test data is created.\nYou should reboot application\nInsert time:" + result.insertTime / 1000 + "us";
			} else {
				for (int i = 0; i < result.selectTestCount; i++) {
					selectData(i);
				}
				for (int i = 0; i < result.selectTestCount; i++) {
					selectLast100(i);
				}
				try {
					String filePath = getApplicationContext().getFilesDir().getPath() + "/objectbox/objectbox/data.mdb";
					File f = new File(filePath);
					resultText = result.formatResults(f.length());
				} catch (Exception ex) {
					Log.e(TAG, "File exp:" + ex.getMessage());
					resultText = "Error" + ex.getMessage();
				}

			}
		} catch (Exception exp) {
			Log.e(TAG, exp.getMessage());
			resultText = "Error:" + exp.getMessage();
		}
		TextView t = findViewById(R.id.textview);
		t.setText(resultText);
	}

	private void insertData() {
		long t1 = System.nanoTime();
		ObjectBox.get().runInTx(new Runnable() {
			@Override
			public void run() {
				Box<TestData> dataBx = ObjectBox.get().boxFor(TestData.class);
				int dataCount = 10000;

				for (int i = 1; i <= dataCount; i++) {
					TestData data = new TestData();
					data.normalNumber = i;
					data.indexedNumber = i;
					data.normalText = String.format(Locale.US, "Normal_Text:%d", i);
					data.indexedText = String.format(Locale.US, "IndexedText:%d", i);
					dataBx.put(data);
				}
			}
		});
		long t2 = System.nanoTime();
		result.insertTime = (t2 - t1);
	}

	private long checkPerformance(Box<TestData> dataBx, Property<TestData> property, long value) {
		long t1 = System.nanoTime();
		TestData data = dataBx.query().equal(property, value).build().findFirst();
		if (data == null) {
			Log.e(TAG, "Data not found");
			return -1;
		}
		result.checkNumData += data.normalNumber;
		result.checkNumData += data.indexedNumber;
		long t2 = System.nanoTime();
		return (t2 - t1);
	}

	private long checkPerformance(Box<TestData> dataBx, Property<TestData> property, String value) {
		long t1 = System.nanoTime();
		TestData data = dataBx.query().equal(property, value).build().findFirst();
		if (data == null) {
			Log.e(TAG, "Data not found");
			return -1;
		}
		result.checkTextData += data.normalText.length();
		result.checkTextData += data.indexedText.length();
		long t2 = System.nanoTime();
		return (t2 - t1);
	}

	private void selectData(int testNo) {
		try {
			long t1, t2;
			t1 = System.nanoTime();
			Box<TestData> dataBx = ObjectBox.get().boxFor(TestData.class);
			t2 = System.nanoTime();
			result.selectResults.getTime[testNo] = (t2 - t1);

			// Normal number
			result.selectResults.nnTime1[testNo] = checkPerformance(dataBx, TestData_.normalNumber, 1);
			result.selectResults.nnTime5K[testNo] = checkPerformance(dataBx, TestData_.normalNumber, 5000);
			result.selectResults.nnTime10K[testNo] = checkPerformance(dataBx, TestData_.normalNumber, 10000);

			// Indexed number
			result.selectResults.inTime1[testNo] = checkPerformance(dataBx, TestData_.indexedNumber, 1);
			result.selectResults.inTime5K[testNo] = checkPerformance(dataBx, TestData_.indexedNumber, 5000);
			result.selectResults.inTime10K[testNo] = checkPerformance(dataBx, TestData_.indexedNumber, 10000);

			// Normal text
			result.selectResults.ntTime1[testNo] = checkPerformance(dataBx, TestData_.normalText, "Normal_Text:1");
			result.selectResults.ntTime5K[testNo] = checkPerformance(dataBx, TestData_.normalText, "Normal_Text:5000");
			result.selectResults.ntTime10K[testNo] = checkPerformance(dataBx, TestData_.normalText, "Normal_Text:10000");

			// Indexed text
			result.selectResults.itTime1[testNo] = checkPerformance(dataBx, TestData_.indexedText, "IndexedText:1");
			result.selectResults.itTime5K[testNo] = checkPerformance(dataBx, TestData_.indexedText, "IndexedText:5000");
			result.selectResults.itTime10K[testNo] = checkPerformance(dataBx, TestData_.indexedText, "IndexedText:10000");
		} catch (Exception ex) {
			Log.e(TAG, "exp:" + ex.getMessage());
		}
	}

	private void selectLast100(int testNo) {
		long t1, t2, t3;

		t1 = System.nanoTime();
		Box<TestData> dataBx = ObjectBox.get().boxFor(TestData.class);
		List<TestData> list = dataBx.query().greater(TestData_.normalNumber, 9900).build().find();
		t2 = System.nanoTime();
		for (TestData data : list) {
			result.checkNumData += data.normalNumber;
			result.checkNumData += data.indexedNumber;
			result.checkTextData += data.normalText.length();
			result.checkTextData += data.indexedText.length();
		}
		t3 = System.nanoTime();
		result.last100Results.queryTime[testNo] = (t2 - t1);
		result.last100Results.retrieveTime[testNo] = (t3 - t2);
		result.last100Results.totalTime[testNo] = (t3 - t1);
	}
}

