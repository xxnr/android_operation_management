package com.xxnr.operation.protocol;

/**
 * Created by CAI on 2016/5/5.
 */
public interface ApiInterface {
    /**
     * 客户详情
     */
    String GET_USERS_DETAIL = "/$manager/api/users";
    /**
     * 客户申请县级信息
     */
    String GET_RSC_INFO = "/$manager/api/v2.2/RSCInfo";
    /**
     * 潜在客户详情
     */
    String GET_POTENTIAL_DETAIL = "/$manager/api/v2.1/potentialCustomer";
    /**
     * 订单详情
     */
    String GET_ORDER_DETAIL = "/$manager/api/orders";
}
