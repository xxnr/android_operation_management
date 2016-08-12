package com.xxnr.operation.modules.datacenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xxnr.operation.MsgID;
import com.xxnr.operation.R;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.developTools.msg.MsgCenter;
import com.xxnr.operation.developTools.msg.MsgListener;
import com.xxnr.operation.modules.BaseActivity;
import com.xxnr.operation.modules.CommonAdapter;
import com.xxnr.operation.modules.CommonViewHolder;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.protocol.bean.WeekReportResult;
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
public class DailyDetailActivity extends BaseActivity {
    private ColumnChartView chart;
    private TextView date_tv;

    private String title;
    private TextView list_title1_tv;
    private TextView list_title2_tv;
    private TextView list_title3_tv;

    private TextView total_count_tv1;
    private TextView total_count_tv2;

    private UnSwipeListView unSwipeListView;
    private ScrollView scrollView;
    private String endDateStr;
    private String startStr;
    private View column_tip_ll;


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
            //设置标题
            endDateStr = bundle.getString("dateStr");
            startStr = DataCenterUtils.dateAddOrDec(endDateStr, -6);
            if (StringUtil.checkStr(endDateStr) && StringUtil.checkStr(startStr)) {
                if (endDateStr.length() > 5 && startStr.length() > 5) {
                    String subEndStr = endDateStr.substring(5);
                    String subStartStr = startStr.substring(5);
                    date_tv.setText(subStartStr + "-" + subEndStr);
                }
            }

            //获取一周数据
            showProgressDialog();
            getData(DataCenterUtils.changeDateFormat(startStr), DataCenterUtils.changeDateFormat(endDateStr));
        }
        //设置标题
        setTitle(title);

        if (title.equals("用户及经纪人")) {
            column_tip_ll.setVisibility(View.VISIBLE);
            list_title2_tv.setVisibility(View.VISIBLE);
            total_count_tv1.setVisibility(View.VISIBLE);
            list_title2_tv.setText("注测用户数");
            list_title3_tv.setText("新经纪人数");

        } else {
            if (title.equals("已支付金额")){
                list_title3_tv.setText("已支付金额(元)");
            }else {
                list_title3_tv.setText(title);
            }
            column_tip_ll.setVisibility(View.GONE);
            list_title2_tv.setVisibility(View.GONE);
            total_count_tv1.setVisibility(View.GONE);
        }
        //选中日期返回
        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                if (args[0] != null) {
                    List<Date> dateList = (List<Date>) args[0];

                    if (!dateList.isEmpty()) {
                        startStr = DataCenterUtils.dateToString(dateList.get(0), DataCenterUtils.CHINESE_DATE_FORMAT);
                        endDateStr = DataCenterUtils.dateToString(dateList.get(dateList.size() - 1), DataCenterUtils.CHINESE_DATE_FORMAT);

                        String subStartStr = DataCenterUtils.dateToString(dateList.get(0), DataCenterUtils.SHORT_DATE_FORMAT);
                        String subEndStr = DataCenterUtils.dateToString(dateList.get(dateList.size() - 1), DataCenterUtils.SHORT_DATE_FORMAT);

                        date_tv.setText(subStartStr + "-" + subEndStr);

                        showProgressDialog();
                        getData(DataCenterUtils.changeDateFormat(startStr), DataCenterUtils.changeDateFormat(endDateStr));
                    }
                }
            }
        }, MsgID.Date_Select_Range);


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

        total_count_tv1=(TextView)findViewById(R.id.total_count_tv1);
        total_count_tv2=(TextView)findViewById(R.id.total_count_tv2);

        unSwipeListView = (UnSwipeListView) findViewById(R.id.unSwipeListView);


        scrollView.setVisibility(View.GONE);
        chart.setZoomEnabled(false);
        chart.setValueSelectionEnabled(true);
        chart.setViewportCalculationEnabled(true);

        setViewClick(R.id.date_tv_ll);


    }

    //获取一周数据
    private void getData(String startDate, String endDate) {

        RequestParams params = new RequestParams();
        params.put("dateStart", startDate);
        params.put("dateEnd", endDate);
        params.put("token", App.getApp().getToken());
        execApi(ApiType.GET_WEEK_REPORT.setMethod(ApiType.RequestMethod.GET), params);

    }


    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.date_tv_ll:
                Bundle bundle = new Bundle();
                bundle.putString("starDateStr", startStr);
                bundle.putString("endDateStr", endDateStr);
                bundle.putBoolean("isRange", true);
                IntentUtil.activityForward(DailyDetailActivity.this, DailyPickerActivity.class, bundle, false);
                break;
        }
    }

    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.GET_WEEK_REPORT) {
            disMissDialog();
            scrollView.setVisibility(View.VISIBLE);
            WeekReportResult reqData = (WeekReportResult) req.getData();
            List<WeekReportResult.DailyReportsBean> dailyReports = reqData.dailyReports;
            if (dailyReports != null && !dailyReports.isEmpty()) {
                //计算总和
                int total_count_1 = 0;
                int total_count_2 = 0;
                float total_count_2f=0f;

                for (int i = 0; i < dailyReports.size(); i++) {
                    WeekReportResult.DailyReportsBean reportsBean = dailyReports.get(i);
                    if (reportsBean!=null){
                        switch (title) {
                            case "用户及经纪人":
                                total_count_1+=reportsBean.registeredUserCount;
                                total_count_2+=reportsBean.agentVerifiedCount;
                                break;
                            case "订单数":
                                total_count_2+=reportsBean.orderCount;
                                break;
                            case "付款订单数":
                                total_count_2+=reportsBean.paidOrderCount;
                                break;
                            case "已支付金额":
                                total_count_2f+=reportsBean.paidAmount;
                                break;
                        }
                    }
                }

                total_count_tv1.setText(total_count_1+"");
                if (total_count_2f!=0f){
                    total_count_tv2.setText(StringUtil.toTwoString(total_count_2f+""));
                }else {
                    total_count_tv2.setText(total_count_2+"");
                }

                Collections.reverse(dailyReports);
                List<Column> columns = new ArrayList<>();
                List<SubcolumnValue> values;
                List<AxisValue> axisValues = new ArrayList<>();
                for (int i = 0; i < dailyReports.size(); i++) {
                    WeekReportResult.DailyReportsBean reportsBean = dailyReports.get(i);
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
                                subcolumnValue1.setValue(reportsBean.paidAmount);
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
                        if (reportsBean.day.length() > 4) {
                            String substring = reportsBean.day.substring(4);//去年份
                            StringBuilder builder = new StringBuilder(substring);
                            builder.insert(2, "/");//月日之间插入斜杠
                            String day;
                            if (builder.charAt(0) == '0') {//去掉月前面的0
                                day = builder.toString().substring(1);
                            } else {
                                day = builder.toString();
                            }
                            axisValues.add(new AxisValue(i).setLabel(day));
                        }
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
                chart.setColumnChartData(data);

                //适配列表
                unSwipeListView.setAdapter(new ContentAdapter(DailyDetailActivity.this, dailyReports));

            }


        }
    }


    class ContentAdapter extends CommonAdapter<WeekReportResult.DailyReportsBean> {


        public ContentAdapter(Context context, List<WeekReportResult.DailyReportsBean> data) {
            super(context, data, R.layout.item_data_detail);
        }

        @Override
        public void convert(CommonViewHolder holder, WeekReportResult.DailyReportsBean dailyReportsBean) {
            if (dailyReportsBean != null) {
                if (holder.getPosition() % 2 != 0) {
                    holder.getView(R.id.item_data_detail_ll).setBackgroundColor(getResources().getColor(R.color.order_title_bg));
                } else {
                    holder.getView(R.id.item_data_detail_ll).setBackgroundColor(getResources().getColor(R.color.white));
                }
                holder.setText(R.id.item_data_detail_date, DataCenterUtils.changeFormat(dailyReportsBean.day, "yyyyMMdd", "yyyy.MM.dd"));

                TextView item_data_detail_content = holder.getView(R.id.item_data_detail_count);
                item_data_detail_content.setVisibility(View.GONE);
                switch (title) {
                    case "用户及经纪人":
                        holder.setText(R.id.item_data_detail_content, dailyReportsBean.agentVerifiedCount + "");
                        item_data_detail_content.setVisibility(View.VISIBLE);
                        item_data_detail_content.setText(dailyReportsBean.registeredUserCount + "");
                        break;
                    case "订单数":
                        holder.setText(R.id.item_data_detail_content, dailyReportsBean.orderCount + "");
                        break;
                    case "付款订单数":
                        holder.setText(R.id.item_data_detail_content, dailyReportsBean.paidOrderCount + "");
                        break;
                    case "已支付金额":
                        holder.setText(R.id.item_data_detail_content, StringUtil.toTwoString(dailyReportsBean.paidAmount + ""));
                        break;
                }


            }
        }
    }


}
