package com.xxnr.operation.modules.datacenter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xxnr.operation.R;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.event.AgentGroupDateEvent;
import com.xxnr.operation.event.AgentGroupRefresh;
import com.xxnr.operation.event.AgentGroupRefreshComplete;
import com.xxnr.operation.event.AgentGroupSearch;
import com.xxnr.operation.event.AgentGroupUnSearch;
import com.xxnr.operation.modules.BaseFragment;
import com.xxnr.operation.modules.CommonAdapter;
import com.xxnr.operation.modules.CommonViewHolder;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.protocol.bean.AgentReportTotalResult;
import com.xxnr.operation.utils.StringUtil;
import com.xxnr.operation.widget.UnSwipeListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by 何鹏 on 2016/5/24.
 */
public class AgentGroupReportAchievementFragment extends BaseFragment {
    private UnSwipeListView unSwipeListView;
    private UnSwipeListView unSwipeListView_name;

    private int page = 1;
    private TitleViewHolder titleViewHolder;
    private String SORT = "";
    private int SORTORDER;
    private String SEARCH;

    private AgentAdapter adapter;
    private NameAgentAdapter nameAdapter;

    private String endDateStr;
    private String startStr;

    View cacheView;


    public static AgentGroupReportAchievementFragment newInstance(String startStr, String endDateStr) {
        AgentGroupReportAchievementFragment mFragment = new AgentGroupReportAchievementFragment();
        Bundle bundle = new Bundle();
        bundle.putString("startStr", startStr);
        bundle.putString("endDateStr", endDateStr);
        mFragment.setArguments(bundle);
        return mFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

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
        }
    }

    //sort
    public void sortOrder(View v, String sort) {
        cacheView = v;
        if (titleViewHolder != null) {
            page = 1;
            if ((v.getTag()) != null && (Boolean) v.getTag()) {
                titleViewHolder.reset();
                SORTORDER = -1;
                getData(sort);
                v.setTag(false);
                try {
                    ((LinearLayout) v).getChildAt(1).setBackgroundResource(R.mipmap.sort_down);
                    ((LinearLayout) v).getChildAt(1).setVisibility(View.VISIBLE);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            } else {
                titleViewHolder.reset();
                SORTORDER = 1;
                getData(sort);
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
        View view = inflater.inflate(R.layout.fragment_agent_group_report_achievement, null);
        initView(view);
        if (getArguments() != null) {
            startStr = getArguments().getString("startStr");
            endDateStr = getArguments().getString("endDateStr");
        }


        showProgressDialog();
        SORT = "NEWINVITEE";
        SORTORDER = -1;
        getData(SORT);
        return view;
    }


    private void initView(View view) {

        unSwipeListView = ((UnSwipeListView) view.findViewById(R.id.unSwipeListView));
        unSwipeListView_name = ((UnSwipeListView) view.findViewById(R.id.unSwipeListView_name));
        View header_unSwipeListView_name = inflater.inflate(R.layout.head_agent_report_name, null);
        View header_unSwipeListView = inflater.inflate(R.layout.head_agent_group_report_achievement, null);
        unSwipeListView_name.addHeaderView(header_unSwipeListView_name);
        unSwipeListView.addHeaderView(header_unSwipeListView);

        titleViewHolder = new TitleViewHolder(header_unSwipeListView);
        titleViewHolder.init();
        titleViewHolder.reset();

        titleViewHolder.title_new_customer_count_icon.setVisibility(View.VISIBLE);
        titleViewHolder.title_new_customer_count_icon.setBackgroundResource(R.mipmap.sort_down);
        titleViewHolder.title_new_customer_count_ll.setTag(false);
    }


    private void getData(String sort) {
        RequestParams params = new RequestParams();
        params.put("token", App.getApp().getToken());
        params.put("max", 20);
        params.put("type", 1);
        params.put("dateStart", DataCenterUtils.changeDateFormat(startStr));
        params.put("dateEnd", DataCenterUtils.changeDateFormat(endDateStr));
        params.put("page", page);
        if (SORTORDER != 0) {
            params.put("sort", sort);
            params.put("sortOrder", SORTORDER);
            execApi(ApiType.GET_AGENT_RANK_TOTAL.setMethod(ApiType.RequestMethod.GET), params);
        }
        if (StringUtil.checkStr(SEARCH)) {
            params.put("search", SEARCH);
            execApi(ApiType.GET_AGENT_RANK_TOTAL.setMethod(ApiType.RequestMethod.GET), params);
        }

    }

    @Override
    public void onResponsed(Request req) {

        if (req.getApi() == ApiType.GET_AGENT_RANK_TOTAL) {
            EventBus.getDefault().post(new AgentGroupRefreshComplete());
            if (req.getData().getStatus().equals("1000")) {
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
            } else {
                if (adapter != null) {
                    adapter.clear();
                }
                if (nameAdapter != null) {
                    nameAdapter.clear();
                }
            }

        }
    }

    //上拉加载更多
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(AgentGroupRefresh event) {
        if (!event.current_page_isOrder) {
            page++;
            getData(SORT);
        }
    }

    //接收选中时间
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveDate(AgentGroupDateEvent event) {
        startStr = event.dateStart;
        endDateStr = event.dateEnd;
        page = 1;
        getData(SORT);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearch(AgentGroupSearch event) {
        if (SORTORDER == 0) {
            SEARCH = event.search;
            getData(null);
        } else {
            page = 1;
            titleViewHolder.resetOnSeach();
            SORTORDER = 0;
        }
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnSearch(AgentGroupUnSearch event) {
        if (cacheView!=null){
            Boolean tag = (Boolean) cacheView.getTag();
            if (tag!=null&&tag){
                cacheView.setTag(false);
            }else {
                cacheView.setTag(true);
            }
            sortOrder(cacheView, SORT);
        }else {
            titleViewHolder.reset();
            titleViewHolder.title_new_customer_count_icon.setVisibility(View.VISIBLE);
            titleViewHolder.title_new_customer_count_icon.setBackgroundResource(R.mipmap.sort_down);
            titleViewHolder.title_new_customer_count_ll.setTag(false);
            SEARCH = null;
            SORT = "NEWINVITEE";
            page = 1;
            SORTORDER = -1;
            getData(SORT);
        }
    }


    /**
     * 业绩进度汇总
     */
    class AgentAdapter extends CommonAdapter<AgentReportTotalResult.AgentReportsBean> {
        public AgentAdapter(Context context, List<AgentReportTotalResult.AgentReportsBean> data) {
            super(context, data, R.layout.item_agent_group_report_achievement);
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
            title_new_customer_count_ll.setEnabled(true);
            title_total_agent_count_ll.setEnabled(true);
            title_reg_customer_count_ll.setEnabled(true);
            title_new_order_count_ll.setEnabled(true);
            title_order_count_ll.setEnabled(true);


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


        public void resetOnSeach() {
            title_new_customer_count_ll.setEnabled(false);
            title_total_agent_count_ll.setEnabled(false);
            title_reg_customer_count_ll.setEnabled(false);
            title_new_order_count_ll.setEnabled(false);
            title_order_count_ll.setEnabled(false);

            this.title_new_customer_count_icon.setVisibility(View.GONE);
            this.title_total_agent_count_icon.setVisibility(View.GONE);
            this.title_reg_customer_count_icon.setVisibility(View.GONE);
            this.title_new_order_count_icon.setVisibility(View.GONE);
            this.title_order_count_icon.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        unSwipeListView.setFocusable(false);
        unSwipeListView_name.setFocusable(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
