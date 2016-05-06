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



}
