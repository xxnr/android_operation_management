package com.xxnr.operation.modules.datacenter;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.xxnr.operation.CommonPagerAdapter;
import com.xxnr.operation.R;
import com.xxnr.operation.developTools.PreferenceUtil;
import com.xxnr.operation.modules.BaseActivity;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.widget.UnSwipeViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 何鹏 on 2016/5/18.
 */
public class DataCenterActivity extends BaseActivity {
    private List<String> titleList = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();

    private RelativeLayout pop_bg;
    private InputMethodManager manager;

    private OutTouchListener listener;

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
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

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
        setViewClick(R.id.tips_button);
        initTabs();
        viewPager.setAdapter(new CommonPagerAdapter(getSupportFragmentManager(), titleList, fragments));
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

        if (titleList.isEmpty()) {
            titleList.add("日报");
            titleList.add("周报");
            titleList.add("经纪人");
        }

        if (fragments.isEmpty()) {
            fragments.add(new DailyReportFragment());
            fragments.add(new WeekReportFragment());
            fragments.add(new AgentFragment());
        }

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


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            /*隐藏软键盘  关闭搜索框 */
            if (view != null) {
                if (isShouldHideInput(view, ev)) {
                    if (manager != null) {
                        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    if (listener!=null){
                        listener.onTouchEvent(ev);
                    }
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }


    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

    public interface OutTouchListener {
         void onTouchEvent(MotionEvent event);
    }

    public void registerOutTouchListener(OutTouchListener listener)
    {
       this.listener=listener;
    }
    public void unRegisterOutTouchListener()
    {
        this.listener=null;
    }

}
