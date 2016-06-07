package lz.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;

import lz.util.LzLog;

/**
 * Created by cussyou on 2016-06-07.
 */
public class LzExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Context context;
    public LzExceptionHandler (Context context) {
        this.context = context;
    }
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        LzLog.e("err", context.getPackageName() + " crashed");
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());

            String fileName = "err_" + System.currentTimeMillis() + ".log";
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "lzlog" + File.separator + fileName;
            File file = new File(path);
            PrintWriter printWriter = new PrintWriter(file);
            printWriter.println("In thread:"+thread.getName()+"-"+thread.getId()+" crashed");
            printWriter.println(ex.toString());
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            ex.printStackTrace(printWriter);

            printWriter.close();
        } catch (Exception e) {
            LzLog.e("err", e.toString(), e);
        }
        System.exit(-1);
    }
}
