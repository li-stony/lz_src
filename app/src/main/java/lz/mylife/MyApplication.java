package lz.mylife;

import android.app.Application;

import lz.common.LzExceptionHandler;
import lz.common.LzLog;
import lz.util.LzGlobalStates;

/**
 * Created by cussyou on 2016-06-06.
 */
public class MyApplication extends Application{
    public void onCreate() {
        super.onCreate();
        LzGlobalStates.globalContext = this.getApplicationContext();
        Thread.setDefaultUncaughtExceptionHandler(new LzExceptionHandler(this.getApplicationContext()));
        LzLog.createInstance();
    }
    public void onTerminate() {
        super.onTerminate();
        LzLog.destroyInstance();
    }
}
