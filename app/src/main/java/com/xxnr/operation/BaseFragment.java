/**
 *
 */
package com.xxnr.operation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.OnApiDataReceivedCallback;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.utils.Utils;


/**
 * 项目名称：newFarmer63 类名称：BaseFragment 类描述： 创建人：王蕾 创建时间：2015-6-3 上午9:29:34 修改备注：
 */
public abstract class BaseFragment extends Fragment implements
        OnApiDataReceivedCallback, View.OnClickListener {

    public BaseActivity activity;
    public LayoutInflater inflater;

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
        if (req.isSuccess()) {
            onResponsed(req);
        } else {
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


}
