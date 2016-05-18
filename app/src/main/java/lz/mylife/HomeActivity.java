package lz.mylife;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import lz.util.LzLog;

public class HomeActivity extends AppCompatActivity {

    private String TAG = getClass().getSimpleName();
    private TextView addressTv;
    private TextView locTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        addressTv = (TextView) findViewById(R.id.loc_text_id);
        locTv = (TextView) findViewById(R.id.loc_id);
        //
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationService.ACTION_LOCATION_CHANED);
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
    }

    private void updateLocation(LocationService.LzLocation loc) {
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
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(LocationService.ACTION_LOCATION_CHANED)) {
                LocationService.LzLocation loc = (LocationService.LzLocation) intent.getSerializableExtra("loc");
                LzLog.d(TAG, loc.toString());
                updateLocation(loc);
            }
        }
    };
}
