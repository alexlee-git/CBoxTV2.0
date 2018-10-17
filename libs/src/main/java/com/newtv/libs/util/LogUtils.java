package com.newtv.libs.util;

import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * 日志输出控制类 (Description)
 */
public class LogUtils {

	/** 日志输出级别V */
	public static final int LEVEL_VERBOSE = 5;
	/** 日志输出级别D */
	public static final int LEVEL_DEBUG = 4;
	/** 日志输出级别I */
	public static final int LEVEL_INFO = 3;
	/** 日志输出级别W */
	public static final int LEVEL_WARN = 2;
	/** 日志输出级别E */
	public static final int LEVEL_ERROR = 1;
	/** 日志输出级别NONE */
	public static final int LEVEL_NONE = 0;

	public static final int MAX_LENGTH = 2000;
	/** 日志输出时的TAG */
	private static String mTag = "CBoxTV";
	/** 是否允许输出log  默认是 0*/
	private static int mDebuggable = LEVEL_VERBOSE;

	/** 用于记时的变量 */
	private static long mTimestamp = 0;
	/** 写文件的锁对象 */
	@SuppressWarnings("unused")
	private static final Object mLogLock = new Object();

	/** 以级别为 d 的形式输出LOG */
	public static void v(String msg) {
		v(mTag,msg);
	}

	public static void v(String tag, String msg) {
		if (mDebuggable >= LEVEL_VERBOSE) {
			while(msg.length() > MAX_LENGTH){
				Log.v(tag, msg.substring(0,MAX_LENGTH));
				msg = msg.substring(MAX_LENGTH);
			}
			Log.v(tag, msg);
		}
	}

	/** 以级别为 d 的形式输出LOG */
	public static void d(String msg) {
		d(mTag,msg);
	}

	public static void d(String tag, String msg) {
		if (mDebuggable >= LEVEL_DEBUG) {
			while(msg.length() > MAX_LENGTH){
				Log.d(tag, msg.substring(0,MAX_LENGTH));
				msg = msg.substring(MAX_LENGTH);
			}
			Log.d(tag, msg);
		}
	}

	/** 以级别为 i 的形式输出LOG */
	public static void i(String msg) {
		i(mTag,msg);
	}

	public static void i(String tag, String msg) {
		if (mDebuggable >= LEVEL_INFO) {
			while(msg.length() > MAX_LENGTH){
				Log.i(tag, msg.substring(0,MAX_LENGTH));
				msg = msg.substring(MAX_LENGTH);
			}
			Log.i(tag, msg);
		}
	}

	/** 以级别为 w 的形式输出LOG */
	public static void w(String msg) {
		w(mTag,msg);
	}

	public static void w(String tag, String msg) {
		if (mDebuggable >= LEVEL_WARN) {
			while(msg.length() > MAX_LENGTH){
				Log.w(tag, msg.substring(0,MAX_LENGTH));
				msg = msg.substring(MAX_LENGTH);
			}
			Log.w(tag, msg);
		}
	}

	/** 以级别为 w 的形式输出Throwable */
	public static void w(Throwable tr) {
		w("",tr);
	}

	/** 以级别为 w 的形式输出LOG信息和Throwable */
	public static void w(String msg, Throwable tr) {
		if (mDebuggable >= LEVEL_WARN && null != msg) {
			Log.w(mTag, msg, tr);
		}
	}

	/** 以级别为 e 的形式输出LOG */
	public static void e(String msg) {
		e(mTag,msg);
	}

	public static void e(String tag, String msg) {
		if (mDebuggable >= LEVEL_ERROR) {
			while(msg.length() > MAX_LENGTH){
				Log.e(tag, msg.substring(0,MAX_LENGTH));
				msg = msg.substring(MAX_LENGTH);
			}
			Log.e(tag, msg);
		}
	}

	/** 以级别为 e 的形式输出Throwable */
	public static void e(Throwable tr) {
		e("",tr);
	}

	/** 以级别为 e 的形式输出LOG信息和Throwable */
	public static void e(String msg, Throwable tr) {
		if (mDebuggable >= LEVEL_ERROR && null != msg) {
			Log.e(mTag, msg, tr);
		}
	}

/*	*//**
	 * 把Log存储到文件中
	 * @param log  需要存储的日志
	 * @param path 存储路径
	 *//*
	public static void log2File(String log, String path) {
		log2File(log, path, true);
	}

	public static void log2File(String log, String path, boolean append) {
		synchronized (mLogLock) {
			FileUtils.writeFile(log + "\r\n", path, append);
		}
	}*/

	/**
	 * 以级别为 e 的形式输出msg信息,附带时间戳，用于输出一个时间段起始点
	 * @param msg 需要输出的msg
	 */
	public static void msgStartTime(String msg) {
		mTimestamp = System.currentTimeMillis();
		if (!TextUtils.isEmpty(msg)) {
			e("[Started：" + mTimestamp + "]" + msg);
		}
	}

	/** 以级别为 e 的形式输出msg信息,附带时间戳，用于输出一个时间段结束点* @param msg 需要输出的msg */
	public static void elapsed(String msg) {
		long currentTime = System.currentTimeMillis();
		long elapsedTime = currentTime - mTimestamp;
		mTimestamp = currentTime;
		e("[Elapsed：" + elapsedTime + "]" + msg);
	}

	public static <T> void printList(List<T> list) {
		if (list == null || list.size() < 1) {
			return;
		}
		int size = list.size();
		i("---begin---");
		for (int i = 0; i < size; i++) {
			i(i + ":" + list.get(i).toString());
		}
		i("---end---");
	}

	public static <T> void printArray(T[] array) {
		if (array == null || array.length < 1) {
			return;
		}
		int length = array.length;
		i("---begin---");
		for (int i = 0; i < length; i++) {
			i(i + ":" + array[i].toString());
		}
		i("---end---");
	}
}
