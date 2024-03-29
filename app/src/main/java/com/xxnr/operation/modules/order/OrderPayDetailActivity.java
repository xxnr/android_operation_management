package com.xxnr.operation.modules.order;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.xxnr.operation.R;
import com.xxnr.operation.modules.BaseActivity;
import com.xxnr.operation.modules.CommonAdapter;
import com.xxnr.operation.modules.CommonViewHolder;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.bean.OrderDetailResult;
import com.xxnr.operation.utils.DateFormatUtils;
import com.xxnr.operation.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 何鹏 on 2016/5/6.
 */
public class OrderPayDetailActivity extends BaseActivity {
    private ListView listView;
    private TextView orderId_tv, orderStep_tv, orderState_tv, to_pay_price_tv, order_yet_tv;

    @Override
    public int getLayout() {
        return R.layout.activity_order_detail;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("查看支付详情");
        initView();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String orderId = bundle.getString("orderId");
            OrderDetailResult.DatasBean.SubOrdersBean subOrder =
                    (OrderDetailResult.DatasBean.SubOrdersBean) bundle.getSerializable("payInfo");
            if (StringUtil.checkStr(orderId)) {
                orderId_tv.setText("订单号：" + orderId);

                if (subOrder != null) {
                    //支付阶段
                    switch (subOrder.type) {
                        case "deposit":
                            orderStep_tv.setText("阶段一：订金");
                            break;
                        case "balance":
                            orderStep_tv.setText("阶段二：尾款");
                            break;
                        case "full":
                            orderStep_tv.setText("订单总额");
                            break;
                    }
                    //支付状态
                    switch (subOrder.payStatus) {
                        case 1:
                            orderState_tv.setTextColor(getResources().getColor(R.color.orange));
                            orderState_tv.setText("待付款");
                            break;
                        case 2:
                            orderState_tv.setTextColor(getResources().getColor(R.color.default_black));
                            orderState_tv.setText("已付款");
                            break;
                        case 3:
                            orderState_tv.setTextColor(getResources().getColor(R.color.orange));
                            orderState_tv.setText("部分付款");
                            break;
                    }
                    //应支付金额
                    to_pay_price_tv.setText("¥" + StringUtil.toTwoString(subOrder.price + ""));
                    //已支付金额
                    order_yet_tv.setText("¥" + StringUtil.toTwoString(subOrder.paidPrice + ""));

                    List<OrderDetailResult.DatasBean.SubOrdersBean.PaymentsBean> payments = subOrder.payments;
                    List<OrderDetailResult.DatasBean.SubOrdersBean.PaymentsBean> payments1 = new ArrayList<>();

                    if (payments != null && !payments.isEmpty()) {
                        for (int i = 0; i < payments.size(); i++) {
                            OrderDetailResult.DatasBean.SubOrdersBean.PaymentsBean bean = payments.get(i);
                            if (bean != null) {
                                if (bean.payStatus == 2) {
                                    payments1.add(bean);
                                }
                            }
                        }

                    }
                    if (!payments1.isEmpty()) {
                        PayInfoAdapter payInfoAdapter = new PayInfoAdapter(OrderPayDetailActivity.this, payments1);
                        listView.setAdapter(payInfoAdapter);
                    }

                }
            }


        }

    }

    private void initView() {
        View headView = LayoutInflater.from(this).inflate(R.layout.head_layout_check_pay_detail, null);
        orderId_tv = ((TextView) headView.findViewById(R.id.check_pay_orderId));    //订单号
        orderStep_tv = ((TextView) headView.findViewById(R.id.check_pay_orderStep)); //阶段状态
        orderState_tv = ((TextView) headView.findViewById(R.id.check_pay_orderState));//付款状态
        to_pay_price_tv = ((TextView) headView.findViewById(R.id.to_pay_price));//应支付金额
        order_yet_tv = ((TextView) headView.findViewById(R.id.order_yet_price));//已支付金额
        listView = ((ListView) findViewById(R.id.order_shangpin_list));
        listView.addHeaderView(headView);
    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {

    }

    class PayInfoAdapter extends CommonAdapter<OrderDetailResult.DatasBean.SubOrdersBean.PaymentsBean> {


        public PayInfoAdapter(Context context, List<OrderDetailResult.DatasBean.SubOrdersBean.PaymentsBean> data) {
            super(context, data, R.layout.item_payinfo_detail);
        }

        @Override
        public void convert(CommonViewHolder holder, OrderDetailResult.DatasBean.SubOrdersBean.PaymentsBean payments) {
            if (payments != null) {
                //文本内容

                if (payments.payType == 1) {
                    holder.setText(R.id.order_pay_type, "支付宝支付");
                } else if (payments.payType == 2) {
                    holder.setText(R.id.order_pay_type, "银联支付");
                } else if (payments.payType == 3) {
                    holder.setText(R.id.order_pay_type, "现金");
                } else if (payments.payType == 4) {
                    holder.setText(R.id.order_pay_type, "线下POS机");
                } else if (payments.payType == 5) {
                    holder.setText(R.id.order_pay_type, "EPOS支付");
                }
                //支付金额
                if (StringUtil.checkStr(payments.price + "")) {
                    holder.setText(R.id.pay_price, "¥" + StringUtil.toTwoString(payments.price + ""));
                }
                //支付时间
                if (StringUtil.checkStr(payments.datePaid)) {
                    holder.setText(R.id.order_pay_time, DateFormatUtils.convertTime(payments.datePaid));
                }
                //支付结果
                if (payments.payStatus == 1) {
                    holder.setText(R.id.item_payInfo_state, "");
                } else if (payments.payStatus == 2) {
                    holder.setText(R.id.item_payInfo_state, "支付成功");
                }

                //第几次付款
                holder.setText(R.id.item_payInfo_times, "第" + payments.slice + "次");

                //支付网点

                View order_pay_state_ll = holder.getView(R.id.order_pay_state_ll);

                if (StringUtil.checkStr(payments.RSCCompanyName)) {
                    order_pay_state_ll.setVisibility(View.VISIBLE);
                    holder.setText(R.id.order_pay_state, payments.RSCCompanyName);
                } else {
                    order_pay_state_ll.setVisibility(View.GONE);
                }

            }


        }
    }

}
