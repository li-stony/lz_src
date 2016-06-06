package lz.mylife.cal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import lz.util.LzLog;

/**
 * Created by cussyou on 2016-05-18.
 */
public class LocationService extends Service implements AMapLocationListener {

    private String TAG = this.getClass().getSimpleName();

    public static final String ACTION_LOCATION_CHANED = "loc_changed";

    private AMapLocationClientOption mLocationOption ;
    private AMapLocationClient mLocationClient;

    private static final String ACTION_START = "start";
    private static final String ACTION_STOP = "stop";
    private static final String ACTION_ADD_CALENDAR_EVENT = "start_pin";

    private String lastCmd = "";

    private AtomicInteger calEventFlag = new AtomicInteger(0);
    private AtomicInteger serviceCnt = new AtomicInteger(0);


    @Override
    public void onCreate() {
        super.onCreate();
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setGpsFirst(true);
        mLocationOption.setInterval(10000);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        mLocationClient = new AMapLocationClient(this);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.setLocationListener(this);
    }
    public static void start(Context context) {
        Intent intent = new Intent();
        intent.setAction(ACTION_START);
        intent.setClass(context, LocationService.class);
        context.startService(intent);
    }
    public static void startPinWeatherEvent(Context context) {
        Intent intent = new Intent();
        intent.setAction(ACTION_ADD_CALENDAR_EVENT);
        intent.setClass(context, LocationService.class);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent();
        intent.setAction(ACTION_STOP);
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
        if(intent == null) {
            return;
        }
        String action = intent.getAction();
        LzLog.d(TAG, "received command: "+action);
        if(action.equals(ACTION_START)
                ||action.equals(ACTION_ADD_CALENDAR_EVENT)){
            int cnt = serviceCnt.addAndGet(1);
            LzLog.d(TAG, "LocationService start "+cnt);
            if(!mLocationClient.isStarted()) {
                mLocationClient.startLocation();
            }
            if(action.equals(ACTION_ADD_CALENDAR_EVENT)){
                calEventFlag.set(1);
            }
        } else {
            int cnt = serviceCnt.addAndGet(-1);
            if(cnt == 0) {
                LzLog.d(TAG, "LocationService stop...");
                if(mLocationClient.isStarted()) {
                    mLocationClient.stopLocation();
                }
                stopSelf();
            }

        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    int weatherCnt = 0;
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        int err = aMapLocation.getErrorCode();
        LzLocation loc = new LzLocation();
        loc.err = err;
        loc.errMsg = aMapLocation.getErrorInfo();
        loc.address = aMapLocation.getAddress();
        loc.lat = aMapLocation.getLatitude();
        loc.lon = aMapLocation.getLongitude();
        loc.city = aMapLocation.getCity();
        loc.province = aMapLocation.getProvince();
        loc.country = aMapLocation.getCountry();
        Intent intent = new Intent();
        intent.setAction(ACTION_LOCATION_CHANED);
        intent.putExtra("loc", loc);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        if(err == 0) {
            if(calEventFlag.compareAndSet(1, 0)){
                WeatherService.fetchPredictWeather(this.getApplicationContext(), loc);
                LocationService.stop(getApplicationContext());
            } else {
                if (weatherCnt % 720 == 0) {
                    WeatherService.fetchLiveWeather(this.getApplicationContext(), loc);
                }
                weatherCnt++;

            }
        } else {
            LzLog.e(TAG, "got err: "+err);
        }
    }

    public static class LzLocation implements Parcelable {
        public int err;
        public String errMsg;
        public String address;
        public double lat;
        public double lon;
        public String city;
        public String province;
        public String country;

        @Override
        public String toString() {
            String fmt = "%d %s (%.4f, %.4f), %s";
            String str = String.format(fmt, err, errMsg, lat, lon, address);
            return str;
        }
        public String toAddressString() {
            String fmt = "%s (%.4f, %.4f)";
            String str = String.format(fmt, address, lat, lon);
            return str;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(err);
            dest.writeString(errMsg);
            dest.writeDouble(lat);
            dest.writeDouble(lon);
            dest.writeString(address);
            dest.writeString(city);
            dest.writeString(province);
            dest.writeString(country);
        }
        public static final Creator<LzLocation> CREATOR = new Creator() {
            public LzLocation createFromParcel(Parcel in) {
                LzLocation loc = new LzLocation();
                loc.err = in.readInt();
                loc.errMsg = in.readString();
                loc.lat = in.readDouble();
                loc.lon = in.readDouble();
                loc.address = in.readString();
                loc.city = in.readString();
                loc.province = in.readString();
                loc.country = in.readString();
                return loc;
            }

            public LzLocation[] newArray(int size) {
                return new LzLocation[size];
            }
        };
    }
}
