package com.sakura_apps.obxperfcheck;

import android.app.Application;
import android.util.Log;

public class MyApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("App", "Open DB");
	}
}
