package com.xxnr.operation.modules.datacenter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.xxnr.operation.MsgID;
import com.xxnr.operation.R;
import com.xxnr.operation.developTools.msg.MsgCenter;
import com.xxnr.operation.modules.BaseActivity;
import com.xxnr.operation.modules.CommonAdapter;
import com.xxnr.operation.modules.CommonViewHolder;
import com.xxnr.operation.protocol.Request;
import com.xxnr.operation.widget.UnSwipeListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 何鹏 on 2016/5/25.
 */
public class WeekPickerActivity extends BaseActivity {
    private UnSwipeListView year_listView;
    private ListView week_listView;

    private Map<Integer, Boolean> checkedMap = new HashMap<>();//用于存放年列表中选中的item
    private Map<Integer, Boolean> weekMap = new HashMap<>();//用于存放周列表中选中的item
    private List<WeekBean> weekList;

    private int oldItemPosition = -1;//上次滚动的下标

    private YearAdapter yearAdapter;
    private int startIndex;
    private int endIndex;
    private int index;
    private boolean isRange;

    @Override
    public int getLayout() {
        return R.layout.activity_week_picker;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("选择日期");
        initView();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isRange = bundle.getBoolean("isRange", false);
            if (isRange) {
                weekList = DataCenterUtils.getWeekList();
                startIndex = bundle.getInt("startIndex");
                endIndex = bundle.getInt("endIndex");
            } else {
                weekList = (List<WeekBean>) bundle.getSerializable("weekList");
                index = bundle.getInt("index");
            }


            if (weekList != null) {
                //设置月列表
                Collections.reverse(weekList);
                WeekAdapter weekAdapter = new WeekAdapter(WeekPickerActivity.this, weekList);
                week_listView.setAdapter(weekAdapter);
                //设置年列表 被反转过，所以倒序
                int endYear = weekList.get(0).Year; //2016
                int startYear = weekList.get(weekList.size() - 1).Year; //2015
                List<Integer> YearList = new ArrayList<>();

                for (int i = startYear; i <= endYear; i++) {
                    YearList.add(i);
                }
                Collections.reverse(YearList);//年份需要反转
                yearAdapter = new YearAdapter(WeekPickerActivity.this, YearList);
                year_listView.setAdapter(yearAdapter);
                //默认选中的属性
                checkedMap.put(YearList.get(0), true);//默认选中第一个年份
                if (isRange) {//如果是多选
                    int tempIndex = startIndex;


                    startIndex = weekList.size() - 1 - endIndex;//反转后获得新的index
                    endIndex = weekList.size() - 1 - tempIndex;//反转后获得新的index


                    for (int i = startIndex; i <= endIndex; i++) {
                        weekMap.put(i, true);
                    }
                    week_listView.setSelection(startIndex);//默认滑动到当前选中的位置

                } else {//如果是单选
                    index = weekList.size() - 1 - index;//反转后获得新的index
                    weekMap.put(index, true);//默认选中一个
                    week_listView.setSelection(index);//默认滑动到当前选中的位置
                }
                yearAdapter.notifyDataSetChanged();
                weekAdapter.notifyDataSetChanged();

                showToast("请选择一个日期");

            }
        }
        //设置联动
        week_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (oldItemPosition != firstVisibleItem) {
                    int year = -1;
                    if (weekList != null && !weekList.isEmpty()) {
                        if (firstVisibleItem >= 1) {
                            WeekBean weekBean = weekList.get(firstVisibleItem - 1);
                            year = weekBean.Year;
                            checkedMap.clear();
                            checkedMap.put(year, true);
                            if (yearAdapter != null) {
                                yearAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    oldItemPosition = firstVisibleItem;
                }
            }
        });

    }


    private void initView() {
        year_listView = (UnSwipeListView) findViewById(R.id.year_listView);
        week_listView = (ListView) findViewById(R.id.week_listView);
    }

    @Override
    public void OnViewClick(View v) {
    }

    @Override
    public void onResponsed(Request req) {

    }

    class WeekAdapter extends CommonAdapter<WeekBean> {
        private List<WeekBean> weekBeanList;

        public WeekAdapter(Context context, List<WeekBean> data) {
            super(context, data, R.layout.item_week_list);
            this.weekBeanList = data;
        }

        @Override
        public void convert(final CommonViewHolder holder, final WeekBean weekBean) {
            if (weekBean != null) {
                View titleView = holder.getView(R.id.item_week_title_ll);

                // 当前年份
                int currentYear = weekBean.Year;
                // 前面的年份
                int previewYear = (holder.getPosition() - 1) >= 0 ? weekBeanList.get(holder.getPosition() - 1).Year : 0;

                if (currentYear != previewYear) {
                    titleView.setVisibility(View.VISIBLE);
                    holder.setText(R.id.item_week_title, weekBean.Year + "年");
                } else {
                    titleView.setVisibility(View.GONE);
                }

                //设置 week内容
                TextView item_week = holder.getView(R.id.item_week);

                String builder = "第" + weekBean.indexOfYear + "周" +
                        "(" + DataCenterUtils.dateToString(weekBean.dateBegin, DataCenterUtils.SHORT_DATE_FORMAT) +
                        "-" + DataCenterUtils.dateToString(weekBean.dateEnd, DataCenterUtils.SHORT_DATE_FORMAT) + ")";
                item_week.setText(builder);

                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (isRange) {
                            int count = 0;
                            for (Integer key : weekMap.keySet()) {
                                if (weekMap.get(key)) {
                                    count++;
                                }
                            }
                            if (count >= 2) {
                                weekMap.clear();
                                weekMap.put(holder.getPosition(), true);
                                startIndex = holder.getPosition();
                                showToast("请再选择一个日期");
                            } else if (count == 0) {
                                weekMap.put(holder.getPosition(), true);
                                startIndex = holder.getPosition();

                            } else if (count == 1) {

                                int select = -1;
                                for (Integer key : weekMap.keySet()) {
                                    if (weekMap.get(key)) {
                                        select = key;
                                    }
                                }
                                if (Math.abs(holder.getPosition() - select) < 7 && Math.abs(holder.getPosition() - select) > 0) {
                                    //返回这种情况下成功
                                    for (int i = select; i <= holder.getPosition(); i++) {
                                        weekMap.put(i, true);
                                    }
                                    if (select > holder.getPosition()) {
                                        endIndex = select;
                                        startIndex = holder.getPosition();
                                    } else {
                                        startIndex = select;
                                        endIndex = holder.getPosition();
                                    }
                                    MsgCenter.fireNull(MsgID.Week_Range_Select, startIndex, endIndex);
                                    finish();
                                } else if (Math.abs(holder.getPosition() - select) >= 7) {
                                    showToast("最多可以选择7周");
                                } else {
                                    weekMap.clear();
                                    weekMap.put(holder.getPosition(), true);
                                    startIndex = holder.getPosition();
                                    showToast("请再选择一个日期");

                                }

                            }


                            notifyDataSetChanged();

                        } else {
                            // 重置，确保最多只有一项被选中
                            weekMap.clear();
                            weekMap.put(holder.getPosition(),
                                    true);
                            int index = weekBeanList.size() - 1 - holder.getPosition();
                            MsgCenter.fireNull(MsgID.Week_Select, index);
                            finish();
                        }

                    }
                });

                Boolean res = weekMap.get(holder.getPosition());
                if (res != null && res) {
                    if (isRange) {
                        if (holder.getPosition() != startIndex && holder.getPosition() != endIndex) {
                            item_week.setTextColor(getResources().getColor(R.color.light_green));
                        } else {
                            item_week.setTextColor(getResources().getColor(R.color.green));
                        }
                    } else {
                        item_week.setTextColor(getResources().getColor(R.color.green));
                    }

                } else {
                    item_week.setTextColor(getResources().getColor(R.color.deep_black));
                }


            }
        }
    }

    class YearAdapter extends CommonAdapter<Integer> {

        public YearAdapter(Context context, List<Integer> YearList) {
            super(context, YearList, R.layout.item_year_list);
        }

        @Override
        public void convert(final CommonViewHolder holder, final Integer year) {
            final CheckBox checkBox = holder.getView(R.id.item_year);
            checkBox.setText(year + "年");

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (weekList != null) {
                        int lastVisiblePosition = week_listView.getLastVisiblePosition();
                        if (lastVisiblePosition != weekList.size() - 1) {
                            // 重置，确保最多只有一项被选中
                            checkedMap.clear();
                            checkedMap.put(year, true);
                            //刷新日期列表的选中状态
                            notifyScroll();
                            //刷新适配器中的选中状态
                            notifyDataSetChanged();
                        } else {
                            notifyDataSetChanged();
                        }
                    }


                }
            });
            if (year != 2015) {
                Boolean res = checkedMap.get(year);
                if (res != null && res) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                }
            } else {
                checkBox.setChecked(false);
            }
        }


    }

    public void notifyScroll() {
        int position = 0;
        int year = 0;
        for (Integer key : checkedMap.keySet()) {
            if (checkedMap.get(key)) {
                year = key;
            }
        }
        if (weekList != null) {
            for (WeekBean key : weekList) {
                if (key.Year == year) {
                    position = weekList.indexOf(key);
                    break;
                }
            }
            week_listView.setSelection(position);

        }

    }

}
