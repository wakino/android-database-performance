package com.sakura_apps.obxperfcheck;
import java.util.Locale;

class TestResult {
	public class SelectResult{
		// For easy min/max/ave calculation, data member should be long[] not SelectResult[]
		long[] getTime = new long[selectTestCount];
		long[] nnTime1 = new long[selectTestCount];
		long[] nnTime5K = new long[selectTestCount];
		long[] nnTime10K = new long[selectTestCount];
		long[] inTime1 = new long[selectTestCount];
		long[] inTime5K = new long[selectTestCount];
		long[] inTime10K = new long[selectTestCount];
		long[] ntTime1 = new long[selectTestCount];
		long[] ntTime5K = new long[selectTestCount];
		long[] ntTime10K = new long[selectTestCount];
		long[] itTime1 = new long[selectTestCount];
		long[] itTime5K = new long[selectTestCount];
		long[] itTime10K = new long[selectTestCount];
	}

	public class Last100Result{
		long[] queryTime = new long[selectTestCount];
		long[] retrieveTime = new long[selectTestCount];
		long[] totalTime = new long[selectTestCount];
	}

	final int selectTestCount = 100;
	//	final int selectTestCount = 10000;
	private long startTime;
	long insertTime;
	long loadTimeMsec;
	long checkNumData = 0;
	long checkTextData = 0;
	SelectResult selectResults = new SelectResult();
	Last100Result last100Results = new Last100Result();

	TestResult(){
		startTime = System.nanoTime();
	}

	String formatResults(long dbFileSize){
		long endTime = System.nanoTime();
		long procMsec = (endTime - startTime)/1000/1000;
		String results = "TEST RESULT\n\n";
		results += String.format(Locale.US, "init time: %d msec\n", loadTimeMsec);
		results += String.format(Locale.US, "total time: %d msec\n", procMsec);
		results += String.format(Locale.US, "test count: %d\n", selectTestCount);
		results += "\n--- test item : min, max, ave (usec) ---\n";
		results += formatResult("Object get", this.selectResults.getTime);
		results += formatResult("NN 1  ", this.selectResults.nnTime1);
		results += formatResult("NN 5K ", this.selectResults.nnTime5K);
		results += formatResult("NN 10K", this.selectResults.nnTime10K);
		results += formatResult("IN 1  ", this.selectResults.inTime1);
		results += formatResult("IN 5K ", this.selectResults.inTime5K);
		results += formatResult("IN 10K", this.selectResults.inTime10K);

		results += formatResult("NT 1  ", this.selectResults.ntTime1);
		results += formatResult("NT 5K ", this.selectResults.ntTime5K);
		results += formatResult("NT 10K", this.selectResults.ntTime10K);
		results += formatResult("IT 1  ", this.selectResults.itTime1);
		results += formatResult("IT 5K ", this.selectResults.itTime5K);
		results += formatResult("IT 10K", this.selectResults.itTime10K);
		results += "\n--- Last 100 (usec) ---\n";
		results += formatResult("Query   ", this.last100Results.queryTime);
		results += formatResult("Retrieve", this.last100Results.retrieveTime);
		results += formatResult("Total   ", this.last100Results.totalTime);

		results += String.format(Locale.US, "\nfile size: %d bytes\n\n", dbFileSize);

		results += String.format(Locale.US, "checkNumData : %d\n", checkNumData );
		results += String.format(Locale.US, "checkTextData : %d\n", checkTextData );
		return results;
	}

	private String formatResult(String caption, long[] results){
		long min = Long.MAX_VALUE;
		long max = 0;
		long ave = 0;
		for(long r : results){
			if(r < min){
				min = r;
			}
			if(r > max){
				max = r;
			}
			ave += r;
		}
		ave /= results.length;
		return String.format(Locale.US, "%s:  %d,  %d,  %d\n", caption, min/1000, max/1000, ave/1000);
	}


}
