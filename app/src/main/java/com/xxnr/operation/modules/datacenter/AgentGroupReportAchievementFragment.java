package com.xxnr.operation.modules.datacenter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.xxnr.operation.MsgID;
import com.xxnr.operation.R;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.developTools.msg.MsgCenter;
import com.xxnr.operation.developTools.msg.MsgListener;
import com.xxnr.operation.modules.BaseFragment;
import com.xxnr.operation.modules.CommonAdapter;
import com.xxnr.operation.modules.CommonViewHolder;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.protocol.bean.AgentReportTotalResult;
import com.xxnr.operation.utils.IntentUtil;
import com.xxnr.operation.utils.StringUtil;
import com.xxnr.operation.widget.UnSwipeListView;

import java.util.Date;
import java.util.List;

/**
 * Created by 何鹏 on 2016/5/24.
 */
public class AgentGroupReportAchievementFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener {
    private UnSwipeListView unSwipeListView;
    private UnSwipeListView unSwipeListView_name;
    private TextView select_page_tv;
    private int page = 1;
    private PullToRefreshScrollView refreshScrollView;
    private TitleViewHolder titleViewHolder;
    private String SORT = "";

    private AgentAdapter adapter;
    private NameAgentAdapter nameAdapter;

    private TextView date_tv;
    private String endDateStr;
    private String startStr;


    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.title_new_customer_count_ll:
                SORT = "NEWINVITEE";
                sortOrder(v, SORT);
                break;
            case R.id.title_total_agent_count_ll:
                SORT = "NEWAGENT";
                sortOrder(v, SORT);
                break;
            case R.id.title_reg_customer_count_ll:
                SORT = "NEWPOTENTIALCUSTOMER";
                sortOrder(v, SORT);
                break;
            case R.id.title_new_order_count_ll:
                SORT = "NEWORDER";
                sortOrder(v, SORT);
                break;
            case R.id.title_order_count_ll:
                SORT = "NEWORDERCOMPLETED";
                sortOrder(v, SORT);
                break;
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
        }
    }

    //sort
    public void sortOrder(View v, String sort) {
        if (titleViewHolder != null) {
            page = 1;
            showProgressDialog();
            if ((v.getTag()) != null && (Boolean) v.getTag()) {
                titleViewHolder.reset();
                getData(sort, -1);
                v.setTag(false);
                try {
                    ((LinearLayout) v).getChildAt(1).setBackgroundResource(R.mipmap.sort_down);
                    ((LinearLayout) v).getChildAt(1).setVisibility(View.VISIBLE);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            } else {
                titleViewHolder.reset();
                getData(sort, 1);
                v.setTag(true);
                try {
                    ((LinearLayout) v).getChildAt(1).setBackgroundResource(R.mipmap.sort_up);
                    ((LinearLayout) v).getChildAt(1).setVisibility(View.VISIBLE);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public View InItView() {
        View view = inflater.inflate(R.layout.fragment_agent_group_report, null);
        initView(view);

        endDateStr = DataCenterUtils.getCurrDateStr(DataCenterUtils.CHINESE_DATE_FORMAT);
        startStr = DataCenterUtils.dateAddOrDec(endDateStr, -6);
        if (StringUtil.checkStr(endDateStr) && StringUtil.checkStr(startStr)) {
            if (endDateStr.length() > 5 && startStr.length() > 5) {
                String subEndStr = endDateStr.substring(5);
                String subStartStr = startStr.substring(5);
                date_tv.setText(subStartStr + "-" + subEndStr);
            }
        }

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

                        page=1;
                        getData(SORT, -1);
                    }
                }
            }
        }, MsgID.Date_Select_Range);


        showProgressDialog();
        SORT = "NEWINVITEE";
        getData(SORT, -1);
        return view;
    }


    private void initView(View view) {

        refreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.pullToRefreshScrollView);
        refreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        refreshScrollView.setOnRefreshListener(this);
        refreshScrollView.getRefreshableView().scrollTo(10, 10);
        refreshScrollView.setVisibility(View.GONE);

        date_tv = (TextView) view.findViewById(R.id.date_tv);
        view.findViewById(R.id.date_tv_ll).setOnClickListener(this);

        select_page_tv = (TextView) view.findViewById(R.id.select_agent_page_tv);
        select_page_tv.setOnClickListener(this);
        unSwipeListView = ((UnSwipeListView) view.findViewById(R.id.unSwipeListView));
        unSwipeListView_name = ((UnSwipeListView) view.findViewById(R.id.unSwipeListView_name));
        View header_unSwipeListView_name = inflater.inflate(R.layout.head_agent_report_name, null);
        View header_unSwipeListView = inflater.inflate(R.layout.head_agent_group_report1, null);
        unSwipeListView_name.addHeaderView(header_unSwipeListView_name);
        unSwipeListView.addHeaderView(header_unSwipeListView);

        titleViewHolder = new TitleViewHolder(header_unSwipeListView);
        titleViewHolder.init();
        titleViewHolder.reset();

        titleViewHolder.title_new_customer_count_icon.setVisibility(View.VISIBLE);
        titleViewHolder.title_new_customer_count_icon.setBackgroundResource(R.mipmap.sort_down);
        titleViewHolder.title_new_customer_count_ll.setTag(false);
    }


    private void getData(String sort, int sortOrder) {
        RequestParams params = new RequestParams();
        params.put("token", App.getApp().getToken());
        params.put("max", 20);
        if (sortOrder != 0) {
            params.put("sort", sort);
            params.put("sortOrder", sortOrder);
        }
        params.put("type", 1);
        params.put("dateStart", DataCenterUtils.changeDateFormat(startStr));
        params.put("dateEnd", DataCenterUtils.changeDateFormat(endDateStr));
        params.put("page", page);
        execApi(ApiType.GET_AGENT_RANK_TOTAL.setMethod(ApiType.RequestMethod.GET), params);
    }

    @Override
    public void onResponsed(Request req) {

        if (req.getApi() == ApiType.GET_AGENT_RANK_TOTAL) {
            if (refreshScrollView != null && refreshScrollView.isRefreshing()) {
                refreshScrollView.onRefreshComplete();
            }
            if (refreshScrollView != null) {
                refreshScrollView.setVisibility(View.VISIBLE);
            }
            disMissDialog();
            AgentReportTotalResult reqData = (AgentReportTotalResult) req.getData();

            List<AgentReportTotalResult.AgentReportsBean> agentReports = reqData.agentReports;
            if (agentReports != null && !agentReports.isEmpty()) {
                if (page == 1) {
                    //agent姓名列表
                    if (nameAdapter == null) {
                        nameAdapter = new NameAgentAdapter(activity, agentReports);
                        unSwipeListView_name.setAdapter(nameAdapter);
                    } else {
                        nameAdapter.clear();
                        nameAdapter.addAll(agentReports);
                    }
                    //agent列表
                    if (adapter == null) {
                        adapter = new AgentAdapter(activity, agentReports);
                        unSwipeListView.setAdapter(adapter);

                    } else {
                        adapter.clear();
                        adapter.addAll(agentReports);
                    }
                } else {
                    //因集合agentReportYesterday 两个适配器是一个对象 所以只需要add一次
                    if (adapter != null && nameAdapter != null) {
                        adapter.addAll(agentReports);
                        nameAdapter.notifyDataSetChanged();
                    }
                }
            } else {
                if (page == 1) {
                    if (adapter != null) {
                        adapter.clear();
                    }
                    if (nameAdapter != null) {
                        nameAdapter.clear();
                    }
                } else {
                    page--;
                    showToast("没有更多经纪人");
                }
            }
        }
    }

    //下拉刷新
    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        page++;
        getData(SORT, -1);
    }

    /**
     * 业绩进度汇总
     */
    class AgentAdapter extends CommonAdapter<AgentReportTotalResult.AgentReportsBean> {


        public AgentAdapter(Context context, List<AgentReportTotalResult.AgentReportsBean> data) {
            super(context, data, R.layout.item_agent_group_report1);
        }

        @Override
        public void convert(CommonViewHolder holder, AgentReportTotalResult.AgentReportsBean agentReportBean) {
            if (agentReportBean != null) {
                if (holder.getPosition() % 2 == 0) {
                    holder.getView(R.id.item_agent_report_ll).setBackgroundColor(getResources().getColor(R.color.order_title_bg));
                } else {
                    holder.getView(R.id.item_agent_report_ll).setBackgroundColor(getResources().getColor(R.color.white));
                }
                holder.setText(R.id.new_customer_count, agentReportBean.newInviteeCount + "");//新增客户
                holder.setText(R.id.new_agent_count, agentReportBean.newAgentCount + "");//新登记经纪人
                holder.setText(R.id.reg_customer_count, agentReportBean.newPotentialCustomerCount + "");//新登记客户
                holder.setText(R.id.new_order_count, agentReportBean.newOrderCount + "");//新订单数
                holder.setText(R.id.order_count, agentReportBean.newOrderCompletedCount + "");//新完成订单数
            }
        }
    }

    class NameAgentAdapter extends CommonAdapter<AgentReportTotalResult.AgentReportsBean> {


        public NameAgentAdapter(Context context, List<AgentReportTotalResult.AgentReportsBean> data) {
            super(context, data, R.layout.item_agent_report_name);
        }

        @Override
        public void convert(CommonViewHolder holder, AgentReportTotalResult.AgentReportsBean agentReportBean) {
            if (agentReportBean != null) {

                if (holder.getPosition() % 2 == 0) {
                    holder.getView(R.id.item_agent_report_ll).setBackgroundColor(getResources().getColor(R.color.order_title_bg));
                } else {
                    holder.getView(R.id.item_agent_report_ll).setBackgroundColor(getResources().getColor(R.color.white));
                }
                if (StringUtil.checkStr(agentReportBean.name)) {
                    holder.setText(R.id.agent_name, agentReportBean.name);
                } else {
                    holder.setText(R.id.agent_name, "");
                }
            }
        }
    }


    public class TitleViewHolder {
        public ImageView title_new_customer_count_icon;
        public ImageView title_total_agent_count_icon;
        public ImageView title_reg_customer_count_icon;
        public ImageView title_new_order_count_icon;
        public ImageView title_order_count_icon;

        public LinearLayout title_new_customer_count_ll;
        public LinearLayout title_total_agent_count_ll;
        public LinearLayout title_reg_customer_count_ll;
        public LinearLayout title_new_order_count_ll;
        public LinearLayout title_order_count_ll;

        public TitleViewHolder(View rootView) {
            this.title_new_customer_count_icon = (ImageView) rootView.findViewById(R.id.title_new_customer_count_icon);
            this.title_total_agent_count_icon = (ImageView) rootView.findViewById(R.id.title_total_agent_count_icon);
            this.title_reg_customer_count_icon = (ImageView) rootView.findViewById(R.id.title_reg_customer_count_icon);
            this.title_new_order_count_icon = (ImageView) rootView.findViewById(R.id.title_new_order_count_icon);
            this.title_order_count_icon = (ImageView) rootView.findViewById(R.id.title_order_count_icon);

            this.title_new_customer_count_ll = (LinearLayout) rootView.findViewById(R.id.title_new_customer_count_ll);
            this.title_total_agent_count_ll = (LinearLayout) rootView.findViewById(R.id.title_total_agent_count_ll);
            this.title_reg_customer_count_ll = (LinearLayout) rootView.findViewById(R.id.title_reg_customer_count_ll);
            this.title_new_order_count_ll = (LinearLayout) rootView.findViewById(R.id.title_new_order_count_ll);
            this.title_order_count_ll = (LinearLayout) rootView.findViewById(R.id.title_order_count_ll);
        }


        public void init() {
            this.title_new_customer_count_ll.setOnClickListener(AgentGroupReportAchievementFragment.this);
            this.title_total_agent_count_ll.setOnClickListener(AgentGroupReportAchievementFragment.this);
            this.title_reg_customer_count_ll.setOnClickListener(AgentGroupReportAchievementFragment.this);
            this.title_new_order_count_ll.setOnClickListener(AgentGroupReportAchievementFragment.this);
            this.title_order_count_ll.setOnClickListener(AgentGroupReportAchievementFragment.this);
        }


        public void reset() {
            this.title_new_customer_count_icon.setVisibility(View.GONE);
            this.title_new_customer_count_ll.setTag(true);

            this.title_total_agent_count_icon.setVisibility(View.GONE);
            this.title_total_agent_count_ll.setTag(true);


            this.title_reg_customer_count_icon.setVisibility(View.GONE);
            this.title_reg_customer_count_ll.setTag(true);


            this.title_new_order_count_icon.setVisibility(View.GONE);
            this.title_new_order_count_ll.setTag(true);


            this.title_order_count_icon.setVisibility(View.GONE);
            this.title_order_count_ll.setTag(true);


        }


    }

    @Override
    public void onResume() {
        super.onResume();
        unSwipeListView.setFocusable(false);
        unSwipeListView_name.setFocusable(false);
        refreshScrollView.requestFocus();
        refreshScrollView.getRefreshableView().smoothScrollTo(0, 20);
    }
}
