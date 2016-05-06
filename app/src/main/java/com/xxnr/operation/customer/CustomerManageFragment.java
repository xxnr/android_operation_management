package com.xxnr.operation.customer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.xxnr.operation.base.BaseFragment;
import com.xxnr.operation.MsgID;
import com.xxnr.operation.R;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.developTools.msg.MsgCenter;
import com.xxnr.operation.developTools.msg.MsgListener;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.protocol.bean.CustomerListResult;
import com.xxnr.operation.utils.RndLog;
import com.xxnr.operation.utils.StringUtil;
import com.xxnr.operation.widget.CustomSwipeRefreshLayout;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by CAI on 2016/5/3.
 */
public class CustomerManageFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, CustomSwipeRefreshLayout.OnLoadListener {
    private CustomSwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private int position = 0;
    private int page = 1;
    private int query = 0;
    private CustomerListAdapter adapter;
    private boolean isOver = false;


    @Override
    public View InItView() {

        View view = inflater.inflate(R.layout.customer_list_fragment_layout, null);

        swipeRefreshLayout = (CustomSwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setOnLoadListener(this);
        // 顶部刷新的样式
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        listView = (ListView) view.findViewById(R.id.custom_list_listView);

        Bundle bundle = getArguments();
        if (bundle != null) {
            position = bundle.getInt("position");
        }
        if (position == 0) {
            query = 0;
        } else if (position == 1) {
            query = 3;
        }
        showProgressDialog();
        swipeRefreshLayout.setRefreshing(true);
        getData();

        //修改用户信息通知
        MsgCenter.addListener(new MsgListener() {

            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                page = 1;
                getData();
            }
        }, MsgID.UP_DATE_CUSTOMER);

        return view;
    }



    private void getData() {
        RequestParams params = new RequestParams();
        params.put("page", page);
        params.put("query", query);
        params.put("max", 20);
        params.put("token",App.getApp().getToken());
        execApi(ApiType.GET_USERS_LIST.setMethod(ApiType.RequestMethod.GET), params);
    }


    @Override
    public void OnViewClick(View v) {

    }


    @Override
    public void onResponsed(Request req) {
        if (req.getApi()==ApiType.GET_USERS_LIST){
            if (req.getData().getStatus().equals("1000")){
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setLoading(false);
                CustomerListResult customerListResult = (CustomerListResult) req.getData();
                List<CustomerListResult.Users.ItemsBean> itemsBeen = customerListResult.users.items;
                if (itemsBeen != null && !itemsBeen.isEmpty()) {
                    isOver = false;
                    if (page == 1) {
                        if (adapter == null) {
                            adapter = new CustomerListAdapter(activity, itemsBeen);
                            listView.setAdapter(adapter);
                        } else {
                            adapter.clear();
                            adapter.addAll(itemsBeen);
                        }
                    } else {
                        adapter.addAll(itemsBeen);
                    }
                } else {
                    isOver = true;
                    if (page == 1) {
                        if (adapter != null) {
                            adapter.clear();
                        } else {
                            page--;
                        }
                    }
                }
            }

        }
    }

    //下拉刷新
    @Override
    public void onRefresh() {
        page = 1;
        getData();
    }

    //加载更多
    @Override
    public void onLoad() {

        if (!isOver) {
            swipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    page++;
                    getData();
                }
            }, 1000);
        } else {
            swipeRefreshLayout.setLoading(false);
        }

    }
}
