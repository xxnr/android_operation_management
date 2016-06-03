package com.xxnr.operation.modules;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;

import com.xxnr.operation.R;
import com.xxnr.operation.UserInfo;
import com.xxnr.operation.developTools.PreferenceUtil;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_welcome);

        //是否大于24小时 如果是就更新
        PreferenceUtil pu = new PreferenceUtil(SplashActivity.this, "config");
        long last_up_date = pu.getLong("login_time", 0L);
        long currentTimeMillis = System.currentTimeMillis();
        if ((currentTimeMillis - last_up_date) > (24 * 60 * 60 * 1000)) {
            //24小时 清除登录信息
            UserInfo.clearUserInfo(SplashActivity.this);
        }

        //判断是否登录，登录 去首页 未登录去登录页
        if (StringUtil.checkStr(UserInfo.getUid(SplashActivity.this))) {
            handler.sendEmptyMessageDelayed(0, 2000);
        } else {
            handler.sendEmptyMessageDelayed(1, 2000);
        }

    }


}
