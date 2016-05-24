package lz.mylife;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import lz.mylife.cal.CalendarService;
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

        initPermissions();
    }

    private void initPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.WRITE_CALENDAR,
                        Manifest.permission.INTERNET,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_CONFIGURATION,
                        Manifest.permission.WRITE_SETTINGS,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
                },
                1);
    }
    boolean granted = false;
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                    LocationService.start(this.getApplicationContext());
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
    public void onStart() {
        super.onStart();
        if(granted) {
            LocationService.start(this.getApplicationContext());
        }
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

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(LocationService.ACTION_LOCATION_CHANED)) {
                LocationService.LzLocation loc = (LocationService.LzLocation) intent.getParcelableExtra("loc");
                LzLog.d(TAG, loc.toString());
                updateLocation(loc);
            } else if (action.equals(WeatherService.ACTION_LIVE_WEATHER_GOT)) {
                WeatherService.LzWeatherLive weather = (WeatherService.LzWeatherLive)intent.getSerializableExtra("weather");
                LzLog.d(TAG, weather.toString());
                updateWeather(weather);
            }
        }
    };
}
