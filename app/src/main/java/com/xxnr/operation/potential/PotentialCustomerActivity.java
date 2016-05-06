package com.xxnr.operation.potential;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

import com.xxnr.operation.base.BaseActivity;
import com.xxnr.operation.R;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.protocol.bean.PotentialListResult;
import com.xxnr.operation.utils.IntentUtil;
import com.xxnr.operation.widget.CustomSwipeRefreshLayout;

import java.util.List;

/**
 * Created by CAI on 2016/5/4.
 */
public class PotentialCustomerActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, CustomSwipeRefreshLayout.OnLoadListener {
    private ListView listView;
    private CustomSwipeRefreshLayout swipeRefreshLayout;
    private int page = 1;

    private boolean isOver = false;
    private PotentialListAdapter adapter;

    @Override
    public int getLayout() {
        return R.layout.potentail_customer_layout;
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

        swipeRefreshLayout = (CustomSwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setOnLoadListener(this);
        // 顶部刷新的样式
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        listView = (ListView) findViewById(R.id.listView);

    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.GET_POTENTIAL_LIST) {
            if (req.getData().getStatus().equals("1000")) {
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setLoading(false);
                PotentialListResult listResult = (PotentialListResult) req.getData();
                List<PotentialListResult.PotentialCustomersBean> potentialCustomers = listResult.potentialCustomers;
                if (potentialCustomers != null && !potentialCustomers.isEmpty()) {
                    isOver = false;
                    if (page == 1) {
                        if (adapter == null) {
                            adapter = new PotentialListAdapter(PotentialCustomerActivity.this, potentialCustomers);
                            listView.setAdapter(adapter);
                        } else {
                            adapter.clear();
                            adapter.addAll(potentialCustomers);
                        }
                    } else {
                        adapter.addAll(potentialCustomers);
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


    @Override
    public void onRefresh() {
        page = 1;
        getData();
    }

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
