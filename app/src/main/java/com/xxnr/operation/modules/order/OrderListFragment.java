package com.xxnr.operation.modules.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.xxnr.operation.modules.BaseFragment;
import com.xxnr.operation.MsgID;
import com.xxnr.operation.R;
import com.xxnr.operation.modules.CommonAdapter;
import com.xxnr.operation.modules.CommonViewHolder;
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
import com.xxnr.operation.utils.ListViewAnimationUtils;
import com.xxnr.operation.utils.PullToRefreshUtils;
import com.xxnr.operation.utils.ShowHideUtils;
import com.xxnr.operation.utils.StringUtil;
import com.xxnr.operation.widget.UnSwipeGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CAI on 2016/5/5.
 */
public class OrderListFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2, OrderListAdapter.ItemOnClickListener, OrderListAdapter.ItemOnClickListener1 {

    private PullToRefreshListView listView;
    private int position;
    private int type = 0;
    private int page = 1;
    private OrderListAdapter adapter;
    //审核付款
    private TextView check_price;
    private TextView recipient_name;
    private TextView check_state;
    private PopupWindow popupWindow;
    private UnSwipeGridView pay_way_gridView;

    private List<OfflinePayTypeResult.OfflinePayTypeBean> offlinePayType;
    private List<OfflineStateListResult.RSCsBean> rsCs;
    private Map<String, Boolean> checkedMap = new HashMap<>();//用于存放popWindow中选中的item
    private Map<String, Boolean> checkedMap1 = new HashMap<>();//用于存放popWindow中选中的item
    private String offlinePaymentId;
    private TextView pop_title;
    private LinearLayout pop_lin_step1;
    private LinearLayout pop_lin_step2;
    private ListView pop_lin_step2_listView;
    private boolean confirm_pay_tag = false;

    //配送发货到服务站
    private String deliveringOrderId;
    private PopupWindow popupWindowDelivery;
    private TextView pop_sureDelivery;
    private ListView pop_listView;
    private TextView pop_sure;


    @Override
    public View InItView() {
        View view = inflater.inflate(R.layout.fragment_list, null);


        listView = (PullToRefreshListView) view.findViewById(R.id.custom_list_listView);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
        //设置刷新的文字
        PullToRefreshUtils.setFreshText(listView);

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
        getData();
        //请求线下付款方式
        getPayWay();
        //请求对应的服务网点
        getStateList();
        //修改用户信息通知
        MsgCenter.addListener(new MsgListener() {

            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                listView.getRefreshableView().setSelection(0);
                page = 1;
                getData();
            }
        }, MsgID.UP_DATE_ORDER);


        //滑动时刷新
        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                try {
                    if (((Integer) args[0]) == position) {
                        showProgressDialog();
                        page = 1;
                        getData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, MsgID.select_tab);
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
                if (null != popupWindowDelivery && popupWindowDelivery.isShowing()) {
                    popupWindowDelivery.dismiss();
                }
                break;
            case R.id.pop_pay_state_ll:
                if (pop_lin_step2.getVisibility() == View.GONE) {
                    ShowHideUtils.hideLeftFadeIn(pop_lin_step1);
                    ShowHideUtils.showRightFadeOut(pop_lin_step2);
                    pop_lin_step1.setVisibility(View.GONE);
                    pop_lin_step2.setVisibility(View.VISIBLE);
                    pop_title.setText("选择付款网点");
                    confirm_pay_tag = true;
                }

                if (rsCs != null) {
                    StateSpinnerAdapter adapter = new StateSpinnerAdapter(activity, rsCs);
                    pop_lin_step2_listView.setAdapter(adapter);
                }

                break;
            //确认审核付款
            case R.id.pop_save:
                if (confirm_pay_tag) {
                    ShowHideUtils.hideLeftFadeIn(pop_lin_step2);
                    ShowHideUtils.showRightFadeOut(pop_lin_step1);
                    pop_lin_step2.setVisibility(View.GONE);
                    pop_lin_step1.setVisibility(View.VISIBLE);
                    pop_title.setText("审核付款");
                    confirm_pay_tag = false;
                } else {
                    if (checkedMap != null && !checkedMap.isEmpty() && checkedMap1 != null && !checkedMap1.isEmpty()) {
                        String offlinePayType = "3";
                        for (String key : checkedMap.keySet()) {
                            if (checkedMap.get(key)) {
                                offlinePayType = key;
                            }
                        }
                        String RscId = "";
                        for (String key : checkedMap1.keySet()) {
                            if (checkedMap1.get(key)) {
                                RscId = key;
                            }
                        }
                        RequestParams params = new RequestParams();
                        if (StringUtil.checkStr(offlinePaymentId) && StringUtil.checkStr(RscId)) {
                            params.put("paymentId", offlinePaymentId);
                            params.put("offlinePayType", offlinePayType);
                            params.put("RSCId", RscId);
                            params.put("token", App.getApp().getToken());
                            execApi(ApiType.CONFIRM_OFFLINE_PAY, params);
                        }
                    }
                    if (null != popupWindow && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }

                }

                break;
            case R.id.save:
                if (checkedMap != null && !checkedMap.isEmpty()) {
                    List<Object> list = new ArrayList<>();
                    for (String key : checkedMap.keySet()) {
                        if (checkedMap.get(key)) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("ref", key);
                            map.put("deliverStatus", 4);
                            list.add(map);
                        }
                    }
                    if (StringUtil.checkStr(deliveringOrderId)) {
                        showProgressDialog();
                        RequestParams params = new RequestParams();
                        Map<String, Object> map = new HashMap<>();
                        map.put("SKUs", list);
                        map.put("id", deliveringOrderId);
                        map.put("token", App.getApp().getToken());
                        Gson gson = new Gson();
                        String toJson = gson.toJson(map);
                        params.put("PUT", toJson);
                        execApi(ApiType.SKUS_DELIVERY.setMethod(ApiType.RequestMethod.PUT), params);
                    }
                }
                if (null != popupWindowDelivery && popupWindowDelivery.isShowing()) {
                    popupWindowDelivery.dismiss();
                }
                break;

        }

    }

    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.GET_ORDER_LIST) {
            listView.onRefreshComplete();
            if (req.getData().getStatus().equals("1000")) {
                OrderListResult listResult = (OrderListResult) req.getData();
                if (listResult.datas != null) {
                    List<OrderListResult.DatasBean.ItemsBean> items = listResult.datas.items;
                    if (items != null && !items.isEmpty()) {
                        if (page == 1) {
                            if (adapter == null) {
                                adapter = new OrderListAdapter(activity, items, this, this, position);
                                ListViewAnimationUtils.setListViewAnimationAndAdapter(listView.getRefreshableView(),adapter);
                            } else {
                                adapter.clear();
                                adapter.addAll(items);
                            }
                        } else {
                            if (adapter != null) {
                                adapter.addAll(items);
                            }
                        }
                    } else {
                        if (page == 1) {
                            if (adapter != null) {
                                adapter.clear();
                            }
                        } else {
                            page--;
                            showToast("没有更多订单");
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
                    if (pay_way_gridView!=null){
                        pay_way_gridView.setAdapter(popSkusAdapter);
                    }
                }
                OrderDetailResult.DatasBean datas = detailResult.datas;
                if (datas != null && datas.order != null) {
                    //收货人和金额
                    if (recipient_name!=null&&check_price!=null){
                        recipient_name.setText(datas.consigneeName);

                        if (datas.order.payment != null) {
                            check_price.setText("¥" + StringUtil.toTwoString(datas.order.payment.price + "") + "元");
                            offlinePaymentId = datas.order.payment.id;
                        }
                    }
                }
                //默认一个付款网点
                if (check_state!=null&&pop_sure!=null){
                    if (datas != null && datas.RSCInfo != null) {
                        check_state.setText(datas.RSCInfo.companyName);
                        if (StringUtil.checkStr(datas.RSCInfo.RSC)) {
                            checkedMap1.put(datas.RSCInfo.RSC, true);
                            pop_sure.setEnabled(true);
                        }
                    } else {
                        check_state.setText("");
                        pop_sure.setEnabled(false);
                    }
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
        } else if (ApiType.CONFIRM_OFFLINE_PAY == req.getApi()) {
            if (req.getData().getStatus().equals("1000")) {
                showCustomToast("审核成功", R.mipmap.toast_success_icon);
                listView.getRefreshableView().setSelection(0);
                page = 1;
                getData();
            }
        } else if (ApiType.SKUS_DELIVERY == req.getApi()) {
            if (req.getData().getStatus().equals("1000")) {
                listView.getRefreshableView().setSelection(0);
                showCustomToast("发货成功", R.mipmap.toast_success_icon);
                page = 1;
                getData();

            }
        }

    }


    //审核付款
    @Override
    public void itemOnClickListener(View view, OrderListResult.DatasBean.ItemsBean itemsBean) {
        if (view.getId() == R.id.go_to_pay) {
            showCheckOfflinePayPopUp(view, itemsBean);
        }
    }

    //发货到服务站
    @Override
    public void itemOnClickListener1(View view, OrderListResult.DatasBean.ItemsBean itemsBean) {
        if (view.getId() == R.id.go_to_pay) {
            showPopDelivery(view, itemsBean);
        }
    }

    /**
     * 以下是审核付款--------------------------------------------------------------------------------------------
     */

    //显示popWindow
    private void showCheckOfflinePayPopUp(View parent, OrderListResult.DatasBean.ItemsBean itemsBean) {
        if (null != popupWindow) {
            popupWindow.dismiss();
        } else {
            initCheckOfflinePayPopUptWindow();
        }
        //初始化数据
        pop_title.setText("审核付款");
        pop_lin_step1.setVisibility(View.VISIBLE);
        pop_lin_step2.setVisibility(View.GONE);
        checkedMap.clear();
        checkedMap1.clear();
        offlinePaymentId = null;
        confirm_pay_tag = false;

        pop_sure.setEnabled(false);
        check_price.setText("");
        recipient_name.setText("");

        //请求订单详情
        getOrderDetail(itemsBean.id);

        //设置背景及展示
        showDialogBg(0, (OrderManageActivity) activity);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

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
        LinearLayout pop_pay_state_ll = (LinearLayout) popupWindow_view.findViewById(R.id.pop_pay_state_ll);
        pop_pay_state_ll.setOnClickListener(this);
        pop_sure = (TextView) popupWindow_view.findViewById(R.id.pop_save);
        pop_sure.setOnClickListener(this);
        pop_sure.setEnabled(false);

        pop_title = (TextView) popupWindow_view.findViewById(R.id.pop_title); //popWindow的标题
        check_price = (TextView) popupWindow_view.findViewById(R.id.check_price); //待审金额
        recipient_name = (TextView) popupWindow_view.findViewById(R.id.recipient_name); //收货人姓名
        check_state = (TextView) popupWindow_view.findViewById(R.id.pop_pay_state); //付款网点
        pay_way_gridView = (UnSwipeGridView) popupWindow_view.findViewById(R.id.pay_way_gridView);
        pop_lin_step1 = (LinearLayout) popupWindow_view.findViewById(R.id.pop_lin_step1);
        pop_lin_step2 = (LinearLayout) popupWindow_view.findViewById(R.id.pop_lin_step2);
        pop_lin_step2_listView = (ListView) popupWindow_view.findViewById(R.id.pop_lin_step2_listView);


    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        PullToRefreshUtils.setFreshClose(refreshView);
        page = 1;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        PullToRefreshUtils.setFreshClose(refreshView);
        page++;
        getData();
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

    class StateSpinnerAdapter extends CommonAdapter<OfflineStateListResult.RSCsBean> {
        public StateSpinnerAdapter(Context context, List<OfflineStateListResult.RSCsBean> data) {
            super(context, data, R.layout.item_for_spinner);
        }

        @Override
        public void convert(CommonViewHolder holder, final OfflineStateListResult.RSCsBean rsCsBean) {

            if (rsCsBean != null && rsCsBean.RSCInfo != null) {
                if (StringUtil.checkStr(rsCsBean.RSCInfo.companyName)) {
                    holder.setText(R.id.spinner_text, rsCsBean.RSCInfo.companyName);
                }else {
                    holder.setText(R.id.spinner_text, "");

                }

                final CheckBox checkBox = (CheckBox) holder.getView(R.id.btn_consignees_item);
                //如果选中，集合中的值为true 否则为false
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 重置，确保最多只有一项被选中
                        for (String key : checkedMap1.keySet()) {
                            checkedMap1.put(key, false);
                        }
                        checkedMap1.put(rsCsBean._id, true);
                        StateSpinnerAdapter.this.notifyDataSetChanged();
                        check_state.setText(rsCsBean.RSCInfo.companyName);
                        pop_sure.setEnabled(true);

                    }

                });

                //根据map刷新适配器的选中状态
                Boolean res = false;
                if (checkedMap1.get(rsCsBean._id) != null) {
                    res = checkedMap1.get(rsCsBean._id);
                }
                checkBox.setChecked(res);

            }
        }
    }


    /***********************************以下是网点发货相关*************************************************************/
    /**
     * 创建PopupWindow
     */
    private void initPopDeliveryWindow() {
        // TODO Auto-generated method stub
        // 获取自定义布局文件activity_popupwindow_left.xml的视图
        @SuppressLint("InflateParams") View popupWindow_view = inflater.inflate(
                R.layout.pop_layout_sureorder_dialog, null, false);

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popupWindowDelivery = new PopupWindow(popupWindow_view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindowDelivery.setAnimationStyle(R.style.popWindow_anim_style);
        popupWindowDelivery.setFocusable(true);
        popupWindowDelivery.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindowDelivery.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                showDialogBg(1, (OrderManageActivity) activity);

            }
        });

        //初始化组件
        ImageView pop_close = (ImageView) popupWindow_view.findViewById(R.id.pop_close);
        TextView pop_order_title = (TextView) popupWindow_view.findViewById(R.id.pop_order_title);
        pop_order_title.setText("发货到服务站");
        pop_close.setOnClickListener(this);
        pop_sureDelivery = (TextView) popupWindow_view.findViewById(R.id.save);
        pop_sureDelivery.setOnClickListener(this);

        pop_listView = (ListView) popupWindow_view.findViewById(R.id.pop_listView);


    }

    //显示popWindow
    private void showPopDelivery(View parent, OrderListResult.DatasBean.ItemsBean itemsBean) {
        if (null != popupWindowDelivery) {
            popupWindowDelivery.dismiss();
        } else {
            initPopDeliveryWindow();
        }
        checkedMap.clear();
        if (StringUtil.checkStr(itemsBean.id)) {
            deliveringOrderId = itemsBean.id;
        } else {
            deliveringOrderId = null;
        }
        //初始化按钮
        pop_sureDelivery.setEnabled(false);
        pop_sureDelivery.setText("确定");
        //初始化数据
        final List<OrderListResult.DatasBean.ItemsBean.SKUsBean> skUs = new ArrayList<>();
        List<OrderListResult.DatasBean.ItemsBean.SKUsBean> skUs1 = itemsBean.SKUs;
        if (skUs1 != null) {
            for (int i = 0; i < skUs1.size(); i++) {
                OrderListResult.DatasBean.ItemsBean.SKUsBean skus = skUs1.get(i);
                if (skus != null && skus.deliverStatus == 1) {
                    skUs.add(skus);
                }
            }
        }
        if (!skUs.isEmpty()) {
            PopSkusDeliveryAdapter popSkusDeliveryAdapter = new PopSkusDeliveryAdapter(activity, skUs);
            pop_listView.setAdapter(popSkusDeliveryAdapter);
        }

        //设置背景及展示
        showDialogBg(0, (OrderManageActivity) activity);
        popupWindowDelivery.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }


    //网点开始发货的Sku列表
    class PopSkusDeliveryAdapter extends CommonAdapter<OrderListResult.DatasBean.ItemsBean.SKUsBean> {
        private List<OrderListResult.DatasBean.ItemsBean.SKUsBean> list;

        public PopSkusDeliveryAdapter(Context context, List<OrderListResult.DatasBean.ItemsBean.SKUsBean> data) {
            super(context, data, R.layout.item_pop_sureorder_layout);
            this.list = data;
        }

        @Override
        public void convert(CommonViewHolder holder, final OrderListResult.DatasBean.ItemsBean.SKUsBean skus) {

            if (skus != null) {
                //商品个数
                holder.setText(R.id.sku_count, "X " + skus.count + "");
                //商品名
                if (StringUtil.checkStr(skus.productName)) {
                    holder.setText(R.id.sku_name, skus.productName);
                } else {
                    holder.setText(R.id.sku_name, "");
                }
                //Sku属性
                StringBuilder stringBuilder = new StringBuilder();
                if (skus.attributes != null && !skus.attributes.isEmpty()) {
                    for (int k = 0; k < skus.attributes.size(); k++) {
                        if (StringUtil.checkStr(skus.attributes.get(k).name)
                                && StringUtil.checkStr(skus.attributes.get(k).value)) {
                            stringBuilder.append(skus.attributes.get(k).name).append(":").append(skus.attributes.get(k).value).append(";");
                        }
                    }
                    String car_attr = stringBuilder.substring(0, stringBuilder.length() - 1);
                    if (StringUtil.checkStr(car_attr)) {
                        holder.setText(R.id.sku_attr, car_attr);
                    } else {
                        holder.setText(R.id.sku_attr, "");
                    }
                } else {
                    holder.setText(R.id.sku_attr, "");
                }

                //附加选项
                TextView sku_addiction = (TextView) holder.getView(R.id.sku_addiction);

                StringBuilder stringAdditions = new StringBuilder();
                if (skus.additions != null && !skus.additions.isEmpty()) {
                    stringAdditions.append("附加项目:");
                    for (int k = 0; k < skus.additions.size(); k++) {
                        if (StringUtil.checkStr(skus.additions.get(k).name)) {
                            stringAdditions.append(skus.additions.get(k).name).append(";");
                        }
                    }
                    String car_additions = stringAdditions.substring(0, stringAdditions.length() - 1);
                    if (StringUtil.checkStr(car_additions)) {
                        sku_addiction.setVisibility(View.VISIBLE);
                        sku_addiction.setText(car_additions);
                    } else {
                        sku_addiction.setText("");
                    }
                } else {
                    sku_addiction.setVisibility(View.GONE);
                }


                //CheckBox
                final CheckBox checkBox = (CheckBox) holder.getView(R.id.btn_surr_order_item);

                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (checkBox.isChecked()) {
                            checkedMap.put(skus.ref,
                                    false);
                        } else {
                            checkedMap.put(skus.ref,
                                    true);
                        }
                        //刷新适配器中的选中状态
                        notifyDataSetChanged();
                        //刷新确定按钮的选中数量
                        int count = setCheckedGoodsCount(list);
                        if (count != 0) {
                            pop_sureDelivery.setText("确定(" + count + ")");
                            pop_sureDelivery.setEnabled(true);
                        } else {
                            pop_sureDelivery.setText("确定");
                            pop_sureDelivery.setEnabled(false);
                        }
                    }
                });
                Boolean res = checkedMap.get(skus.ref);
                if (res != null && res) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                }
            }

        }
    }


    //请求订单详情以获取金额
    private void getOrderDetail(String orderId) {

        RequestParams params = new RequestParams();
        params.put("token", App.getApp().getToken());
        execApi(ApiType.GET_ORDER_DETAIL.setMethod(ApiType.RequestMethod.GET)
                .setOpt(ApiInterface.GET_ORDER_DETAIL + "/" + orderId), params);

    }

    //获取付款方式
    private void getPayWay() {
        RequestParams params = new RequestParams();
        params.put("token", App.getApp().getToken());
        execApi(ApiType.GET_OFFLINE_PAY_TYPE.setMethod(ApiType.RequestMethod.GET), params);
    }

    //请求对应的服务网点
    private void getStateList() {

        RequestParams params = new RequestParams();
        params.put("token", App.getApp().getToken());
        params.put("max", 50);
        params.put("page", 1);
        execApi(ApiType.GET_OFFLINE_STATE_LIST.setMethod(ApiType.RequestMethod.GET), params);

    }


    //控制背景灯光
    private void showDialogBg(int bg, BgSwitch bgSwitch) {
        bgSwitch.backgroundSwitch(bg);
    }

    public interface BgSwitch {
        void backgroundSwitch(int bg);
    }


    //确定选中按钮的数量
    public int setCheckedGoodsCount(List<OrderListResult.DatasBean.ItemsBean.SKUsBean> list) {
        int count = 0;
        List<String> refList = new ArrayList<>();
        for (String key : checkedMap.keySet()) {
            if (checkedMap.get(key)) {
                refList.add(key);
            }
        }
        for (OrderListResult.DatasBean.ItemsBean.SKUsBean skus : list) {
            if (refList.contains(skus.ref)) {
                count += skus.count;
            }
        }
        return count;
    }


}
