package com.xxnr.operation.modules.datacenter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
public class AgentGroupReportOrderFragment extends BaseFragment {
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
    private View cacheView;


    public static AgentGroupReportOrderFragment newInstance(String startStr, String endDateStr) {
        AgentGroupReportOrderFragment mFragment = new AgentGroupReportOrderFragment();
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
            case R.id.order_complete_count_ll:
                SORT = "COMPLETEDORDER";
                sortOrder(v, SORT);
                break;
            case R.id.order_complete_price_ll:
                SORT = "COMPLETEDORDERPAIDAMOUT";
                sortOrder(v, SORT);
                break;
        }
    }

    //sort
    public void sortOrder(View v, String sort) {

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
        cacheView=v;
    }

    @Override
    public View InItView() {
        View view = inflater.inflate(R.layout.fragment_agent_group_report_order, null);
        initView(view);
        if (getArguments() != null) {
            startStr = getArguments().getString("startStr");
            endDateStr = getArguments().getString("endDateStr");
        }
        showProgressDialog();
        SORT = "COMPLETEDORDER";
        SORTORDER = -1;
        getData(SORT);
        return view;
    }


    private void initView(View view) {

        unSwipeListView = ((UnSwipeListView) view.findViewById(R.id.unSwipeListView));
        unSwipeListView_name = ((UnSwipeListView) view.findViewById(R.id.unSwipeListView_name));
        View header_unSwipeListView_name = inflater.inflate(R.layout.head_agent_report_name, null);
        View header_unSwipeListView = inflater.inflate(R.layout.head_agent_group_report_order, null);
        unSwipeListView_name.addHeaderView(header_unSwipeListView_name);
        unSwipeListView.addHeaderView(header_unSwipeListView);

        titleViewHolder = new TitleViewHolder(header_unSwipeListView);
        titleViewHolder.init();
        titleViewHolder.reset();
        titleViewHolder.order_complete_count_icon.setVisibility(View.VISIBLE);
        titleViewHolder.order_complete_count_icon.setBackgroundResource(R.mipmap.sort_down);
        titleViewHolder.order_complete_count_ll.setTag(false);
    }


    private void getData(String sort) {
        RequestParams params = new RequestParams();
        params.put("token", App.getApp().getToken());
        params.put("max", 20);
        params.put("type", 2);
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
            }else {
                if (adapter != null) {
                    adapter.clear();
                }
                if (nameAdapter != null) {
                    nameAdapter.clear();
                }
            }
        }
    }

    //下拉刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(AgentGroupRefresh event) {
        if (event.current_page_isOrder){
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
            page=1;
            SEARCH = event.search;
            getData(SORT);
        } else {
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
            titleViewHolder.order_complete_count_icon.setVisibility(View.VISIBLE);
            titleViewHolder.order_complete_count_icon.setBackgroundResource(R.mipmap.sort_down);
            titleViewHolder.order_complete_count_ll.setTag(false);
            SEARCH=null;
            SORT = "COMPLETEDORDER";
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
            super(context, data, R.layout.item_agent_group_report_order);
        }

        @Override
        public void convert(CommonViewHolder holder, AgentReportTotalResult.AgentReportsBean agentReportBean) {
            if (agentReportBean != null) {
                if (holder.getPosition() % 2 == 0) {
                    holder.getView(R.id.item_agent_report_ll).setBackgroundColor(getResources().getColor(R.color.order_title_bg));
                } else {
                    holder.getView(R.id.item_agent_report_ll).setBackgroundColor(getResources().getColor(R.color.white));
                }
                holder.setText(R.id.item_order_complete_count, agentReportBean.completedOrderCount + "");//完成订单数
                holder.setText(R.id.item_order_complete_price, StringUtil.toTwoString(agentReportBean.completedOrderPaidAmount + ""));//完成订单金额
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
        public ImageView order_complete_count_icon;
        public ImageView order_complete_price_icon;


        public LinearLayout order_complete_count_ll;
        public LinearLayout order_complete_price_ll;


        public TitleViewHolder(View rootView) {
            this.order_complete_count_icon = (ImageView) rootView.findViewById(R.id.order_complete_count_icon);
            this.order_complete_price_icon = (ImageView) rootView.findViewById(R.id.order_complete_price_icon);


            this.order_complete_count_ll = (LinearLayout) rootView.findViewById(R.id.order_complete_count_ll);
            this.order_complete_price_ll = (LinearLayout) rootView.findViewById(R.id.order_complete_price_ll);

        }


        public void init() {
            this.order_complete_count_ll.setOnClickListener(AgentGroupReportOrderFragment.this);
            this.order_complete_price_ll.setOnClickListener(AgentGroupReportOrderFragment.this);
        }


        public void reset() {
            order_complete_count_ll.setEnabled(true);
            order_complete_price_ll.setEnabled(true);

            this.order_complete_count_icon.setVisibility(View.GONE);
            this.order_complete_count_ll.setTag(true);

            this.order_complete_price_icon.setVisibility(View.GONE);
            this.order_complete_price_ll.setTag(true);
        }

        public void resetOnSeach() {
            this.order_complete_count_icon.setVisibility(View.GONE);
            this.order_complete_price_icon.setVisibility(View.GONE);

            order_complete_count_ll.setEnabled(false);
            order_complete_price_ll.setEnabled(false);
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
