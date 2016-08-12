package com.xxnr.operation.modules.datacenter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * Created by 何鹏 on 2016/5/18.
 */
public class DataCenterAdapter extends FragmentPagerAdapter {

    private ArrayList<String> titles;

    public DataCenterAdapter(FragmentManager fm, ArrayList<String> titles) {
        super(fm);
        this.titles = titles;
    }


    @Override
    public Fragment getItem(int arg0) {

        switch (arg0) {
            case 0:
                return new DailyReportFragment();
            case 1:
                return new WeekReportFragment();
            case 2:
                return new AgentFragment();
            default:
                return new DailyReportFragment();
        }
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }


}
