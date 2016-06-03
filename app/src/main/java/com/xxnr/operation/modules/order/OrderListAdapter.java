package com.xxnr.operation.modules.order;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xxnr.operation.MsgID;
import com.xxnr.operation.R;
import com.xxnr.operation.modules.CommonAdapter;
import com.xxnr.operation.modules.CommonViewHolder;
import com.xxnr.operation.protocol.bean.OrderListResult;
import com.xxnr.operation.utils.IntentUtil;
import com.xxnr.operation.utils.RndLog;
import com.xxnr.operation.utils.StringUtil;
import com.xxnr.operation.widget.RecyclerImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 何鹏 on 2016/5/5.
 */
public class OrderListAdapter extends CommonAdapter<OrderListResult.DatasBean.ItemsBean> {
    private static final String TAG = OrderListAdapter.class.getSimpleName();
    private Context context;
    private ItemOnClickListener mItemOnClickListener;
    private ItemOnClickListener1 mItemOnClickListener1;
    private int tag = 0;//当前是哪个列表


    public OrderListAdapter(Context context, List<OrderListResult.DatasBean.ItemsBean> data, ItemOnClickListener listener, ItemOnClickListener1 listener1, int tag) {
        super(context, data, R.layout.item_order_list);
        this.context = context;
        this.mItemOnClickListener = listener;
        this.mItemOnClickListener1 = listener1;
        this.tag = tag;
    }

    @Override
    public void convert(CommonViewHolder holder, final OrderListResult.DatasBean.ItemsBean itemsBean) {
        if (itemsBean != null) {
            //设置订单号
            if (StringUtil.checkStr(itemsBean.id)) {
                holder.setText(R.id.my_order_id, "订单号：" + itemsBean.id);
            } else {
                holder.setText(R.id.my_order_id, "订单号：");
            }
            ///配送方式
            if (itemsBean.deliveryType == 1) {
                holder.setText(R.id.my_order_delivery_type, "网点自提");
            } else if (itemsBean.deliveryType == 2) {
                holder.setText(R.id.my_order_delivery_type, "配送到户");
            } else {
                holder.setText(R.id.my_order_delivery_type, "");

            }
            //订单合计金额
            if (StringUtil.checkStr(itemsBean.price + "")) {
                holder.setText(R.id.my_order_pay_price, "¥" + StringUtil.toTwoString(itemsBean.price + ""));
            } else {
                holder.setText(R.id.my_order_pay_price, "");
            }
            //订单状态 及不同订单状态下所对应的操作
            RelativeLayout go_to_pay_rel = holder.getView(R.id.go_to_pay_rel);
            Button go_to_pay = holder.getView(R.id.go_to_pay);

            holder.setText(R.id.my_order_pay_state, "");
            go_to_pay_rel.setVisibility(View.GONE);
            go_to_pay.setOnClickListener(null);
            OrderListResult.DatasBean.ItemsBean.OrderBean orderBean = itemsBean.order;
            if (orderBean != null && orderBean.orderStatus != null) {
                if (StringUtil.checkStr(orderBean.orderStatus.value)) {
                    holder.setText(R.id.my_order_pay_state, orderBean.orderStatus.value);
                } else {
                    holder.setText(R.id.my_order_pay_state, "");
                }
                if (orderBean.orderStatus.type == 7) {

                    go_to_pay_rel.setVisibility(View.VISIBLE);
                    go_to_pay.setText("审核付款");
                    go_to_pay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RndLog.d(TAG, "setOnClickListener-->onClick...");
                            //回调传递点击的view
                            mItemOnClickListener.itemOnClickListener(v, itemsBean);
                        }
                    });
                }
            }
            if (tag == 0 || tag == 2) {
                if (orderBean != null && orderBean.pendingDeliverToRSC) {
                    boolean isSure = false;
                    final List<OrderListResult.DatasBean.ItemsBean.SKUsBean> skUs = new ArrayList<>();
                    List<OrderListResult.DatasBean.ItemsBean.SKUsBean> skUs1 = itemsBean.SKUs;
                    if (skUs1 != null) {

                        for (int i = 0; i < skUs1.size(); i++) {

                            OrderListResult.DatasBean.ItemsBean.SKUsBean skus = skUs1.get(i);
                            if (skus != null && skus.deliverStatus == 1) {
                                skUs.add(skus);
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

                                RndLog.d(TAG, "setOnClickListener1-->onClick...");
                                //回调传递点击的view
                                mItemOnClickListener1.itemOnClickListener1(v, itemsBean);
                            }
                        });
                    }
                }
            }

            LinearLayout llCommerceContainer = holder.getView(R.id.my_order_llCommerceContainer);

            List<OrderListResult.DatasBean.ItemsBean.SKUsBean> skUs = itemsBean.SKUs;
            List<OrderListResult.DatasBean.ItemsBean.ProductBean> products = itemsBean.products;

            //商品列表
            if (llCommerceContainer.getChildCount() > 0) {
                llCommerceContainer.removeAllViews();
            }
            if (skUs != null && !skUs.isEmpty()) {

                for (int i = 0; i < skUs.size(); i++) {
                    ViewGroup rootView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.item_item_order_list, null);

                    ViewHolderChild viewHolderChild = new ViewHolderChild(rootView);
                    //商品图片
                    if (StringUtil.checkStr(skUs.get(i).thumbnail)) {
                        Picasso.with(context)
                                .load(MsgID.IP + skUs.get(i).thumbnail)
                                .error(R.mipmap.error)
                                .placeholder(R.mipmap.zhanweitu)
                                .into(viewHolderChild.ordering_item_img);
                    }
                    //商品个数
                    viewHolderChild.ordering_item_geshu.setText("X " + skUs.get(i).count + "");
                    //商品名
                    if (StringUtil.checkStr(skUs.get(i).productName)) {
                        viewHolderChild.ordering_item_name.setText(skUs.get(i).productName);
                    }

                    //附加选项
                    StringBuilder stringAdditions = new StringBuilder();
                    float car_additions_price = 0;
                    if (skUs.get(i).additions != null && !skUs.get(i).additions.isEmpty()) {
                        viewHolderChild.additions_lin.setVisibility(View.VISIBLE);
                        stringAdditions.append("附加项目:");
                        for (int k = 0; k < skUs.get(i).additions.size(); k++) {
                            if (StringUtil.checkStr(skUs.get(i).additions.get(k).name)) {
                                stringAdditions.append(skUs.get(i).additions.get(k).name).append(";");
                                car_additions_price += skUs.get(i).additions.get(k).price;
                            }
                        }
                        String car_additions = stringAdditions.toString().substring(0, stringAdditions.toString().length() - 1);
                        if (StringUtil.checkStr(car_additions)) {
                            viewHolderChild.additions_text.setText(car_additions);
                            viewHolderChild.additions_price.setText("¥" + StringUtil.toTwoString(car_additions_price + ""));
                        }
                    } else {
                        viewHolderChild.additions_lin.setVisibility(View.GONE);
                    }
                    //商品 单价 阶段 订金 尾款
                    viewHolderChild.ordering_now_pri.setTextColor(context.getResources().getColor(R.color.orange_goods_price));
                    if (skUs.get(i).deposit == 0) {
                        viewHolderChild.goods_car_bar.setVisibility(View.GONE);
                    } else {
                        viewHolderChild.goods_car_bar.setVisibility(View.VISIBLE);
                        String deposit = StringUtil.toTwoString(skUs
                                .get(i).deposit * skUs.get(i).count + "");
                        if (StringUtil.checkStr(deposit)) {
                            viewHolderChild.goods_car_deposit.setText("¥" + deposit);
                        }
                        String weiKuan = StringUtil.toTwoString((skUs.get(i).price + car_additions_price - skUs
                                .get(i).deposit) * skUs.get(i).count + "");
                        if (StringUtil.checkStr(weiKuan)) {
                            viewHolderChild.goods_car_weikuan.setText("¥" + weiKuan);
                        }
                    }
                    viewHolderChild.ordering_now_pri.setText("¥" + StringUtil.toTwoString(skUs
                            .get(i).price + ""));

                    //Sku属性
                    StringBuilder stringSku = new StringBuilder();
                    if (skUs.get(i).attributes != null && !skUs.get(i).attributes.isEmpty()) {
                        for (int k = 0; k < skUs.get(i).attributes.size(); k++) {
                            if (StringUtil.checkStr(skUs.get(i).attributes.get(k).name)
                                    && StringUtil.checkStr(skUs.get(i).attributes.get(k).value)) {
                                stringSku.append(skUs.get(i).attributes.get(k).name).append(":").append(skUs.get(i).attributes.get(k).value).append(";");
                            }
                        }
                        String car_attr = stringSku.toString().substring(0, stringSku.toString().length() - 1);
                        if (StringUtil.checkStr(car_attr)) {
                            viewHolderChild.goods_car_attr.setText(car_attr);
                        }
                    }

                    llCommerceContainer.addView(rootView);

                }
                //老的订单
            } else if (products != null && !products.isEmpty()) {


                for (int i = 0; i < products.size(); i++) {
                    ViewGroup rootView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.item_item_order_list, null);
                    ViewHolderChild viewHolderChild = new ViewHolderChild(rootView);
                    //商品图片
                    if (StringUtil.checkStr(products.get(i).thumbnail)) {
                        Picasso.with(context)
                                .load(MsgID.IP + products.get(i).thumbnail)
                                .error(R.mipmap.error)
                                .placeholder(R.mipmap.zhanweitu)
                                .into(viewHolderChild.ordering_item_img);
                    }
                    //商品个数
                    viewHolderChild.ordering_item_geshu.setText("X " + products.get(i).count + "");
                    //商品名
                    if (StringUtil.checkStr(products.get(i).name)) {
                        viewHolderChild.ordering_item_name.setText(products.get(i).name);
                    }

                    if (products.get(i).deposit == 0) {
                        viewHolderChild.ordering_now_pri.setTextColor(context.getResources().getColor(R.color.orange_goods_price));
                        viewHolderChild.goods_car_bar.setVisibility(View.GONE);
                    } else {
                        viewHolderChild.ordering_now_pri.setTextColor(context.getResources().getColor(R.color.deep_black));
                        viewHolderChild.goods_car_bar.setVisibility(View.VISIBLE);
                        String deposit = StringUtil.toTwoString(products
                                .get(i).deposit * products.get(i).count + "");
                        if (StringUtil.checkStr(deposit)) {
                            viewHolderChild.goods_car_deposit.setText("¥" + deposit);
                        }
                        String weiKuan = StringUtil.toTwoString((products.get(i).price - products
                                .get(i).deposit) * products.get(i).count + "");
                        if (StringUtil.checkStr(weiKuan)) {
                            viewHolderChild.goods_car_weikuan.setText("¥" + weiKuan);
                        }
                    }
                    String now_pri = StringUtil.toTwoString(products
                            .get(i).price + "");
                    if (StringUtil.checkStr(now_pri)) {
                        viewHolderChild.ordering_now_pri.setText("¥" + now_pri);
                    }
                    llCommerceContainer.addView(rootView);
                }

            }
            llCommerceContainer.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //点击商品跳转到订单详情界面
                    Bundle bundle = new Bundle();
                    bundle.putString("orderId", itemsBean.id);
                    if (context instanceof OrderSearchActivity){
                        IntentUtil.activityForward(context, OrderDetailActivity.class, bundle, true);
                    }else {
                        IntentUtil.activityForward(context, OrderDetailActivity.class, bundle, false);
                    }
                }
            });
        }
    }

    class ViewHolderChild {
        private LinearLayout goods_car_bar, additions_lin;
        private RecyclerImageView ordering_item_img;
        private TextView ordering_now_pri, ordering_item_name, goods_car_deposit,
                goods_car_weikuan, ordering_item_geshu, goods_car_attr;
        private TextView additions_text, additions_price;

        public ViewHolderChild(View convertView) {
            goods_car_bar = (LinearLayout) convertView.findViewById(R.id.goods_car_item_bar);
            ordering_item_img = (RecyclerImageView) convertView//商品图
                    .findViewById(R.id.ordering_item_img);
            ordering_item_geshu = (TextView) convertView//商品个数
                    .findViewById(R.id.ordering_item_geshu);
            ordering_now_pri = (TextView) convertView//商品价格
                    .findViewById(R.id.ordering_now_pri);
            ordering_item_name = (TextView) convertView//商品名
                    .findViewById(R.id.ordering_item_name);
            goods_car_deposit = (TextView) convertView//汽车定金
                    .findViewById(R.id.goods_car_item_bar_deposit);
            goods_car_weikuan = (TextView) convertView//汽车尾款
                    .findViewById(R.id.goods_car_item_bar_weikuan);
            goods_car_attr = (TextView) convertView//汽车尾款
                    .findViewById(R.id.ordering_item_attr);
            additions_text = (TextView) convertView//附加选项
                    .findViewById(R.id.additions_text);
            additions_price = (TextView) convertView//附加选项价格
                    .findViewById(R.id.additions_price);
            additions_lin = (LinearLayout) convertView
                    .findViewById(R.id.additions_lin);
        }
    }


    //用于审核付款
    public interface ItemOnClickListener {
        /**
         * 传递点击的view
         *
         * @param view
         */
        public void itemOnClickListener(View view, OrderListResult.DatasBean.ItemsBean itemsBean);
    }


    //用于配送发货
    public interface ItemOnClickListener1 {
        /**
         * 传递点击的view
         *
         * @param view
         */
        public void itemOnClickListener1(View view, OrderListResult.DatasBean.ItemsBean itemsBean);
    }


}
