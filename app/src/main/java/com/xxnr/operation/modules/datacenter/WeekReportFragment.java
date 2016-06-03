package com.xxnr.operation.modules.datacenter;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.xxnr.operation.protocol.bean.EveryWeekReportResult;
import com.xxnr.operation.utils.BgSelectorUtils;
import com.xxnr.operation.utils.IntentUtil;
import com.xxnr.operation.utils.StringUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * Created by 何鹏 on 2016/5/24.
 */
public class WeekReportFragment extends BaseFragment {

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

    private int index;

    private List<WeekBean> weekList;
    private ColorStateList colorStateList;


    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.date_picker:

                if (weekList != null) {
                    if (index >= 0 && index < weekList.size()) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("index", index);
                        bundle.putSerializable("weekList", (Serializable) weekList);
                        IntentUtil.activityForward(activity, WeekPickerActivity.class, bundle, false);
                    }
                }

                break;
            case R.id.date_before:
                if (index > 0) {
                    index--;
                    setDate_picker();
                } else {
                    showToast("已经是第一周");
                }
                break;
            case R.id.date_after:
                if (weekList != null) {
                    if (index < weekList.size() - 1) {
                        index++;
                        setDate_picker();
                    } else {
                        showToast("已经是最后一周");
                    }
                }
                break;

            case R.id.reg_count_ll_1:

                if (weekList != null) {
                    if (index >= 0 && index < weekList.size()) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("index", index);
                        bundle.putSerializable("weekList", (Serializable) weekList);
                        bundle.putString("title", "注册用户数");
                        IntentUtil.activityForward(activity, WeekDetailActivity.class, bundle, false);
                    }
                }
                break;
            case R.id.order_count_ll_1:

                if (weekList != null) {
                    if (index >= 0 && index < weekList.size()) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("index", index);
                        bundle.putSerializable("weekList", (Serializable) weekList);
                        bundle.putString("title", "订单数");
                        IntentUtil.activityForward(activity, WeekDetailActivity.class, bundle, false);
                    }
                }
                break;
            case R.id.order_paid_count_ll_1:

                if (weekList != null) {
                    if (index >= 0 && index < weekList.size()) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("index", index);
                        bundle.putSerializable("weekList", (Serializable) weekList);
                        bundle.putString("title", "付款订单数");
                        IntentUtil.activityForward(activity, WeekDetailActivity.class, bundle, false);
                    }
                }
                break;
            case R.id.pay_price_ll_1:
                if (weekList != null) {
                    if (index >= 0 && index < weekList.size()) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("index", index);
                        bundle.putSerializable("weekList", (Serializable) weekList);
                        bundle.putString("title", "已支付金额");
                        IntentUtil.activityForward(activity, WeekDetailActivity.class, bundle, false);
                    }
                }
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

        //设置selector
        colorStateList = BgSelectorUtils.createColorStateList(activity.getResources().getColor(R.color.default_black), activity.getResources().getColor(R.color.deep_black));
        if (colorStateList!=null){
            date_before.setTextColor(colorStateList);
            date_after.setTextColor(colorStateList);
        }


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


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //初始化列表
        weekList = DataCenterUtils.getWeekList();
        if (weekList != null && !weekList.isEmpty()) {
            index = weekList.size() - 1;
        }

        //选中日期返回
        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                if (args[0] != null) {
                    index = (Integer) args[0];
                    setDate_picker();
                }
            }
        }, MsgID.Week_Select);

        //获得本周
        setDate_picker();

    }

    public void setDate_picker() {

        if (weekList != null && !weekList.isEmpty()) {
            if (index >= 0 && index < weekList.size()) {

                WeekBean weekBean = weekList.get(index);
                String subStart = DataCenterUtils.dateToString(weekBean.dateBegin, DataCenterUtils.SHORT_DATE_FORMAT);
                String subEnd = DataCenterUtils.dateToString(weekBean.dateEnd, DataCenterUtils.SHORT_DATE_FORMAT);
                date_picker.setText(subStart + "-" + subEnd);
                if (index == 0) {
                    date_before.setTextColor(getResources().getColor(R.color.date_unable));
                } else if (index == weekList.size() - 1) {
                    date_after.setTextColor(getResources().getColor(R.color.date_unable));
                } else {
                    date_before.setTextColor(colorStateList);
                    date_after.setTextColor(colorStateList);
                }
                //获得周数据
                getData(weekBean.dateBegin);
            }
        }
    }

    //获取本周数据
    private void getData(Date inWeekDate) {
        showProgressDialog();
        RequestParams params = new RequestParams();
        params.put("token", App.getApp().getToken());
        if (inWeekDate != null) {
            String dateToString = DataCenterUtils.dateToString(inWeekDate, DataCenterUtils.UNDERLINE_DATE_FORMAT);
            if (StringUtil.checkStr(dateToString)) {
                params.put("date", dateToString);
            }
        }
        execApi(ApiType.GET_EVERY_WEEK_REPORT.setMethod(ApiType.RequestMethod.GET), params);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResponsed(Request req) {
        if (ApiType.GET_EVERY_WEEK_REPORT == req.getApi()) {
            disMissDialog();

            EveryWeekReportResult reqData = (EveryWeekReportResult) req.getData();
            reg_count_1.setText(reqData.registeredUserCount + "");
            order_count_1.setText(reqData.orderCount + "");
            order_paid_count_1.setText(reqData.paidOrderCount + "");
            pay_price_1.setText("¥" + StringUtil.toTwoString(reqData.paidAmount + ""));

        }
    }




}
