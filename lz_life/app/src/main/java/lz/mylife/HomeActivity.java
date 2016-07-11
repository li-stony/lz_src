package lz.mylife;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import lz.mylife.cal.CalEventReceiver;
import lz.mylife.cal.CalendarService;
import lz.mylife.cal.LocationService;
import lz.mylife.cal.WeatherService;
import lz.util.LzGlobalStates;
import lz.common.LzLog;
import lz.util.SystemBarUtil;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = getClass().getSimpleName();
    private TextView addressTv;
    private TextView locTv;
    private TextView weatherTv;

    private LocationService.LzLocation location;
    private WeatherService.LzWeatherLive weatherLive;
    private WeatherService.LzWeatherDay weatherDay;

    SharedPreferences pref ;

    private View pinBtn;

    //
    private View testDayBtn;
    private TextView statusText;

    private TextView accountTv ;
    private TextView timeTv;
    private View settingBtn ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SystemBarUtil.setSystemBar(this);

        settingBtn = findViewById(R.id.edit_setting_btn);
        settingBtn.setOnClickListener(this);

        accountTv = (TextView) findViewById(R.id.selected_account_name);
        timeTv = (TextView) findViewById(R.id.selected_alarm_time);

        addressTv = (TextView) findViewById(R.id.loc_text_id);
        locTv = (TextView) findViewById(R.id.loc_id);
        weatherTv = (TextView) findViewById(R.id.weather_text);

        pinBtn = findViewById(R.id.pin_btn);
        pinBtn.setOnClickListener(this);
        progress = findViewById(R.id.loading_progress);
        statusText = (TextView) findViewById(R.id.status_bar);
        //
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationService.ACTION_LOCATION_CHANED);
        filter.addAction(WeatherService.ACTION_LIVE_WEATHER_GOT);
        filter.addAction(WeatherService.ACTION_PREDICT_WEATHER_GOT);
        filter.addAction(CalendarService.ACTION_EVENT_PINNED);
        filter.addAction(SettingActivity.ACTION_SETTING_CHANGED);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        initPermissions();
        // init variable
        pref = getSharedPreferences("mylife", 0);
        accountTv.setText(pref.getString(SettingActivity.KEY_CAL_ACCOUNT, ""));
        timeTv.setText(pref.getString(SettingActivity.KEY_ALARM_TIME, "06:15"));

        //
        testDayBtn = findViewById(R.id.test_day_ev);
        testDayBtn.setOnClickListener(this);

        //
        statusText.setText(Environment.getExternalStorageDirectory().getAbsolutePath());
        // tomorrow at 6.00 add a weather event
        CalEventReceiver.startAlarmEvent(getApplicationContext(), 1);

    }

    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
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
                        Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                        Manifest.permission.RECEIVE_BOOT_COMPLETED,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CHANGE_NETWORK_STATE,
                        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS
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
        showProgress();
    }
    @Override
    public void onStop() {
        super.onStop();
        LocationService.stop(this.getApplicationContext(), 0);
        //WeatherService.stop(this.getApplicationContext());
    }
    private View progress;
    private void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }
    private void hideProgress() {
        progress.setVisibility(View.GONE);
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
                String json = intent.getStringExtra("weather");
                try {
                    JSONObject obj = new JSONObject(json);
                    WeatherService.LzWeatherLive weather = new WeatherService.LzWeatherLive(obj);
                    LzLog.d(TAG, weather.toString());
                    updateWeather(weather);
                } catch (Exception e) {
                    LzLog.e(TAG, e.toString(), e);
                }
                hideProgress();

            } else if(action.equals(CalendarService.ACTION_EVENT_PINNED)) {
                Long ev = intent.getLongExtra("event", -1);
                if(ev == -1) {
                    Toast.makeText(context, R.string.event_pinned_error, Toast.LENGTH_SHORT).show();
                } else {
                    Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, ev);
                    Intent calIntent = new Intent(Intent.ACTION_VIEW).setData(uri);
                    startActivity(calIntent);
                }
                hideProgress();
            } else if(action.equals(SettingActivity.ACTION_SETTING_CHANGED)) {
                accountTv.setText(pref.getString(SettingActivity.KEY_CAL_ACCOUNT, ""));
                timeTv.setText(pref.getString(SettingActivity.KEY_ALARM_TIME, "06:15"));
            }
        }
    };

    @Override
    public void onClick(View v) {
        if(v == pinBtn) {
            if(weatherLive != null && location != null) {
                CalendarService.addLiveWeatherEvent(this, location, weatherLive);
                showProgress();
            }
        } else if ( v == testDayBtn ) {
            //LocationService.startPinWeatherEvent(this);
            CalEventReceiver.startAlarmEvent(this.getApplicationContext(), -1);
            showProgress();
        } else if (v == settingBtn){
            startActivity(new Intent(this, SettingActivity.class));
        }
    }

}
