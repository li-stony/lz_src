package lz.mylife.cal;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

import lz.mylife.R;
import lz.util.LzGlobalStates;
import lz.common.LzLog;

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
        YahooWeatherCode.init();
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
            requestWeather(loc, action);
        } else if(action.equals(ACTION_PREDICT_WEATHER)){
            LocationService.LzLocation loc = (LocationService.LzLocation)intent.getParcelableExtra("loc");
            requestWeather(loc, action);
        } else {
            stopSelf();
        }

    }

    public static class LzWeatherLive implements Serializable {
        protected JSONObject json;
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
            String str = LzGlobalStates.globalContext.getResources().getString(R.string.weather_now_fmt,
                    LzGlobalStates.globalContext.getString(YahooWeatherCode.weatherCodes.get(code)),
                    temperature,
                    windDirectionDegree,
                    windSpeed
                    );
            return str;
        }
        public LzWeatherLive() {
            this.json = null;
            summary = "err: volley";
        }
        public LzWeatherLive(JSONObject json) {
            this.json = json;
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
        public JSONObject toJson() {
            return json;
        }
    }
    public static class LzWeatherDay extends LzWeatherLive{
        // 日期
        public Date date;
        // 天气现象文字
        public String text;
        // 天气现象代码
        public String code1;
        public String code2;
        // 当天最高温度
        public int highTemperature;
        // 当天最低温度
        public int lowTemperature;

        @Override
        public String toString() {
            String summary = String.format("%s-%s",
                    LzGlobalStates.globalContext.getString(YahooWeatherCode.weatherCodes.get(code1)),
                    LzGlobalStates.globalContext.getString(YahooWeatherCode.weatherCodes.get(code2)));
            String str = LzGlobalStates.globalContext.getResources().getString(R.string.weather_day_fmt,
                    summary,
                    lowTemperature,
                    highTemperature,
                    windDirectionDegree,
                    windSpeed
            );
            return str;
        }
        public LzWeatherDay(JSONObject json){
            super(json);
            // parse forecast
            JSONObject result =  json.optJSONObject("query").optJSONObject("results").optJSONObject("channel");
            JSONArray forecast = result.optJSONObject("item").optJSONArray("forecast");
            if(forecast!= null &&forecast.length()>0){
                JSONObject obj1 = forecast.optJSONObject(0);
                this.code1 = obj1.optString("code");
                this.text = obj1.optString("text");
                this.highTemperature = Integer.parseInt(obj1.optString("high"));
                this.lowTemperature = Integer.parseInt(obj1.optString("low"));
                JSONObject obj2 = forecast.optJSONObject(1);
                if(obj2 != null) {
                    this.code2 = obj2.optString("code");
                }
            }
        }

    }
    private void sendWeatherBroadcast(String action, LzWeatherLive weather) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("weather", weather.toJson().toString());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    private final String baseUrl = "https://query.yahooapis.com/v1/public/yql?format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&q=";
    private final String paramFmt = "select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s, %s\") and u='c'";
    private void requestWeather(final LocationService.LzLocation loc, final String action) {
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
                    if(action.equals(ACTION_LIVE_WEATHER)) {
                        LzWeatherLive weather = new LzWeatherLive(json);
                        sendWeatherBroadcast(ACTION_LIVE_WEATHER_GOT, weather);
                    } else if(action.equals(ACTION_PREDICT_WEATHER)) {
                        LzWeatherDay weather = new LzWeatherDay(json);
                        CalendarService.addDayWeatherEvent(getApplicationContext(), loc, weather);
                    }

                } catch (Exception e) {
                    LzLog.e(TAG, e.toString(), e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LzLog.e(TAG, volleyError.toString());
                sendWeatherBroadcast(ACTION_LIVE_WEATHER_GOT, new LzWeatherLive());
            }
        });
        Volley.getDefaultRequestQueue(this).add(req);
    }

}
