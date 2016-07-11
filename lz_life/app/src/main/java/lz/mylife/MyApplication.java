package lz.mylife;

        import android.app.Application;

        import com.autonavi.indoor.constant.Configuration;

        import lz.common.LzExceptionHandler;
        import lz.common.LzLog;
        import lz.mylife.cal.YahooWeatherCode;
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
    @Override
    public void onTerminate() {
        LzLog.destroyInstance();
        super.onTerminate();

    }
}
