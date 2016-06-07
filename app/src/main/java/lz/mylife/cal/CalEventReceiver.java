package lz.mylife.cal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import java.util.Calendar;

import lz.util.LzLog;

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
        if(action.equals("lz.mylife.CAL_EVENT_ACTION")){
            PowerManager pm = (PowerManager)context.getSystemService(
                    Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "CalEventReceiver");

            wl.acquire(5000);
            LocationService.startPinWeatherEvent(context);
            startAlarmEvent( context, 1);
        }
    }

    static  boolean  test = false;
    public static void startAlarmEvent(Context context, int dayDelta) {
        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent();
        intent.setAction("lz.mylife.CAL_EVENT_ACTION");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 99, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        if(!test) {
            cal.set(Calendar.HOUR, 6);
            cal.set(Calendar.MINUTE, 15);
            cal.set(Calendar.SECOND, 0);
            cal.add(Calendar.DAY_OF_MONTH, dayDelta);
        } else {
            cal.add(Calendar.MINUTE, 15);
        }
        alarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }
}
