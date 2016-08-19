/**
 *
 */
package com.xxnr.operation.modules;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;


import com.xxnr.operation.UserInfo;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.OnApiDataReceivedCallback;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.utils.Utils;
import com.xxnr.operation.widget.CustomProgressDialog;
import com.xxnr.operation.widget.CustomToast;


/**
 * 项目名称：newFarmer63 类名称：BaseFragment 类描述： 创建人：王蕾 创建时间：2015-6-3 上午9:29:34 修改备注：
 */
public abstract class BaseFragment extends Fragment implements
        OnApiDataReceivedCallback, View.OnClickListener {

    public BaseActivity activity;
    public LayoutInflater inflater;

    private Dialog progressDialog;

    private CustomToast customToast;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (BaseActivity) getActivity();
        this.inflater = inflater;
        return InItView();
    }

    @Override
    public final void onClick(View v) {
        // 过滤要处理的控件  无法连续点击一个控件
        if (Utils.isFastClick()) {
            return;
        }
        OnViewClick(v);
    }


    /**
     * 控件的点击事件
     *
     * @param v
     */
    public abstract void OnViewClick(View v);

    /**
     * 加载本地布局文件
     */
    public abstract View InItView();


    @Override
    public void onResponse(Request req) {
        disMissDialog();
        if (req.getData().getStatus().equals("1401")) {
            req.showErrorMsg();
            UserInfo.tokenToLogin(activity);
        } else if (req.isSuccess()) {
            onResponsed(req);
        } else {
            if (req.getApi()==ApiType.GET_AGENT_RANK_TOTAL){
                onResponsed(req);
            }

            req.showErrorMsg();
        }
    }

    public abstract void onResponsed(Request req);

    /**
     * 短时间显示Toast
     *
     * @param info 显示的内容
     */
    public void showToast(String info) {
        App.getApp().showToast(info);
    }


    /*
     * 执行网络请求
     *
     * @param api
     *
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            progressDialog = null;
        }
        progressDialog = CustomProgressDialog
                .createLoadingDialog(activity, msg, Color.parseColor("#000000"));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        try {
            progressDialog.show();
        } catch (WindowManager.BadTokenException exception) {
            exception.printStackTrace();
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
        CustomToast.Builder builder = new CustomToast.Builder(activity);
        customToast = builder.setMessage(msg).setMessageImage(imgRes).create();
        customToast.show();

    }




}
