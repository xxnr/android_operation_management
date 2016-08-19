package com.xxnr.operation.modules.datacenter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.xxnr.operation.MsgID;
import com.xxnr.operation.R;
import com.xxnr.operation.developTools.msg.MsgCenter;
import com.xxnr.operation.developTools.msg.MsgListener;
import com.xxnr.operation.event.AgentGroupDateEvent;
import com.xxnr.operation.event.AgentGroupRefresh;
import com.xxnr.operation.event.AgentGroupRefreshComplete;
import com.xxnr.operation.event.AgentGroupSearch;
import com.xxnr.operation.event.AgentGroupUnSearch;
import com.xxnr.operation.modules.BaseFragment;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.utils.IntentUtil;
import com.xxnr.operation.utils.StringUtil;
import com.xxnr.operation.widget.ClearEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.List;


/**
 * Created by 何鹏 on 2016/5/24.
 */
public class AgentGroupReportFragment extends BaseFragment implements
        PullToRefreshBase.OnRefreshListener, DataCenterActivity.OutTouchListener {
    private PullToRefreshScrollView refreshScrollView;

    private TextView date_tv;

    private String endDateStr;
    private String startStr;

    private ImageView select_group_icon;
    private TextView select_group_tv;

    // 声明PopupWindow对象的引用
    private PopupWindow popupWindow;

    private ImageView item_group_achievement_iv;
    private ImageView item_group_order_iv;
    private TextView item_group_achievement_tv;
    private TextView item_group_order_tv;

    private View search_content_ll;
    private ClearEditText clear_edit_text;
    private ImageView search_icon_iv;

    private boolean current_page_isOrder = false;
    private AgentGroupReportOrderFragment orderFragment;
    private AgentGroupReportAchievementFragment achievementFragment;


    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.select_agent_page_tv:
                MsgCenter.fireNull(MsgID.Agent_Page_Select, "yesterday");
                break;
            case R.id.date_tv_ll:
                Bundle bundle = new Bundle();
                bundle.putString("starDateStr", startStr);
                bundle.putString("endDateStr", endDateStr);
                bundle.putBoolean("isRange", true);
                bundle.putBoolean("isLimit", false);
                IntentUtil.activityForward(activity, DailyPickerActivity.class, bundle, false);
                break;
            case R.id.select_agent_group_ll:
                showPopupWindow(v);
                break;
            case R.id.item_group_achievement_ll:
                if (current_page_isOrder) {
                    current_page_isOrder = false;
                    changeFragment();
                }
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                break;
            case R.id.item_group_order_ll:
                if (!current_page_isOrder) {
                    current_page_isOrder = true;
                    changeFragment();
                }
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                break;
            case R.id.search_icon_iv:
                search_icon_iv.setVisibility(View.GONE);
                search_content_ll.setVisibility(View.VISIBLE);
                startAnimation();
                EventBus.getDefault().post(new AgentGroupSearch(null));
                break;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        try {
            ((DataCenterActivity) getActivity()).registerOutTouchListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public View InItView() {
        View view = inflater.inflate(R.layout.fragment_agent_group_report, null);
        initView(view);
        String day = DataCenterUtils.getCurrDateStr(DataCenterUtils.CHINESE_DATE_FORMAT);
        endDateStr = DataCenterUtils.dateAddOrDec(day, -1);
        startStr = DataCenterUtils.dateAddOrDec(endDateStr, -6);

        if (StringUtil.checkStr(endDateStr) && StringUtil.checkStr(startStr)) {
            if (endDateStr.length() > 5 && startStr.length() > 5) {
                String subEndStr = endDateStr.substring(5);
                String subStartStr = startStr.substring(5);
                date_tv.setText(subStartStr + "-" + subEndStr);
            }
        }
        orderFragment = AgentGroupReportOrderFragment.newInstance(startStr, endDateStr);
        achievementFragment = AgentGroupReportAchievementFragment.newInstance(startStr, endDateStr);

        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.agent_group_frameLayout, orderFragment)
                .add(R.id.agent_group_frameLayout, achievementFragment)
                .commitAllowingStateLoss();
        current_page_isOrder = false;
        changeFragment();

        //选中日期返回
        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                if (args[0] != null) {
                    List<Date> dateList = (List<Date>) args[0];
                    if (!dateList.isEmpty()) {
                        startStr = DataCenterUtils.dateToString(dateList.get(0), DataCenterUtils.CHINESE_DATE_FORMAT);
                        endDateStr = DataCenterUtils.dateToString(dateList.get(dateList.size() - 1), DataCenterUtils.CHINESE_DATE_FORMAT);
                        String subStartStr = DataCenterUtils.dateToString(dateList.get(0), DataCenterUtils.SHORT_DATE_FORMAT);
                        String subEndStr = DataCenterUtils.dateToString(dateList.get(dateList.size() - 1), DataCenterUtils.SHORT_DATE_FORMAT);
                        date_tv.setText(subStartStr + "-" + subEndStr);
                        EventBus.getDefault().post(new AgentGroupDateEvent(startStr, endDateStr));
                    }
                }
            }
        }, MsgID.Date_Select_Range);

        return view;
    }

    private void startAnimation() {
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(search_content_ll, "scaleY", 0.5f, 1.0f);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(search_content_ll, "translationX", 200, 0);
        AnimatorSet animatorSetOpen = new AnimatorSet();
        animatorSetOpen.playTogether(animator1, animator2);
        animatorSetOpen.setDuration(500);
        animatorSetOpen.start();
        search_content_ll.requestFocus();
    }


    private void initView(View view) {


        refreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.pullToRefreshScrollView);
        refreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        refreshScrollView.setOnRefreshListener(this);
        refreshScrollView.getRefreshableView().scrollTo(10, 10);
        refreshScrollView.setVisibility(View.GONE);

        date_tv = (TextView) view.findViewById(R.id.date_tv);
        view.findViewById(R.id.date_tv_ll).setOnClickListener(this);
        view.findViewById(R.id.select_agent_page_tv).setOnClickListener(this);

        select_group_icon = ((ImageView) view.findViewById(R.id.select_agent_group_icon));
        select_group_tv = ((TextView) view.findViewById(R.id.select_agent_group_tv));
        view.findViewById(R.id.select_agent_group_ll).setOnClickListener(this);

        search_content_ll = view.findViewById(R.id.search_content_ll);
        clear_edit_text = (ClearEditText) view.findViewById(R.id.clear_edit_text);
        search_icon_iv = (ImageView) view.findViewById(R.id.search_icon_iv);
        search_icon_iv.setOnClickListener(this);
        search_icon_iv.setVisibility(View.VISIBLE);
        search_content_ll.setVisibility(View.GONE);

        clear_edit_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {//判断是否是“搜索”键
                     /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }

                    String searchText = clear_edit_text.getText().toString().trim();
                    EventBus.getDefault().post(new AgentGroupSearch(searchText));
                    return true;
                }
                return false;
            }
        });

    }


    /***
     * 获取PopupWindow实例
     */
    private void showPopupWindow(View v) {
        if (null != popupWindow) {
            popupWindow.dismiss();
        }
        initPopuptWindow();
        select_group_icon.setBackgroundResource(R.mipmap.arrow_up);
        //当前展示哪个item
        if (current_page_isOrder) {
            item_group_order_iv.setVisibility(View.VISIBLE);
            item_group_order_tv.setTextColor(getResources().getColor(R.color.green));
            item_group_achievement_iv.setVisibility(View.INVISIBLE);
            item_group_achievement_tv.setTextColor(getResources().getColor(R.color.default_black));
        } else {
            item_group_order_iv.setVisibility(View.INVISIBLE);
            item_group_order_tv.setTextColor(getResources().getColor(R.color.default_black));
            item_group_achievement_iv.setVisibility(View.VISIBLE);
            item_group_achievement_tv.setTextColor(getResources().getColor(R.color.green));
        }

        popupWindow.showAsDropDown(v);
    }

    /**
     * 创建PopupWindow
     */
    private void initPopuptWindow() {
        View popupWindow_view = LayoutInflater.from(activity).inflate(
                R.layout.pop_select_agent_group_layout, null);
        popupWindow = new PopupWindow(popupWindow_view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        item_group_achievement_iv = ((ImageView) popupWindow_view.findViewById(R.id.item_group_achievement_iv));
        item_group_achievement_tv = (TextView) popupWindow_view.findViewById(R.id.item_group_achievement_tv);
        item_group_order_iv = ((ImageView) popupWindow_view.findViewById(R.id.item_group_order_iv));
        item_group_order_tv = (TextView) popupWindow_view.findViewById(R.id.item_group_order_tv);

        popupWindow_view.findViewById(R.id.item_group_achievement_ll).setOnClickListener(this);
        popupWindow_view.findViewById(R.id.item_group_order_ll).setOnClickListener(this);


        // 设置动画效果
        popupWindow.setAnimationStyle(R.style.AnimTop2);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                select_group_icon.setBackgroundResource(R.mipmap.arrow_down);
            }
        });
    }


    private void changeFragment() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (current_page_isOrder) {
            select_group_tv.setText("完成订单汇总");
            transaction.hide(achievementFragment).show(orderFragment);
        } else {
            select_group_tv.setText("业绩进度汇总");
            transaction.hide(orderFragment).show(achievementFragment);
        }
        transaction.commitAllowingStateLoss();
    }


    @Override
    public void onResume() {
        super.onResume();
        refreshScrollView.requestFocus();
        refreshScrollView.getRefreshableView().smoothScrollTo(10, 10);
    }

    @Override
    public void onResponsed(Request req) {

    }


    //上拉加载更多
    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        EventBus.getDefault().post(new AgentGroupRefresh(current_page_isOrder));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshComplete(AgentGroupRefreshComplete complete) {
        if (refreshScrollView != null) {
            refreshScrollView.onRefreshComplete();
        }
    }


    //点击外部状态还原
    @Override
    public void onTouchEvent(MotionEvent event) {
        if (search_content_ll != null && clear_edit_text != null) {
            if (!StringUtil.checkStr(clear_edit_text.getText().toString())
                    && search_content_ll.getVisibility() == View.VISIBLE) {
                search_icon_iv.setVisibility(View.VISIBLE);
                search_content_ll.setVisibility(View.GONE);
                EventBus.getDefault().post(new AgentGroupUnSearch());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
