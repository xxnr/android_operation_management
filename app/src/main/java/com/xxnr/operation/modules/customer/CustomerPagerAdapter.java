package com.xxnr.operation.modules.customer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by CAI on 2016/5/3.
 */
public class CustomerPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<String> titles;

    public CustomerPagerAdapter(FragmentManager fm, ArrayList<String> titles) {
        super(fm);
        this.titles = titles;
    }


    @Override
    public Fragment getItem(int arg0) {
        CustomerManageFragment fragment = new CustomerManageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", arg0);//创建fragment的时候传值
        fragment.setArguments(bundle);
        return fragment;
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
