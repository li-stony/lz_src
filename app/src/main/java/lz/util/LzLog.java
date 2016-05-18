package lz.util;

import android.util.Log;

/**
 * Created by cussyou on 2016-05-17.
 */
public class LzLog {
    public static void d(String tag, String text) {
        Log.d(tag, text);
    }
    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }
    public static void e(String tag, String msg, Throwable e) {
        Log.e(tag, msg, e);
    }
}
