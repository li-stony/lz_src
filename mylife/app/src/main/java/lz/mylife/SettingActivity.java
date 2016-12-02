package lz.mylife;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

import lz.common.LzLog;
import lz.util.SystemBarUtil;

/**
 * Created by cussyou on 2016-06-23.
 */
public class SettingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener, TimePickerDialog.OnTimeSetListener {

    public static final String ACTION_SETTING_CHANGED = "lz.mylife.setting_changed";
    public static final String KEY_CAL_ACCOUNT = "calAccount";
    public static final String KEY_CAL_ID = "calId";
    public static final String KEY_ALARM_TIME = "alarmTime";

    private static final String TAG = "setting";
    private Spinner calSpinner;
    private ArrayAdapter<CalendarStruct> calAdapter;

    private SharedPreferences pref ;
    private String calAccount = null;
    private TextView alarmTime = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        SystemBarUtil.setSystemBar(this);
        pref = getSharedPreferences("mylife", 0);
        calAdapter = new CalAdapter(this);
        calAdapter.setDropDownViewResource(R.layout.cal_list_item);
        calSpinner = (Spinner) findViewById(R.id.calendar_spinner);
        calSpinner.setAdapter(calAdapter);
        calSpinner.setOnItemSelectedListener(this);

        alarmTime = (TextView) findViewById(R.id.alarm_time);
        alarmTime.setText(pref.getString(KEY_ALARM_TIME, "06:15"));
        alarmTime.setOnClickListener(this);

        new CalendarListTask().execute();
    }

    @Override
    public void onClick(View v) {
        if(v == alarmTime) {
            TimePickerDialog dialog = new TimePickerDialog(this,this, 6, 15, true);
            dialog.show();
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String timeStr = String.format("%02d:%02d", hourOfDay, minute);
        pref.edit().putString(KEY_ALARM_TIME, timeStr).commit();
        alarmTime.setText(timeStr);
        Intent intent = new Intent();
        intent.setAction(ACTION_SETTING_CHANGED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private class CalendarStruct {
        public String accountName;
        public long id;
    }
    private class CalAdapter extends ArrayAdapter<CalendarStruct> {

        public CalAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = View.inflate(getContext(), R.layout.cal_list_item, null);

            }
            TextView tv = (TextView) convertView.findViewById(R.id.account_name);
            CalendarStruct account = getItem(position);
            tv.setText(account.accountName);
            return convertView;
        }
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = View.inflate(getContext(), R.layout.cal_list_item, null);

            }
            TextView tv = (TextView) convertView.findViewById(R.id.account_name);
            CalendarStruct account = getItem(position);
            tv.setText(account.accountName);
            return convertView;
        }
    };
    private class CalendarListTask extends AsyncTask<Void, Void, CalendarStruct[]> {
        @Override
        protected CalendarStruct[] doInBackground(Void... params) {
            ArrayList<CalendarStruct> results = new ArrayList<>();
            Cursor cur = null;
            try {
                ContentResolver cr = getContentResolver();
                Uri uri = CalendarContract.Calendars.CONTENT_URI;
                cur = cr.query(uri, null, null, null, null);
                while(cur.moveToNext()) {
                    long id = cur.getLong(cur.getColumnIndex(CalendarContract.Calendars._ID));
                    String calAccount = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME));
                    String calName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME));
                    String deleted = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.DELETED));
                    //String dirty = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.DIRTY));
                    LzLog.d(TAG, id+" " +calAccount+" "+calName + " "+deleted + " ");
                    CalendarStruct account = new CalendarStruct();
                    account.accountName = calAccount + " " + calName;
                    account.id = id;
                    results.add(account);
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
            CalendarStruct[] re = new CalendarStruct[results.size()];
            return results.toArray(re);
        }

        @Override
        protected void onPostExecute(CalendarStruct[] results) {
            calAdapter.clear();
            calAdapter.addAll(results);
            calAdapter.notifyDataSetChanged();
            calAccount = pref.getString(KEY_CAL_ACCOUNT, "");
            for(int i=0;i<results.length;i++) {
                if(calAccount.equals(results[i].accountName)){
                    calSpinner.setSelection(i);
                }
            }
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        CalendarStruct item = calAdapter.getItem(position);
        calAccount = item.accountName;
        pref.edit().putString(KEY_CAL_ACCOUNT, calAccount)
                .putLong(KEY_CAL_ID, item.id)
                .commit();

        LzLog.d(TAG, "calAccount="+calAccount);
        Intent intent = new Intent();
        intent.setAction(ACTION_SETTING_CHANGED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
