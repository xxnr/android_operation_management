package com.xxnr.operation.modules.customer;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xxnr.operation.modules.BaseActivity;
import com.xxnr.operation.MsgID;
import com.xxnr.operation.R;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.developTools.msg.MsgCenter;
import com.xxnr.operation.protocol.RequestParams;
import com.xxnr.operation.protocol.bean.CustomerDetailResult;
import com.xxnr.operation.protocol.bean.RscInfoResult;
import com.xxnr.operation.protocol.ApiInterface;
import com.xxnr.operation.protocol.ApiType;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.protocol.bean.CustomerListResult;
import com.xxnr.operation.utils.DateFormatUtils;
import com.xxnr.operation.utils.StringUtil;
import com.xxnr.operation.widget.CustomDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 何鹏 on 2016/5/3.
 */
public class CustomerDetailActivity extends BaseActivity {
    private TextView customer_detail_phone;
    private TextView customer_detail_name;
    private TextView customer_detail_sex;
    private TextView customer_detail_address;
    private TextView customer_detail_score;
    private TextView customer_detail_invitee_name;
    private TextView customer_detail_create_time;
    private TextView customer_detail_applyType;
    private TextView customer_approve_icon;
    private TextView approve_customer;
    private TextView connty_approve_icon;
    private TextView is_fill_county_type;
    private TextView approve_county_customer;
    private TextView rsc_name;
    private TextView rsc_card_id;
    private TextView rsc_company_name;
    private TextView rsc_rsc_company_address;
    private LinearLayout rsc_info_ll;
    private CustomerListResult.Users.ItemsBean customer;
    private TextView rsc_company_phone;


    @Override
    public int getLayout() {
        return R.layout.activity_customer_detail;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("客户详情");
        initView();
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            customer = (CustomerListResult.Users.ItemsBean) bundle.getSerializable("customer");
            if (customer != null) {
                showProgressDialog();
                getUser(customer);
                //用户Rsc信息
                RequestParams params1 = new RequestParams();
                params1.put("token", App.getApp().getToken());
                execApi(ApiType.GET_RSC_INFO.setOpt(ApiInterface.GET_RSC_INFO + "/" + customer._id)
                        .setMethod(ApiType.RequestMethod.GET), params1);
            }
        }
    }

    private void getUser(CustomerListResult.Users.ItemsBean customer) {
        //用户详情信息
        RequestParams params = new RequestParams();
        params.put("token", App.getApp().getToken());
        execApi(ApiType.GET_USERS_DETAIL.setOpt(ApiInterface.GET_USERS_DETAIL + "/" + customer.id)
                .setMethod(ApiType.RequestMethod.GET), params);
    }

    private void initView() {
        customer_detail_phone = (TextView) findViewById(R.id.customer_detail_phone);
        customer_detail_name = (TextView) findViewById(R.id.customer_detail_name);
        customer_detail_sex = (TextView) findViewById(R.id.customer_detail_sex);
        customer_detail_address = (TextView) findViewById(R.id.customer_detail_address);
        customer_detail_score = (TextView) findViewById(R.id.customer_detail_score);
        customer_detail_invitee_name = (TextView) findViewById(R.id.customer_detail_invitee_name);
        customer_detail_create_time = (TextView) findViewById(R.id.customer_detail_create_time);
        customer_detail_applyType = (TextView) findViewById(R.id.customer_detail_applyType);
        customer_approve_icon = (TextView) findViewById(R.id.customer_approve_icon);
        approve_customer = (TextView) findViewById(R.id.approve_customer);
        connty_approve_icon = (TextView) findViewById(R.id.connty_approve_icon);
        is_fill_county_type = (TextView) findViewById(R.id.is_fill_county_type);
        approve_county_customer = (TextView) findViewById(R.id.approve_county_customer);

        rsc_name = (TextView) findViewById(R.id.rsc_name);
        rsc_card_id = (TextView) findViewById(R.id.rsc_card_id);
        rsc_company_name = (TextView) findViewById(R.id.rsc_company_name);
        rsc_company_phone = (TextView) findViewById(R.id.rsc_company_phone);
        rsc_rsc_company_address = (TextView) findViewById(R.id.rsc_rsc_company_address);
        rsc_info_ll = (LinearLayout) findViewById(R.id.rsc_info_ll);

        rsc_info_ll.setVisibility(View.GONE);
        connty_approve_icon.setVisibility(View.GONE);
        customer_approve_icon.setVisibility(View.GONE);

    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.GET_USERS_DETAIL) {
            if (req.getData().getStatus().equals("1000")) {

                CustomerDetailResult detailResult = (CustomerDetailResult) req.getData();
                final CustomerDetailResult.UserBean user = detailResult.user;
                if (user != null) {
                    customer_detail_name.setText(user.name);
                    customer_detail_phone.setText(user.account);
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
                    //积分
                    if (user.score == 0) {
                        customer_detail_score.setText("");

                    } else {
                        customer_detail_score.setText(user.score + "");
                    }
                    CustomerDetailResult.UserBean.InviterBean inviter = user.inviter;
                    //邀请人姓名
                    if (inviter != null) {
                        customer_detail_invitee_name.setText(inviter.name);
                    }
                    //用户注册时间
                    customer_detail_create_time.setText(DateFormatUtils.convertTime(user.datecreated));
                    //申请认证类型
                    switch (user.type) {
                        case "1":
                            customer_detail_applyType.setText("普通用户");
                            break;
                        case "5":
                            customer_detail_applyType.setText("县级经销商");
                            break;
                        case "6":
                            customer_detail_applyType.setText("新农经纪人");
                            break;
                        default:
                            customer_detail_applyType.setText("");
                            break;
                    }
                    //是否认证

                    approve_county_customer.setVisibility(View.VISIBLE);
                    approve_customer.setVisibility(View.VISIBLE);

                    if (user.isRSC) {
                        approve_county_customer.setText("取消认证");
                        approve_county_customer.setBackgroundResource(R.drawable.gray_corners_bg);
                        approve_county_customer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                List<Object> list = new ArrayList<>();
                                if (user.isXXNRAgent) {
                                    list.add("6");
                                }
                                approveOrCancel(user.id, list, "确定取消认证该客户吗？");
                            }
                        });
                        connty_approve_icon.setVisibility(View.VISIBLE);
                    } else {
                        approve_county_customer.setText("认证客户");
                        Drawable drawable = getResources().getDrawable(R.drawable.selector_green_lightgreen);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            approve_county_customer.setBackground(drawable);
                        } else {
                            approve_county_customer.setBackgroundResource(R.drawable.green_corners_bg);
                        }
                        connty_approve_icon.setVisibility(View.GONE);
                        approve_county_customer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                List<Object> list = new ArrayList<>();
                                if (user.isXXNRAgent) {
                                    list.add("6");
                                }
                                list.add("5");
                                approveOrCancel(user.id, list, "确定认证该客户吗？");
                            }
                        });
                    }
                    if (user.isXXNRAgent) {
                        approve_customer.setText("取消认证");
                        approve_customer.setBackgroundResource(R.drawable.gray_corners_bg);
                        customer_approve_icon.setVisibility(View.VISIBLE);
                        approve_customer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                List<Object> list = new ArrayList<>();
                                if (user.isRSC) {
                                    list.add("5");
                                }
                                approveOrCancel(user.id, list, "确定取消认证该客户吗？");
                            }
                        });
                    } else {
                        approve_customer.setText("认证客户");
                        Drawable drawable = getResources().getDrawable(R.drawable.selector_green_lightgreen);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            approve_customer.setBackground(drawable);
                        } else {
                            approve_customer.setBackgroundResource(R.drawable.green_corners_bg);
                        }
                        customer_approve_icon.setVisibility(View.GONE);
                        approve_customer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                List<Object> list = new ArrayList<>();
                                if (user.isRSC) {
                                    list.add("5");
                                }
                                list.add("6");
                                approveOrCancel(user.id, list, "确定认证该客户吗？");
                            }
                        });
                    }
                }

            }

        } else if (req.getApi() == ApiType.GET_RSC_INFO) {
            if (req.getData().getStatus().equals("1000")) {
                RscInfoResult infoResult = (RscInfoResult) req.getData();
                if (infoResult.RSCInfo != null && StringUtil.checkStr(infoResult.RSCInfo.companyName)) {
                    rsc_info_ll.setVisibility(View.VISIBLE);
                    is_fill_county_type.setText("");

                    rsc_name.setText(infoResult.RSCInfo.name);
                    rsc_card_id.setText(infoResult.RSCInfo.IDNo);
                    rsc_company_name.setText(infoResult.RSCInfo.companyName);
                    rsc_name.setText(infoResult.RSCInfo.name);
                    rsc_company_phone.setText(infoResult.account);

                    String province = "";
                    String city = "";
                    String county = "";
                    String town = "";
                    RscInfoResult.RSCInfoBean.CompanyAddressBean companyAddress = infoResult.RSCInfo.companyAddress;
                    if (companyAddress != null) {
                        if (companyAddress.province != null) {
                            province = companyAddress.province.name;
                        }
                        if (companyAddress.city != null) {
                            city = companyAddress.city.name;
                        }
                        if (companyAddress.county != null) {
                            county = companyAddress.county.name;
                        }
                        if (companyAddress.town != null) {
                            town = companyAddress.town.name;
                        }
                        String address = StringUtil.checkBufferStr
                                (province, city, county, town);
                        if (StringUtil.checkStr(companyAddress.details)){
                            rsc_rsc_company_address.setText(address+companyAddress.details);
                        }
                    }
                } else {
                    rsc_info_ll.setVisibility(View.GONE);
                    is_fill_county_type.setText("未填写认证信息");
                }
            }
        } else if (ApiType.CHANGE_USER == req.getApi()) {
            if (req.getData().getStatus().equals("1000")) {
                if (customer != null) {
                    //刷新当前页 和列表页
                    getUser(customer);
                    MsgCenter.fireNull(MsgID.UP_DATE_CUSTOMER);
                }
            }

        }
    }


    //认证或者取消认证
    public void approveOrCancel(final String id, final List list, String msg) {

        CustomDialog.Builder builder = new CustomDialog.Builder(
                CustomerDetailActivity.this);
        builder.setMessage(msg)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                dialog.dismiss();
                                showProgressDialog();
                                RequestParams params = new RequestParams();
                                Gson gson = new Gson();
                                Map<String, Object> map = new HashMap<>();
                                map.put("id", id);
                                map.put("token", App.getApp().getToken());
                                map.put("typeVerified", list);
                                String json = gson.toJson(map);
                                params.put("PUT", json);
                                execApi(ApiType.CHANGE_USER.setMethod(ApiType.RequestMethod.PUT), params);

                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });
        builder.create().show();


    }
}
