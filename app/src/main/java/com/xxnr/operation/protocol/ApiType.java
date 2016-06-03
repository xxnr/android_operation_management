package com.xxnr.operation.protocol;


import com.xxnr.operation.protocol.bean.AgentReportResult;
import com.xxnr.operation.protocol.bean.EveryWeekReportResult;
import com.xxnr.operation.protocol.bean.CustomerDetailResult;
import com.xxnr.operation.protocol.bean.CustomerListResult;
import com.xxnr.operation.protocol.bean.DailyReportResult;
import com.xxnr.operation.protocol.bean.GetPublicKeyResult;
import com.xxnr.operation.protocol.bean.OfflinePayTypeResult;
import com.xxnr.operation.protocol.bean.OrderListResult;
import com.xxnr.operation.protocol.bean.PotentialDetailResult;
import com.xxnr.operation.protocol.bean.PotentialListResult;
import com.xxnr.operation.protocol.bean.RscInfoResult;
import com.xxnr.operation.protocol.bean.LoginResult;
import com.xxnr.operation.protocol.bean.OfflineStateListResult;
import com.xxnr.operation.protocol.bean.OrderDetailResult;
import com.xxnr.operation.protocol.bean.SomeWeekReportResult;
import com.xxnr.operation.protocol.bean.StatisticReportResult;
import com.xxnr.operation.protocol.bean.WeekReportResult;

/**
 * API 请求的类型
 *
 * @author wqz
 */
public enum ApiType {
    /**
     * 获取公钥
     */
    GET_PUBLIC_KEY("/api/v2.0/user/getpubkey", GetPublicKeyResult.class),
    /**
     * 用户登陆
     */
    LOGIN("/manager/api/login", LoginResult.class),

    /**
     * 客户管理列表
     */
    GET_USERS_LIST("/manager/api/users", CustomerListResult.class),
    /**
     * 客户详情
     */
    GET_USERS_DETAIL("", CustomerDetailResult.class),

    /**
     * 客户申请县级信息
     */
    GET_RSC_INFO("", RscInfoResult.class),

    /**
     * 修改客户信息
     */
    CHANGE_USER("/manager/api/users", ResponseResult.class),

    /**
     * 潜在客户列表
     */
    GET_POTENTIAL_LIST("/manager/api/v2.1/potentialCustomer/query", PotentialListResult.class),

    /**
     * 潜在客户详情
     */
    GET_POTENTIAL_DETAIL("", PotentialDetailResult.class),
    /**
     * 订单列表
     */
    GET_ORDER_LIST("/manager/api/orders", OrderListResult.class),
    /**
     * 订单详情
     */
    GET_ORDER_DETAIL("", OrderDetailResult.class),

    /**
     * 线下付款方式
     */
    GET_OFFLINE_PAY_TYPE("/manager/api/getOfflinePayType", OfflinePayTypeResult.class),

    /**
     * 线下支付网点
     */
    GET_OFFLINE_STATE_LIST("/manager/api/v2.2/RSCs", OfflineStateListResult.class),
    /**
     * 审核付款
     */
    CONFIRM_OFFLINE_PAY("/manager/api/orders/confirmOfflinePay", ResponseResult.class),
    /**
     * 发货到服务站
     */
    SKUS_DELIVERY("/manager/api/orders/SKUsDelivery", ResponseResult.class),

    /**
     * 数据中心：今日实时
     */
    GET_DAILY_REPORT("/manager/api/dashboard/getDailyReport", DailyReportResult.class),

    /**
     * 数据中心：累计数据
     */
    GET_STATISTIC_REPORT("/manager/api/dashboard/getStatistic", StatisticReportResult.class),

    /**
     * 数据中心：一周数据
     */
    GET_WEEK_REPORT("/manager/api/dashboard/queryDailyReport", WeekReportResult.class),
    /**
     * 数据中心：经纪人排行
     */
    GET_AGENT_RANK("/manager/api/dashboard/queryAgentReportYesterday", AgentReportResult.class),

    /**
     * 数据中心：获得一周数据
     */
    GET_EVERY_WEEK_REPORT("/manager/api/dashboard/getWeeklyReport", EveryWeekReportResult.class),
    /**
     * 数据中心：获得几周数据
     */
    GET_SOME_WEEK_REPORT("/manager/api/dashboard/queryWeeklyReport", SomeWeekReportResult.class),

    TEST("", ResponseResult.class);

//    private static String server_url = "http://101.200.194.203";
    private static String server_url = "http://www.xinxinnongren.com";


    public static final String url = server_url;
    private String opt;
    private Class<? extends ResponseResult> clazz;
    private RequestMethod requestMethod = RequestMethod.POST;
    private int retryNumber = 1;

    public ApiType setOpt(String opt) {
        this.opt = opt;
        return this;
    }

    public ApiType setMethod(RequestMethod method) {
        requestMethod = method;
        return this;
    }

    ApiType(String opt, Class<? extends ResponseResult> clazz) {
        this.opt = opt;
        this.clazz = clazz;
    }

    ApiType(String opt, RequestMethod requestMethod) {
        this.opt = opt;
        this.requestMethod = requestMethod;
    }

    ApiType(String opt, RequestMethod requestMethod, int retryNumber) {
        this.opt = opt;
        this.requestMethod = requestMethod;
        this.retryNumber = retryNumber;
    }

    public String getOpt() {
        return server_url + opt;
    }


    public Class<? extends ResponseResult> getClazz() {
        return clazz;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public int getRetryNumber() {
        return retryNumber;
    }

    public enum RequestMethod {
        POST("POST"), GET("GET"), POSTJSON("POSTJSON"), PUT("PUT");
        private String requestMethodName;

        RequestMethod(String requestMethodName) {
            this.requestMethodName = requestMethodName;
        }

        public String getRequestMethodName() {
            return requestMethodName;
        }
    }
}
