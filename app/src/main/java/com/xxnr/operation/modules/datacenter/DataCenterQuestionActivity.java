package com.xxnr.operation.modules.datacenter;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.View;

import com.xxnr.operation.CommonPagerAdapter;
import com.xxnr.operation.R;
import com.xxnr.operation.modules.BaseActivity;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.widget.UnSwipeViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CAI on 2016/8/11.
 */
public class DataCenterQuestionActivity extends BaseActivity {
    private List<String> titleList = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();


    @Override
    public int getLayout() {
        return R.layout.activity_data_center_question;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("数据中心");
        UnSwipeViewPager viewPager = (UnSwipeViewPager) findViewById(R.id.viewPager);
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager.setScanScroll(true);
        initTabs();
        for (int i = 0; i < titleList.size(); i++) {
            fragments.add(DataCenterQuestionFragment.newInstance(i));
        }
        viewPager.setAdapter(new CommonPagerAdapter(getSupportFragmentManager(), titleList, fragments));
        mTabLayout.setupWithViewPager(viewPager);//设置联动
        viewPager.setOffscreenPageLimit(titleList.size());
    }

    private void initTabs() {
        titleList.add("日报");
        titleList.add("周报");
        titleList.add("经纪人");
    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {

    }


}
