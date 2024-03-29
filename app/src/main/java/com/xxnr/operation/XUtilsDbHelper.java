package com.xxnr.operation;

import android.content.Context;


import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.xxnr.operation.protocol.bean.LoginResult;

import java.util.List;

/**
 * Created by 何鹏 on 2016/4/12.
 */
public class XUtilsDbHelper {
    // dbUtils实例
    private static DbUtils dbUtils;
    private static Context mContext;
    private static String db_Name;
    private static String old_dbName;

    /**
     * 获取dbUtils实例 ,单例模式
     */
    public static DbUtils getInstance(Context context, String dbName) {

        db_Name = "operate" + dbName;
        mContext = context;
        if (dbUtils == null || !db_Name.equals(old_dbName)) {
            initDbUtils();
        }
        return dbUtils;
    }


    private static void initDbUtils() {
        DbUtils.DaoConfig config = new DbUtils.DaoConfig(mContext);
        old_dbName = db_Name;
        config.setDbName(db_Name);
        config.setDbVersion(1);
        config.setDbUpgradeListener(new DbUtils.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbUtils db, int oldVersion, int newVersion) {
                if (newVersion > oldVersion) {
                    try {
                        db.dropDb();
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        dbUtils = DbUtils.create(config);
        dbUtils.configAllowTransaction(true);
        dbUtils.configDebug(false);


        try {
            dbUtils.createTableIfNotExist(LoginResult.DatasBean.class);
        } catch (DbException e) {
            e.printStackTrace();
        }


    }


    public static void deleteAll(DbUtils dbUtils, Class aClass) {
        try {
            dbUtils.deleteAll(aClass);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static void saveOrUpdateAll(DbUtils dbUtils, List list) {
        try {
            dbUtils.saveOrUpdateAll(list);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
