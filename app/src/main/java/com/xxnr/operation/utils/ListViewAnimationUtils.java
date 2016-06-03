package com.xxnr.operation.utils;

import android.widget.BaseAdapter;
import android.widget.ListView;

import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

/**
 * Created by 何鹏 on 2016/5/23.
 */
public class ListViewAnimationUtils {
    public static final int Short_Delay_Millis=100;

    public static void setListViewAnimationAndAdapter(ListView listView , BaseAdapter adapter){
        AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(adapter);
        animationAdapter.setAbsListView(listView);
        assert animationAdapter.getViewAnimator() != null;
        animationAdapter.getViewAnimator().setInitialDelayMillis(Short_Delay_Millis);
        listView.setAdapter(animationAdapter);
    }

}
