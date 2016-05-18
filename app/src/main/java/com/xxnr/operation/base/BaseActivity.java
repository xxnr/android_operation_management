package com.xxnr.operation.base;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.xxnr.operation.LoginActivity;
import com.xxnr.operation.R;
import com.xxnr.operation.RndApplication;
import com.xxnr.operation.UserInfo;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.OnApiDataReceivedCallback;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.utils.RndLog;
import com.xxnr.operation.utils.Utils;
import com.xxnr.operation.widget.CustomProgressDialog;
import com.xxnr.operation.widget.CustomToast;

public abstract class BaseActivity extends AppCompatActivity implements
        OnClickListener, OnApiDataReceivedCallback {


    public String TAG = this.getClass().getSimpleName();

    private boolean titleLoaded = false; // 标题是否加载成功
    protected View titleLeftView;
    protected View titleRightView;
    private View titleView;
    private TextView tv_title;
    private TextView tvTitleRight;
    private ImageView ivTitleRight;

    private Dialog progressDialog;
    private CustomToast customToast;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RndApplication.unDestroyActivityList.add(this);
        // 屏幕竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (getLayout() != 0) {
            setContentView(getLayout());
        }
        loadTitle();
        OnActCreate(savedInstanceState);

    }

    // -------------------------------------------------------------
    // 重写方法区

    /**
     * 返回本界面的布局文件
     *
     * @return
     */
    public abstract int getLayout();

    /**
     * 子类OnCreate方法
     *
     * @param savedInstanceState
     */
    public abstract void OnActCreate(Bundle savedInstanceState);

    /**
     * 控件的点击事件
     *
     * @param v
     */
    public abstract void OnViewClick(View v);

    /**
     * 网络返回数据回调方法 此方法只处理成功的请求, 不需要弹出错误信息和取消对话框显示,如果需要对错误的请求做单独处理,请重写
     * {@link #onResponsedError}方法
     *
     * @param req
     */
    public abstract void onResponsed(Request req);

    /**
     * 当网络请求数据失败时执行此方法
     *
     * @param req
     */
    public void onResponsedError(Request req) {
        // empty
    }

    // -------------------------------------------------------------
    // 父类方法区

    /**
     * 加载标题
     */
    private void loadTitle() {
        titleLeftView = findViewById(R.id.ll_title_left_view);
        titleRightView = findViewById(R.id.ll_title_right_view);
        titleView = findViewById(R.id.titleView);
        tv_title = (TextView) findViewById(R.id.title_name_text);
        tvTitleRight = (TextView) findViewById(R.id.title_right_text);
        ivTitleRight = (ImageView) findViewById(R.id.title_right_img);
        if (titleView != null) {
            titleLeftView.setOnClickListener(this);
            titleLoaded = true;
            RndLog.i(TAG, "titleView loaded.");
        }

    }

    @Override
    public final void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.ll_title_left_view:
                // 左上角返回按钮
                finish();
                break;
            default:
                break;
        }
        OnViewClick(v);
    }

    @Override
    public final void onResponse(Request req) {
        disMissDialog();

        if (req.getData().getStatus().equals("1401")) {
            req.showErrorMsg();
            UserInfo.tokenToLogin(this);
        } else if (req.isSuccess()) {
            onResponsed(req);
        } else {
            req.showErrorMsg();
        }


    }

    // -------------------------------------------------------------
    // 公共方法区

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        if (titleLoaded) {
            tv_title.setText(title);
        }
    }

    /**
     * 隐藏标题
     */
    public void hideTitle() {
        if (titleLoaded) {
            titleView.setVisibility(View.GONE);
        }
    }

    /**
     * 隐藏左控件
     */
    public void hideLeft() {
        if (titleLoaded) {
            titleLeftView.setVisibility(View.GONE);
        }
    }


    /**
     * 隐藏右控件
     */
    public void hideRight() {
        if (titleLoaded) {
            tvTitleRight.setVisibility(View.GONE);
        }
    }


    /*
     * 显示有图片
     */
    public void showRightImage() {

        if (titleLoaded) {
            ivTitleRight.setVisibility(View.VISIBLE);
        }
    }

    /*
     * 设置右图片
     */
    @SuppressLint("NewApi")
    public void setRightImage(int bg) {
        if (titleLoaded) {
            ivTitleRight.setImageResource(bg);
        }
    }


    /*
    * 隐藏右图片
    */
    @SuppressLint("NewApi")
    public void hideRightImage() {
        if (titleLoaded) {
            ivTitleRight.setVisibility(View.GONE);
        }

    }

    /*
     * 设置右图片的监听事件
     */
    public void setRightViewListener(OnClickListener listener) {
        if (titleLoaded) {
            titleRightView.setOnClickListener(listener);
        }
    }

    /*
     * 得到右图片组件
     */
    public ImageView getRightImageView() {
        return ivTitleRight;
    }

    /*
     * 设置右文本的监听事件
     */
    public void setRightTextViewListener(OnClickListener listener) {
        if (titleLoaded) {
            tvTitleRight.setOnClickListener(listener);
        }
    }

    /*
     * 设置右文本
     */
    public void showRightTextView() {
        if (titleLoaded) {
            tvTitleRight.setVisibility(View.VISIBLE);
        }
    }

    /*
     * 设置右文本显示
     */
    public void setRightTextView(String str) {
        if (titleLoaded) {
            tvTitleRight.setText(str);
        }
    }

    /**
     * 设置左控件的点击事件
     */
    public void setLeftClickListener(OnClickListener listener) {
        if (titleLoaded) {
            titleLeftView.setOnClickListener(listener);
        }
    }

    /**
     * 给控件设置监听
     *
     * @param resId
     * @param listener
     */
    public View setViewClick(int resId, OnClickListener listener) {
        View view = findViewById(resId);
        if (view != null) {
            view.setOnClickListener(listener);
        }
        return view;
    }

    /**
     * 给控件设置监听
     *
     * @param resId
     */
    public View setViewClick(int resId) {
        return setViewClick(resId, this);
    }

    /**
     * 跳转一个界面不传递数据
     *
     * @param clazz
     */
    public void startActivity(Class<? extends BaseActivity> clazz) {
        Intent intent = new Intent();
        intent.setClass(this, clazz);
        startActivity(intent);
    }


    /**
     * 执行网络请求
     *
     * @param api
     * @param params
     */
    public void execApi(final ApiType api, RequestParams params) {
        // 判断是不是通过验证
        final Request req = new Request();
        req.setApi(api);
        req.setParams(params);
        req.executeNetworkApi(this);

    }


    /**
     * 显示正在加载的进度条
     */
    public void showProgressDialog() {
        showProgressDialog("加载中...");
    }

    public void showProgressDialog(String msg) {
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
                progressDialog = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        progressDialog = CustomProgressDialog.createLoadingDialog(this, msg, Color.parseColor("#000000"));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消对话框显示
     */
    public void disMissDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示大号提示框
     */
    public void showCustomToast(String msg, int imgRes) {

        if (customToast != null) {
            customToast.cancel();
        }
        CustomToast.Builder builder = new CustomToast.Builder(this);
        customToast = builder.setMessage(msg).setMessageImage(imgRes).create();
        customToast.show();

    }


    /**
     * 短时间显示Toast
     *
     * @param info 显示的内容
     */
    public void showToast(String info) {
        App.getApp().showToast(info);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        RndApplication.unDestroyActivityList.remove(this);
    }




}
