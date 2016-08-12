package com.xxnr.operation.modules.datacenter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.xxnr.operation.CommonPagerAdapter;
import com.xxnr.operation.MsgID;
import com.xxnr.operation.R;
import com.xxnr.operation.developTools.msg.MsgCenter;
import com.xxnr.operation.developTools.msg.MsgListener;
import com.xxnr.operation.modules.BaseFragment;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.widget.UnSwipeViewPager;
import com.xxnr.operation.widget.transforms.FlipHorizontalTransformer;

import java.util.ArrayList;


/**
 * Created by CAI on 2016/8/12.
 */
public class AgentFragment extends BaseFragment{
    private UnSwipeViewPager viewPager;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    @Override
    public View InItView() {
        View inflate = inflater.inflate(R.layout.fragment_agent, null);
        viewPager= (UnSwipeViewPager) inflate.findViewById(R.id.unSwipeViewPager);
        if (fragments.isEmpty()) {
            initTabsFragments();
        }
        viewPager.setScanScroll(false);
        viewPager.setAdapter(new CommonPagerAdapter(getChildFragmentManager(), null, fragments));
        viewPager.setPageTransformer(true,new FlipHorizontalTransformer());
        viewPager.setOffscreenPageLimit(fragments.size());

        //Agent_Page_Select
        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                if (args[0] != null) {
                    String page = (String) args[0];
                    Log.d("AgentFragment", page);
                    if (page.equals("group")){
                        viewPager.setCurrentItem(1);
                    }else {
                        viewPager.setCurrentItem(0);
                    }
                }
            }
        }, MsgID.Agent_Page_Select);

        return inflate;
    }

    private void initTabsFragments() {
        fragments.add(new AgentYesterdayDayReportFragment());
        fragments.add(new AgentGroupReportFragment());
    }

    @Override
    public void onResponsed(Request req) {
    }

    @Override
    public void OnViewClick(View v) {


    }





}
