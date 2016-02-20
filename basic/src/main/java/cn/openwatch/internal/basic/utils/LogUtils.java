package cn.openwatch.internal.basic.utils;

import android.util.Log;

public final class LogUtils {
	private static boolean DEBUG;

	private LogUtils() {
	}

	public static void setEnableLog(boolean dEBUG) {
		DEBUG = dEBUG;
	}

	private static void log(String tag, String msg) {
		int maxLogSize = 1000;

		int len = msg.length();
		int line = len / maxLogSize;

		for (int i = 0; i <= line; i++) {

			int start = i * maxLogSize;

			int end = (i + 1) * maxLogSize;

			end = end > len ? len : end;

			Log.d(tag, msg.substring(start, end));

		}
	}

	public static void d(String tag, String msg) {
		if (DEBUG)
			log(tag, msg);
	}

	public static void d(Class<?> cls, String msg) {
		d(cls.getSimpleName(), msg);
	}

	public static void d(Object obj, String msg) {
		d(obj.getClass(), msg);
	}

}
