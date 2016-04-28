package com.xxnr.operation.protocol;


import com.xxnr.operation.protocol.bean.GetPublicKeyResult;
import com.xxnr.operation.protocol.bean.LoginResult;

/**
 * API 请求的类型
 *
 * @author wqz
 */
public enum ApiType {
    GET_PUBLIC_KEY("api/v2.0/user/getpubkey/", GetPublicKeyResult.class),
    LOGIN("$manager/api/login/", LoginResult.class),
    TEST("", ResponseResult.class);

    private static String server_url = "http://api.xinxinnongren.com";


    public static final String url = server_url + "/";
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

    private ApiType(String opt, Class<? extends ResponseResult> clazz) {
        this.opt = opt;
        this.clazz = clazz;
    }

    private ApiType(String opt, RequestMethod requestMethod) {
        this.opt = opt;
        this.requestMethod = requestMethod;
    }

    private ApiType(String opt, RequestMethod requestMethod, int retryNumber) {
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
        POST("POST"), GET("GET"), POSTJSON("POSTJSON");
        private String requestMethodName;

        RequestMethod(String requestMethodName) {
            this.requestMethodName = requestMethodName;
        }

        public String getRequestMethodName() {
            return requestMethodName;
        }
    }
}
