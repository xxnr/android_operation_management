package com.xxnr.operation.protocol.bean;

import com.xxnr.operation.protocol.ResponseResult;

import java.util.List;

/**
 * Created by 何鹏 on 2016/5/6.
 */
public class OfflinePayTypeResult extends ResponseResult {

    /**
     * type : 3
     * name : 现金
     */

    public List<OfflinePayTypeBean> offlinePayType;

    public static class OfflinePayTypeBean {
        public int type;
        public String name;
    }
}
