package com.xxnr.operation.customer;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.xxnr.operation.R;
import com.xxnr.operation.base.CommonAdapter;
import com.xxnr.operation.base.CommonViewHolder;
import com.xxnr.operation.protocol.bean.CustomerListResult;
import com.xxnr.operation.utils.DateFormatUtils;
import com.xxnr.operation.utils.IntentUtil;
import com.xxnr.operation.utils.StringUtil;

import java.util.List;

/**
 * Created by CAI on 2016/5/4.
 */
public class CustomerListAdapter extends CommonAdapter<CustomerListResult.Users.ItemsBean> {
    private Context context;

    public CustomerListAdapter(Context context, List<CustomerListResult.Users.ItemsBean> data) {
        super(context, data, R.layout.item_customer_list_layout);
        this.context=context;
    }

    @Override
    public void convert(CommonViewHolder holder, final CustomerListResult.Users.ItemsBean itemsBean) {
        if (itemsBean != null) {

            if (StringUtil.checkStr(itemsBean.name)) {
                holder.setText(R.id.customer_name, itemsBean.name);
            } else {
                holder.setText(R.id.customer_name, "未填写姓名");
            }

            if (StringUtil.checkStr(itemsBean.account)) {
                holder.setText(R.id.customer_phone, itemsBean.account);
            } else {
                holder.setText(R.id.customer_phone, "");
            }
            ImageView sex_iv = holder.getView(R.id.customer_sex);

            if (itemsBean.sex) {
                sex_iv.setImageResource(R.mipmap.girl_icon);
            } else {
                sex_iv.setImageResource(R.mipmap.boy_icon);

            }

            if (StringUtil.checkStr(itemsBean.datecreated)) {
                holder.setText(R.id.customer_time, DateFormatUtils.convertTime(itemsBean.datecreated));
            } else {
                holder.setText(R.id.customer_time, "");
            }

            //是否点亮经销商 经纪人
            TextView county_agency = holder.getView(R.id.customer_county_agency);//县级
            TextView customer_agent = holder.getView(R.id.customer_agent);//经纪人
            county_agency.setVisibility(View.GONE);
            customer_agent.setVisibility(View.GONE);

            if (itemsBean.type.equals("5")) {
                county_agency.setVisibility(View.VISIBLE);
                county_agency.setBackgroundResource(R.drawable.circle_gray_bg);
            }
            if (itemsBean.type.equals("6")) {
                customer_agent.setVisibility(View.VISIBLE);
                customer_agent.setBackgroundResource(R.drawable.circle_gray_bg);
            }
            List<String> typeVerified = itemsBean.typeVerified;
            if (typeVerified != null) {
                if (typeVerified.contains("5")) {
                    county_agency.setVisibility(View.VISIBLE);
                    county_agency.setBackgroundResource(R.drawable.circle_orange_bg);
                }
                if (typeVerified.contains("6")) {
                    customer_agent.setVisibility(View.VISIBLE);
                    customer_agent.setBackgroundResource(R.drawable.circle_blue_bg);
                }
            }

            holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //此时应该影藏键盘
                    try {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("customer", itemsBean);
                    if (context instanceof CustomerSearchActivity){
                        IntentUtil.activityForward(context, CustomerDetailActivity.class, bundle, true);
                    }else {
                        IntentUtil.activityForward(context, CustomerDetailActivity.class, bundle, false);
                    }
                }
            });

        }


    }
}
