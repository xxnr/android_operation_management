package com.xxnr.operation;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.xxnr.operation.XUtilsDb.XUtilsDbHelper;
import com.xxnr.operation.developTools.PreferenceUtil;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.protocol.bean.LoginResult;

/**
 * Created by CAI on 2016/4/29.
 */
public class UserInfo {

    public static String PreferenceUtil_NAME = "userInfo";


    //保存用户数据到本地
    public static void saveToken(String token, Context context) {
        PreferenceUtil preferenceUtil = new PreferenceUtil(context, PreferenceUtil_NAME);
        preferenceUtil.putString("token", token);
    }


    //读取用户的token
    public static String getToken(Context context) {
        PreferenceUtil preferenceUtil = new PreferenceUtil(context, PreferenceUtil_NAME);
        return preferenceUtil.getString("token", "");
    }

    //获取用户Id

    public static String getUid(Context context) {
        PreferenceUtil preferenceUtil = new PreferenceUtil(context, PreferenceUtil_NAME);
        return preferenceUtil.getString("userId", "");
    }

    //保存用户Id
    public static void saveUid(String uid, Context context) {
        PreferenceUtil preferenceUtil = new PreferenceUtil(context, PreferenceUtil_NAME);
        preferenceUtil.putString("userId", uid);
    }


    //获取用户信息

    public static LoginResult.DatasBean getUserInfo(Context context) {
        DbUtils dbUtils = XUtilsDbHelper.getInstance(context, getUid(context));
        try {
            return dbUtils.findById(LoginResult.DatasBean.class, getUid(context));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    //保存用户信息

    public static void saveUserInfo(LoginResult.DatasBean datasBean, Context context) {
        DbUtils dbUtils = XUtilsDbHelper.getInstance(context, getUid(context));
        try {
            dbUtils.saveOrUpdate(datasBean);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    //清除当前用户所有信息
    public static void clearUserInfo(Context context) {

        PreferenceUtil preferenceUtil = new PreferenceUtil(context, PreferenceUtil_NAME);
        preferenceUtil.clear();

        DbUtils dbUtils = XUtilsDbHelper.getInstance(context, App.getApp().getUid());
        try {
            dbUtils.deleteById(LoginResult.DatasBean.class, App.getApp().getUid());
        } catch (DbException e) {
            e.printStackTrace();
        }
        App.getApp().setUid("");
        App.getApp().setToken("");

    }
    // 调用此方法 去登录页面
    public static void tokenToLogin(Activity activity) {
        UserInfo.clearUserInfo(activity);
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

}
