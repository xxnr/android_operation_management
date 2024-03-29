package com.xxnr.operation.modules;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.xxnr.operation.R;
import com.xxnr.operation.UserInfo;
import com.xxnr.operation.developTools.PreferenceUtil;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.protocol.bean.GetPublicKeyResult;
import com.xxnr.operation.protocol.bean.LoginResult;
import com.xxnr.operation.utils.RSAUtil;
import com.xxnr.operation.utils.StringUtil;
import com.xxnr.operation.widget.ClearEditText;
import com.xxnr.operation.widget.KeyboardListenRelativeLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 何鹏 on 2016/4/28.
 */
public class LoginActivity extends BaseActivity implements KeyboardListenRelativeLayout.IOnKeyboardStateChangedListener {

    private ClearEditText login_name_et, login_pass_et;
    private String phone_number, password;
    private ScrollView login_layout;
    private Handler handler = new Handler();

    @Override
    public int getLayout() {
        return R.layout.activity_login;
    }

    public void initView() {
        login_name_et = (ClearEditText) findViewById(R.id.login_name_et);
        login_pass_et = (ClearEditText) findViewById(R.id.login_pass_et);
        login_layout = (ScrollView) findViewById(R.id.login_layout);
        KeyboardListenRelativeLayout rootView = (KeyboardListenRelativeLayout) findViewById(R.id.rootView);
        rootView.setBackgroundResource(R.mipmap.login_bg_img);
        setViewClick(R.id.login_sure_bt);

        rootView.setOnKeyboardStateChangedListener(this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                login_layout.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 300);


    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        initView();
        setSwipeBackEnable(false);
    }

    @Override
    public void OnViewClick(View v) {

        switch (v.getId()) {
            case R.id.login_sure_bt:
                phone_number = login_name_et.getText().toString();
                password = login_pass_et.getText().toString();

                if (!StringUtil.checkStr(phone_number)) {
                    showToast("请输入用户名");
                    return;
                }

                if (!StringUtil.checkStr(password)) {
                    showToast("请输入密码");
                    return;
                }

                showProgressDialog("登录中...");
                execApi(ApiType.GET_PUBLIC_KEY.setMethod(ApiType.RequestMethod.GET), new RequestParams());

                break;
        }
    }

    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.GET_PUBLIC_KEY) {
            if (req.getData().getStatus().equals("1000")) {
                GetPublicKeyResult data = (GetPublicKeyResult) req.getData();
                try {
                    RequestParams params = new RequestParams();
                    Gson gson = new Gson();
                    Map<String, Object> map = new HashMap<>();
                    map.put("account", phone_number);
                    map.put("password", RSAUtil.encryptByPublicKey(password,
                            RSAUtil.generatePublicKey(data.public_key)));
                    String json = gson.toJson(map);
                    params.put("JSON", json);
                    execApi(ApiType.LOGIN.setMethod(ApiType.RequestMethod.POSTJSON), params);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (req.getApi() == ApiType.LOGIN) {

            if (req.getData().getStatus().equals("1000")) {
                LoginResult result = (LoginResult) req.getData();
                //保存用户信息token
                if (StringUtil.checkStr(result.token)) {
                    App.getApp().setToken(result.token);
                    UserInfo.saveToken(result.token, LoginActivity.this);
                }
                LoginResult.DatasBean datasBean = result.datas;
                if (datasBean != null) {
                    //这里注意顺序 不能错
                    App.getApp().setUid(datasBean._id);
                    UserInfo.saveUid(datasBean._id, LoginActivity.this);
                    UserInfo.saveUserInfo(datasBean, LoginActivity.this);

                    if (datasBean.role != null) {
                        List<String> view_roles = datasBean.role.view_roles;
                        if (view_roles != null) {
                            UserInfo.saveRole(view_roles, LoginActivity.this);
                        }
                    }
                }
                //记录一下登录时间，以便于登录信息过期
                PreferenceUtil pu = new PreferenceUtil(LoginActivity.this, "config");
                pu.putLong("login_time", System.currentTimeMillis());

                showToast("登录成功");
                //去主页
                startActivity(MainActivity.class);
                finish();
            }


        }
    }


    @Override
    public void onKeyboardStateChanged(int state) {
        if (state == KeyboardListenRelativeLayout.KEYBOARD_STATE_SHOW) {

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    login_layout.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }, 300);

        }
    }


}
