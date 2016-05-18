package com.xxnr.operation.potential;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xxnr.operation.base.BaseActivity;
import com.xxnr.operation.R;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.protocol.ApiInterface;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.protocol.bean.PotentialDetailResult;
import com.xxnr.operation.protocol.bean.PotentialListResult;
import com.xxnr.operation.utils.DateFormatUtils;
import com.xxnr.operation.utils.StringUtil;


/**
 * Created by CAI on 2016/5/3.
 */
public class PotentialDetailActivity extends BaseActivity {
    private TextView customer_detail_phone;
    private TextView customer_detail_name;
    private TextView customer_detail_sex;
    private TextView customer_detail_address;
    private TextView customer_intention_goods;
    private TextView customer_detail_remark;
    private TextView customer_invite_name;
    private TextView customer_detail_addTime;
    private TextView customer_detail_register_time;

    private PotentialListResult.PotentialCustomersBean customer;


    @Override
    public int getLayout() {
        return R.layout.potential_detail_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("潜在客户详情");
        initView();
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            customer = (PotentialListResult.PotentialCustomersBean) bundle.getSerializable("potential");
            if (customer != null) {
                showProgressDialog();
                getUser(customer);
                //用户登记时间
                if (StringUtil.checkStr(customer.dateTimeAdded)) {
                    customer_detail_addTime.setText(DateFormatUtils.convertTime(customer.dateTimeAdded));
                }
                //用户注册时间
                if (StringUtil.checkStr(customer.dateTimeRegistered)) {
                    customer_detail_register_time.setText(DateFormatUtils.convertTime(customer.dateTimeRegistered));
                }
            }
        }
    }

    private void getUser(PotentialListResult.PotentialCustomersBean customer) {
        //用户详情信息
        RequestParams params = new RequestParams();
        params.put("token", App.getApp().getToken());
        execApi(ApiType.GET_POTENTIAL_DETAIL.setOpt(ApiInterface.GET_POTENTIAL_DETAIL + "/" + customer._id)
                .setMethod(ApiType.RequestMethod.GET), params);
    }

    private void initView() {
        customer_detail_phone = (TextView) findViewById(R.id.customer_detail_phone);
        customer_detail_name = (TextView) findViewById(R.id.customer_detail_name);
        customer_detail_sex = (TextView) findViewById(R.id.customer_detail_sex);
        customer_detail_address = (TextView) findViewById(R.id.customer_detail_address);
        customer_intention_goods = (TextView) findViewById(R.id.customer_intention_goods);
        customer_detail_remark = (TextView) findViewById(R.id.customer_detail_remark);
        customer_invite_name = (TextView) findViewById(R.id.customer_invite_name);
        customer_detail_addTime = (TextView) findViewById(R.id.customer_detail_addTime);
        customer_detail_register_time = (TextView) findViewById(R.id.customer_detail_register_time);
    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.GET_POTENTIAL_DETAIL) {
            if (req.getData().getStatus().equals("1000")) {
                PotentialDetailResult detailResult = (PotentialDetailResult) req.getData();
                PotentialDetailResult.PotentialCustomerBean user = detailResult.potentialCustomer;
                if (user != null) {
                    customer_detail_name.setText(user.name);
                    customer_detail_phone.setText(user.phone);
                    if (user.sex) {
                        customer_detail_sex.setText("女");
                    } else {
                        customer_detail_sex.setText("男");
                    }
                    //地址
                    if (user.address != null) {

                        String province = "";
                        String city = "";
                        String county = "";
                        String town = "";
                        if (user.address.province != null) {
                            province = user.address.province.name;
                        }
                        if (user.address.city != null) {
                            city = user.address.city.name;
                        }
                        if (user.address.county != null) {
                            county = user.address.county.name;
                        }
                        if (user.address.town != null) {
                            town = user.address.town.name;
                        }
                        String address = StringUtil.checkBufferStr
                                (province, city, county, town);
                        customer_detail_address.setText(address);
                    }
                    //意向商品
                    if (user.buyIntentions != null && !user.buyIntentions.isEmpty()) {
                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < user.buyIntentions.size(); i++) {
                            PotentialDetailResult.PotentialCustomerBean.BuyIntentionsBean bean = user.buyIntentions.get(i);
                            if (bean != null) {
                                builder.append(bean.name);
                                if (i != user.buyIntentions.size() - 1) {
                                    builder.append("，");
                                }
                            }
                        }
                        customer_intention_goods.setText(builder.toString());
                    } else {
                        customer_intention_goods.setText("");
                    }
                    //邀请人姓名
                    if (user.user != null) {
                        customer_invite_name.setText(user.user.name);
                    }

                    //备注
                    if (StringUtil.checkStr(user.remarks)){
                        customer_detail_remark.setText(user.remarks);
                    }
                }

            }

        }
    }
}
