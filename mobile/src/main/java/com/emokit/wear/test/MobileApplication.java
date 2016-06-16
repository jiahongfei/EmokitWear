package com.emokit.wear.test;

import android.app.Application;

import com.emokit.wear.init.EmokitApiManager;
import com.emokit.wear.init.MobileApiConfiguration;

/**
 * Created by jiahongfei on 16/3/19.
 */
public class MobileApplication extends Application {

    private String rcType = MobileApiConfiguration.RC_TYPE_7;


    @Override
    public void onCreate() {
        super.onCreate();
        MobileApiConfiguration configuration = new MobileApiConfiguration
                .Builder()
                .setPlatflag("EmokitWearSDK")
                .setUserName("userName")
                .setPassword("123456")
                .setRcType(rcType)
                .create();
        EmokitApiManager.getInstance().mobileApiConfig(getApplicationContext(), configuration);

    }
}
