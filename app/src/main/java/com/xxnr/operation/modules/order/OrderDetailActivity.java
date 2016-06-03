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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.xxnr.operation.MsgID;
import com.xxnr.operation.R;
import com.xxnr.operation.modules.BaseActivity;
import com.xxnr.operation.modules.CommonAdapter;
import com.xxnr.operation.modules.CommonViewHolder;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.developTools.msg.MsgCenter;
import com.xxnr.operation.protocol.ApiInterface;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.protocol.bean.OfflinePayTypeResult;
import com.xxnr.operation.protocol.bean.OfflineStateListResult;
import com.xxnr.operation.protocol.bean.OrderDetailResult;
import com.xxnr.operation.utils.DateFormatUtils;
import com.xxnr.operation.utils.IntentUtil;
import com.xxnr.operation.utils.PopWindowUtils;
import com.xxnr.operation.utils.ShowHideUtils;
import com.xxnr.operation.utils.StringUtil;
import com.xxnr.operation.widget.RecyclerImageView;
import com.xxnr.operation.widget.UnSwipeGridView;
import com.xxnr.operation.widget.UnSwipeListView;
import com.xxnr.operation.widget.WidgetUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 何鹏 on 2016/5/6.
 */
public class OrderDetailActivity extends BaseActivity {
    private TextView name_phone_tv, order_detail_address_tv, order_tv, pay_state_tv,
            total_price_tv;
    private ListView order_shangpin_list;
    private RelativeLayout go_to_pay_rel;
    private String orderId; //订单号
    private UnSwipeListView pay_info_listView;//支付信息列表
    private TextView RSC_state_name;
    private TextView RSC_state_address;
    private TextView RSC_state_phone;
    private ImageView delivery_icon;
    private TextView delivery_text;
    private Button go_to_pay;

    private RelativeLayout pop_bg;
    private LinearLayout state_address_ll;
    private LinearLayout address_shouhuo_ll;
    private TextView pay_time_tv;
    private LinearLayout state_address_ll_none;
    private LinearLayout select_state_address_ll_person;
    private TextView select_state_person_info;

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
    private OrderDetailResult.DatasBean datasBean;
    private TextView add_order_man_tv;
    private Button change_pay_type;
    private TextView pop_sure;

    @Override
    public int getLayout() {
        return R.layout.activity_order_detail;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("订单详情");
        initView();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            orderId = bundle.getString("orderId");
        }
        if (StringUtil.checkStr(orderId)) {
            requestData(orderId);
        }
        //请求线下付款方式
        getPayWay();
        //请求对应的服务网点
        getStateList();
    }

    private void initView() {
        go_to_pay_rel = (RelativeLayout) findViewById(R.id.go_to_pay_rel);
        go_to_pay = (Button) findViewById(R.id.go_to_pay);
        change_pay_type = (Button) findViewById(R.id.change_pay_type);
        pop_bg = (RelativeLayout) findViewById(R.id.pop_bg);

        //头部信息 订单号： 交易状态 送货人 地址
        View head_layout = LayoutInflater.from(OrderDetailActivity.this).inflate(R.layout.head_layout_order_detail, null);
        name_phone_tv = (TextView) head_layout.findViewById(R.id.order_detail_name_tv);
        order_detail_address_tv = (TextView) head_layout.findViewById(R.id.order_detail_address_tv);
        order_tv = (TextView) head_layout.findViewById(R.id.my_order_detail_id);
        add_order_man_tv = (TextView) head_layout.findViewById(R.id.add_order_man);
        pay_state_tv = (TextView) head_layout.findViewById(R.id.pay_state);
        pay_time_tv = (TextView) head_layout.findViewById(R.id.pay_time);

        state_address_ll = (LinearLayout) head_layout.findViewById(R.id.select_state_address_ll); //网点自提
        select_state_address_ll_person = (LinearLayout) head_layout.findViewById(R.id.select_state_address_ll_person);
        address_shouhuo_ll = (LinearLayout) head_layout.findViewById(R.id.address_shouhuo_ll); //配送到户
        state_address_ll_none = (LinearLayout) head_layout.findViewById(R.id.select_state_address_none); //配送到户

        state_address_ll.setVisibility(View.GONE);
        address_shouhuo_ll.setVisibility(View.GONE);
        state_address_ll_none.setVisibility(View.GONE);
        select_state_address_ll_person.setVisibility(View.GONE);

        select_state_person_info = (TextView) head_layout.findViewById(R.id.select_state_person_info);
        RSC_state_name = (TextView) head_layout.findViewById(R.id.select_state_name);
        RSC_state_address = (TextView) head_layout.findViewById(R.id.select_state_address);
        RSC_state_phone = (TextView) head_layout.findViewById(R.id.select_state_phone);


        delivery_icon = (ImageView) head_layout.findViewById(R.id.delivery_icon);
        delivery_text = (TextView) head_layout.findViewById(R.id.delivery_text);

        pay_info_listView = (UnSwipeListView) head_layout.findViewById(R.id.pay_info_listView);
        //尾部信息 去付款
        View foot_layout = LayoutInflater.from(OrderDetailActivity.this).inflate(R.layout.foot_layout_order_detail, null);
        total_price_tv = (TextView) foot_layout.findViewById(R.id.my_order_detail_price);

        order_shangpin_list = (ListView) findViewById(R.id.order_shangpin_list);
        order_shangpin_list.addHeaderView(head_layout);
        order_shangpin_list.addFooterView(foot_layout);

    }


    // 请求网络，获取数据
    private void requestData(String orderId) {
        showProgressDialog();
        RequestParams params = new RequestParams();
        params.put("token", App.getApp().getToken());
        execApi(ApiType.GET_ORDER_DETAIL.setMethod(ApiType.RequestMethod.GET).setOpt(ApiInterface.GET_ORDER_DETAIL + "/" + orderId), params);
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
                    StateSpinnerAdapter adapter = new StateSpinnerAdapter(OrderDetailActivity.this, rsCs);
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
        if (ApiType.GET_ORDER_DETAIL == req.getApi()) {
            OrderDetailResult data = (OrderDetailResult) req.getData();
            if (data.getStatus().equals("1000")) {
                datasBean = data.datas;
                if (datasBean != null) {
                    //订单号
                    order_tv.setText("订单号：" + datasBean.id);
                    //下单时间
                    pay_time_tv.setText(DateFormatUtils.convertTime(datasBean.dateCreated));

                    //下单人
                    if (StringUtil.checkStr(datasBean.buyerName)) {
                        add_order_man_tv.setText(datasBean.buyerName);
                    } else {
                        add_order_man_tv.setText(datasBean.consigneePhone);
                    }
                    //配送方式

                    if (datasBean.deliveryType == 1) {  //网点自提
                        delivery_icon.setVisibility(View.VISIBLE);
                        delivery_text.setVisibility(View.VISIBLE);
                        delivery_icon.setBackgroundResource(R.mipmap.home_delivery_icon);
                        delivery_text.setText("网点自提");

                        select_state_address_ll_person.setVisibility(View.VISIBLE);
                        select_state_person_info.setText(datasBean.consigneeName + "  " + datasBean.consigneePhone);

                    } else {
                        delivery_icon.setVisibility(View.VISIBLE);//其他的暂用 送货到家图标
                        delivery_text.setVisibility(View.VISIBLE);
                        delivery_icon.setBackgroundResource(R.mipmap.state_delivery_icon);
                        delivery_text.setText("配送到户");

                        address_shouhuo_ll.setVisibility(View.VISIBLE);
                        //联系人 及电话
                        name_phone_tv.setText(datasBean.consigneeName + "  " + datasBean.consigneePhone);
                        //联系人地址
                        order_detail_address_tv.setText(datasBean.consigneeAddress);
                    }


                    //设置RSCInfo
                    if (datasBean.RSCInfo != null) {
                        //收货地址网点等信息
                        state_address_ll.setVisibility(View.VISIBLE);
                        if (StringUtil.checkStr(datasBean.RSCInfo.companyName)) {
                            RSC_state_name.setText(datasBean.RSCInfo.companyName);
                        }

                        if (StringUtil.checkStr(datasBean.RSCInfo.RSCAddress)) {
                            RSC_state_address.setText(datasBean.RSCInfo.RSCAddress);
                        }

                        if (StringUtil.checkStr(datasBean.RSCInfo.RSCPhone)) {
                            RSC_state_phone.setText(datasBean.RSCInfo.RSCPhone);
                        }
                    } else {
                        state_address_ll_none.setVisibility(View.VISIBLE);
                    }


                    //合计 与 去支付
                    if (datasBean.order != null) {
                        total_price_tv.setText("¥" + StringUtil.toTwoString(datasBean.order.totalPrice + ""));
                        if (datasBean.order.orderStatus != null) {
                            //订单状态
                            if (StringUtil.checkStr(datasBean.order.orderStatus.value)) {
                                pay_state_tv.setText(datasBean.order.orderStatus.value);
                            }
                            //不支付
                            go_to_pay_rel.setVisibility(View.GONE);
                        }
                    }


                    //支付信息列表
                    if (datasBean.subOrders != null && !datasBean.subOrders.isEmpty()) {
                        PayInfoAdapter payInfoAdapter = new PayInfoAdapter(this, datasBean.subOrders, datasBean.orderType);
                        pay_info_listView.setAdapter(payInfoAdapter);
                    }

                    //子商品列表
                    List<OrderDetailResult.DatasBean.SKUsBean> skUs = datasBean.SKUs;
                    if (skUs != null && !skUs.isEmpty()) {
                        CarAdapter carAdapter = new CarAdapter(true, skUs);
                        order_shangpin_list.setAdapter(carAdapter);
                    } else {
                        CarAdapter carAdapter = new CarAdapter(datasBean.products, false);
                        order_shangpin_list.setAdapter(carAdapter);
                    }


                    //发货

                    go_to_pay_rel.setVisibility(View.GONE);
                    go_to_pay.setVisibility(View.GONE);
                    change_pay_type.setVisibility(View.GONE);
                    go_to_pay.setOnClickListener(null);
                    change_pay_type.setOnClickListener(null);

                    boolean isSure = false;
                    if (datasBean.order != null && datasBean.order.pendingDeliverToRSC) {
                        final List<OrderDetailResult.DatasBean.SKUsBean> skUs2 = new ArrayList<>();
                        List<OrderDetailResult.DatasBean.SKUsBean> skUs1 = datasBean.SKUs;
                        if (skUs1 != null) {

                            for (int i = 0; i < skUs1.size(); i++) {

                                OrderDetailResult.DatasBean.SKUsBean sku = skUs1.get(i);
                                if (sku != null && sku.deliverStatus == 1) {
                                    skUs2.add(sku);
                                    isSure = true;
                                }
                            }

                        }

                        if (isSure) {
                            go_to_pay_rel.setVisibility(View.VISIBLE);
                            go_to_pay.setVisibility(View.VISIBLE);
                            go_to_pay.setText("发货到服务站");
                            go_to_pay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showPopDelivery(v, skUs2);
                                }
                            });
                        }

                    }


                    //审核

                    if (isSure) {
                        if (datasBean.order != null && datasBean.order.orderStatus != null) {
                            if (datasBean.order.orderStatus.type == 7) {
                                go_to_pay_rel.setVisibility(View.VISIBLE);
                                change_pay_type.setVisibility(View.VISIBLE);
                                change_pay_type.setText("审核付款");
                                change_pay_type.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showCheckOfflinePayPopUp(v);
                                    }
                                });
                            }

                        }
                    } else {
                        if (datasBean.order != null && datasBean.order.orderStatus != null) {
                            if (datasBean.order.orderStatus.type == 7) {

                                go_to_pay_rel.setVisibility(View.VISIBLE);
                                go_to_pay.setVisibility(View.VISIBLE);
                                go_to_pay.setText("审核付款");
                                go_to_pay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showCheckOfflinePayPopUp(v);
                                    }
                                });
                            }

                        }

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
                requestData(orderId);
                MsgCenter.fireNull(MsgID.UP_DATE_ORDER);
            }
        } else if (ApiType.SKUS_DELIVERY == req.getApi()) {
            if (req.getData().getStatus().equals("1000")) {
                showCustomToast("发货成功", R.mipmap.toast_success_icon);
                requestData(orderId);
                MsgCenter.fireNull(MsgID.UP_DATE_ORDER);
            }
        }
    }


    //支付信息列表

    public class PayInfoAdapter extends CommonAdapter<OrderDetailResult.DatasBean.SubOrdersBean> {
        private List<OrderDetailResult.DatasBean.SubOrdersBean> data;
        private int orderType;

        public PayInfoAdapter(Context context, List<OrderDetailResult.DatasBean.SubOrdersBean> data, int orderType) {
            super(context, data, R.layout.item_payinfo_orderdetail);
            this.data = data;
            this.orderType = orderType;
        }

        @Override
        public void convert(CommonViewHolder holder, final OrderDetailResult.DatasBean.SubOrdersBean subOrder) {
            if (subOrder != null) {

                //支付阶段
                TextView item_payInfo_step = ( holder.getView(R.id.item_payInfo_step));
                switch (subOrder.type) {
                    case "deposit":
                        item_payInfo_step.setText("阶段一：订金");
                        break;
                    case "balance":
                        item_payInfo_step.setText("阶段二：尾款");
                        break;
                    case "full":
                        item_payInfo_step.setText("订单总额");
                        break;
                }

                //支付状态
                TextView item_payInfo_type =  holder.getView(R.id.item_payInfo_type);
                if (orderType != 0) { //如果交易状态 是已关闭 下方设置已关闭
                    if (subOrder.type.equals("balance")) {//阶段二的子订单
                        try {
                            if (data.get(holder.getPosition() - 1).payStatus == 2) {//如果阶段一的子订单 已付款
                                switch (subOrder.payStatus) {
                                    case 1:
                                        item_payInfo_type.setTextColor(getResources().getColor(R.color.orange));
                                        item_payInfo_type.setText("待付款");
                                        break;
                                    case 2:
                                        item_payInfo_type.setTextColor(getResources().getColor(R.color.default_black));
                                        item_payInfo_type.setText("已付款");
                                        break;
                                    case 3:
                                        item_payInfo_type.setTextColor(getResources().getColor(R.color.orange));
                                        item_payInfo_type.setText("部分付款");
                                        break;
                                }

                            } else {
                                item_payInfo_type.setTextColor(getResources().getColor(R.color.default_black));
                                item_payInfo_type.setText("未开始");
                            }
                        } catch (Exception e) {//如果下标越界 就 设置 默认设置

                            switch (subOrder.payStatus) {
                                case 1:
                                    item_payInfo_type.setTextColor(getResources().getColor(R.color.orange));
                                    item_payInfo_type.setText("待付款");
                                    break;
                                case 2:
                                    item_payInfo_type.setTextColor(getResources().getColor(R.color.default_black));
                                    item_payInfo_type.setText("已付款");
                                    break;
                                case 3:
                                    item_payInfo_type.setTextColor(getResources().getColor(R.color.orange));
                                    item_payInfo_type.setText("部分付款");
                                    break;
                            }
                        }

                    } else {
                        switch (subOrder.payStatus) {
                            case 1:
                                item_payInfo_type.setTextColor(getResources().getColor(R.color.orange));
                                item_payInfo_type.setText("待付款");
                                break;
                            case 2:
                                item_payInfo_type.setTextColor(getResources().getColor(R.color.default_black));
                                item_payInfo_type.setText("已付款");
                                break;
                            case 3:
                                item_payInfo_type.setTextColor(getResources().getColor(R.color.orange));
                                item_payInfo_type.setText("部分付款");
                                break;
                        }
                    }

                } else {
                    item_payInfo_type.setTextColor(getResources().getColor(R.color.default_black));
                    item_payInfo_type.setText("已关闭");
                }

                //应支付金额
                holder.setText(R.id.to_pay_price, "¥" + StringUtil.toTwoString(subOrder.price + ""));
                //已支付金额
                holder.setText(R.id.order_yet_price, "¥" + StringUtil.toTwoString(subOrder.paidPrice + ""));
                //查看详情
                TextView to_get_pay_detail =  holder.getView(R.id.to_get_pay_detail);
                if (subOrder.payStatus == 2 || subOrder.payStatus == 3) {
                    to_get_pay_detail.setVisibility(View.VISIBLE);
                    to_get_pay_detail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("payInfo",  subOrder);
                            bundle.putString("orderId", orderId);
                            IntentUtil.activityForward(OrderDetailActivity.this, OrderPayDetailActivity.class, bundle, false);
                        }
                    });
                } else {
                    to_get_pay_detail.setVisibility(View.GONE);
                }
            }
        }
    }


    //商品列表(方法特殊 暂时继承BaseAdapter)
    public class CarAdapter extends BaseAdapter {

        private List<OrderDetailResult.DatasBean.ProductBean> goodsList;
        private List<OrderDetailResult.DatasBean.SKUsBean> SKUsList;
        private boolean flag;

        public CarAdapter(List<OrderDetailResult.DatasBean.ProductBean> goodsList, boolean flag) {
            this.goodsList = goodsList;
            this.flag = flag;
        }

        public CarAdapter(boolean flag, List<OrderDetailResult.DatasBean.SKUsBean> SKUsList) {
            this.flag = flag;
            this.SKUsList = SKUsList;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if (flag) {
                return SKUsList.size() > 0 ? SKUsList.size() : 0;
            } else {
                return goodsList.size() > 0 ? goodsList.size() : 0;
            }
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            if (flag) {
                return SKUsList.get(position);
            } else {
                return goodsList.get(position);
            }

        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        class ViewHolder {
            private LinearLayout goods_car_bar;
            private RecyclerImageView ordering_item_img;
            private TextView ordering_now_pri, ordering_item_name, goods_car_deposit,
                    goods_car_weikuan, ordering_item_geshu, ordering_item_attr, ordering_item_orderType;
            private UnSwipeListView additions_listView;


            public ViewHolder(View convertView) {
                goods_car_bar = (LinearLayout) convertView.findViewById(R.id.goods_car_item_bar);
                ordering_item_img = (RecyclerImageView) convertView//商品图
                        .findViewById(R.id.ordering_item_img);
                ordering_item_geshu = (TextView) convertView//商品个数
                        .findViewById(R.id.ordering_item_geshu);
                ordering_now_pri = (TextView) convertView//商品价格
                        .findViewById(R.id.ordering_now_pri);
                ordering_item_name = (TextView) convertView//商品名
                        .findViewById(R.id.ordering_item_name);
                ordering_item_attr = (TextView) convertView  //商品sku属性
                        .findViewById(R.id.ordering_item_attr);
                ordering_item_orderType = (TextView) convertView//商品发货状态
                        .findViewById(R.id.ordering_item_orderType);
                goods_car_deposit = (TextView) convertView//汽车定金
                        .findViewById(R.id.goods_car_item_bar_deposit);
                goods_car_weikuan = (TextView) convertView//汽车尾款
                        .findViewById(R.id.goods_car_item_bar_weikuan);
                additions_listView = (UnSwipeListView) convertView//附加选项
                        .findViewById(R.id.additions_listView);
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(OrderDetailActivity.this)
                        .inflate(R.layout.item_item_order_list, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();


            if (flag) {
                //商品图片
                if (StringUtil.checkStr(SKUsList.get(position).thumbnail)) {
                    Picasso.with(OrderDetailActivity.this)
                            .load(MsgID.IP + SKUsList.get(position).thumbnail)
                            .error(R.mipmap.error)
                            .placeholder(R.mipmap.zhanweitu)
                            .into(holder.ordering_item_img);
                }
                //商品个数
                holder.ordering_item_geshu.setText("X " + SKUsList.get(position).count + "");
                //商品名
                if (StringUtil.checkStr(SKUsList.get(position).productName)) {
                    holder.ordering_item_name.setEms(10);
                    holder.ordering_item_name.setText(SKUsList.get(position).productName);
                }
                //商品发货状态
                if (SKUsList.get(position).deliverStatus != 0) {
                    holder.ordering_item_orderType.setVisibility(View.VISIBLE);
                    if (SKUsList.get(position).deliverStatus == 1) {
                        holder.ordering_item_orderType.setTextColor(getResources().getColor(R.color.orange));
                        holder.ordering_item_orderType.setText("未发货");
                    } else if (SKUsList.get(position).deliverStatus == 2) {
                        holder.ordering_item_orderType.setTextColor(getResources().getColor(R.color.orange));
                        holder.ordering_item_orderType.setText("配送中");
                    } else if (SKUsList.get(position).deliverStatus == 4) {
                        holder.ordering_item_orderType.setTextColor(getResources().getColor(R.color.orange));
                        holder.ordering_item_orderType.setText("已到服务站");
                    } else if (SKUsList.get(position).deliverStatus == 5) {
                        holder.ordering_item_orderType.setTextColor(getResources().getColor(R.color.orange));
                        holder.ordering_item_orderType.setText("已收货");
                    }
                }
                //附加选项的总价
                float car_additions_price = 0;
                List<OrderDetailResult.DatasBean.SKUsBean.Additions> additions = SKUsList.get(position).additions;
                if (additions != null && !additions.isEmpty()) {
                    for (int k = 0; k < additions.size(); k++) {
                        if (StringUtil.checkStr(additions.get(k).name)) {
                            car_additions_price += additions.get(k).price;
                        }
                    }
                }

                if (SKUsList.get(position).deposit == 0) {
                    holder.goods_car_bar.setVisibility(View.GONE);
                } else {
                    holder.goods_car_bar.setVisibility(View.VISIBLE);
                    String deposit = StringUtil.toTwoString(SKUsList
                            .get(position).deposit * SKUsList.get(position).count + "");
                    if (StringUtil.checkStr(deposit)) {
                        holder.goods_car_deposit.setText("¥" + deposit);
                    }
                    String weiKuan = StringUtil.toTwoString((SKUsList.get(position).price + car_additions_price - SKUsList
                            .get(position).deposit) * SKUsList.get(position).count + "");
                    if (StringUtil.checkStr(weiKuan)) {
                        holder.goods_car_weikuan.setText("¥" + weiKuan);
                    }
                }
                holder.ordering_now_pri.setText("¥" + StringUtil.toTwoString(SKUsList
                        .get(position).price + ""));

                //Sku属性
                StringBuilder stringBuilder = new StringBuilder();
                if (SKUsList.get(position).attributes != null && !SKUsList.get(position).attributes.isEmpty()) {
                    for (int k = 0; k < SKUsList.get(position).attributes.size(); k++) {
                        if (StringUtil.checkStr(SKUsList.get(position).attributes.get(k).name)
                                && StringUtil.checkStr(SKUsList.get(position).attributes.get(k).value)) {
                            stringBuilder.append(SKUsList.get(position).attributes.get(k).name).append(":").append(SKUsList.get(position).attributes.get(k).value).append(";");
                        }
                    }
                    String car_attr = stringBuilder.substring(0, stringBuilder.length() - 1);
                    if (StringUtil.checkStr(car_attr)) {
                        holder.ordering_item_attr.setText(car_attr);
                    }
                }
                //附加选项
                if (additions != null && !additions.isEmpty()) {
                    holder.additions_listView.setVisibility(View.VISIBLE);
                    AdditionsAdapter adapter = new AdditionsAdapter(OrderDetailActivity.this, additions);
                    holder.additions_listView.setAdapter(adapter);
                    WidgetUtil.setListViewHeightBasedOnChildren(holder.additions_listView);
                } else {
                    holder.additions_listView.setVisibility(View.GONE);
                }


            } else {
                //商品图片
                if (StringUtil.checkStr(goodsList.get(position).thumbnail)) {

                    Picasso.with(OrderDetailActivity.this)
                            .load(MsgID.IP + goodsList.get(position).thumbnail)
                            .error(R.mipmap.error)
                            .placeholder(R.mipmap.zhanweitu)
                            .into(holder.ordering_item_img);
                }
                //商品个数
                holder.ordering_item_geshu.setText("X " + goodsList.get(position).count + "");
                //商品名
                if (StringUtil.checkStr(goodsList.get(position).name)) {
                    holder.ordering_item_name.setEms(10);
                    holder.ordering_item_name.setText(goodsList.get(position).name);
                }
                //商品发货状态
                if (StringUtil.checkStr(goodsList.get(position).deliverStatus)) {
                    holder.ordering_item_orderType.setVisibility(View.VISIBLE);
                    if (goodsList.get(position).deliverStatus.equals("1")) {
                        holder.ordering_item_orderType.setTextColor(getResources().getColor(R.color.orange));
                        holder.ordering_item_orderType.setText("未发货");
                    } else if (goodsList.get(position).deliverStatus.equals("2")) {
                        holder.ordering_item_orderType.setTextColor(getResources().getColor(R.color.orange));
                        holder.ordering_item_orderType.setText("配送中");
                    } else if (goodsList.get(position).deliverStatus.equals("4")) {
                        holder.ordering_item_orderType.setTextColor(getResources().getColor(R.color.orange));
                        holder.ordering_item_orderType.setText("已到服务站");
                    } else if (goodsList.get(position).deliverStatus.equals("5")) {
                        holder.ordering_item_orderType.setTextColor(getResources().getColor(R.color.orange));
                        holder.ordering_item_orderType.setText("已收货");
                    }
                }

                if (goodsList.get(position).deposit == 0) {
                    holder.goods_car_bar.setVisibility(View.GONE);
                } else {
                    holder.goods_car_bar.setVisibility(View.VISIBLE);
                    String deposit = StringUtil.toTwoString(goodsList
                            .get(position).deposit * goodsList.get(position).count + "");
                    if (StringUtil.checkStr(deposit)) {
                        holder.goods_car_deposit.setText("¥" + deposit);
                        String weiKuan = StringUtil.toTwoString((goodsList.get(position).price - goodsList
                                .get(position).deposit) * goodsList.get(position).count + "");
                        if (StringUtil.checkStr(weiKuan)) {
                            holder.goods_car_weikuan.setText("¥" + weiKuan);
                        }
                    }
                    String now_pri = StringUtil.toTwoString(goodsList
                            .get(position).price + "");
                    if (StringUtil.checkStr(now_pri)) {
                        holder.ordering_now_pri.setText("¥" + now_pri);
                    }
                }

            }
            return convertView;
        }
    }


    class AdditionsAdapter extends CommonAdapter<OrderDetailResult.DatasBean.SKUsBean.Additions> {


        public AdditionsAdapter(Context context, List<OrderDetailResult.DatasBean.SKUsBean.Additions> data) {
            super(context, data, R.layout.item_for_additions_layout);
        }

        @Override
        public void convert(CommonViewHolder holder, OrderDetailResult.DatasBean.SKUsBean.Additions additions) {
            if (additions != null) {
                if (StringUtil.checkStr(additions.name)) {
                    holder.setText(R.id.item_additions_name, additions.name);
                    holder.setText(R.id.item_additions_price, "¥" + StringUtil.toTwoString(additions.price + ""));
                } else {
                    holder.setText(R.id.item_additions_name, "");
                    holder.setText(R.id.item_additions_price, "");
                }
            }

        }
    }


    /**
     * 以下是审核付款--------------------------------------------------------------------------------------------
     */

    //显示popWindow
    private void showCheckOfflinePayPopUp(View parent) {
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

        //订单详情
        //初始化数据
        if (offlinePayType != null && !offlinePayType.isEmpty()) {
            checkedMap.put("3", true);//默认选中现金
            PayWayAdapter popSkusAdapter = new PayWayAdapter(OrderDetailActivity.this, offlinePayType);
            pay_way_gridView.setAdapter(popSkusAdapter);
        }

        if (datasBean != null && datasBean.order != null) {
            //收货人和金额
            recipient_name.setText(datasBean.consigneeName);
            if (datasBean.order.payment != null) {
                check_price.setText("¥" + StringUtil.toTwoString(datasBean.order.payment.price + "") + "元");
                offlinePaymentId = datasBean.order.payment.id;
            }
        }
        //默认一个付款网点
        if (datasBean != null && datasBean.RSCInfo != null) {
            check_state.setText(datasBean.RSCInfo.companyName);
            if (StringUtil.checkStr(datasBean.RSCInfo.RSC)) {
                checkedMap1.put(datasBean.RSCInfo.RSC, true);
            }
            pop_sure.setEnabled(true);
        } else {
            pop_sure.setEnabled(false);
            check_state.setText("");
        }
        //设置背景及展示
        PopWindowUtils.setBackgroundBlack(pop_bg, 0);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

    }


    /**
     * 创建PopupWindow
     */
    private void initCheckOfflinePayPopUptWindow() {
        // TODO Auto-generated method stub
        View popupWindow_view = LayoutInflater.from(OrderDetailActivity.this).inflate(
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
                PopWindowUtils.setBackgroundBlack(pop_bg, 1);
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


    class PayWayAdapter extends CommonAdapter<OfflinePayTypeResult.OfflinePayTypeBean> {
        public PayWayAdapter(Context context, List<OfflinePayTypeResult.OfflinePayTypeBean> data) {
            super(context, data, R.layout.item_rsc_pay_way_gird_layout);
        }

        @Override
        public void convert(final CommonViewHolder holder, final OfflinePayTypeResult.OfflinePayTypeBean offlinePayTypeEntity) {
            if (offlinePayTypeEntity != null) {

                final CheckBox checkBox =  holder.getView(R.id.offline_pay_way_checkBox);
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

                final CheckBox checkBox =  holder.getView(R.id.btn_consignees_item);
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
        @SuppressLint("InflateParams") View popupWindow_view = LayoutInflater.from(OrderDetailActivity.this).inflate(
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
                PopWindowUtils.setBackgroundBlack(pop_bg, 1);

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
    private void showPopDelivery(View parent, List<OrderDetailResult.DatasBean.SKUsBean> skUs) {
        if (null != popupWindowDelivery) {
            popupWindowDelivery.dismiss();
        } else {
            initPopDeliveryWindow();
        }
        checkedMap.clear();
        if (StringUtil.checkStr(datasBean.id)) {
            deliveringOrderId = datasBean.id;
        } else {
            deliveringOrderId = null;
        }
        //初始化按钮
        pop_sureDelivery.setEnabled(false);
        pop_sureDelivery.setText("确定");
        //初始化数据

        if (!skUs.isEmpty()) {
            PopSkusDeliveryAdapter popSkusDeliveryAdapter = new PopSkusDeliveryAdapter(OrderDetailActivity.this, skUs);
            pop_listView.setAdapter(popSkusDeliveryAdapter);
        }

        //设置背景及展示
        PopWindowUtils.setBackgroundBlack(pop_bg, 0);
        popupWindowDelivery.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }


    //网点开始发货的Sku列表
    class PopSkusDeliveryAdapter extends CommonAdapter<OrderDetailResult.DatasBean.SKUsBean> {
        private List<OrderDetailResult.DatasBean.SKUsBean> list;

        public PopSkusDeliveryAdapter(Context context, List<OrderDetailResult.DatasBean.SKUsBean> data) {
            super(context, data, R.layout.item_pop_sureorder_layout);
            this.list = data;
        }

        @Override
        public void convert(CommonViewHolder holder, final OrderDetailResult.DatasBean.SKUsBean skus) {

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
                TextView sku_addiction = holder.getView(R.id.sku_addiction);

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
                final CheckBox checkBox =  holder.getView(R.id.btn_surr_order_item);

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

    //确定选中按钮的数量
    public int setCheckedGoodsCount(List<OrderDetailResult.DatasBean.SKUsBean> list) {
        int count = 0;
        List<String> refList = new ArrayList<>();
        for (String key : checkedMap.keySet()) {
            if (checkedMap.get(key)) {
                refList.add(key);
            }
        }
        for (OrderDetailResult.DatasBean.SKUsBean skus : list) {
            if (refList.contains(skus.ref)) {
                count += skus.count;
            }
        }
        return count;
    }


}
