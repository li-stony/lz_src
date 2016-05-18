package lz.mylife;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.io.Serializable;

import lz.util.LzLog;

/**
 * Created by cussyou on 2016-05-18.
 */
public class LocationService extends Service implements AMapLocationListener {

    private String TAG = this.getClass().getSimpleName();

    public static final String ACTION_LOCATION_CHANED = "loc_changed";

    private AMapLocationClientOption mLocationOption ;
    private AMapLocationClient mLocationClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setGpsFirst(true);
        mLocationOption.setInterval(5000);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationClient = new AMapLocationClient(this);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.setLocationListener(this);
    }
    public static void start(Context context) {
        Intent intent = new Intent();
        intent.setAction("start");
        intent.setClass(context, LocationService.class);
        context.startService(intent);
    }
    public static void stop(Context context) {
        Intent intent = new Intent();
        intent.setAction("stop");
        intent.setClass(context, LocationService.class);
        context.startService(intent);
    }
    @Override
    public void onStart(Intent intent, int startId) {
        handleCommand(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleCommand(intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    private void handleCommand(Intent intent) {
        String action = intent.getAction();
        LzLog.d(TAG, "received command: "+action);
        if(action.equals("start")){
            if(!mLocationClient.isStarted()) {
                mLocationClient.startLocation();
            }
        } else {
            if(mLocationClient.isStarted()) {
                mLocationClient.stopLocation();
            }
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        int err = aMapLocation.getErrorCode();
        LzLocation loc = new LzLocation();
        loc.err = err;
        loc.errMsg = aMapLocation.getErrorInfo();
        loc.address = aMapLocation.getAddress();
        loc.lat = aMapLocation.getLatitude();
        loc.lon = aMapLocation.getLongitude();
        Intent intent = new Intent();
        intent.setAction(ACTION_LOCATION_CHANED);
        intent.putExtra("loc", loc);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public class LzLocation implements Serializable {
        public int err;
        public String errMsg;
        public String address;
        public double lat;
        public double lon;

        @Override
        public String toString() {
            String fmt = "%d %s (%.4f, %.4f), %s";
            String str = String.format(fmt, err, errMsg, lat, lon, address);
            return str;
        }
    }
}
