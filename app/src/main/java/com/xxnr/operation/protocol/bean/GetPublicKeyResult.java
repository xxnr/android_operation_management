package com.xxnr.operation.protocol.bean;

import com.xxnr.operation.protocol.ResponseResult;

/**
 * Created by hehuanyu on 2016/4/28.
 */
public class GetPublicKeyResult extends ResponseResult{




    private String public_key;

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }
}
