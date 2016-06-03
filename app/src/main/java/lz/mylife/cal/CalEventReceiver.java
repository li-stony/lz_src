package lz.mylife.cal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
            LocationService.startPinWeatherEvent(context);
            startAlarmEvent( context, 1);
        }
    }

    public static void startAlarmEvent(Context context, int dayDelta) {
        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent();
        intent.setAction("lz.mylife.CAL_EVENT_ACTION");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 99, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR, 6);
        cal.add(Calendar.DAY_OF_MONTH, dayDelta);
        alarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }
}
