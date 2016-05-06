package com.xxnr.operation;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.utils.CrashHandler;
import com.xxnr.operation.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CAI on 2016/4/21.
 */
public class RndApplication extends Application {
    private static final String TAG = "RndApplication";
    public static Context applicationContext;
    private static RndApplication instance;

    public static List<Activity> unDestroyActivityList = new ArrayList<>();
    public static List<Activity> tempDestroyActivityList = new ArrayList<>();

    // login user name
    private String uid = "";
    private String token = "";
    private String pwd = "";


    @Override
    public void onCreate() {
        super.onCreate();

        applicationContext = this;
        instance = this;
        App.setApp(this);
        //初始化CrashHandler
        CrashHandler.getInstance().init(this);
        //初始化全局的uid token
        String uid = UserInfo.getUid(this);
        if (StringUtil.checkStr(uid)){
            setUid(uid);
        }
        String token = UserInfo.getToken(this);
        if (StringUtil.checkStr(token)){
            setToken(token);
        }
    }


    public static RndApplication getInstance() {
        return instance;
    }


    /**
     * 退出应用
     */
    public void quit() {
        for (Activity activity : unDestroyActivityList) {
            if (null != activity) {
                activity.finish();
            }
        }
        tempDestroyActivityList.clear();
        for (Activity activity : tempDestroyActivityList) {
            if (null != activity) {
                activity.finish();
            }
        }
        unDestroyActivityList.clear();

    }


    /**
     * 局部退出
     */
    public void partQuit() {
        for (Activity activity : RndApplication.tempDestroyActivityList) {
            if (null != activity) {
                activity.finish();
            }
        }
        RndApplication.tempDestroyActivityList.clear();
    }


    //设置全局的用户名和密码
    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getUid() {

        return uid;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }


    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPwd() {
        return this.pwd;
    }


    public Toast toast;

    /**
     * 短时间显示Toast 作用:不重复弹出Toast,如果当前有toast正在显示，则先取消
     *
     * @param info 显示的内容
     */
    public void showToast(String info) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, info, Toast.LENGTH_SHORT);
        toast.setText(info);
        toast.show();
    }


}
