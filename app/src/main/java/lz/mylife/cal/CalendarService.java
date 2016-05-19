package lz.mylife.cal;

import android.Manifest;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;

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
    public static final String ACTION_ADD_EVENT = "add_event";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public static void addEvent(Context context, LocationService.LzLocation loc){
        Intent intent = new Intent();
        intent.setClass(context, CalendarService.class);
        intent.setAction(ACTION_ADD_EVENT);
        intent.putExtra("loc", loc);
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
        if(action.equals(ACTION_ADD_EVENT)){
            LocationService.LzLocation loc = (LocationService.LzLocation)intent.getParcelableExtra("loc");
            new EventAddingTask().execute(loc.lat, loc.lon);
        } else {
            stopSelf();
        }

    }

    private class EventAddingTask extends AsyncTask<Double, Void, LzCalEvent> {

        private void printCal(Cursor cur) {
            while(cur.moveToNext()){
                long calId = cur.getLong(cur.getColumnIndex(CalendarContract.Calendars._ID));
                String calAccount = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME));
                String calName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.NAME));
                String calType = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.ACCOUNT_TYPE));
                String str = String.format("%d %s %s %s", calId, calAccount, calName, calType);
                LzLog.d(TAG, str);
            }
        }
        @Override
        protected LzCalEvent doInBackground(Double... params) {
            LzCalEvent ev = new LzCalEvent();
            ev.loc = new Location("lizl");
            ev.loc.setLatitude(params[0]);
            ev.loc.setLongitude(params[1]);
            PackageManager pm = getApplicationContext().getPackageManager();
            boolean pRead = false;
            boolean pWrite = false;
            if(pm.checkPermission(Manifest.permission.READ_CALENDAR,getPackageName()) == PackageManager.PERMISSION_GRANTED){
                pRead = true;
            }
            if(pm.checkPermission(Manifest.permission.WRITE_CALENDAR,getPackageName()) == PackageManager.PERMISSION_GRANTED){
                pWrite = true;
            }
            Cursor cur = null;
            try {

                ContentResolver cr = getContentResolver();
                Uri uri = CalendarContract.Calendars.CONTENT_URI;

                if(pRead && pWrite) {
                    cur = cr.query(uri, null, null, null, null);
                    printCal(cur);
                }
            } catch (Exception e) {
                LzLog.e(TAG, e.toString(), e);
            } finally {
                if(cur != null) {
                    cur.close();
                }
            }

            return ev;
        }

        @Override
        protected void onPostExecute(LzCalEvent event) {

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
