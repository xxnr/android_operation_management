package com.xxnr.operation.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.xxnr.operation.BaseActivity;
import com.xxnr.operation.R;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.protocol.bean.GetPublicKeyResult;
import com.xxnr.operation.utils.RSAUtil;
import com.xxnr.operation.utils.StringUtil;
import com.xxnr.operation.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CAI on 2016/4/28.
 */
public class LoginActivity extends BaseActivity {

    private EditText login_name_et, login_pass_et;
    private String phone_number,password;

    @Override
    public int getLayout() {
        return R.layout.login_layout;
    }

    public void initView() {
        login_name_et = (EditText) findViewById(R.id.login_name_et);
        login_pass_et = (EditText) findViewById(R.id.login_pass_et);
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
                if(!StringUtil.checkStr(phone_number)){
                  showToast("请输入用户名");
                    return;
                }
                if(!Utils.isMobileNum(phone_number)){
                    showToast("请输入正确的手机号");
                    return;
                }
                password =login_pass_et.getText().toString();
                if(!StringUtil.checkStr(password)){
                    showToast("请输入密码");
                    return;
                }

                execApi(ApiType.GET_PUBLIC_KEY.setMethod(ApiType.RequestMethod.GET),new RequestParams());

                break;
        }
    }

    @Override
    public void onResponsed(Request req) {
            if(req.getApi()==ApiType.GET_PUBLIC_KEY){
                if(req.getData().getStatus().equals("1000")){
                    GetPublicKeyResult data = (GetPublicKeyResult) req.getData();
                    try {
                      RSAUtil.encryptByPublicKey(password,
                                RSAUtil.generatePublicKey(data.getPublic_key()));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
    }
}
