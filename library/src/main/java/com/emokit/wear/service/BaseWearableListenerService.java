package com.emokit.wear.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.wearable.Wearable;
import com.mobvoi.android.wearable.WearableListenerService;

/**
 * 基类
 * Created by jiahongfei on 16/3/16.
 */
public abstract class BaseWearableListenerService extends WearableListenerService {

    protected Context mContext;
    protected MobvoiApiClient mSuperMobvoiApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        initMobvoiApiClient();
    }

    private void initMobvoiApiClient() {
        mSuperMobvoiApiClient = new MobvoiApiClient.Builder(this).addApi(
                Wearable.API).build();
        mSuperMobvoiApiClient.connect();
        superMobvoiApiClient(mSuperMobvoiApiClient);
    }

    /**
     * 广播回调
     * @param action
     * @param bundle
     */
    public void broadcastCallback(String action, Bundle bundle){
        Intent intent = new Intent(action);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    /**
     * 父类的MobvoiApiClient对象传递到子类中，
     * 子类可以利用这个对象实现自己的发送接收消息，发送接收数据
     * @param superMobvoiApiClient
     */
    protected abstract void superMobvoiApiClient(MobvoiApiClient superMobvoiApiClient);
}
