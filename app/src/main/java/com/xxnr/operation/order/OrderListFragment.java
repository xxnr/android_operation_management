package com.xxnr.operation.order;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.xxnr.operation.base.BaseFragment;
import com.xxnr.operation.MsgID;
import com.xxnr.operation.R;
import com.xxnr.operation.base.CommonAdapter;
import com.xxnr.operation.base.CommonViewHolder;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.developTools.msg.MsgCenter;
import com.xxnr.operation.developTools.msg.MsgListener;
import com.xxnr.operation.protocol.ApiInterface;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.protocol.bean.OfflinePayTypeResult;
import com.xxnr.operation.protocol.bean.OfflineStateListResult;
import com.xxnr.operation.protocol.bean.OrderDetailResult;
import com.xxnr.operation.protocol.bean.OrderListResult;
import com.xxnr.operation.utils.StringUtil;
import com.xxnr.operation.widget.CustomSwipeRefreshLayout;
import com.xxnr.operation.widget.UnSwipeGridView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CAI on 2016/5/5.
 */
public class OrderListFragment extends BaseFragment implements CustomSwipeRefreshLayout.OnLoadListener,
        SwipeRefreshLayout.OnRefreshListener, OrderListAdapter.ItemOnClickListener {


    private CustomSwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private int position;
    private int type = 0;
    private int page = 1;
    private boolean isOver = false;
    private OrderListAdapter adapter;
    private PopupWindow popupWindow;
    private TextView check_price;
    private TextView recipient_name;
    private TextView check_state;
    private UnSwipeGridView pay_way_gridView;
    private LinearLayout pop_pay_state_ll;

    private List<OfflinePayTypeResult.OfflinePayTypeBean> offlinePayType;
    List<OfflineStateListResult.RSCsBean> rsCs;

    private Map<String, Boolean> checkedMap = new HashMap<>();//用于存放popWindow中选中的item
    private Map<String, Boolean> checkedMap1 = new HashMap<>();//用于存放popWindow中选中的item
    private String offlinePaymentId;
    private String offlinePayPrice;


    @Override
    public View InItView() {
        View view = inflater.inflate(R.layout.customer_list_fragment_layout, null);

        swipeRefreshLayout = (CustomSwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setOnLoadListener(this);
        // 顶部刷新的样式
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        listView = (ListView) view.findViewById(R.id.custom_list_listView);

        Bundle bundle = getArguments();
        if (bundle != null) {
            position = bundle.getInt("position");
        }
        if (position == 0) {
            type = 0;
        } else if (position == 1) {
            type = 6;
        } else if (position == 2) {
            type = 5;
        }
        showProgressDialog();
        swipeRefreshLayout.setRefreshing(true);
        getData();
        //请求线下付款方式
        getPayWay();
        //请求对应的服务网点
        getStateList();
        //修改用户信息通知
        MsgCenter.addListener(new MsgListener() {

            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                page = 1;
                getData();
            }
        }, MsgID.UP_DATE_ORDER);

        return view;
    }

    private void getData() {
        RequestParams params = new RequestParams();
        params.put("token", App.getApp().getToken());
        params.put("page", page);
        params.put("max", 20);
        if (type != 0) {
            params.put("type", type);
        }
        execApi(ApiType.GET_ORDER_LIST.setMethod(ApiType.RequestMethod.GET), params);
    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {

            case R.id.pop_close:
                if (null != popupWindow && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                break;
            case R.id.pop_pay_state_ll:

                break;
            case R.id.pop_save:
                break;

        }

    }

    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.GET_ORDER_LIST) {
            swipeRefreshLayout.setLoading(false);
            swipeRefreshLayout.setRefreshing(false);
            if (req.getData().getStatus().equals("1000")) {

                OrderListResult listResult = (OrderListResult) req.getData();
                if (listResult.datas != null) {
                    List<OrderListResult.DatasBean.ItemsBean> items = listResult.datas.items;
                    if (items != null && !items.isEmpty()) {
                        isOver = false;
                        if (page == 1) {
                            if (adapter == null) {
                                adapter = new OrderListAdapter(activity, items, this);
                                listView.setAdapter(adapter);
                            } else {
                                adapter.clear();
                                adapter.addAll(items);
                            }
                        } else {
                            adapter.addAll(items);
                        }
                    } else {
                        isOver = true;
                        if (page == 1) {
                            if (adapter != null) {
                                adapter.clear();
                            } else {
                                page--;
                            }
                        }
                    }
                }
            }
        } else if (ApiType.GET_ORDER_DETAIL == req.getApi()) {

            if (req.getData().getStatus().equals("1000")) {

                OrderDetailResult detailResult = (OrderDetailResult) req.getData();

                //初始化数据
                if (offlinePayType != null && !offlinePayType.isEmpty()) {
                    checkedMap.put("3", true);//默认选中现金
                    PayWayAdapter popSkusAdapter = new PayWayAdapter(activity, offlinePayType);
                    pay_way_gridView.setAdapter(popSkusAdapter);
                }
                OrderDetailResult.DatasBean datas = detailResult.datas;
                if (datas != null && datas.order != null) {
                    //收货人和金额
                    recipient_name.setText(datas.consigneeName);
                    if (datas.order.payment != null) {
                        check_price.setText("¥" + StringUtil.toTwoString(datas.order.payment.price + "") + "元");
                        offlinePaymentId = datas.order.payment.id;
                        offlinePayPrice = datas.order.payment.price + "";
                    }
                }
                //默认一个付款网点
                if (datas != null && datas.RSCInfo != null) {
                    check_state.setText(datas.RSCInfo.companyName);
                } else {
                    check_state.setText("");
                }


            }

        } else if (ApiType.GET_OFFLINE_PAY_TYPE == req.getApi()) {
            if (req.getData().getStatus().equals("1000")) {
                OfflinePayTypeResult data = (OfflinePayTypeResult) req.getData();
                offlinePayType = data.offlinePayType;
            }

        } else if (ApiType.GET_OFFLINE_STATE_LIST == req.getApi()) {
            if (req.getData().getStatus().equals("1000")) {
                OfflineStateListResult data = (OfflineStateListResult) req.getData();
                rsCs = data.RSCs;
            }
        }


    }


    @Override
    public void onLoad() {
        if (!isOver) {
            swipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    page++;
                    getData();
                }
            }, 1000);
        } else {
            swipeRefreshLayout.setLoading(false);
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        getData();
    }

    @Override
    public void itemOnClickListener(View view, OrderListResult.DatasBean.ItemsBean itemsBean) {
        if (view.getId() == R.id.go_to_pay) {
            showCheckOfflinePayPopUp(view, itemsBean);

        }
    }

    private void getPayWay() {
        RequestParams params = new RequestParams();
        params.put("token", App.getApp().getToken());
        execApi(ApiType.GET_OFFLINE_PAY_TYPE.setMethod(ApiType.RequestMethod.GET), params);
    }


    //显示popWindow
    private void showCheckOfflinePayPopUp(View parent, OrderListResult.DatasBean.ItemsBean itemsBean) {
        if (null != popupWindow) {
            popupWindow.dismiss();
        } else {
            initCheckOfflinePayPopUptWindow();
        }
        //请求订单详情
        getOrderDetail(itemsBean.id);

        //设置背景及展示
        showDialogBg(0, (OrderManageActivity) activity);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

    }

    //请求对应的服务网点
    private void getStateList() {

        RequestParams params = new RequestParams();
        params.put("token", App.getApp().getToken());
        params.put("max", 50);
        params.put("page", 1);
        execApi(ApiType.GET_OFFLINE_STATE_LIST.setMethod(ApiType.RequestMethod.GET), params);


    }

    //请求订单详情以获取金额
    private void getOrderDetail(String orderId) {

        RequestParams params = new RequestParams();
        params.put("token", App.getApp().getToken());
        execApi(ApiType.GET_ORDER_DETAIL.setMethod(ApiType.RequestMethod.GET)
                .setOpt(ApiInterface.GET_ORDER_DETAIL + "/" + orderId), params);

    }


    /**
     * 创建PopupWindow
     */
    private void initCheckOfflinePayPopUptWindow() {
        // TODO Auto-generated method stub
        View popupWindow_view = LayoutInflater.from(activity).inflate(
                R.layout.pop_layout_rsc_check_pay_dialog, null, false);
        // 创建PopupWindow实例
        popupWindow = new PopupWindow(popupWindow_view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setAnimationStyle(R.style.popWindow_anim_style);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //设置背景及展示
                showDialogBg(1, (OrderManageActivity) activity);
            }
        });

        //初始化组件
        ImageView pop_close = (ImageView) popupWindow_view.findViewById(R.id.pop_close);
        pop_close.setOnClickListener(this);
        check_price = (TextView) popupWindow_view.findViewById(R.id.check_price); //待审金额
        recipient_name = (TextView) popupWindow_view.findViewById(R.id.recipient_name); //收货人姓名
        check_state = (TextView) popupWindow_view.findViewById(R.id.pop_pay_state); //付款网点
        pop_pay_state_ll = (LinearLayout) popupWindow_view.findViewById(R.id.pop_pay_state_ll);//付款网点lin

        TextView pop_sure = (TextView) popupWindow_view.findViewById(R.id.pop_save);
        pop_sure.setOnClickListener(this);
        pop_pay_state_ll.setOnClickListener(this);
        pay_way_gridView = (UnSwipeGridView) popupWindow_view.findViewById(R.id.pay_way_gridView);

    }


    class PayWayAdapter extends CommonAdapter<OfflinePayTypeResult.OfflinePayTypeBean> {

        public PayWayAdapter(Context context, List<OfflinePayTypeResult.OfflinePayTypeBean> data) {
            super(context, data, R.layout.item_rsc_pay_way_gird_layout);
        }

        @Override
        public void convert(final CommonViewHolder holder, final OfflinePayTypeResult.OfflinePayTypeBean offlinePayTypeEntity) {
            if (offlinePayTypeEntity != null) {

                final CheckBox checkBox = (CheckBox) holder.getView(R.id.offline_pay_way_checkBox);
                checkBox.setText(offlinePayTypeEntity.name);

                //如果选中，集合中的值为true 否则为false
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 重置，确保最多只有一项被选中
                        for (String key : checkedMap.keySet()) {
                            checkedMap.put(key, false);
                        }
                        checkedMap.put(offlinePayTypeEntity.type + "", true);
                        PayWayAdapter.this.notifyDataSetChanged();
                    }

                });


                //根据map刷新适配器的选中状态
                Boolean res = false;
                if (checkedMap.get(offlinePayTypeEntity.type + "") != null) {
                    res = checkedMap.get(offlinePayTypeEntity.type + "");
                }
                checkBox.setChecked(res);

            }

        }
    }

    //控制背景灯光
    private void showDialogBg(int bg, BgSwitch bgSwitch) {
        bgSwitch.backgroundSwitch(bg);
    }

    public interface BgSwitch {
        void backgroundSwitch(int bg);
    }


    class StateSpinnerAdapter extends CommonAdapter<OfflineStateListResult.RSCsBean> {


        public StateSpinnerAdapter(Context context, List<OfflineStateListResult.RSCsBean> data) {
            super(context, data, R.layout.item_for_spinner);
        }

        @Override
        public void convert(CommonViewHolder holder, OfflineStateListResult.RSCsBean rsCsBean) {

            if (rsCsBean != null && rsCsBean.RSCInfo != null) {
                if (StringUtil.checkStr(rsCsBean.RSCInfo.companyName)) {
                    holder.setText(R.id.spinner_text, rsCsBean.RSCInfo.companyName);
                }
            }
        }
    }


}
