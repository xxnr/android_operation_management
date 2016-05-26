package com.xxnr.operation.modules.potential;

import android.os.Bundle;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.xxnr.operation.modules.BaseActivity;
import com.xxnr.operation.R;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.protocol.bean.PotentialListResult;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.utils.IntentUtil;
import com.xxnr.operation.utils.ListViewAnimationUtils;
import com.xxnr.operation.utils.PullToRefreshUtils;

import java.util.List;

/**
 * Created by CAI on 2016/5/4.
 */
public class PotentialCustomerActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2 {
    private PullToRefreshListView listView;
    private int page = 1;

    private PotentialListAdapter adapter;

    @Override
    public int getLayout() {
        return R.layout.activity_potentail_customer;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("潜在客户");
        initView();
        showRightImage();
        setRightImage(R.mipmap.search_icon);
        setRightViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.activityForward(PotentialCustomerActivity.this, PotentialSearchActivity.class, null, false);
                int version = Integer.valueOf(android.os.Build.VERSION.SDK);
                if (version > 5) {
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });

        showProgressDialog();
        getData();
    }

    private void getData() {
        RequestParams params = new RequestParams();
        params.put("max", 20);
        params.put("token", App.getApp().getToken());
        params.put("page", page);
        execApi(ApiType.GET_POTENTIAL_LIST.setMethod(ApiType.RequestMethod.GET), params);
    }

    private void initView() {

        listView = (PullToRefreshListView) findViewById(R.id.listView);

        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
        //设置刷新的文字
        PullToRefreshUtils.setFreshText(listView);

    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.GET_POTENTIAL_LIST) {
            listView.onRefreshComplete();
            if (req.getData().getStatus().equals("1000")) {
                PotentialListResult listResult = (PotentialListResult) req.getData();
                List<PotentialListResult.PotentialCustomersBean> potentialCustomers = listResult.potentialCustomers;
                if (potentialCustomers != null && !potentialCustomers.isEmpty()) {
                    if (page == 1) {
                        if (adapter == null) {
                            adapter = new PotentialListAdapter(PotentialCustomerActivity.this, potentialCustomers);
                            ListViewAnimationUtils.setListViewAnimationAndAdapter(listView.getRefreshableView(),adapter);
                        } else {
                            adapter.clear();
                            adapter.addAll(potentialCustomers);
                        }
                    } else {
                        if (adapter != null) {
                            adapter.addAll(potentialCustomers);
                        }
                    }
                } else {
                    if (page == 1) {
                        if (adapter != null) {
                            adapter.clear();
                        }
                    } else {
                        page--;
                        showToast("没有更多客户");
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
