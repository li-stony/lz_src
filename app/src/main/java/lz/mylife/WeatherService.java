package lz.mylife;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import lz.util.LzLog;

/**
 * Created by cussyou on 2016-05-18.
 */
public class WeatherService extends Service  {
    private String TAG = this.getClass().getSimpleName();

    public static final String ACTION_LIVE_WEATHER_GOT = "live_weather_got";
    public static final String ACTION_PREDICT_WEATHER_GOT = "predict_weather_got";

    public static final String ACTION_LIVE_WEATHER = "live_weather";
    public static final String ACTION_PREDICT_WEATHER = "predict_weather";
    public static final String ACTION_STOP = "stop";

    public void onCreate() {
        super.onCreate();
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
        if(intent == null){
            return;
        }
        String action = intent.getAction();
        LzLog.d(TAG, "received command: "+action);
        if(action.equals(ACTION_LIVE_WEATHER)){
            LocationService.LzLocation loc = (LocationService.LzLocation)intent.getParcelableExtra("loc");
            requestLiveWeather(loc);
        } else if(action.equals(ACTION_PREDICT_WEATHER)){

        } else {
            stopSelf();
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
        // 风向角度，范围0~360，0为正北，90为正东，180为正南，270为正西
        public int windDirectionDegree;
        // 风速，单位为km/h公里每小时或mph英里每小时
        public double windSpeed;

        public String toString() {
            String str = getResources().getString(R.string.weather_now_fmt,
                    summary,
                    temperature,
                    windDirectionDegree,
                    windSpeed
                    );
            return str;
        }

        public LzWeatherLive(JSONObject json) {
            JSONObject result =  json.optJSONObject("query").optJSONObject("results").optJSONObject("channel");
            JSONObject condition = result.optJSONObject("item").optJSONObject("condition");
            summary = condition.optString("text");
            code = condition.optString("code");
            temperature = Integer.parseInt(condition.optString("temp"));
            visibility = result.optJSONObject("atmosphere").optDouble("visibility");
            pressure = result.optJSONObject("atmosphere").optDouble("pressure");
            windDirectionDegree = result.optJSONObject("wind").optInt("direction");
            windSpeed = result.optJSONObject("wind").optDouble("speed");
            humidity = result.optJSONObject("atmosphere").optDouble("humidity");;
        }
    }
    public class LzWeatherDay extends LzWeatherLive{
        // 日期
        public Date date;
        // 天气现象文字
        public String text;
        // 白天天气现象代码
        public String code;

        // 当天最高温度
        public int highTemperature;
        // 当天最低温度
        public int lowTemperature;

        public String toString() {
            String str = getResources().getString(R.string.weather_day_fmt,
                    text,
                    lowTemperature,
                    highTemperature,
                    windDirectionDegree,
                    windSpeed
            );
            return str;
        }
        public LzWeatherDay(JSONObject json) {
            super(json);
            // parse forecast

        }

    }
    private void sendWeatherBroadcast(String action, LzWeatherLive weather) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("weather", weather);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    private final String baseUrl = "https://query.yahooapis.com/v1/public/yql?format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&q=";
    private final String paramFmt = "select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s, %s\") and u='c'";
    private void requestLiveWeather(LocationService.LzLocation loc) {
        String param = String.format(paramFmt, loc.city, loc.country);
        String encodeParam = Uri.encode(param);
        String fullUrl = baseUrl + encodeParam;
        LzLog.d(TAG, fullUrl);
        StringRequest req = new StringRequest(fullUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    LzLog.d(TAG,s);
                    JSONObject json = new JSONObject(s);
                    LzWeatherLive weather = new LzWeatherLive(json);
                    sendWeatherBroadcast(ACTION_LIVE_WEATHER_GOT, weather);
                } catch (Exception e) {
                    LzLog.e(TAG, e.getMessage(), e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LzLog.e(TAG, volleyError.toString());
            }
        });
        Volley.getDefaultRequestQueue(this).add(req);
    }

}
