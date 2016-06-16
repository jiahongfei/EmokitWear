package com.emokit.wear.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 腕表端广播监听，用于回调监听，开发者可以继承这个类
 */
public abstract class WearWearableReceive extends BroadcastReceiver implements IWearDataTransferListener{

    /**腕表端广播监听Action*/
    public static final String ACTION_WEAR_EMO = "com.emokit.wear.action.WEAR_EMO_LISTENER";
    /**开始测试心率*/
    public static final String KEY_WEAR_START_HEART_RATE = "com.emokit.wear.key.wear.start.heart.rate";
    /**停止测试心率*/
    public static final String KEY_WEAR_CANCEL_HEART_RATE = "com.emokit.wear.key.wear.cancel.heart.rate";
    /**心率数组*/
    public static final String KEY_WEAR_ARRAY_HEART_RATE = "com.emokit.wear.key.wear.array.heart.rate";
    /**每次返回心率值*/
    public static final String KEY_WEAR_VALUE_HEART_RATE = "com.emokit.wear.key.wear.value.heart.rate";
    /**每次返回心率值,的索引*/
    public static final String KEY_WEAR_INDEX_HEART_RATE = "com.emokit.wear.key.wear.index.heart.rate";
    /**心率结果*/
    public static final String KEY_WEAR_EMOTION = "com.emokit.wear.key.wear.emotion";
    /**错误类型代码*/
    public static final String KEY_WEAR_ERROR_CODE = "com.emokit.wear.key.wear.error.code";

    protected Context mContext;

    public WearWearableReceive() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if(null != intent && ACTION_WEAR_EMO.equals(intent.getAction())){
            if(intent.hasExtra(KEY_WEAR_START_HEART_RATE)){
                onStartHeartRateListener();
            }else if(intent.hasExtra(KEY_WEAR_CANCEL_HEART_RATE)){
                onCancelHeartRateListener();
            }else if(intent.hasExtra(KEY_WEAR_ARRAY_HEART_RATE)){
                float[] array = intent.getFloatArrayExtra(KEY_WEAR_ARRAY_HEART_RATE);
                onArrayHeartRateListener(array);
            }else if(intent.hasExtra(KEY_WEAR_VALUE_HEART_RATE) &&
                    intent.hasExtra(KEY_WEAR_INDEX_HEART_RATE)){
                float value = intent.getFloatExtra(KEY_WEAR_VALUE_HEART_RATE,0.0f);
                int index = intent.getIntExtra(KEY_WEAR_INDEX_HEART_RATE,0);
                onHeartRateValueListener(index, value);
            }else if(intent.hasExtra(KEY_WEAR_EMOTION)){
                String emotion = intent.getStringExtra(KEY_WEAR_EMOTION);
                onEmotionListener(emotion);
            }else if(intent.hasExtra(KEY_WEAR_ERROR_CODE)){
                String errCode = intent.getStringExtra(KEY_WEAR_ERROR_CODE);
                onErrorListener(errCode);
            }
        }

    }

}
