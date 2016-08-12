package com.xxnr.operation.modules.datacenter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.squareup.timessquare.CalendarPickerView;
import com.xxnr.operation.MsgID;
import com.xxnr.operation.R;
import com.xxnr.operation.developTools.msg.MsgCenter;
import com.xxnr.operation.modules.BaseActivity;
import com.xxnr.operation.protocol.Request;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by 何鹏 on 2016/5/18.
 */
public class DailyPickerActivity extends BaseActivity {
    private boolean isRange = false;
    private boolean isLimit = true;

    private int limitDay =7;


    @Override
    public int getLayout() {
        return R.layout.activity_date_picker;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("选择日期");
        final CalendarPickerView pickerView = (CalendarPickerView) findViewById(R.id.calendar_view);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isRange = bundle.getBoolean("isRange");
            isLimit = bundle.getBoolean("isLimit",true);

            if (isLimit){
                limitDay=7;
            }else {
                limitDay=Integer.MAX_VALUE;
            }

            showToast("请选择日期");
            if (isRange) {
                String starDateStr = bundle.getString("starDateStr");
                String endDateStr = bundle.getString("endDateStr");
                initPicker(starDateStr, endDateStr, null, pickerView);
            } else {
                String selectDate = bundle.getString("selectDate");
                initPicker(null, null, selectDate, pickerView);
            }

            pickerView.setOnInvalidDateSelectedListener(null);
            pickerView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
                @Override
                public void onDateSelected(Date date) {
                    if (isRange) {
                        List<Date> selectedDates = pickerView.getSelectedDates();
                        if (selectedDates != null&&!selectedDates.isEmpty()) {
                            if (selectedDates.size() > limitDay) {
                                showToast("不能大于"+limitDay+"天");
                                Date dateStart = selectedDates.get(0);//保存第一个
                                pickerView.reset();
                                pickerView.selectDate(dateStart);
                            } else if (selectedDates.size() == 1) {
                                showToast("请再选择一个日期");
                                Date dateStart = selectedDates.get(0);//保存第一个
                                pickerView.reset();
                                pickerView.selectDate(dateStart);
                            } else {
                                MsgCenter.fireNull(MsgID.Date_Select_Range, selectedDates);
                                finish();
                            }
                        }

                    } else {
                        MsgCenter.fireNull(MsgID.Date_Select, date);
                        finish();
                    }

                }

                @Override
                public void onDateUnselected(Date date) {

                }
            });


        }

    }


    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {

    }

    /**
     * 单选的情况
     */
    private void initPicker(String starDateStr, String endDateStr, String selectDate, CalendarPickerView pickerView) {

        Calendar calendar = Calendar.getInstance();//获得日历对象
        calendar.setTime(DataCenterUtils.getCurrDate());//当前时间
        calendar.add(Calendar.DATE, 1);//把日期往后增加一天.正数往后推,负数往前移动
        Date tomorrow = calendar.getTime(); //这个时间就是日期往后推一天的结果
        try {
            @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat(DataCenterUtils.CHINESE_DATE_FORMAT);
            Date begin = df.parse(DataCenterUtils.startDateStr);
            if (isRange) {
                List<Date> listFormDateStr = DataCenterUtils.getListFormDateStr(starDateStr, endDateStr, DataCenterUtils.CHINESE_DATE_FORMAT);
                if (listFormDateStr != null && !listFormDateStr.isEmpty()) {
                    pickerView.init(begin, tomorrow)
                            .inMode(CalendarPickerView.SelectionMode.RANGE)
                            .withSelectedDates(listFormDateStr);//此处的init包含begin 不包含tomorrow;
                }
            } else {
                Date select = df.parse(selectDate);
                pickerView.init(begin, tomorrow)
                        .inMode(CalendarPickerView.SelectionMode.SINGLE)
                        .withSelectedDate(select);//此处的init包含begin 不包含tomorrow

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}
