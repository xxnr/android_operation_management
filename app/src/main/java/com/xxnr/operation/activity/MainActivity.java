package com.xxnr.operation.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioGroup;

import com.xxnr.operation.BaseActivity;
import com.xxnr.operation.R;
import com.xxnr.operation.protocol.Request;

/**
 * Created by CAI on 2016/4/22.
 */
public class MainActivity extends BaseActivity  {


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
    }


    private void initView() {


    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {

    }





}
