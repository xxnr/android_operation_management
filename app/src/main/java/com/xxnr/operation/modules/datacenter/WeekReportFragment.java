package com.xxnr.operation.modules.datacenter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxnr.operation.R;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.modules.BaseFragment;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.protocol.bean.CurrentWeekReportResult;
import com.xxnr.operation.utils.IntentUtil;
import com.xxnr.operation.utils.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by CAI on 2016/5/24.
 */
public class WeekReportFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private TextView date_picker;
    private TextView date_after, date_before;

    private TextView reg_count_1;
    private LinearLayout reg_count_ll_1;
    private TextView order_count_1;
    private LinearLayout order_count_ll_1;
    private TextView order_paid_count_1;
    private LinearLayout order_paid_count_ll_1;
    private TextView pay_price_1;
    private LinearLayout pay_price_ll_1;

    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.date_picker:
                IntentUtil.activityForward(activity, WeekPickerActivity.class, null, false);
                break;
        }
    }


    @Override
    public View InItView() {

        View view = inflater.inflate(R.layout.fragment_week_report, null);
        date_picker = (TextView) view.findViewById(R.id.date_picker);
        date_before = (TextView) view.findViewById(R.id.date_before);
        date_after = (TextView) view.findViewById(R.id.date_after);
        date_picker.setOnClickListener(this);
        date_before.setOnClickListener(this);
        date_after.setOnClickListener(this);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        this.reg_count_1 = (TextView) view.findViewById(R.id.reg_count_1);
        this.reg_count_ll_1 = (LinearLayout) view.findViewById(R.id.reg_count_ll_1);
        this.order_count_1 = (TextView) view.findViewById(R.id.order_count_1);
        this.order_count_ll_1 = (LinearLayout) view.findViewById(R.id.order_count_ll_1);
        this.order_paid_count_1 = (TextView) view.findViewById(R.id.order_paid_count_1);
        this.order_paid_count_ll_1 = (LinearLayout) view.findViewById(R.id.order_paid_count_ll_1);
        this.pay_price_1 = (TextView) view.findViewById(R.id.pay_price_1);
        this.pay_price_ll_1 = (LinearLayout) view.findViewById(R.id.pay_price_ll_1);

        reg_count_ll_1.setOnClickListener(this);
        order_count_ll_1.setOnClickListener(this);
        order_paid_count_ll_1.setOnClickListener(this);
        pay_price_ll_1.setOnClickListener(this);


        //获取本周数据
        initDate();
        getData();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void initDate() {

        Date weekBeginDay = DataCenterUtils.getYearWeekBeginDay(new Date());

        String subBeginDay = DataCenterUtils.dateToString(weekBeginDay, DataCenterUtils.SHORT_DATE_FORMAT);
        String subCurrDate = DataCenterUtils.dateToString(DataCenterUtils.getCurrDate(), DataCenterUtils.SHORT_DATE_FORMAT);
        date_picker.setText(subBeginDay + "-" + subCurrDate);
    }



    //获取本周数据
    private void getData() {
        RequestParams params = new RequestParams();
        params.put("token", App.getApp().getToken());
        execApi(ApiType.GET_CURRENT_WEEK.setMethod(ApiType.RequestMethod.GET), params);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResponsed(Request req) {
        if (ApiType.GET_CURRENT_WEEK == req.getApi()) {
            disMissDialog();
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            CurrentWeekReportResult reqData = (CurrentWeekReportResult) req.getData();
            reg_count_1.setText(reqData.registeredUserCount + "");
            order_count_1.setText(reqData.orderCount + "");
            order_paid_count_1.setText(reqData.paidOrderCount + "");
            pay_price_1.setText("¥" + StringUtil.toTwoString(reqData.paidAmount + ""));

        }

    }


    @Override
    public void onRefresh() {

    }


}
