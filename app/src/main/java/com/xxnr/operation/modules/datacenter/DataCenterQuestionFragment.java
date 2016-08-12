package com.xxnr.operation.modules.datacenter;

import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

import com.xxnr.operation.R;
import com.xxnr.operation.modules.BaseFragment;
import com.xxnr.operation.protocol.Request;

/**
 * Created by CAI on 2016/8/11.
 */
public class DataCenterQuestionFragment extends BaseFragment {
    private int position;

    public static DataCenterQuestionFragment newInstance(int position) {
        DataCenterQuestionFragment mFragment = new DataCenterQuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        mFragment.setArguments(bundle);
        return mFragment;
    }



    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public View InItView() {
        if (getArguments() != null) {
            position = getArguments().getInt("position");
        }
        View view = inflater.inflate(R.layout.fragment_question_layout, null);

        ScrollView question_scrollView1 = (ScrollView) view.findViewById(R.id.question_scrollView1);
        ScrollView question_scrollView2 = (ScrollView) view.findViewById(R.id.question_scrollView2);
        ScrollView question_scrollView3 = (ScrollView) view.findViewById(R.id.question_scrollView3);

        question_scrollView1.setVisibility(View.GONE);
        question_scrollView2.setVisibility(View.GONE);
        question_scrollView3.setVisibility(View.GONE);
        switch (position){
            case 0:
                question_scrollView1.setVisibility(View.VISIBLE);
                break;
            case 1:
                question_scrollView2.setVisibility(View.VISIBLE);
                break;
            case 2:
                question_scrollView3.setVisibility(View.VISIBLE);
                break;
        }
        return view;
    }

    @Override
    public void onResponsed(Request req) {

    }


}
