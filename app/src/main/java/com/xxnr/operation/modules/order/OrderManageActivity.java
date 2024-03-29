package com.xxnr.operation.modules.order;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

import com.xxnr.operation.MsgID;
import com.xxnr.operation.modules.BaseActivity;
import com.xxnr.operation.R;
import com.xxnr.operation.developTools.msg.MsgCenter;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.utils.IntentUtil;
import com.xxnr.operation.utils.PopWindowUtils;

import java.util.ArrayList;

/**
 * Created by 何鹏 on 2016/5/5.
 */
public class OrderManageActivity extends BaseActivity implements OrderListFragment.BgSwitch {
    private ArrayList<String> titleList = new ArrayList<>();
    private ViewPager viewPager;
    private FragmentManager fragmentManager;
    private TabLayout mTabLayout;
    private RelativeLayout pop_bg;


    @Override
    public int getLayout() {
        return R.layout.activity_order_manage;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("订单管理");
        initView();

        showRightImage();
        setRightImage(R.mipmap.search_icon);
        setRightViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.activityForward(OrderManageActivity.this, OrderSearchActivity.class, null, false);
                int version = Integer.valueOf(android.os.Build.VERSION.SDK);
                if (version > 5) {
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });
    }

    private void initView() {

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        pop_bg = (RelativeLayout) findViewById(R.id.pop_bg);

        initTabs();
        fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new OrderListPagerAdapter(fragmentManager, titleList));
        mTabLayout.setupWithViewPager(viewPager);//设置联动
        viewPager.setOffscreenPageLimit(titleList.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                //通知 订单列表刷新
                MsgCenter.fireNull(MsgID.select_tab, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
        titleList.add("全部");
        titleList.add("待审核");
        titleList.add("待发货");
    }


    @Override
    public void backgroundSwitch(int bg) {
        if (pop_bg != null) {
            PopWindowUtils.setBackgroundBlack(pop_bg, bg);
        }
    }
}
