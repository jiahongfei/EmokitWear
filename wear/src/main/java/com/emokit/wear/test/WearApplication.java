package com.emokit.wear.test;

import android.app.Application;

import com.emokit.wear.init.EmokitApiManager;
import com.emokit.wear.init.WearApiConfiguration;

/**
 * Created by jiahongfei on 16/3/19.
 */
public class WearApplication extends Application {

    private static final int HEART_RATE_DURATION = 30*1000;

    @Override
    public void onCreate() {
        super.onCreate();

        WearApiConfiguration configuration = new WearApiConfiguration
                .Builder()
                .setHeartRateDuration(HEART_RATE_DURATION)
                .setHeartRateTimeOut(45 * 1000)
                .create();
        EmokitApiManager.getInstance().wearApiConfig(getApplicationContext(), configuration);

    }
}
