package com.emokit.wear.init;

import android.app.Application;
import android.content.Context;
import com.emokit.sdk.util.SDKAppInit;
import com.emokit.wear.service.wear.HeartRateDataService;

/**
 * Emokit api 初始化
 * Created by jiahongfei on 16/3/17.
 */
public class EmokitApiManager {

    private static final Object LOC_OBJ = new Object();
    private static EmokitApiManager sInstance;

    private boolean isWearApiConfig = false;
    private MobileApiConfiguration mobileConfiguration;
    private WearApiConfiguration wearConfiguration;

    public static final EmokitApiManager getInstance() {
        if (null == sInstance) {
            synchronized (LOC_OBJ) {
                if (null == sInstance) {
                    sInstance = new EmokitApiManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 手机端使用sdk调用
     * 最好在Application中的onCreate中最前面调用
     * @param context
     * @param configuration
     */
    public void mobileApiConfig(final Context context,MobileApiConfiguration configuration){
        this.mobileConfiguration = configuration;
        //Emokit SDK注册
        SDKAppInit.createInstance(context);
        SDKAppInit.registerforuid(configuration.mPlatflag, configuration.mUserName, configuration.mPassword);
    }

    public MobileApiConfiguration getMobileConfiguration(){
        return this.mobileConfiguration;
    }

    /**
     * 腕表端使用sdk调用
     * 最好在Application中的onCreate中最前面调用
     * @param context
     */
    public void wearApiConfig(final Context context, WearApiConfiguration configuration){
        this.wearConfiguration = configuration;
        HeartRateDataService.getInstance(context);
        isWearApiConfig = true;

    }

    public WearApiConfiguration getWearConfiguration(){
        return this.wearConfiguration;
    }

    public boolean isWearApiConfig(){
        return this.isWearApiConfig;
    }

}
