package com.xxnr.operation.activity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xxnr.operation.BaseActivity;
import com.xxnr.operation.R;
import com.xxnr.operation.UserInfo;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.protocol.bean.GetPublicKeyResult;
import com.xxnr.operation.protocol.bean.LoginResult;
import com.xxnr.operation.utils.RSAUtil;
import com.xxnr.operation.utils.ScreenUtil;
import com.xxnr.operation.utils.StringUtil;
import com.xxnr.operation.widget.ClearEditText;
import com.xxnr.operation.widget.SoftKeyBoardSatusView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CAI on 2016/4/28.
 */
public class LoginActivity extends BaseActivity implements SoftKeyBoardSatusView.SoftkeyBoardListener {

    private ClearEditText login_name_et, login_pass_et;
    private String phone_number, password;
    private int screenHeight;
    private TextView login_bottom;
    private int scroll_dx;
    private LinearLayout login_layout;

    @Override
    public int getLayout() {
        return R.layout.login_layout;
    }

    public void initView() {
        login_name_et = (ClearEditText) findViewById(R.id.login_name_et);
        login_pass_et = (ClearEditText) findViewById(R.id.login_pass_et);
        login_layout = (LinearLayout) findViewById(R.id.login_layout);

        login_bottom = (TextView) findViewById(R.id.login_bottom);

        SoftKeyBoardSatusView satusView = (SoftKeyBoardSatusView) findViewById(R.id.login_soft_status_view);
        screenHeight = ScreenUtil.getScreenHeight(this);
        satusView.setSoftKeyBoardListener(this);
        setViewClick(R.id.login_sure_bt);
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        initView();
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
                            RSAUtil.generatePublicKey(data.getPublic_key())));
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
                //保存用户信息到本地
                UserInfo.saveToken(result.getToken(), LoginActivity.this);
                if (StringUtil.checkStr(result.getToken())) {
                    App.getApp().setToken(result.getToken());
                }
                LoginResult.DatasBean datasBean = result.getDatas();
                if (datasBean != null) {
                    App.getApp().setUid(datasBean.get_id());
                    UserInfo.saveUserInfo(datasBean, LoginActivity.this);
                    UserInfo.saveUid(datasBean.get_id(), LoginActivity.this);
                }
                showToast("登录成功");
                //去主页
                startActivity(MainActivity.class);
                finish();
            }


        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(null != this.getCurrentFocus()){
            /**
             * 点击空白位置 隐藏软键盘
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super .onTouchEvent(event);
    }

    //下面三个方法是 键盘弹出时不隐藏按钮

    @Override
    public void keyBoardStatus(int w, int h, int oldw, int oldh) {

    }

    @Override
    public void keyBoardVisable(int move) {
        int[] location = new int[2];
        login_bottom.getLocationOnScreen(location);
        int btnToBottom = screenHeight - location[1] - login_bottom.getHeight();
        scroll_dx = btnToBottom > move ? 0 : move - btnToBottom;
        login_layout.scrollBy(0, scroll_dx);
    }

    @Override
    public void keyBoardInvisable(int move) {
        login_layout.scrollBy(0, -scroll_dx);
    }
}
