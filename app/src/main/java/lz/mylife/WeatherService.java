package lz.mylife;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.thinkpage.lib.api.TPCity;
import com.thinkpage.lib.api.TPListeners;
import com.thinkpage.lib.api.TPWeatherDaily;
import com.thinkpage.lib.api.TPWeatherManager;
import com.thinkpage.lib.api.TPWeatherNow;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import lz.util.LzLog;

/**
 * Created by cussyou on 2016-05-18.
 */
public class WeatherService extends Service implements TPListeners.TPWeatherNowListener, TPListeners.TPWeatherDailyListener {
    private String TAG = this.getClass().getSimpleName();

    public static final String ACTION_LIVE_WEATHER_GOT = "live_weather_got";
    public static final String ACTION_PREDICT_WEATHER_GOT = "predict_weather_got";

    public static final String ACTION_LIVE_WEATHER = "live_weather";
    public static final String ACTION_PREDICT_WEATHER = "predict_weather";
    public static final String ACTION_STOP = "stop";

    TPWeatherManager weatherManager;
    public void onCreate() {
        super.onCreate();
        weatherManager = TPWeatherManager.sharedWeatherManager();
        weatherManager.initWithKeyAndUserId("iehucbbfk1luf6ec", "UC67F0671D");
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void fetchLiveWeather(Context context, LocationService.LzLocation loc) {
        Intent intent = new Intent();
        intent.setClass(context, WeatherService.class);
        intent.setAction(ACTION_LIVE_WEATHER);
        intent.putExtra("loc", loc);
        context.startService(intent);
    }

    public static void fetchPredictWeather(Context context, LocationService.LzLocation loc) {
        Intent intent = new Intent();
        intent.setClass(context, WeatherService.class);
        intent.setAction(ACTION_PREDICT_WEATHER);
        intent.putExtra("loc", loc);
        context.startService(intent);
    }
    public static void stop(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, WeatherService.class);
        intent.setAction(ACTION_STOP);
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
        if(action.equals(ACTION_LIVE_WEATHER)){
            LocationService.LzLocation loc = (LocationService.LzLocation)intent.getParcelableExtra("loc");
            Location tpLoc = new Location("lizl");
            tpLoc.setLatitude(loc.lat);
            tpLoc.setLongitude(loc.lon);
            //TPCity city = TPCity.cityWithLocation(tpLoc);
            //TPCity city = TPCity.cityWithName("beijing");
            TPCity city = new TPCity("beijing");
            weatherManager.getWeatherNow(city
                    , TPWeatherManager.TPWeatherReportLanguage.kSimplifiedChinese
                    , TPWeatherManager.TPTemperatureUnit.kCelsius
                    , this);
        } else if(action.equals(ACTION_PREDICT_WEATHER)){
            LocationService.LzLocation loc = (LocationService.LzLocation)intent.getSerializableExtra("loc");
            Location tpLoc = new Location("lizl");
            tpLoc.setLatitude(loc.lat);
            tpLoc.setLongitude(loc.lon);
            TPCity city = TPCity.cityWithLocation(tpLoc);
            weatherManager.getWeatherDailyArray(city
                    , TPWeatherManager.TPWeatherReportLanguage.kSimplifiedChinese
                    , TPWeatherManager.TPTemperatureUnit.kCelsius
                    , Calendar.getInstance().getTime()
                    , 1
                    , this);
        } else {
            stopSelf();
        }

    }

    @Override
    public void onTPWeatherNowAvailable(TPWeatherNow tpWeatherNow, String s) {
        if(tpWeatherNow != null) {
            LzWeatherLive now = copyWeather(tpWeatherNow);
            Intent intent = new Intent();
            intent.setAction(ACTION_LIVE_WEATHER_GOT);
            intent.putExtra("weather", now);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else {
            LzLog.d(TAG, s);
        }
    }
    private LzWeatherLive copyWeather(TPWeatherNow now) {
        LzWeatherLive weather = new LzWeatherLive();
        weather.summary = now.text;
        weather.code = now.code;
        weather.temperature = now.temperature;
        weather.visibility = now.visibility;
        weather.clouds = now.clouds;
        weather.pressure = now.pressure;
        weather.windDirection = now.windDirection;
        weather.windDirectionDegree = now.windDirectionDegree;
        weather.windSpeed = now.windSpeed;
        weather.humidity = now.humidity;
        return weather;
    }
    private LzWeatherDay copyWeather(TPWeatherDaily today) {
        LzWeatherDay weather = new LzWeatherDay();
        weather.date = today.date;
        weather.textDay = today.textDay;
        weather.textNight = today.textNight;
        weather.windDirection = today.windDirection;
        weather.windDirectionDegree = today.windDirectionDegree;
        weather.windSpeed = today.windSpeed;
        weather.highTemperature = today.highTemperature;
        weather.lowTemperature = today.lowTemperature;

        return  weather;
    };
    @Override
    public void onTPWeatherDailyAvailable(TPWeatherDaily[] tpWeatherDailies, String s) {
        if(tpWeatherDailies!=null && tpWeatherDailies.length > 0){
            LzWeatherDay today = copyWeather(tpWeatherDailies[0]);
            Intent intent = new Intent();
            intent.setAction(ACTION_PREDICT_WEATHER_GOT);
            intent.putExtra("weather", today);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else {
            LzLog.d(TAG, s);
        }
    }

    public class LzWeatherLive implements Serializable {
        // 天气现象文字，例如“多云”
        public String summary;
        // 天气现象代码，例如“4”
        public String code;
        // 温度，单位为c摄氏度或f华氏度
        public int temperature;

        // 气压。单位为mb百帕或in英寸
        public double pressure;
        // 相对湿度，范围0~1
        public double humidity;
        // 能见度，单位为km公里或mi英里
        public double visibility;
        // 风向文字，例如“西北”
        public String windDirection;
        // 风向角度，范围0~360，0为正北，90为正东，180为正南，270为正西
        public int windDirectionDegree;
        // 风速，单位为km/h公里每小时或mph英里每小时
        public double windSpeed;

        // 云量，范围0~100，天空被云覆盖的百分比
        public double clouds;
        // 露点温度
        public int dewPoint;

        // 数据更新时间（该城市的本地时间）
        public Date lastUpdateDate;

        public String toString() {
            String str = getResources().getString(R.string.weather_now_fmt,
                    summary,
                    temperature,
                    windDirection,
                    windSpeed,
                    clouds
                    );
            return str;
        }
    }
    public class LzWeatherDay implements Serializable{
        // 日期
        public Date date;
        // 白天天气现象文字
        public String textDay;
        // 白天天气现象代码
        public String codeDay;
        // 晚间天气现象文字
        public String textNight;
        // 晚间天气现象代码
        public String codeNight;
        // 当天最高温度
        public int highTemperature;
        // 当天最低温度
        public int lowTemperature;
        // 降水概率，范围0~100，单位百分比
        public double chanceOfRain;
        // 风向文字
        public String windDirection;
        // 风向角度，范围0~360
        public int windDirectionDegree;
        // 风速，单位km/h（当unit=c时）、mph（当unit=f时）
        public double windSpeed;
        // 风力等级
        public double windScale;
        public String toString() {
            String str = getResources().getString(R.string.weather_day_fmt,
                    textDay,
                    textNight,
                    lowTemperature,
                    highTemperature,
                    windDirection,
                    windSpeed,
                    chanceOfRain
            );
            return str;
        }

    }

}
