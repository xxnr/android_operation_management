package com.xxnr.operation.protocol.bean;

import com.xxnr.operation.protocol.ResponseResult;

/**
 * Created by CAI on 2016/5/23.
 */
public class StatisticReportResult extends ResponseResult{


    /**
     * registeredUserCount : 20
     * orderCount : 2223
     * completedOrderCount : 51
     * paidAmount : 1.907611801E7
     * serviceStartTime : 2015-11-17
     */

    public int registeredUserCount;
    public int orderCount;
    public int completedOrderCount;
    public double paidAmount;
    public String serviceStartTime;
}
