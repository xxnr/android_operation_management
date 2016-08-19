package com.xxnr.operation.modules.datacenter;

import android.content.Context;
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
import com.xxnr.operation.modules.BaseFragment;
import com.xxnr.operation.modules.CommonAdapter;
import com.xxnr.operation.modules.CommonViewHolder;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.protocol.bean.AgentReportResult;
import com.xxnr.operation.utils.DateFormatUtils;
import com.xxnr.operation.utils.StringUtil;
import com.xxnr.operation.widget.UnSwipeListView;

import java.util.List;

/**
 * Created by 何鹏 on 2016/5/24.
 */
public class AgentYesterdayDayReportFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener {
    private UnSwipeListView unSwipeListView;
    private UnSwipeListView unSwipeListView_name;
    private TextView updateTime;
    private TextView select_page_tv;

    private int page = 1;
    private PullToRefreshScrollView refreshScrollView;
    private TitleViewHolder titleViewHolder;
    private String SORT = "";
    private int SORTORDER;

    private AgentAdapter adapter;
    private NameAgentAdapter nameAdapter;


    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.title_new_customer_count_ll:
                SORT = "NEWINVITEE";
                sortOrder(v, SORT);
                break;
            case R.id.title_total_customer_count_ll:
                SORT = "TOTALINVITEE";
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
            case R.id.title_total_reg_customer_count_ll:
                SORT = "TOTALPOTENTIALCUSTOMER";
                sortOrder(v, SORT);
                break;
            case R.id.title_order_count_ll:
                SORT = "TOTALCOMPLETEDORDER";
                sortOrder(v, SORT);
                break;
            case R.id.title_price_amount_ll:
                SORT = "TOTALPAIDAMOUT";
                sortOrder(v, SORT);
                break;
            case R.id.select_agent_page_tv:
                MsgCenter.fireNull(MsgID.Agent_Page_Select, "group");
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
                SORTORDER=-1;
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
                SORTORDER=1;
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

        View view = inflater.inflate(R.layout.fragment_agent_yesterday_report, null);

        refreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.pullToRefreshScrollView);
        refreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        refreshScrollView.setOnRefreshListener(this);
        refreshScrollView.getRefreshableView().scrollTo(10, 10);
        refreshScrollView.setVisibility(View.GONE);

        updateTime = (TextView) view.findViewById(R.id.updateTime);
        select_page_tv = (TextView) view.findViewById(R.id.select_agent_page_tv);
        select_page_tv.setOnClickListener(this);

        unSwipeListView = ((UnSwipeListView) view.findViewById(R.id.unSwipeListView));
        unSwipeListView_name = ((UnSwipeListView) view.findViewById(R.id.unSwipeListView_name));
        View header_unSwipeListView_name = inflater.inflate(R.layout.head_agent_report_name, null);
        View header_unSwipeListView = inflater.inflate(R.layout.head_agent_yesterday_report, null);
        unSwipeListView_name.addHeaderView(header_unSwipeListView_name);
        unSwipeListView.addHeaderView(header_unSwipeListView);

        titleViewHolder = new TitleViewHolder(header_unSwipeListView);
        titleViewHolder.init();
        titleViewHolder.reset();

        titleViewHolder.title_new_customer_count_icon.setVisibility(View.VISIBLE);
        titleViewHolder.title_new_customer_count_icon.setBackgroundResource(R.mipmap.sort_down);
        titleViewHolder.title_new_customer_count_ll.setTag(false);

        showProgressDialog();
        SORT = "NEWINVITEE";
        SORTORDER=-1;
        getData(SORT);
        return view;
    }


    private void getData(String sort) {
        RequestParams params = new RequestParams();
        params.put("token", App.getApp().getToken());
        params.put("max", 20);
        if (SORTORDER != 0) {
            params.put("sort", sort);
            params.put("sortOrder", SORTORDER);
        }
        params.put("page", page);
        execApi(ApiType.GET_AGENT_RANK.setMethod(ApiType.RequestMethod.GET), params);
    }

    @Override
    public void onResponsed(Request req) {

        if (req.getApi() == ApiType.GET_AGENT_RANK) {
            if (refreshScrollView != null && refreshScrollView.isRefreshing()) {
                refreshScrollView.onRefreshComplete();
            }
            if (refreshScrollView != null) {
                refreshScrollView.setVisibility(View.VISIBLE);
            }
            disMissDialog();
            AgentReportResult reqData = (AgentReportResult) req.getData();

            if (StringUtil.checkStr(reqData.lastUpdateTime)) {
                updateTime.setText(DataCenterUtils.changeFormat
                        (DateFormatUtils.convertTime(reqData.lastUpdateTime),
                                "yyyy-MM-dd HH:mm", DataCenterUtils.CHINESE_DATE_FORMAT));
            } else {
                updateTime.setText(DataCenterUtils.getCurrDateStr(DataCenterUtils.CHINESE_DATE_FORMAT));
            }


            List<AgentReportResult.AgentReportYesterdayBean> agentReportYesterday = reqData.agentReportYesterday;
            if (agentReportYesterday != null && !agentReportYesterday.isEmpty()) {
                if (page == 1) {
                    //agent姓名列表
                    if (nameAdapter == null) {
                        nameAdapter = new NameAgentAdapter(activity, agentReportYesterday);
                        unSwipeListView_name.setAdapter(nameAdapter);
                    } else {
                        nameAdapter.clear();
                        nameAdapter.addAll(agentReportYesterday);
                    }
                    //agent列表
                    if (adapter == null) {
                        adapter = new AgentAdapter(activity, agentReportYesterday);
                        unSwipeListView.setAdapter(adapter);

                    } else {
                        adapter.clear();
                        adapter.addAll(agentReportYesterday);
                    }
                } else {
                    //因集合agentReportYesterday 两个适配器是一个对象 所以只需要add一次
                    if (adapter != null && nameAdapter != null) {
                        adapter.addAll(agentReportYesterday);
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
        getData(SORT);
    }

    class AgentAdapter extends CommonAdapter<AgentReportResult.AgentReportYesterdayBean> {


        public AgentAdapter(Context context, List<AgentReportResult.AgentReportYesterdayBean> data) {
            super(context, data, R.layout.item_agent_yesterday_report);
        }

        @Override
        public void convert(CommonViewHolder holder, AgentReportResult.AgentReportYesterdayBean agentReportYesterdayBean) {
            if (agentReportYesterdayBean != null) {

                if (holder.getPosition() % 2 == 0) {
                    holder.getView(R.id.item_agent_report_ll).setBackgroundColor(getResources().getColor(R.color.order_title_bg));
                } else {
                    holder.getView(R.id.item_agent_report_ll).setBackgroundColor(getResources().getColor(R.color.white));
                }
                holder.setText(R.id.new_customer_count, agentReportYesterdayBean.newInviteeCount + "");//昨日新增客户
                holder.setText(R.id.total_customer_count, agentReportYesterdayBean.totalInviteeCount + "");//总客户数
                holder.setText(R.id.total_agent_count, agentReportYesterdayBean.newAgentCount + "");//新增经纪人数
                holder.setText(R.id.reg_customer_count, agentReportYesterdayBean.newPotentialCustomerCount + "");//昨日登记客户
                holder.setText(R.id.total_reg_customer_count, agentReportYesterdayBean.totalPotentialCustomerCount + "");//总登记客户
                holder.setText(R.id.order_count, agentReportYesterdayBean.totalCompletedOrderCount + "");
                holder.setText(R.id.price_amount, StringUtil.toTwoString(agentReportYesterdayBean.totalCompletedOrderPaidAmount + ""));
            }
        }
    }

    class NameAgentAdapter extends CommonAdapter<AgentReportResult.AgentReportYesterdayBean> {


        public NameAgentAdapter(Context context, List<AgentReportResult.AgentReportYesterdayBean> data) {
            super(context, data, R.layout.item_agent_report_name);
        }

        @Override
        public void convert(CommonViewHolder holder, AgentReportResult.AgentReportYesterdayBean agentReportYesterdayBean) {
            if (agentReportYesterdayBean != null) {

                if (holder.getPosition() % 2 == 0) {
                    holder.getView(R.id.item_agent_report_ll).setBackgroundColor(getResources().getColor(R.color.order_title_bg));
                } else {
                    holder.getView(R.id.item_agent_report_ll).setBackgroundColor(getResources().getColor(R.color.white));
                }
                if (StringUtil.checkStr(agentReportYesterdayBean.name)) {
                    holder.setText(R.id.agent_name, agentReportYesterdayBean.name);
                } else {
                    holder.setText(R.id.agent_name, "");
                }
            }
        }
    }


    public class TitleViewHolder {
        public ImageView title_new_customer_count_icon;
        public ImageView title_total_customer_count_icon;
        public ImageView title_total_agent_count_icon;
        public ImageView title_reg_customer_count_icon;
        public ImageView title_total_reg_customer_count_icon;
        public ImageView title_order_count_icon;
        public ImageView title_price_amount_icon;

        public LinearLayout title_new_customer_count_ll;
        public LinearLayout title_total_customer_count_ll;
        public LinearLayout title_total_agent_count_ll;

        public LinearLayout title_reg_customer_count_ll;
        public LinearLayout title_total_reg_customer_count_ll;
        public LinearLayout title_order_count_ll;
        public LinearLayout title_price_amount_ll;

        public TitleViewHolder(View rootView) {
            this.title_new_customer_count_icon = (ImageView) rootView.findViewById(R.id.title_new_customer_count_icon);
            this.title_total_customer_count_icon = (ImageView) rootView.findViewById(R.id.title_total_customer_count_icon);
            this.title_total_agent_count_icon = (ImageView) rootView.findViewById(R.id.title_total_agent_count_icon);
            this.title_reg_customer_count_icon = (ImageView) rootView.findViewById(R.id.title_reg_customer_count_icon);
            this.title_total_reg_customer_count_icon = (ImageView) rootView.findViewById(R.id.title_total_reg_customer_count_icon);
            this.title_order_count_icon = (ImageView) rootView.findViewById(R.id.title_order_count_icon);
            this.title_price_amount_icon = (ImageView) rootView.findViewById(R.id.title_price_amount_icon);

            this.title_new_customer_count_ll = (LinearLayout) rootView.findViewById(R.id.title_new_customer_count_ll);
            this.title_total_customer_count_ll = (LinearLayout) rootView.findViewById(R.id.title_total_customer_count_ll);
            this.title_total_agent_count_ll = (LinearLayout) rootView.findViewById(R.id.title_total_agent_count_ll);
            this.title_reg_customer_count_ll = (LinearLayout) rootView.findViewById(R.id.title_reg_customer_count_ll);
            this.title_total_reg_customer_count_ll = (LinearLayout) rootView.findViewById(R.id.title_total_reg_customer_count_ll);
            this.title_order_count_ll = (LinearLayout) rootView.findViewById(R.id.title_order_count_ll);
            this.title_price_amount_ll = (LinearLayout) rootView.findViewById(R.id.title_price_amount_ll);
        }


        public void init() {
            this.title_new_customer_count_ll.setOnClickListener(AgentYesterdayDayReportFragment.this);
            this.title_total_customer_count_ll.setOnClickListener(AgentYesterdayDayReportFragment.this);
            this.title_total_agent_count_ll.setOnClickListener(AgentYesterdayDayReportFragment.this);
            this.title_reg_customer_count_ll.setOnClickListener(AgentYesterdayDayReportFragment.this);
            this.title_total_reg_customer_count_ll.setOnClickListener(AgentYesterdayDayReportFragment.this);
            this.title_order_count_ll.setOnClickListener(AgentYesterdayDayReportFragment.this);
            this.title_price_amount_ll.setOnClickListener(AgentYesterdayDayReportFragment.this);
        }


        public void reset() {
            this.title_new_customer_count_icon.setVisibility(View.GONE);
            this.title_new_customer_count_ll.setTag(true);

            this.title_total_customer_count_icon.setVisibility(View.GONE);
            this.title_total_customer_count_ll.setTag(true);

            this.title_total_agent_count_icon.setVisibility(View.GONE);
            this.title_total_agent_count_ll.setTag(true);

            this.title_reg_customer_count_icon.setVisibility(View.GONE);
            this.title_reg_customer_count_ll.setTag(true);


            this.title_total_reg_customer_count_icon.setVisibility(View.GONE);
            this.title_total_reg_customer_count_ll.setTag(true);


            this.title_order_count_icon.setVisibility(View.GONE);
            this.title_order_count_ll.setTag(true);


            this.title_price_amount_icon.setVisibility(View.GONE);
            this.title_price_amount_ll.setTag(true);

        }


    }

    @Override
    public void onResume() {
        super.onResume();
        unSwipeListView.setFocusable(false);
        unSwipeListView_name.setFocusable(false);
        refreshScrollView.requestFocus();
        refreshScrollView.getRefreshableView().smoothScrollTo(0, 10);
    }
}
