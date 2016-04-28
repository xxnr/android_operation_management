package com.xxnr.operation.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.xxnr.operation.R;
import com.xxnr.operation.utils.IntentUtil;


public class SplashActivity extends Activity {

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 1:
                    IntentUtil.activityForward(SplashActivity.this, LoginActivity.class, null, true);
                    break;

            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 屏幕竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_welcome);
        handler.sendEmptyMessageDelayed(1, 2000);

    }


}
