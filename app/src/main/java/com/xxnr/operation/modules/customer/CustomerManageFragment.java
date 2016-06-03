package com.xxnr.operation.modules.customer;

import android.os.Bundle;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.xxnr.operation.modules.BaseFragment;
import com.xxnr.operation.MsgID;
import com.xxnr.operation.R;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.developTools.msg.MsgCenter;
import com.xxnr.operation.developTools.msg.MsgListener;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.protocol.bean.CustomerListResult;
import com.xxnr.operation.utils.ListViewAnimationUtils;
import com.xxnr.operation.utils.PullToRefreshUtils;

import java.util.List;

/**
 * Created by 何鹏 on 2016/5/3.
 */
public class CustomerManageFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2 {
    private PullToRefreshListView listView;
    private int position = 0;
    private int page = 1;
    private int query = 0;
    private CustomerListAdapter adapter;


    @Override
    public View InItView() {

        View view = inflater.inflate(R.layout.fragment_list, null);

        listView = (PullToRefreshListView) view.findViewById(R.id.custom_list_listView);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
        //设置刷新的文字
        PullToRefreshUtils.setFreshText(listView);

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
        getData();

        //修改用户信息通知
        MsgCenter.addListener(new MsgListener() {

            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                listView.getRefreshableView().setSelection(0);
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
        params.put("token", App.getApp().getToken());
        execApi(ApiType.GET_USERS_LIST.setMethod(ApiType.RequestMethod.GET), params);
    }


    @Override
    public void OnViewClick(View v) {

    }


    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.GET_USERS_LIST) {
            listView.onRefreshComplete();
            if (req.getData().getStatus().equals("1000")) {
                CustomerListResult customerListResult = (CustomerListResult) req.getData();
                if (customerListResult.users != null) {
                    List<CustomerListResult.Users.ItemsBean> itemsBeen = customerListResult.users.items;
                    if (itemsBeen != null && !itemsBeen.isEmpty()) {
                        if (page == 1) {
                            if (adapter == null) {
                                adapter = new CustomerListAdapter(activity, itemsBeen);
                                ListViewAnimationUtils.setListViewAnimationAndAdapter(listView.getRefreshableView(),adapter);
                            } else {
                                adapter.clear();
                                adapter.addAll(itemsBeen);
                            }
                        } else {
                            if (adapter != null) {
                                adapter.addAll(itemsBeen);
                            }
                        }
                    } else {
                        if (page == 1) {
                            if (adapter != null) {
                                adapter.clear();
                            }
                        } else {
                            showToast("没有更多客户");
                            page--;
                        }
                    }

                }

            }

        }
    }


    //下拉刷新
    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        PullToRefreshUtils.setFreshClose(refreshView);
        page = 1;
        getData();
    }

    //上拉加载更多
    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        PullToRefreshUtils.setFreshClose(refreshView);
        page++;
        getData();
    }
}
