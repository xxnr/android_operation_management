package com.xxnr.operation.utils;

import android.os.Bundle;

/**
 * Created by CAI on 2016/4/27.
 */
public class BundleUtils  {

    public static Bundle getBundle(int fragmentId){
        Bundle bundle=new Bundle();
        bundle.putInt("fragmentId",fragmentId);
        return bundle;
    }
}
