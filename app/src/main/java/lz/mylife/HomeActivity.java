package lz.mylife;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import lz.mylife.cal.LocationService;
import lz.mylife.cal.WeatherService;
import lz.util.LzLog;

public class HomeActivity extends AppCompatActivity {

    private String TAG = getClass().getSimpleName();
    private TextView addressTv;
    private TextView locTv;
    private TextView weatherTv;

    private LocationService.LzLocation location;
    private WeatherService.LzWeatherLive weatherLive;
    private WeatherService.LzWeatherDay weatherDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        addressTv = (TextView) findViewById(R.id.loc_text_id);
        locTv = (TextView) findViewById(R.id.loc_id);
        weatherTv = (TextView) findViewById(R.id.weather_text);
        //
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationService.ACTION_LOCATION_CHANED);
        filter.addAction(WeatherService.ACTION_LIVE_WEATHER_GOT);
        filter.addAction(WeatherService.ACTION_PREDICT_WEATHER_GOT);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

    }

    public void onStart() {
        super.onStart();
        LocationService.start(this.getApplicationContext());

    }
    @Override
    public void onStop() {
        super.onStop();
        LocationService.stop(this.getApplicationContext());
        WeatherService.stop(this.getApplicationContext());
    }

    private void updateLocation(LocationService.LzLocation loc) {
        location = loc;
        if(loc.err == 0) {
            String str1 = getResources().getString(R.string.location_text, loc.address);
            addressTv.setText(str1);
            String str2 = getResources().getString(R.string.lat_lon_text,loc.lat, loc.lon);
            locTv.setText(str2);
        } else {
            locTv.setText("");
            String str1 = getResources().getString(R.string.location_text, loc.err + " " +loc.errMsg);
            addressTv.setText(str1);
        }
    }
    private void updateWeather(WeatherService.LzWeatherLive weather){
        weatherLive = weather;
        weatherTv.setText(weather.toString());
    }
    int weatherCnt = 0;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(LocationService.ACTION_LOCATION_CHANED)) {
                LocationService.LzLocation loc = (LocationService.LzLocation) intent.getParcelableExtra("loc");
                LzLog.d(TAG, loc.toString());
                updateLocation(loc);
                if(loc.err == 0) {
                    if (weatherCnt % 720 == 0) {
                        WeatherService.fetchLiveWeather(context, loc);
                    }
                    weatherCnt++;
                }
            } else if (action.equals(WeatherService.ACTION_LIVE_WEATHER_GOT)) {
                WeatherService.LzWeatherLive weather = (WeatherService.LzWeatherLive)intent.getSerializableExtra("weather");
                LzLog.d(TAG, weather.toString());
                updateWeather(weather);
            }
        }
    };
}
