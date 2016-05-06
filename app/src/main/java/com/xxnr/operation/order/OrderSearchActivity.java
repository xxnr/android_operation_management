package com.xxnr.operation.order;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;

import com.xxnr.operation.base.BaseActivity;
import com.xxnr.operation.R;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.protocol.bean.OrderListResult;
import com.xxnr.operation.utils.StringUtil;
import com.xxnr.operation.widget.ClearEditText;
import com.xxnr.operation.widget.CustomSwipeRefreshLayout;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by CAI on 2016/5/4.
 */
public class OrderSearchActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, CustomSwipeRefreshLayout.OnLoadListener,OrderListAdapter.ItemOnClickListener {
    private ClearEditText rsc_search_edit;
    private TextView rsc_search_text;
    private ListView custom_search_list_listView;
    private CustomSwipeRefreshLayout swipeRefreshLayout;
    private int page = 1;

    private OrderListAdapter adapter;

    private boolean isOver = false;


    @Override
    public int getLayout() {
        return R.layout.customer_search_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        initView();


        rsc_search_text.setText("取消");
        rsc_search_text.setOnClickListener(this);

        rsc_search_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“搜索”键*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }
                    String searchText = rsc_search_edit.getText().toString().trim();
                    if (StringUtil.checkStr(searchText)) {
                        page = 1;
                        getData();
                    }
                    return true;
                }
                return false;
            }
        });

        rsc_search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtil.checkStr(s.toString())) {
                    page = 1;
                    getData();
                }
            }
        });


        //软件盘自动弹出
        rsc_search_edit.requestFocus(); //edittext是一个EditText控件
        Timer timer = new Timer(); //设置定时器
        timer.schedule(new TimerTask() {
            @Override
            public void run() { //弹出软键盘的代码
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(rsc_search_edit, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }, 300); //设置300毫秒的时
    }


    private void initView() {
        rsc_search_edit = (ClearEditText) findViewById(R.id.rsc_search_edit);
        rsc_search_edit.setHint("下单人手机号/收货人手机号/订单号");
        rsc_search_text = (TextView) findViewById(R.id.rsc_search_text);
        custom_search_list_listView = (ListView) findViewById(R.id.custom_search_list_listView);
        swipeRefreshLayout = (CustomSwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setOnLoadListener(this);
        // 顶部刷新的样式
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
    }

    private void getData() {
        String searchText = rsc_search_edit.getText().toString().trim();
        RequestParams params = new RequestParams();
        params.put("token", App.getApp().getToken());
        params.put("page", page);
        params.put("max", 20);
        params.put("search", searchText);
        execApi(ApiType.GET_ORDER_LIST.setMethod(ApiType.RequestMethod.GET), params);

    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.rsc_search_text:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                finish();
                break;
        }
    }

    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.GET_ORDER_LIST) {
            swipeRefreshLayout.setLoading(false);
            swipeRefreshLayout.setRefreshing(false);
            if (req.getData().getStatus().equals("1000")) {
                OrderListResult listResult = (OrderListResult) req.getData();
                if (listResult.datas != null) {
                    List<OrderListResult.DatasBean.ItemsBean> items = listResult.datas.items;
                    if (items != null && !items.isEmpty()) {
                        isOver = false;
                        if (page == 1) {
                            if (adapter == null) {
                                adapter = new OrderListAdapter(OrderSearchActivity.this, items,this);
                                custom_search_list_listView.setAdapter(adapter);
                            } else {
                                adapter.clear();
                                adapter.addAll(items);
                            }
                        } else {
                            adapter.addAll(items);
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

    @Override
    public void itemOnClickListener(View view, OrderListResult.DatasBean.ItemsBean itemsBean) {

    }
}
