package lz.mylife.cal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import lz.common.LzLog;
import lz.mylife.BuildConfig;
import lz.mylife.SettingActivity;

/**
 * Created by cussyou on 2016-06-03.
 */
public class CalEventReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent == null) {
            return;
        }
        String action = intent.getAction();
        LzLog.d("CalEventReceiver", "receive: "+action);
        if(action.equals("lz.mylife.CAL_EVENT_ACTION")
                || action.equals(Intent.ACTION_BOOT_COMPLETED)){
            PowerManager pm = (PowerManager)context.getSystemService(
                    Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "CalEventReceiver");

            wl.acquire(5000);
            // not pin it after reboot.
            if(action.equals("lz.mylife.CAL_EVENT_ACTION")) {
                LocationService.startPinWeatherEvent(context);
            }
            startAlarmEvent( context, 1);
        }
    }
    static boolean test = false;
    public static void startAlarmEvent(Context context, int dayDelta) {
        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent();
        intent.setAction("lz.mylife.CAL_EVENT_ACTION");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 99, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        if(!test) {
            SharedPreferences pref = context.getSharedPreferences("mylife", 0);
            String timeStr = pref.getString(SettingActivity.KEY_ALARM_TIME, "06:15");
            String[] tokens = timeStr.split(":");
            int hour = Integer.parseInt(tokens[0]);
            int minute = Integer.parseInt(tokens[1]);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.SECOND, 0);
            cal.add(Calendar.DAY_OF_MONTH, dayDelta);
        } else {
            cal.add(Calendar.HOUR, 2);
        }
        String time = new SimpleDateFormat("yyyyMMdd-HHmmss")
                .format(cal.getTimeInMillis());
        LzLog.d("CalEventReceiver", "Alarm at "+time);
        alarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }
}
