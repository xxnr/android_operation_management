package com.xxnr.operation.protocol;


import com.google.gson.Gson;
import com.xxnr.operation.utils.RndLog;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 服务器接口
 *
 * @author wqz
 */
public final class ServerInterface {

    private static final String TAG = "ServerInterface";

    private static Gson mGson;

    private NetworkHelper mHelper;

    public ServerInterface() {
        mGson = new Gson();
        mHelper = new NetworkHelper();
    }

    public ResponseResult request(final ApiType api, final RequestParams params)
            throws NetworkException {

        Response response = getResponseByApi(api, params);

        if (response != null) {// response == null   可能是无网络引起

            ResponseBody responseBody = response.body();
            if (response.isSuccessful()) {
                if (responseBody != null) {
                    try {
                        String json = responseBody.string();
                        RndLog.d(TAG, "response. json.length = " + json);
                        return parseJson(json, getJsonClassByApi(api));
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new NetworkException(e);
                    }
                }
                return null;
            } else {
                RndLog.e(TAG, "HTTP CODE :" + response.code());
                throw new NetworkException(response.code(),
                        response.message());
            }
        } else {
            // 执行过程中产生异常
            return new ResponseResult(Request.HTTP_ERROR, "");
        }
    }

    /**
     * 修改为根据result类型返回结果
     *
     * @param json
     * @param clazz
     * @return
     * @throws JSONException
     */
    public static ResponseResult parseJson(String json,
                                           Class<? extends ResponseResult> clazz) throws JSONException {

        ResponseResult res;
        try {
            JSONObject obj = new JSONObject(json);
            json = obj.toString();
            res = mGson.fromJson(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            res = new ResponseResult(Request.PARSE_DATA_FAILED, "");
        }
        return res;
    }

    /**
     * 通过参数返回http响应
     *
     * @param api
     * @param params
     * @return
     */
    private Response getResponseByApi(ApiType api, RequestParams params) {

        params.put("user-agent", "Android-v1.0");
        try {
            // TODO 判断api类型
            if (api.getRequestMethod() == ApiType.RequestMethod.GET) {
                return mHelper.performGet(api.getOpt(), params);
            } else if (api.getRequestMethod() == ApiType.RequestMethod.POSTJSON) {
                return mHelper.postBody(api.getOpt(), params);
            } else if (api.getRequestMethod() == ApiType.RequestMethod.PUT) {
                return mHelper.putBody(api.getOpt(), params);
            } else {
                return mHelper.performPost(api.getOpt(), params);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Class<? extends ResponseResult> getJsonClassByApi(ApiType api) {
        // TODO 返回api对应的CLASS
        return api.getClazz();
    }

}
