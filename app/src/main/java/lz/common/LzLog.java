package lz.common;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import lz.mylife.BuildConfig;

/**
 * Created by cussyou on 2016-05-17.
 */
public class LzLog {
    PrintWriter writer;
    String logFolder;
    private LzLog() {
        logFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "lzlog";
        File f = new File(logFolder);
        if(!f.exists()){
            f.mkdirs();
        }
        String fileName = new SimpleDateFormat("'LOG'_yyyyMMdd_HHmmss'.txt'")
                .format(System.currentTimeMillis());
        String path = logFolder + File.separator + fileName;
        File file = new File(path);
        try {
            writer = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void println(String tag, String msg) {
        if(BuildConfig.DEBUG) {
            String time = new SimpleDateFormat("yyyyMMdd-HHmmssSSS")
                    .format(System.currentTimeMillis());
            String fmt = "%s [%s](%s) %s";
            String line = String.format(fmt, time, tag, Thread.currentThread().getName(), msg);
            writer.println(line);
            writer.flush();
        }
    }
    private void println(String tag, String msg, Throwable e) {
        if(BuildConfig.TEST_ENABLED) {
            String time = new SimpleDateFormat("yyyyMMdd-HHmmssSSS")
                    .format(System.currentTimeMillis());
            String fmt = "%s [%s](%s) %s";
            String line = String.format(fmt, time, tag, Thread.currentThread().getName(), msg);
            writer.println(line);
            e.printStackTrace(writer);
            writer.flush();
        }
    }
    private void close() {
        if(writer != null) {
            writer.close();
            writer = null;
        }
    }
    private static LzLog lzLog;
    public static void createInstance() {
        if(BuildConfig.TEST_ENABLED) {
            lzLog = new LzLog();
            lzLog.println("LOG", "-- start --");
        }
    }
    public static void destroyInstance() {
        if(BuildConfig.TEST_ENABLED){
            lzLog.println("LOG", "-- end --");
            lzLog.close();
        }
    }

    public static void d(String tag, String text) {
        Log.d(tag, text);
        lzLog.println(tag,text);
    }
    public static void e(String tag, String msg) {
        Log.e(tag, msg);
        lzLog.println(tag,msg);
    }
    public static void e(String tag, String msg, Throwable e) {
        Log.e(tag, msg, e);
        lzLog.println(tag, msg, e);
    }

}
