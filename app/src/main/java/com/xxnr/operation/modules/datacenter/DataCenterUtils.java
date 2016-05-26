package com.xxnr.operation.modules.datacenter;

import android.util.Log;

import com.xxnr.operation.developTools.DateUtil;
import com.xxnr.operation.utils.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by CAI on 2016/5/24.
 */
public class DataCenterUtils {
    public static final String startDateStr = "2015年11月01日";
    // 格式：年－月－日
    public static final String CHINESE_DATE_FORMAT = "yyyy年MM月dd日";
    // 格式：年－月－日
    public static final String UNDERLINE_DATE_FORMAT = "yyyy-MM-dd";
    // 格式：年－月－日
    public static final String SHORT_DATE_FORMAT = "MM月dd日";

    public static final long ONE_DAT_TIME = 24 * 60 * 60 * 1000;

    /**
     * 获取当前时间的指定格式
     */
    public static String getCurrDateStr(String format) {
        return dateToString(new Date(), format);
    }

    /**
     * 获取当前时间的指定格式
     */
    public static Date getCurrDate() {
        return new Date();
    }


    /**
     * 把符合日期格式的字符串转换为日期类型
     */
    public static Date stringtoDate(String dateStr, String format) {
        Date d = null;
        SimpleDateFormat formater = new SimpleDateFormat(format);
        try {
            formater.setLenient(false);
            d = formater.parse(dateStr);
        } catch (Exception e) {
            // log.error(e);
            d = null;
        }
        return d;
    }

    /**
     * 把日期转换为字符串
     */
    public static String dateToString(Date date, String format) {
        String result = "";
        SimpleDateFormat formater = new SimpleDateFormat(format);
        try {
            result = formater.format(date);
        } catch (Exception e) {
        }
        return result;
    }


    /**
     * 日期的加减
     */
    public static String dateAddOrDec(String data, int add) {
        SimpleDateFormat df = new SimpleDateFormat(DataCenterUtils.CHINESE_DATE_FORMAT);
        String result = null;
        try {
            Date parse = df.parse(data);
            result = df.format(new Date(parse.getTime() + add * ONE_DAT_TIME));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 日期的加减
     */
    public static Date dateAddOrDec(Date data, int add) {
        return new Date(data.getTime() + add * ONE_DAT_TIME);
    }


    /**
     * 将yyyy年mm月dd日转换成yyyy-mm-dd
     */
    public static String changeDateFormat(String dateStr) {
        if (StringUtil.checkStr(dateStr)) {
            return dateStr.replace("年", "-").replace("月", "-").replace("日", "");
        }
        return null;
    }

    /**
     * 日期字符串转换集合
     */
    public static List<Date> getListFormDateStr(String startDateStr, String endDateStr, String format) {
        List<Date> dateList = new ArrayList<>();
        Date startDate = stringtoDate(startDateStr, format);
        Date endDate = stringtoDate(endDateStr, format);
        if (startDate != null && endDate != null) {
            dateList.add(startDate);
            dateList.add(endDate);
        }
        return dateList;
    }

    /*****************************************
     * @功能 转换格式
     ****************************************/
    public static String changeFormat(String dateStr, String format1, String format2) {
        return dateToString(stringtoDate(dateStr, format1), format2);
    }

    /*****************************************
     * @return interger
     * @功能 计算当前日期某年的第几周
     ****************************************/
    public static int getWeekNumOfYear(Date date) {

        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setMinimalDaysInFirstWeek(4);
        c.setTime(date);

        return c.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 返回日期的年
     *
     * @param date Date
     * @return int
     */
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }


    /**
     * 计算某年某周的结束日期
     */
    public static Date getYearWeekEndDay(Date date) {
        Log.d("DataCenterUtils", "前:" + DataCenterUtils.dateToString(date, CHINESE_DATE_FORMAT));
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        Log.d("DataCenterUtils", "后:" + DataCenterUtils.dateToString(c.getTime(), CHINESE_DATE_FORMAT));
        return c.getTime();
    }


    /**
     * 计算某年某周的开始日期
     */
    public static Date getYearWeekBeginDay(Date date) {

        Log.d("DataCenterUtils", "前:" + DataCenterUtils.dateToString(date, CHINESE_DATE_FORMAT));
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Log.d("DataCenterUtils", "后:" + DataCenterUtils.dateToString(c.getTime(), CHINESE_DATE_FORMAT));
        return c.getTime();

    }


}
