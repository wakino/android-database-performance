package com.sakura_apps.obxperfcheck;

import android.content.Context;

import io.objectbox.BoxStore;

class ObjectBox {
	private static BoxStore boxStore;

	public static void init(Context context) {
		boxStore = MyObjectBox.builder()
				.androidContext(context.getApplicationContext())
				.build();
	}

	public static BoxStore get() { return boxStore; }

	public static void deleteAll(){
		if(!boxStore.isClosed()){
			boxStore.close();
		}
		boxStore.deleteAllFiles();
	}
}
