package com.xxnr.operation.modules.customer;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.xxnr.operation.modules.BaseActivity;
import com.xxnr.operation.R;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.utils.IntentUtil;

import java.util.ArrayList;

/**
 * Created by 何鹏 on 2016/5/3.
 */
public class CustomerManageActivity extends BaseActivity {
    private ArrayList<String> titleList = new ArrayList<>();

    @Override
    public int getLayout() {
        return R.layout.activity_customer_manage;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("客户管理");
        initView();
        showRightImage();
        setRightImage(R.mipmap.search_icon);
        setRightViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.activityForward(CustomerManageActivity.this, CustomerSearchActivity.class, null, false);
                int version = Integer.valueOf(android.os.Build.VERSION.SDK);
                if (version > 5) {
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });
    }

    private void initView() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs);
        initTabs();
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new CustomerPagerAdapter(fragmentManager, titleList));
        mTabLayout.setupWithViewPager(viewPager);//设置联动
        viewPager.setOffscreenPageLimit(titleList.size());

    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {

    }


    /**
     * 添加title
     */
    private void initTabs() {
        titleList.add("所有客户");
        titleList.add("待认证");
    }
}
