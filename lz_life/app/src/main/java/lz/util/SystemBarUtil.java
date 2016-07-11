package lz.util;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import lz.mylife.R;


public class SystemBarUtil {
    
    public static SystemBarTintManager setSystemBar(Activity activity, String color) {
        return setSystemBarInner(activity, color);
    }
    
    public static SystemBarTintManager setSystemBar(Activity activity) {
        return setSystemBarInner(activity, ContextCompat.getColor(activity, R.color.system_bar_color));
    }
    
    private static SystemBarTintManager setSystemBarInner(Activity activity, String color) {
        return setSystemBarInner(activity, Color.parseColor(color));
    }
    
    private static SystemBarTintManager setSystemBarInner(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
                //After LOLLIPOP not translucent status bar
                window.clearFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //Then call setStatusBarColor.
                window.addFlags(LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
                //set child View not fill the system window
                View mChildView = mContentView.getChildAt(0);
                if (mChildView != null) {
                    ViewCompat.setFitsSystemWindows(mChildView, true);
                }
            } else {
                LayoutParams params = window.getAttributes();
                params.flags |= LayoutParams.FLAG_TRANSLUCENT_STATUS;
                window.setAttributes(params);
                SystemBarTintManager tintManager = new SystemBarTintManager(activity);
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setTintColor(color);
                return tintManager;
            }
        }
        return null;
    }

}
