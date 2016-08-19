package com.xxnr.operation.modules.datacenter;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.xxnr.operation.R;
import com.xxnr.operation.developTools.PreferenceUtil;
import com.xxnr.operation.modules.BaseActivity;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.widget.UnSwipeViewPager;

import java.util.ArrayList;

/**
 * Created by 何鹏 on 2016/5/18.
 */
public class DataCenterActivity extends BaseActivity {
    private ArrayList<String> titleList = new ArrayList<>();
    private RelativeLayout pop_bg;


    @Override
    public int getLayout() {
        return R.layout.activity_data_center;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("数据中心");
        showRightImage();
        setRightImage(R.mipmap.question_unpress);
        setRightViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(DataCenterQuestionActivity.class);
            }
        });
        initView();

    }

    //设置返回监听的，回传数据
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (pop_bg.getVisibility() == View.VISIBLE) {
                pop_bg.setVisibility(View.GONE);
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        pop_bg = (RelativeLayout) findViewById(R.id.pop_bg);
        pop_bg.setVisibility(View.GONE);


        UnSwipeViewPager viewPager = (UnSwipeViewPager) findViewById(R.id.viewPager);
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager.setScanScroll(false);
        initTabs();
        setViewClick(R.id.tips_button);

        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new DataCenterAdapter(fragmentManager, titleList));
        mTabLayout.setupWithViewPager(viewPager);//设置联动
        viewPager.setOffscreenPageLimit(titleList.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    //是否是第一次到经纪人tab 如果是的话展示引导
                    PreferenceUtil pu = new PreferenceUtil(DataCenterActivity.this, "config");
                    boolean isFirst = pu.getBool("first_agent", true);
                    if (isFirst) {
                        pop_bg.setVisibility(View.VISIBLE);
                        pop_bg.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                pop_bg.setVisibility(View.GONE);
                                return false;
                            }
                        });
                    }
                    pu.putBool("first_agent", false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * 添加title
     */
    private void initTabs() {
        titleList.add("日报");
        titleList.add("周报");
        titleList.add("经纪人");
    }


    @Override
    public void OnViewClick(View v) {
        if (v.getId() == R.id.tips_button) {
            pop_bg.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResponsed(Request req) {

    }


}
