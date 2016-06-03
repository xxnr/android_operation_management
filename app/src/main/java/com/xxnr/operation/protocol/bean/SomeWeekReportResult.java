package com.xxnr.operation.protocol.bean;

import com.xxnr.operation.protocol.ResponseResult;

import java.util.List;

/**
 * Created by 何鹏 on 2016/5/26.
 */
public class SomeWeekReportResult extends ResponseResult {


    /**
     * registeredUserCount : 0
     * orderCount : 80
     * paidOrderCount : 54
     * paidAmount : 2712191.1
     * signedUserCount : 6
     * completedOrderCount : 19
     * newPotentialCustomerCount : 1
     * potentialCustomerRegisteredCount : 0
     * sequenceNo : 28
     * week : 20160523
     */

    public List<WeeklyReportsBean> weeklyReports;

    public static class WeeklyReportsBean {
        public int registeredUserCount;
        public int orderCount;
        public int paidOrderCount;
        public float paidAmount;
        public int signedUserCount;
        public int completedOrderCount;
        public int newPotentialCustomerCount;
        public int potentialCustomerRegisteredCount;
        public int sequenceNo;
        public String week;
    }
}
