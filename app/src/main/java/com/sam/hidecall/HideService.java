package com.sam.hidecall;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.*;
import android.app.admin.DevicePolicyManager;
import android.widget.ImageView;


public class HideService extends Service {
    private static boolean viewAdded;
    private long upTime;
    private long downTime;
    private long ellapseTime;
    private DevicePolicyManager policyManager;
    ImageView black;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        policyManager.lockNow();
        addToWindow();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void addToWindow() {
        if (!viewAdded) {
            final WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

            black = new ImageView(this);
            black.setBackgroundResource(R.drawable.black);

            WindowManager.LayoutParams paramsNotification = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.RGB_888);
            paramsNotification.gravity = Gravity.LEFT | Gravity.TOP;
            paramsNotification.windowAnimations = android.R.style.Animation_Dialog;
            paramsNotification.setTitle("Fooled!");

            LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            black.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        downTime = System.currentTimeMillis();
                            return true;
                    }
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        return  true;

                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        upTime = System.currentTimeMillis();
                        ellapseTime = upTime - downTime;
                        if (ellapseTime > 10) {
                            wm.removeView(v);
                            viewAdded = false;
                            stopSelf();
                        }
                    }

                    return false;
                }
            });
            wm.addView( black,  paramsNotification);
            viewAdded = true;
        }
    }
}
