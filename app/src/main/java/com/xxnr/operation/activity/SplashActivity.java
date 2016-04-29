package com.xxnr.operation.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.xxnr.operation.R;
import com.xxnr.operation.UserInfo;
import com.xxnr.operation.utils.IntentUtil;
import com.xxnr.operation.utils.StringUtil;


public class SplashActivity extends Activity {

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    IntentUtil.activityForward(SplashActivity.this, MainActivity.class, null, true);
                    break;
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

        //判断是否登录，登录 去首页 未登录去登录页
        if (StringUtil.checkStr(UserInfo.getUid(SplashActivity.this))) {
            handler.sendEmptyMessageDelayed(0, 2000);
        } else {
            handler.sendEmptyMessageDelayed(1, 2000);
        }

    }


}
