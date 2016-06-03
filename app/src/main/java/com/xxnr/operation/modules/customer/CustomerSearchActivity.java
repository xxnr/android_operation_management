package com.xxnr.operation.modules.customer;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.xxnr.operation.modules.BaseActivity;
import com.xxnr.operation.R;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.protocol.bean.CustomerListResult;
import com.xxnr.operation.utils.PullToRefreshUtils;
import com.xxnr.operation.utils.StringUtil;
import com.xxnr.operation.widget.ClearEditText;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 何鹏 on 2016/5/4.
 */
public class CustomerSearchActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2 {
    private ClearEditText rsc_search_edit;
    private TextView rsc_search_text;
    private PullToRefreshListView custom_search_list_listView;


    private CustomerListAdapter adapter;

    private int page = 1;
    private RelativeLayout empty_View;

    @Override
    public int getLayout() {
        return R.layout.activity_search;
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
                } else {
                    if (adapter != null) {
                        adapter.clear();
                    }
                    empty_View.setVisibility(View.GONE);
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
        rsc_search_text = (TextView) findViewById(R.id.rsc_search_text);
        custom_search_list_listView = (PullToRefreshListView) findViewById(R.id.custom_search_list_listView);

        empty_View = (RelativeLayout) findViewById(R.id.empty_View);
        empty_View.setVisibility(View.GONE);
        ImageView empty_image = (ImageView) findViewById(R.id.empty_image);
        empty_image.setImageResource(R.mipmap.none_customer_icon);
        TextView empty_text = (TextView) findViewById(R.id.empty_text);
        empty_text.setText("未查找到相关用户");

        custom_search_list_listView.setOnRefreshListener(this);
        custom_search_list_listView.setMode(PullToRefreshBase.Mode.DISABLED);
    }

    private void getData() {
        String searchText = rsc_search_edit.getText().toString().trim();
        RequestParams params = new RequestParams();
        params.put("page", page);
        params.put("search", searchText);
        params.put("max", 20);
        params.put("token", App.getApp().getToken());
        execApi(ApiType.GET_USERS_LIST.setMethod(ApiType.RequestMethod.GET), params);
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
        if (req.getApi() == ApiType.GET_USERS_LIST) {
            custom_search_list_listView.onRefreshComplete();
            if (req.getData().getStatus().equals("1000")) {
                CustomerListResult customerListResult = (CustomerListResult) req.getData();
                if (customerListResult.users != null) {
                    List<CustomerListResult.Users.ItemsBean> itemsBeen = customerListResult.users.items;
                    if (itemsBeen != null && !itemsBeen.isEmpty()) {
                        custom_search_list_listView.setMode(PullToRefreshBase.Mode.BOTH);
                        empty_View.setVisibility(View.GONE);
                        if (page == 1) {
                            if (adapter == null) {

                                adapter = new CustomerListAdapter(CustomerSearchActivity.this, itemsBeen);
                                custom_search_list_listView.setAdapter(adapter);
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
                            custom_search_list_listView.setMode(PullToRefreshBase.Mode.DISABLED);
                            empty_View.setVisibility(View.VISIBLE);
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


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (this.getCurrentFocus() != null) {
                    if (this.getCurrentFocus().getWindowToken() != null) {
                        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0); //强制隐藏键盘
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.dispatchTouchEvent(event);
    }
}
