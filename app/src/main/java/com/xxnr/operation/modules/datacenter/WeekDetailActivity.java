package com.xxnr.operation.modules.datacenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xxnr.operation.MsgID;
import com.xxnr.operation.R;
import com.xxnr.operation.developTools.DateUtil;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.developTools.msg.MsgCenter;
import com.xxnr.operation.developTools.msg.MsgListener;
import com.xxnr.operation.modules.BaseActivity;
import com.xxnr.operation.modules.CommonAdapter;
import com.xxnr.operation.modules.CommonViewHolder;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.protocol.bean.SomeWeekReportResult;
import com.xxnr.operation.utils.IntentUtil;
import com.xxnr.operation.utils.StringUtil;
import com.xxnr.operation.widget.UnSwipeListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by 何鹏 on 2016/5/23.
 */
public class WeekDetailActivity extends BaseActivity {
    private ColumnChartView chart;
    private TextView date_tv;
    private String title;

    private TextView list_title1_tv;
    private TextView list_title2_tv;
    private TextView list_title3_tv;

    private TextView total_count_tv1;
    private TextView total_count_tv2;

    private View column_tip_ll;

    private UnSwipeListView unSwipeListView;
    private ScrollView scrollView;
    private String endDateStr;
    private String startStr;

    private int startIndex = -1;
    private int endIndex = -1;


    @Override
    public int getLayout() {
        return R.layout.activity_data_week_detail;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        initView();
        Bundle bundle = getIntent().getExtras();
        title = "";
        if (bundle != null) {
            title = bundle.getString("title");
            List<WeekBean> weekList = (List<WeekBean>) bundle.getSerializable("weekList");
            endIndex = bundle.getInt("index");

            if (weekList != null && weekList.size() >= 6) {
                WeekBean weekBeanEnd = weekList.get(endIndex);
                WeekBean weekBeanStart;
                if (endIndex >= 6) {
                    weekBeanStart = weekList.get(endIndex - 6);
                    startIndex = endIndex - 6;
                } else {
                    weekBeanStart = weekList.get(0);
                    startIndex = 0;
                }

                startStr = DataCenterUtils.dateToString(weekBeanStart.dateBegin, DataCenterUtils.SHORT_DATE_FORMAT);
                endDateStr = DataCenterUtils.dateToString(weekBeanEnd.dateEnd, DataCenterUtils.SHORT_DATE_FORMAT);

                date_tv.setText(startStr + "-" + endDateStr);
                //获取一周数据
                getData(weekBeanStart.dateBegin, weekBeanEnd.dateEnd);
            }

        }
        //设置标题
        setTitle(title);
        if (title.equals("用户及经纪人")) {
            column_tip_ll.setVisibility(View.VISIBLE);
            list_title2_tv.setVisibility(View.VISIBLE);
            total_count_tv1.setVisibility(View.VISIBLE);
            list_title2_tv.setText("注册用户数");
            list_title3_tv.setText("新经纪人数");

        } else {
            if (title.equals("已支付金额")) {
                list_title3_tv.setText("已支付金额(元)");
            } else {
                list_title3_tv.setText(title);
            }
            column_tip_ll.setVisibility(View.GONE);
            list_title2_tv.setVisibility(View.GONE);
            total_count_tv1.setVisibility(View.GONE);
        }


        //选中日期区间返回
        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {

                List<WeekBean> weekList = DataCenterUtils.getWeekList();
                if (args[1] != null) {
                    startIndex = weekList.size() - 1 - (Integer) args[1];
                }
                if (args[0] != null) {
                    endIndex = weekList.size() - 1 - (Integer) args[0];
                }

                WeekBean weekBeanStart = weekList.get(startIndex);
                WeekBean weekBeanEnd = weekList.get(endIndex);

                startStr = DataCenterUtils.dateToString(weekBeanStart.dateBegin, DataCenterUtils.SHORT_DATE_FORMAT);
                endDateStr = DataCenterUtils.dateToString(weekBeanEnd.dateEnd, DataCenterUtils.SHORT_DATE_FORMAT);

                date_tv.setText(startStr + "-" + endDateStr);
                //获取一周数据
                getData(weekBeanStart.dateBegin, weekBeanEnd.dateEnd);


            }
        }, MsgID.Week_Range_Select);


    }


    private void initView() {
        chart = (ColumnChartView) findViewById(R.id.chart);
        date_tv = (TextView) findViewById(R.id.date_tv);
        scrollView = (ScrollView) findViewById(R.id.data_detail_scrollView);


        column_tip_ll = findViewById(R.id.column_tip_ll);
        list_title1_tv = (TextView) findViewById(R.id.list_title1_tv);
        list_title1_tv.setText("日期");
        list_title2_tv = (TextView) findViewById(R.id.list_title2_tv);
        list_title3_tv = (TextView) findViewById(R.id.list_title3_tv);

        total_count_tv1 = (TextView) findViewById(R.id.total_count_tv1);
        total_count_tv2 = (TextView) findViewById(R.id.total_count_tv2);


        unSwipeListView = (UnSwipeListView) findViewById(R.id.unSwipeListView);
        scrollView.setVisibility(View.GONE);
        chart.setZoomEnabled(false);
        chart.setValueSelectionEnabled(true);
        chart.setViewportCalculationEnabled(true);

        setViewClick(R.id.date_tv_ll);


    }

    //获取一周数据
    private void getData(Date inWeekDateStart, Date inWeekDateEnd) {
        showProgressDialog();
        RequestParams params = new RequestParams();
        params.put("token", App.getApp().getToken());
        if (inWeekDateStart != null && inWeekDateEnd != null) {
            String inWeekDateStartStr = DataCenterUtils.dateToString(inWeekDateStart, DataCenterUtils.UNDERLINE_DATE_FORMAT);
            String inWeekDateEndStr = DataCenterUtils.dateToString(inWeekDateEnd, DataCenterUtils.UNDERLINE_DATE_FORMAT);
            if (StringUtil.checkStr(inWeekDateStartStr) && StringUtil.checkStr(inWeekDateEndStr)) {
                params.put("dateStart", inWeekDateStartStr);
                params.put("dateEnd", inWeekDateEndStr);
            }
        }
        execApi(ApiType.GET_SOME_WEEK_REPORT.setMethod(ApiType.RequestMethod.GET), params);
    }


    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.date_tv_ll:
                Bundle bundle = new Bundle();
                bundle.putInt("startIndex", startIndex);
                bundle.putInt("endIndex", endIndex);
                bundle.putBoolean("isRange", true);
                IntentUtil.activityForward(WeekDetailActivity.this, WeekPickerActivity.class, bundle, false);

                break;
        }
    }

    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.GET_SOME_WEEK_REPORT) {
            disMissDialog();
            scrollView.setVisibility(View.VISIBLE);
            SomeWeekReportResult reqData = (SomeWeekReportResult) req.getData();
            List<SomeWeekReportResult.WeeklyReportsBean> weeklyReports = reqData.weeklyReports;
            if (weeklyReports != null && !weeklyReports.isEmpty()) {
                //计算总和
                int total_count_1 = 0;
                int total_count_2 = 0;
                double total_count_2f = 0;

                for (int i = 0; i < weeklyReports.size(); i++) {
                    SomeWeekReportResult.WeeklyReportsBean reportsBean = weeklyReports.get(i);
                    if (reportsBean != null) {
                        switch (title) {
                            case "用户及经纪人":
                                total_count_1 += reportsBean.registeredUserCount;
                                total_count_2 += reportsBean.agentVerifiedCount;
                                break;
                            case "订单数":
                                total_count_2 += reportsBean.orderCount;
                                break;
                            case "付款订单数":
                                total_count_2 += reportsBean.paidOrderCount;
                                break;
                            case "已支付金额":
                                total_count_2f += reportsBean.paidAmount;
                                break;
                        }
                    }
                }

                total_count_tv1.setText(total_count_1 + "");
                if (total_count_2f != 0) {
                    total_count_tv2.setText(StringUtil.toTwoString(total_count_2f + ""));
                } else {
                    total_count_tv2.setText(total_count_2 + "");
                }

                Collections.reverse(weeklyReports);
                List<Column> columns = new ArrayList<>();
                List<SubcolumnValue> values;
                List<AxisValue> axisValues = new ArrayList<>();
                for (int i = 0; i < weeklyReports.size(); i++) {
                    SomeWeekReportResult.WeeklyReportsBean reportsBean = weeklyReports.get(i);
                    if (reportsBean != null) {
                        values = new ArrayList<>();
                        SubcolumnValue subcolumnValue1 = null;
                        SubcolumnValue subcolumnValue2 = null;
                        switch (title) {
                            case "用户及经纪人":
                                subcolumnValue1 = new SubcolumnValue();
                                subcolumnValue1.setValue(reportsBean.registeredUserCount);
                                subcolumnValue2 = new SubcolumnValue(reportsBean.agentVerifiedCount);
                                break;
                            case "订单数":
                                subcolumnValue1 = new SubcolumnValue();
                                subcolumnValue1.setValue(reportsBean.orderCount);
                                break;
                            case "付款订单数":
                                subcolumnValue1 = new SubcolumnValue();
                                subcolumnValue1.setValue(reportsBean.paidOrderCount);
                                break;
                            case "已支付金额":
                                subcolumnValue1 = new SubcolumnValue();
                                subcolumnValue1.setValue((float) reportsBean.paidAmount);
                                break;
                        }
                        values.add(subcolumnValue1);
                        if (subcolumnValue2 != null) {
                            subcolumnValue2.setColor(getResources().getColor(R.color.column_orange));
                            values.add(subcolumnValue2);
                        }
                        Column column = new Column(values);
                        column.setHasLabelsOnlyForSelected(true);
                        columns.add(column);
                        int weekNumOfYear = DataCenterUtils.getWeekNumOfYear
                                (DataCenterUtils.stringtoDate(reportsBean.week, DataCenterUtils.UN_SEPARATOR_SHORT_DATE_FORMAT));
                        axisValues.add(new AxisValue(i).setLabel(weekNumOfYear + "周"));
                    }
                }
                ColumnChartData data = new ColumnChartData(columns);
                Axis axisX = new Axis(axisValues);
                Axis axisY = new Axis().setHasLines(true);
                if (title.equals("已支付金额")) {
                    axisY.setMaxLabelChars(7);
                } else {
                    axisY.setMaxLabelChars(3);
                }
                data.setAxisXBottom(axisX);
                data.setAxisYLeft(axisY);
                data.setValueLabelBackgroundEnabled(false);
                data.setValueLabelsTextColor(getResources().getColor(R.color.deep_black));
                chart.setColumnChartData(data);
                //适配列表
                unSwipeListView.setAdapter(new ContentAdapter(WeekDetailActivity.this, weeklyReports));

            }


        }
    }


    class ContentAdapter extends CommonAdapter<SomeWeekReportResult.WeeklyReportsBean> {


        public ContentAdapter(Context context, List<SomeWeekReportResult.WeeklyReportsBean> data) {
            super(context, data, R.layout.item_data_detail);
        }

        @Override
        public void convert(CommonViewHolder holder, SomeWeekReportResult.WeeklyReportsBean weeklyReportsBean) {
            if (weeklyReportsBean != null) {
                if (holder.getPosition() % 2 != 0) {
                    holder.getView(R.id.item_data_detail_ll).setBackgroundColor(getResources().getColor(R.color.order_title_bg));
                } else {
                    holder.getView(R.id.item_data_detail_ll).setBackgroundColor(getResources().getColor(R.color.white));
                }

                StringBuilder builder = new StringBuilder();
                Date dateBegin = DataCenterUtils.stringtoDate(weeklyReportsBean.week, DataCenterUtils.UN_SEPARATOR_SHORT_DATE_FORMAT);
                int weekNumOfYear = DataCenterUtils.getWeekNumOfYear(dateBegin);//第多少个周
                builder.append(weekNumOfYear).append("周");
                builder.append("(").append(DataCenterUtils.dateToString(dateBegin, DateUtil.SHORT_DATE_FORMAT_DOT));//开始时间
                Date dateEnd = DataCenterUtils.dateAddOrDec(dateBegin, 6);
                if (dateEnd.getTime() > DataCenterUtils.getCurrDate().getTime()) {
                    builder.append("-").append(DataCenterUtils.dateToString(DataCenterUtils.getCurrDate(), DateUtil.SHORT_DATE_FORMAT_DOT)).append(")");//结束时间
                } else {
                    builder.append("-").append(DataCenterUtils.dateToString(dateEnd, DateUtil.SHORT_DATE_FORMAT_DOT)).append(")");//结束时间
                }

                TextView item_data_detail_content = holder.getView(R.id.item_data_detail_count);
                item_data_detail_content.setVisibility(View.GONE);

                holder.setText(R.id.item_data_detail_date, builder.toString());
                switch (title) {
                    case "用户及经纪人":
                        holder.setText(R.id.item_data_detail_content, weeklyReportsBean.agentVerifiedCount + "");
                        item_data_detail_content.setVisibility(View.VISIBLE);
                        item_data_detail_content.setText(weeklyReportsBean.registeredUserCount + "");
                        break;
                    case "订单数":
                        holder.setText(R.id.item_data_detail_content, weeklyReportsBean.orderCount + "");
                        break;
                    case "付款订单数":
                        holder.setText(R.id.item_data_detail_content, weeklyReportsBean.paidOrderCount + "");
                        break;
                    case "已支付金额":
                        holder.setText(R.id.item_data_detail_content, StringUtil.toTwoString(weeklyReportsBean.paidAmount + ""));
                        break;
                }


            }
        }
    }


}
