package com.nikhil.neom;

import android.util.Log;

public class LogCat {
	public static String tag = "NEOM";
	public static boolean enabled = true;

	public static void d(String msg) {
		d(tag, msg);
	}

	public static void d(String tag, String msg) {
		if (!enabled) {
			return;
		}

		for (String line : msg.split("\n")) {
			Log.d(tag, line);
		}
	}

}