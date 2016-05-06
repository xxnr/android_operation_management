package com.xxnr.operation.order;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xxnr.operation.MsgID;
import com.xxnr.operation.R;
import com.xxnr.operation.base.BaseActivity;
import com.xxnr.operation.base.CommonAdapter;
import com.xxnr.operation.base.CommonViewHolder;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.protocol.ApiInterface;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.protocol.bean.OrderDetailResult;
import com.xxnr.operation.utils.DateFormatUtils;
import com.xxnr.operation.utils.IntentUtil;
import com.xxnr.operation.utils.RndLog;
import com.xxnr.operation.utils.StringUtil;
import com.xxnr.operation.widget.RecyclerImageView;
import com.xxnr.operation.widget.UnSwipeListView;
import com.xxnr.operation.widget.WidgetUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CAI on 2016/5/6.
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

    private Map<String, Boolean> checkedMap = new HashMap<>();//用于存放popWindow中选中item
    private PopupWindow popupWindow;
    private TextView pop_sure;
    private ListView pop_listView;
    private RelativeLayout pop_bg;
    private LinearLayout state_address_ll;
    private LinearLayout address_shouhuo_ll;
    private TextView pay_time_tv;
    private LinearLayout state_address_ll_none;
    private LinearLayout select_state_address_ll_person;
    private TextView select_state_person_info;


    @Override
    public int getLayout() {
        return R.layout.order_detail_layout;
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
    }

    private void initView() {
        go_to_pay_rel = (RelativeLayout) findViewById(R.id.go_to_pay_rel);
        go_to_pay = (Button) findViewById(R.id.go_to_pay);
        pop_bg = (RelativeLayout) findViewById(R.id.pop_bg);

        //头部信息 订单号： 交易状态 送货人 地址
        View head_layout = LayoutInflater.from(OrderDetailActivity.this).inflate(R.layout.order_detail_head_layout, null);
        name_phone_tv = (TextView) head_layout.findViewById(R.id.order_detail_name_tv);
        order_detail_address_tv = (TextView) head_layout.findViewById(R.id.order_detail_address_tv);
        order_tv = (TextView) head_layout.findViewById(R.id.my_order_detail_id);
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
        View foot_layout = LayoutInflater.from(OrderDetailActivity.this).inflate(R.layout.order_detail_foot_layout, null);
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

    }

    @Override
    public void onResponsed(Request req) {
        if (ApiType.GET_ORDER_DETAIL == req.getApi()) {
            OrderDetailResult data = (OrderDetailResult) req.getData();
            if (data.getStatus().equals("1000")) {

                OrderDetailResult.DatasBean datas = data.datas;
                if (datas != null) {

                    //订单号
                    order_tv.setText("订单号：" + datas.id);
                    //下单时间
                    pay_time_tv.setText(DateFormatUtils.convertTime(datas.dateCreated));

                    //配送方式

                    if (datas.deliveryType == 1) {  //网点自提
                        delivery_icon.setVisibility(View.VISIBLE);
                        delivery_text.setVisibility(View.VISIBLE);
                        delivery_icon.setBackgroundResource(R.mipmap.home_delivery_icon);
                        delivery_text.setText("网点自提");

                        select_state_address_ll_person.setVisibility(View.VISIBLE);
                        select_state_person_info.setText(datas.consigneeName + "  " + datas.consigneePhone);

                    } else {
                        delivery_icon.setVisibility(View.VISIBLE);//其他的暂用 送货到家图标
                        delivery_text.setVisibility(View.VISIBLE);
                        delivery_icon.setBackgroundResource(R.mipmap.state_delivery_icon);
                        delivery_text.setText("配送到户");

                        address_shouhuo_ll.setVisibility(View.VISIBLE);
                        //联系人 及电话
                        name_phone_tv.setText(datas.consigneeName + "  " + datas.consigneePhone);
                        //联系人地址
                        order_detail_address_tv.setText(datas.consigneeAddress);
                    }


                    //设置RSCInfo
                    if (datas.RSCInfo != null) {
                        //收货地址网点等信息
                        state_address_ll.setVisibility(View.VISIBLE);
                        if (StringUtil.checkStr(datas.RSCInfo.companyName)) {
                            RSC_state_name.setText(datas.RSCInfo.companyName);
                        }

                        if (StringUtil.checkStr(datas.RSCInfo.RSCAddress)) {
                            RSC_state_address.setText(datas.RSCInfo.RSCAddress);
                        }

                        if (StringUtil.checkStr(datas.RSCInfo.RSCPhone)) {
                            RSC_state_phone.setText(datas.RSCInfo.RSCPhone);
                        }
                    } else {
                        state_address_ll_none.setVisibility(View.VISIBLE);
                    }


                    //合计 与 去支付
                    if (datas.order != null) {
                        total_price_tv.setText("¥" + datas.order.totalPrice);
                        if (datas.order.orderStatus != null) {
                            //订单状态
                            if (StringUtil.checkStr(datas.order.orderStatus.value)) {
                                pay_state_tv.setText(datas.order.orderStatus.value);
                            }
                            //不支付
                            go_to_pay_rel.setVisibility(View.GONE);
                        }
                    }


                    //支付信息列表
                    if (datas.subOrders != null && !datas.subOrders.isEmpty()) {
                        PayInfoAdapter payInfoAdapter = new PayInfoAdapter(this, datas.subOrders, datas.orderType);
                        pay_info_listView.setAdapter(payInfoAdapter);
                    }

                    //子商品列表
                    List<OrderDetailResult.DatasBean.SKUsBean> skUs = datas.SKUs;
                    if (skUs != null && !skUs.isEmpty()) {
                        CarAdapter carAdapter = new CarAdapter(true, skUs);
                        order_shangpin_list.setAdapter(carAdapter);
                    } else {
                        CarAdapter carAdapter = new CarAdapter(datas.products, false);
                        order_shangpin_list.setAdapter(carAdapter);
                    }


                    //审核
                    if (datas.order != null && datas.order.orderStatus != null) {
                        if (datas.order.orderStatus.type == 7) {

                            go_to_pay_rel.setVisibility(View.VISIBLE);
                            go_to_pay.setText("审核付款");
                            go_to_pay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                        }

                    }

                    //发货

                    if (datas.order != null&&datas.order.pendingDeliverToRSC) {
                        boolean isSure = false;

                        final List<OrderDetailResult.DatasBean.SKUsBean> skUs2 = new ArrayList<>();
                        List<OrderDetailResult.DatasBean.SKUsBean> skUs1 = datas.SKUs;
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
                            go_to_pay.setText("发货到服务站");
                            go_to_pay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                        }

                    }


                }


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
                TextView item_payInfo_step = ((TextView) holder.getView(R.id.item_payInfo_step));
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
                TextView item_payInfo_type = (TextView) holder.getView(R.id.item_payInfo_type);
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
                TextView to_get_pay_detail = (TextView) holder.getView(R.id.to_get_pay_detail);
                if (subOrder.payStatus == 2 || subOrder.payStatus == 3) {
                    to_get_pay_detail.setVisibility(View.VISIBLE);
                    to_get_pay_detail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("payInfo", (Serializable) subOrder);
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
                        .inflate(R.layout.order_list_item_item, null);
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
                }
            }

        }
    }


}
