package com.xxnr.operation.modules.datacenter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.xxnr.operation.R;
import com.xxnr.operation.modules.BaseActivity;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.utils.RndLog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by CAI on 2016/5/25.
 */
public class WeekPickerActivity extends BaseActivity {
    @Override
    public int getLayout() {
        return R.layout.activity_week_picker;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("选择日期");
        initView();
        initData();
    }

    @Override
    public void OnViewClick(View v) {

    }

    private void initView() {

    }

    /**
     * 初始化周列表
     */
    private void initData() {

        Calendar calendarStart = Calendar.getInstance();//开始时间
        Calendar calendarEnd = Calendar.getInstance();//结束时间
        calendarStart.setTime(DataCenterUtils.stringtoDate(DataCenterUtils.startDateStr, DataCenterUtils.CHINESE_DATE_FORMAT));//2009-6-1
        calendarEnd.setTime(DataCenterUtils.getCurrDate());//

        List<Date> returnList = new ArrayList<>();
        while (calendarStart.compareTo(calendarEnd) < 1) {
            returnList.add(calendarStart.getTime());
            calendarStart.add(Calendar.DATE, 1);//每次循环增加一天
        }

        List<WeekBean> weekBeanList = new ArrayList<>();
        int weekNumOfYearOld = -1;

        for (Date key : returnList) {
            int weekNumOfYear = DataCenterUtils.getWeekNumOfYear(key);
            if (weekNumOfYear != weekNumOfYearOld) {
                WeekBean weekBean = new WeekBean();
                weekBean.indexOfYear = weekNumOfYear;
                weekBean.dateBegin = key;
                weekBean.dateEnd = DataCenterUtils.getYearWeekEndDay(key);
                weekBean.Year = DataCenterUtils.getYear(key);
                weekBeanList.add(weekBean);
            }
            weekNumOfYearOld = weekNumOfYear;
        }
        for (WeekBean key : weekBeanList) {
            RndLog.d("WeekReportFragment", key.Year + "年" + key.indexOfYear + "周(" +
                    DataCenterUtils.dateToString(key.dateBegin, "MM月dd日")
                    + "-" + DataCenterUtils.dateToString(key.dateEnd, "MM月dd日") + ")");
        }
    }


    @Override
    public void onResponsed(Request req) {

    }
}
