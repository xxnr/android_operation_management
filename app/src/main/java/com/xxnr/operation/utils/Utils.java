package com.xxnr.operation.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static long lastClickTime;


    // 判断是否是手机号
    public static boolean isMobileNum(String mobiles) {
        Pattern p = Pattern
                .compile("^[1]([0-8]{1}[0-9]{1}|59|58|88|89)[0-9]{8}");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 判断是否是身份证
     */

    public static boolean isIDCardNum(String IDCards) {

        Pattern p = Pattern
                .compile("((11|12|13|14|15|21|22|23|31|32|33|34|35|36|37|41|42|43|44|45|46|50|51|52|53|54|61|62|63|64|65)[0-9]{4})" +
                        "(([1|2][0-9]{3}[0|1][0-9][0-3][0-9][0-9]{3}" +
                        "[Xx0-9])|([0-9]{2}[0|1][0-9][0-3][0-9][0-9]{3}))");
        Matcher m = p.matcher(IDCards);
        return m.matches();


    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    //判断某个app是否安装
    public static boolean isPkgInstalled(Context context, String pkgName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    //从Assets中判断是否存在
    public static boolean copyApkFromAssets(Context context, String fileName, String path) {
        boolean copyIsFinish = false;
        try {
            InputStream is = context.getAssets().open(fileName);
            File file = new File(path);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();
            copyIsFinish = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return copyIsFinish;
    }


    /**
     * traversal bundle with string values.
     *
     * @param bundle
     * @return
     */
    public static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder("");
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            sb.append(key).append(":").append(bundle.get(key)).append(";\n");
        }
        return sb.toString();
    }

    /**
     * 格式化日期
     *
     * @param time
     * @return
     */
    public static String getDateToString(long time) {
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        return simpleDateFormat.format(date);
    }

    //隐藏输入法
    public static void hideInputMethod(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && activity.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    // 将字母装换成数字

    public static int getNum(String letter, List<String> list) {

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i).equals(letter)) {

                return i;
            }
        }
        return 0;
    }


    public static void dial(final Activity activity, final String phoneNum) {

        AlertDialog.Builder builder = new AlertDialog.Builder(
                activity);
        builder.setMessage(phoneNum)
                .setPositiveButton("拨打",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:"
                                        + phoneNum));
                                dialog.dismiss();
                                // 开启系统拨号器
                                activity.startActivity(intent);
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });
        builder.create().show();

    }




}
