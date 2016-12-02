package lz.common;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by cussyou on 2016-06-07.
 */
public class LzExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Context context;
    private String logFolder;
    public LzExceptionHandler (Context context) {
        this.context = context;
        logFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "lzlog";
        File f = new File(logFolder);
        if(!f.exists()){
            f.mkdirs();
        }
    }
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        LzLog.e("err", context.getPackageName() + " crashed");
        try {
            String fileName = new SimpleDateFormat("'ERR'_yyyyMMdd_HHmmss'.txt'")
                    .format(System.currentTimeMillis());
            String path = logFolder + File.separator + fileName;
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
