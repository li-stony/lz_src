package lz.mylife.cal;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;

import lz.common.LzLog;
import lz.mylife.SettingActivity;

/**
 * Created by cussyou on 2016-05-19.
 */
public class CalendarService extends Service {

    String TAG = getClass().getSimpleName();
    public static final String ACTION_ADD_LIVE_WEATHER = "add_live_event";
    public static final String ACTION_ADD_DAY_WEATHER = "add_day_event";

    public static final String ACTION_EVENT_PINNED = "live_event_pinned";

    public static void addLiveWeatherEvent(Context context, LocationService.LzLocation loc, WeatherService.LzWeatherLive weather){
        Intent intent = new Intent();
        intent.setClass(context, CalendarService.class);
        intent.setAction(ACTION_ADD_LIVE_WEATHER);
        intent.putExtra("loc", loc);
        intent.putExtra("weather", weather.toJson().toString());
        context.startService(intent);
    }
    public static void addDayWeatherEvent(Context context, LocationService.LzLocation loc, WeatherService.LzWeatherDay weather){
        Intent intent = new Intent();
        intent.setClass(context, CalendarService.class);
        intent.setAction(ACTION_ADD_DAY_WEATHER);
        intent.putExtra("loc", loc);
        intent.putExtra("weather", weather.toJson().toString());
        context.startService(intent);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
        String str = intent.getStringExtra("weather");
        JSONObject obj = null;
        try {
            obj = new JSONObject(str);
        } catch (Exception e) {
            LzLog.e(TAG, e.toString(), e);
        }

        if(action.equals(ACTION_ADD_LIVE_WEATHER)){
            LocationService.LzLocation loc = (LocationService.LzLocation)intent.getParcelableExtra("loc");
            WeatherService.LzWeatherLive weather = new WeatherService.LzWeatherLive(obj);
            new EventAddingTask(action, loc, weather).execute();
        } if(action.equals(ACTION_ADD_DAY_WEATHER)){
            LocationService.LzLocation loc = (LocationService.LzLocation)intent.getParcelableExtra("loc");
            WeatherService.LzWeatherDay weather = new WeatherService.LzWeatherDay(obj);
            new EventAddingTask(action, loc, weather).execute();
        } else {
            stopSelf();
        }

    }

    private class EventAddingTask extends AsyncTask<Double, Void, Long> {
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
        private long insertEvent(long calId) {
            long evId = -1;
            Cursor cur = null;
            try {
                // get suitable time
                Calendar now = Calendar.getInstance();
                now.setTimeInMillis(System.currentTimeMillis());
                now.setTimeZone(TimeZone.getDefault());
                Calendar endTime = Calendar.getInstance();
                endTime.setTimeInMillis(now.getTimeInMillis());
                endTime.setTimeZone(TimeZone.getDefault());
                endTime.add(Calendar.MINUTE, 15);
                // find old first
                ContentResolver cr = getContentResolver();
                ContentValues values = new ContentValues();values.put(CalendarContract.Events.DTSTART, now.getTimeInMillis());
                values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
                // MyEvent:
                values.put(CalendarContract.Events.TITLE, "MyEvent: "+weather.toString());
                values.put(CalendarContract.Events.EVENT_LOCATION, loc.toAddressString());
                values.put(CalendarContract.Events.DESCRIPTION, "Created by MyLife");
                values.put(CalendarContract.Events.CALENDAR_ID, calId);
                values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName());
                Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                // get the event ID that is the last element in the Uri
                long eventID = Long.parseLong(uri.getLastPathSegment());
                evId = eventID;
                LzLog.d(TAG, "inserted event: "+eventID);
            } catch (SecurityException e) {
                LzLog.e(TAG, e.toString(), e);
            } finally {
                if(cur != null) {
                    cur.close();
                }
            }
            return evId;
        }
        @Override
        protected Long doInBackground(Double... params) {
            SharedPreferences pref = getSharedPreferences("mylife", 0);
            long calId = pref.getLong(SettingActivity.KEY_CAL_ID, -1);
            if(calId == -1L) {
                LzLog.e(TAG, "no calendar selected");
                return -1L;
            }
            long evId = insertEvent(calId);

            return evId;
        }

        @Override
        protected void onPostExecute(Long event) {
            if(event == -1L) {
                LzLog.e(TAG, "insert event failed");
            }
            Intent intent = new Intent(ACTION_EVENT_PINNED);
            intent.putExtra("event", event);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

}
