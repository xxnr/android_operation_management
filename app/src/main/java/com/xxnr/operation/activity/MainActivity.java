package com.xxnr.operation.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.xxnr.operation.BaseActivity;
import com.xxnr.operation.R;
import com.xxnr.operation.UserInfo;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.bean.LoginResult;
import com.xxnr.operation.utils.IntentUtil;
import com.xxnr.operation.utils.RndLog;
import com.xxnr.operation.utils.StringUtil;

/**
 * Created by CAI on 2016/4/22.
 */
public class MainActivity extends BaseActivity {


    private TextView user_name;
    private long backPressTime;


    //布局文件
    @Override
    public int getLayout() {
        return R.layout.main_activity_layout;
    }


    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("新新农人");
        hideLeft();
        initView();

        LoginResult.DatasBean userInfo = UserInfo.getUserInfo(MainActivity.this);
        if (userInfo != null) {
            if (StringUtil.checkStr(userInfo.getAccount())) {
                user_name.setText(userInfo.getAccount());
            }
        }
    }


    private void initView() {
        user_name = (TextView) findViewById(R.id.user_name);
        setViewClick(R.id.login_out);
    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.login_out:
                //清除用户所有本地的信息
                UserInfo.clearUserInfo(MainActivity.this);
                IntentUtil.activityForward(MainActivity.this, LoginActivity.class, null, true);
                break;
        }
    }

    @Override
    public void onResponsed(Request req) {

    }

    // 再按一次退出程序
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            long current = System.currentTimeMillis();
            if (current - backPressTime > 2000) {
                backPressTime = current;
                App.getApp().showToast("再次按返回键退出程序");
            } else {
                RndLog.i("MainActivity", "Exiting...");
                App.getApp().quit();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }




}
