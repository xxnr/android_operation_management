package com.xxnr.operation.protocol.bean;

import com.xxnr.operation.protocol.ResponseResult;

/**
 * Created by CAI on 2016/5/19.
 */
public class DailyReportResult extends ResponseResult {

    /**
     * registeredUserCount : 0
     * orderCount : 3
     * paidOrderCount : 0
     * paidAmount : 0
     * lastUpdateTime : 2016-05-19T06:01:02.506Z
     */

    public int registeredUserCount;
    public int orderCount;
    public int paidOrderCount;
    public double paidAmount;
    public String lastUpdateTime;
}
