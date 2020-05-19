package li.lingfeng.mxdanmaku.util;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by smallville on 2016/11/23.
 */
public class Logger {
    private static String TAG = "MX_Danmaku";

    public static void v(String msg) {
        Log.v(TAG, msg);
    }

    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void i(String msg) {
        Log.i(TAG, msg);
    }

    public static void w(String msg) {
        Log.w(TAG, msg);
    }

    public static void w(String msg, Throwable e) {
        Log.w(TAG, msg, e);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

    public static void e(String msg, Throwable e) {
        Log.e(TAG, msg, e);
    }

    public static void throwException(Throwable e) {
        e("throwException", e);
        throw new RuntimeException(e);
    }

    public static void stackTrace() {
        stackTrace("");
    }

    public static void stackTrace(String message) {
        Log.v(TAG, "[print stack] " + message);
        stackTrace(new Exception("[print stack] " + message));
    }

    public static void stackTrace(Throwable e) {
        if (e instanceof InvocationTargetException) {
            e = ((InvocationTargetException) e).getTargetException();
        }
        Log.e(TAG, Log.getStackTraceString(e));
    }
}
