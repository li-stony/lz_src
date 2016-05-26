package lz.mylife.cal;

import android.Manifest;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import java.security.Permission;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import lz.util.LzLog;

/**
 * Created by cussyou on 2016-05-19.
 */
public class CalendarService extends Service {

    String TAG = getClass().getSimpleName();
    public static final String ACTION_ADD_LIVE_EVENT = "add_live_event";
    public static final String ACTION_ADD_DAY_EVENT = "add_day_event";


    public static final String ACTION_LIVE_EVENT_PINNED = "live_event_pinned";
    public static final String ACTION_DAY_EVENT_PINNED = "day_event_pinned";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public static void addDayEvent(Context context, LocationService.LzLocation loc, WeatherService.LzWeatherDay weather){
        Intent intent = new Intent();
        intent.setClass(context, CalendarService.class);
        intent.setAction(ACTION_ADD_DAY_EVENT);
        intent.putExtra("loc", loc);
        intent.putExtra("weather", weather);
        context.startService(intent);
    }

    public static void addLiveEvent(Context context, LocationService.LzLocation loc, WeatherService.LzWeatherLive weather){
        Intent intent = new Intent();
        intent.setClass(context, CalendarService.class);
        intent.setAction(ACTION_ADD_LIVE_EVENT);
        intent.putExtra("loc", loc);
        intent.putExtra("weather", weather);
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
        if(action.equals(ACTION_ADD_LIVE_EVENT)){
            LocationService.LzLocation loc = (LocationService.LzLocation)intent.getParcelableExtra("loc");
            WeatherService.LzWeatherLive weather = (WeatherService.LzWeatherLive)intent.getSerializableExtra("weather");
            new EventAddingTask(action, loc, weather).execute();
        } else {
            stopSelf();
        }

    }

    private class EventAddingTask extends AsyncTask<Double, Void, Integer> {
        String cmd;
        LocationService.LzLocation loc;
        WeatherService.LzWeatherLive weather ;
        public EventAddingTask(String cmd, LocationService.LzLocation loc, WeatherService.LzWeatherLive weather) {
            this.cmd = cmd;
            this.loc = loc;
            this.weather = weather;
        }
        private long getDestCal(String account) {
            Cursor cur = null;
            try {
                ContentResolver cr = getContentResolver();
                Uri uri = CalendarContract.Calendars.CONTENT_URI;
                cur = cr.query(uri, null, null, null, null);
                while(cur.moveToNext()){
                    long calId = cur.getLong(cur.getColumnIndex(CalendarContract.Calendars._ID));
                    String calAccount = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME));
                    String calName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.NAME));
                    String calType = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.ACCOUNT_TYPE));
                    String str = String.format("%d %s %s %s", calId, calAccount, calName, calType);
                    LzLog.d(TAG, str);
                    if(calAccount.equals(account)) {
                        return calId;
                    }
                }
            } catch (SecurityException e) {
                LzLog.e(TAG, e.toString(), e);
            } catch (Exception e) {
                LzLog.e(TAG, e.toString(), e);
            } finally {
                if(cur != null) {
                    cur.close();
                }
            }

            return -1;
        }
        @Override
        protected Integer doInBackground(Double... params) {
            SharedPreferences pref = getSharedPreferences("mylife", 0);
            String account = pref.getString("calAccount", "");
            if(TextUtils.isEmpty(account)) {
                return -1;
            }
            LzCalEvent ev = new LzCalEvent();
            ev.loc = new Location("lizl");
            ev.loc.setLatitude(loc.lat);
            ev.loc.setLongitude(loc.lon);



            return -1;
        }

        @Override
        protected void onPostExecute(Integer event) {
            if(event == -1) {
                LzLog.e(TAG, "insert event failed");
            }
            Intent intent = new Intent(ACTION_LIVE_EVENT_PINNED);
            intent.putExtra("event", event);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }

    }

    public class LzCalEvent {
        public String subject;
        public Date startTime;
        public Date endTime;
        public TimeZone timeZone;
        public Location loc;
    }

}
