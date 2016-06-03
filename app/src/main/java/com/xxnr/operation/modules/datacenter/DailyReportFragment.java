package com.xxnr.operation.modules.datacenter;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxnr.operation.MsgID;
import com.xxnr.operation.R;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.developTools.msg.MsgCenter;
import com.xxnr.operation.developTools.msg.MsgListener;
import com.xxnr.operation.modules.BaseFragment;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.protocol.bean.DailyReportResult;
import com.xxnr.operation.protocol.bean.StatisticReportResult;
import com.xxnr.operation.utils.BgSelectorUtils;
import com.xxnr.operation.utils.IntentUtil;
import com.xxnr.operation.utils.StringUtil;

import java.util.Date;

/**
 * Created by 何鹏 on 2016/5/18.
 */
public class DailyReportFragment extends BaseFragment {
    private TextView date_picker;

    private TextView reg_count_1;
    private LinearLayout reg_count_ll_1;
    private TextView order_count_1;
    private LinearLayout order_count_ll_1;
    private TextView order_paid_count_1;
    private LinearLayout order_paid_count_ll_1;
    private TextView pay_price_1;
    private LinearLayout pay_price_ll_1;
    private TextView reg_count_2;
    private TextView order_count_2;
    private TextView order_paid_count_2;
    private TextView pay_price_2;
    private LinearLayout statistic_ll;
    private TextView date_after, date_before;
    private ColorStateList colorStateList;


    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.date_picker:
                if (StringUtil.checkStr(date_picker.getText().toString())) {
                    Bundle bundle = new Bundle();
                    bundle.putString("selectDate", date_picker.getText().toString());
                    bundle.putBoolean("isRange", false);
                    IntentUtil.activityForward(activity, DailyPickerActivity.class, bundle, false);
                }
                break;
            case R.id.date_before://前一天
                if (StringUtil.checkStr(date_picker.getText().toString())) {
                    String res = DataCenterUtils.dateAddOrDec(date_picker.getText().toString(), -1);
                    if (res != null) {
                        if (DataCenterUtils.stringtoDate(res, DataCenterUtils.CHINESE_DATE_FORMAT).getTime()
                                < DataCenterUtils.stringtoDate(DataCenterUtils.startDateStr, DataCenterUtils.CHINESE_DATE_FORMAT).getTime()) {
                            showToast("已经是第一天");
                            return;
                        } else {
                            date_picker.setText(res);
                            getDaily(DataCenterUtils.changeDateFormat(res));
                        }
                    }

                }
                break;
            case R.id.date_after://后一天
                if (StringUtil.checkStr(date_picker.getText().toString())) {
                    String res = DataCenterUtils.dateAddOrDec(date_picker.getText().toString(), 1);
                    if (res != null) {
                        if (DataCenterUtils.stringtoDate(res, DataCenterUtils.CHINESE_DATE_FORMAT).getTime()
                                >= DataCenterUtils.getCurrDate().getTime()) {
                            showToast("已经是最后一天");
                            return;
                        } else {
                            date_picker.setText(res);
                            getDaily(DataCenterUtils.changeDateFormat(res));
                        }
                    }
                }
                break;
            case R.id.reg_count_ll_1:
                Bundle bundle1 = new Bundle();
                bundle1.putString("title", "注册用户数");
                bundle1.putString("dateStr", date_picker.getText().toString());
                IntentUtil.activityForward(activity, DailyDetailActivity.class, bundle1, false);
                break;
            case R.id.order_count_ll_1:
                Bundle bundle2 = new Bundle();
                bundle2.putString("title", "订单数");
                bundle2.putString("dateStr", date_picker.getText().toString());
                IntentUtil.activityForward(activity, DailyDetailActivity.class, bundle2, false);
                break;
            case R.id.order_paid_count_ll_1:
                Bundle bundle3 = new Bundle();
                bundle3.putString("title", "付款订单数");
                bundle3.putString("dateStr", date_picker.getText().toString());
                IntentUtil.activityForward(activity, DailyDetailActivity.class, bundle3, false);
                break;
            case R.id.pay_price_ll_1:
                Bundle bundle4 = new Bundle();
                bundle4.putString("title", "已支付金额");
                bundle4.putString("dateStr", date_picker.getText().toString());
                IntentUtil.activityForward(activity, DailyDetailActivity.class, bundle4, false);
                break;
        }

    }

    @Override
    public View InItView() {

        View view = inflater.inflate(R.layout.fragment_daily_report, null);

        date_picker = (TextView) view.findViewById(R.id.date_picker);
        date_before = (TextView) view.findViewById(R.id.date_before);
        date_after = (TextView) view.findViewById(R.id.date_after);
        date_picker.setOnClickListener(this);
        date_before.setOnClickListener(this);
        date_after.setOnClickListener(this);
        //设置selector
        colorStateList = BgSelectorUtils.createColorStateList(activity.getResources().getColor(R.color.default_black), activity.getResources().getColor(R.color.deep_black));
        if (colorStateList != null) {
            date_before.setTextColor(colorStateList);
            date_after.setTextColor(colorStateList);
        }


        statistic_ll = (LinearLayout) view.findViewById(R.id.statistic_ll);

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


        this.reg_count_2 = (TextView) view.findViewById(R.id.reg_count_2);
        this.order_count_2 = (TextView) view.findViewById(R.id.order_count_2);
        this.order_paid_count_2 = (TextView) view.findViewById(R.id.order_paid_count_2);
        this.pay_price_2 = (TextView) view.findViewById(R.id.pay_price_2);


        //默认展示今天的日期
        String today = DataCenterUtils.getCurrDateStr(DataCenterUtils.CHINESE_DATE_FORMAT);
        date_picker.setText(today);
        date_after.setTextColor(getResources().getColor(R.color.date_unable));//默认当天不可点击

        //如果是今天才展示累计数据
        date_picker.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(DataCenterUtils.getCurrDateStr(DataCenterUtils.CHINESE_DATE_FORMAT))) {
                    statistic_ll.setVisibility(View.VISIBLE);
                    date_after.setTextColor(getResources().getColor(R.color.date_unable));
                } else {
                    statistic_ll.setVisibility(View.GONE);
                    if (colorStateList != null) {
                        date_after.setTextColor(colorStateList);
                    }
                }

                if (s.toString().equals(DataCenterUtils.startDateStr)) {
                    date_before.setTextColor(getResources().getColor(R.color.date_unable));
                } else {
                    if (colorStateList != null) {
                        date_before.setTextColor(colorStateList);
                    }

                }
            }
        });

        //选中日期返回
        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                if (args[0] != null) {
                    String dateToString = DataCenterUtils.dateToString((Date) args[0], DataCenterUtils.CHINESE_DATE_FORMAT);
                    if (StringUtil.checkStr(dateToString)) {
                        date_picker.setText(dateToString);
                        getDaily(DataCenterUtils.changeDateFormat(dateToString));
                    }
                }
            }
        }, MsgID.Date_Select);

        //请求数据

        getDaily(DataCenterUtils.changeDateFormat(date_picker.getText().toString()));
        getStatistic();

        return view;
    }

    //获取某一天的数据
    private void getDaily(String date) {
        showProgressDialog();
        RequestParams params = new RequestParams();
        params.put("token", App.getApp().getToken());
        params.put("date", date);
        execApi(ApiType.GET_DAILY_REPORT.setMethod(ApiType.RequestMethod.GET), params);
    }

    //获取累计数据
    private void getStatistic() {
        RequestParams params = new RequestParams();
        params.put("token", App.getApp().getToken());
        execApi(ApiType.GET_STATISTIC_REPORT.setMethod(ApiType.RequestMethod.GET), params);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (req.getApi() == ApiType.GET_DAILY_REPORT) {
            DailyReportResult reqData = (DailyReportResult) req.getData();
            reg_count_1.setText(reqData.registeredUserCount + "");
            order_count_1.setText(reqData.orderCount + "");
            order_paid_count_1.setText(reqData.paidOrderCount + "");
            pay_price_1.setText("¥" + StringUtil.toTwoString(reqData.paidAmount + ""));

        } else if (req.getApi() == ApiType.GET_STATISTIC_REPORT) {
            StatisticReportResult reqData = (StatisticReportResult) req.getData();
            reg_count_2.setText(reqData.registeredUserCount + "");
            order_count_2.setText(reqData.orderCount + "");
            order_paid_count_2.setText(reqData.completedOrderCount + "");
            pay_price_2.setText("¥" + StringUtil.toTwoString(reqData.paidAmount + ""));
        }

    }


}
