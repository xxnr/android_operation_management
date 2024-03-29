package com.xxnr.operation.modules;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 *
 * @author Stephen Huang
 *
 */
public class CommonViewHolder {

    private SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;

    public CommonViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mPosition = position;
        this.mViews = new SparseArray<>();
        this.mConvertView = LayoutInflater.from(context).inflate(layoutId, null);
        mConvertView.setTag(this);
    }

    public static CommonViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int postion) {
        if (convertView == null) {
            return new CommonViewHolder(context, parent, layoutId, postion);
        } else {
            CommonViewHolder holder = (CommonViewHolder)convertView.getTag();
            holder.mPosition = postion;
            return holder;
        }
    }

    /**
     * 通过viewId获取控件
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 返回下标
     */
    public int getPosition() {
        return mPosition;
    }

    public View getConvertView() {
        return mConvertView;
    }

    public CommonViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }
}