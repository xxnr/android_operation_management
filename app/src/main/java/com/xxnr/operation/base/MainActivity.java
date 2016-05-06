package com.xxnr.operation.base;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.xxnr.operation.LoginActivity;
import com.xxnr.operation.R;
import com.xxnr.operation.UserInfo;
import com.xxnr.operation.potential.PotentialCustomerActivity;
import com.xxnr.operation.customer.CustomerManageActivity;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.order.OrderManageActivity;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.bean.LoginResult;
import com.xxnr.operation.utils.IntentUtil;
import com.xxnr.operation.utils.RndLog;
import com.xxnr.operation.utils.StringUtil;
import com.xxnr.operation.widget.CustomDialog;

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
        setTitle("首页");
        hideLeft();
        initView();

        LoginResult.DatasBean userInfo = UserInfo.getUserInfo(MainActivity.this);
        if (userInfo != null) {
            if (StringUtil.checkStr(userInfo.account)) {
                user_name.setText(userInfo.account);
            }
        }

        setViewClick(R.id.customer_manage_ll);
        setViewClick(R.id.potential_customer_ll);
        setViewClick(R.id.order_manage_ll);

    }


    private void initView() {
        user_name = (TextView) findViewById(R.id.user_name);
        setViewClick(R.id.login_out);
    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.login_out:
                CustomDialog.Builder builder = new CustomDialog.Builder(
                        MainActivity.this);
                builder.setMessage("确定要退出新新农人吗？")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        //清除用户所有本地的信息
                                        dialog.dismiss();
                                        UserInfo.clearUserInfo(MainActivity.this);
                                        showToast("您已退出登录");
                                        IntentUtil.activityForward(MainActivity.this, LoginActivity.class, null, true);

                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });
                builder.create().show();
                break;
            case R.id.customer_manage_ll: //客户管理
                IntentUtil.activityForward(this, CustomerManageActivity.class, null, false);
                break;
            case R.id.potential_customer_ll://潜在客户
                IntentUtil.activityForward(this, PotentialCustomerActivity.class, null, false);
                break;
            case R.id.order_manage_ll://订单管理
                IntentUtil.activityForward(this, OrderManageActivity.class, null, false);
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
