package com.xxnr.operation.potential;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.xxnr.operation.R;
import com.xxnr.operation.base.CommonAdapter;
import com.xxnr.operation.base.CommonViewHolder;
import com.xxnr.operation.protocol.bean.PotentialListResult;
import com.xxnr.operation.utils.DateFormatUtils;
import com.xxnr.operation.utils.IntentUtil;
import com.xxnr.operation.utils.StringUtil;

import java.util.List;

/**
 * Created by CAI on 2016/5/4.
 */
public class PotentialListAdapter extends CommonAdapter<PotentialListResult.PotentialCustomersBean> {
    private Context context;

    public PotentialListAdapter(Context context, List<PotentialListResult.PotentialCustomersBean> data) {
        super(context, data, R.layout.item_potential_list_layout);
        this.context = context;
    }

    @Override
    public void convert(CommonViewHolder holder, final PotentialListResult.PotentialCustomersBean itemsBean) {
        if (itemsBean != null) {

            if (StringUtil.checkStr(itemsBean.name)) {
                holder.setText(R.id.customer_name, itemsBean.name);
            } else {
                holder.setText(R.id.customer_name, "");
            }
            if (StringUtil.checkStr(itemsBean.phone)) {
                holder.setText(R.id.customer_phone, itemsBean.phone);
            } else {
                holder.setText(R.id.customer_phone, "");
            }

            ImageView sex_iv = holder.getView(R.id.customer_sex);

            if (itemsBean.sex) {
                sex_iv.setImageResource(R.mipmap.girl_icon);
            } else {
                sex_iv.setImageResource(R.mipmap.boy_icon);
            }

            if (StringUtil.checkStr(itemsBean.dateTimeAdded)) {
                holder.setText(R.id.customer_time, DateFormatUtils.convertTime(itemsBean.dateTimeAdded));
            } else {
                holder.setText(R.id.customer_time, "");
            }

            //是否已注册
            View registered_text = holder.getView(R.id.registered_text);
            View registered_icon = holder.getView(R.id.registered_icon);
            if (itemsBean.isRegistered) {
                registered_text.setVisibility(View.VISIBLE);
                registered_icon.setVisibility(View.VISIBLE);
            } else {
                registered_text.setVisibility(View.INVISIBLE);
                registered_icon.setVisibility(View.INVISIBLE);
            }
            //跳转到潜在客户详情
            holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //此时应该影藏键盘
                    try {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("potential", itemsBean);
                    if (context instanceof PotentialSearchActivity) {
                        IntentUtil.activityForward(context, PotentialDetailActivity.class, bundle, true);
                    } else {
                        IntentUtil.activityForward(context, PotentialDetailActivity.class, bundle, false);
                    }
                }
            });

        }


    }
}
