package com.xxnr.operation;


import com.xxnr.operation.protocol.ApiType;

/**
 * 广播ID
 *
 * @author Bruce.Wang
 */
public interface MsgID {

    String IP = ApiType.url;
    String UP_DATE_CUSTOMER = "UP_DATE_CUSTOMER";//修改或者更新了用户信息
    String UP_DATE_ORDER = "UP_DATE_ORDER";//修改或者更新了订单信息
    String select_tab = "select_tab";//选中tab
    String Date_Select = "Date_Select";//选中日期
    String Date_Select_Range = "Date_Select_Range";//选中日期区间
    String Week_Select = "Week_Select";//选中周期
    String Week_Range_Select = "Week_Range_Select";//选中周期区间





}
