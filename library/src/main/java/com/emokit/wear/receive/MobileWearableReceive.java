package com.emokit.wear.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 手机端广播监听，用于获取情绪的回调监听，开发者可以继承这个类
 */
public abstract class MobileWearableReceive extends BroadcastReceiver implements IHeartRateEmoResultListener {

    /**手机端广播监听Action*/
    public static final String ACTION_MOBILE_EMO = "com.emokit.wear.action.MOBILE_EMO_LISTENER";
    /**开始测试心率*/
    public static final String KEY_MOBILE_START_HEART_RATE = "com.emokit.wear.key.mobile.start.heart.rate";
    /**停止测试心率*/
    public static final String KEY_MOBILE_CANCEL_HEART_RATE = "com.emokit.wear.key.mobile.cancel.heart.rate";
    /**心率数组*/
    public static final String KEY_MOBILE_ARRAY_HEART_RATE = "com.emokit.wear.key.mobile.array.heart.rate";
    /**心率结果*/
    public static final String KEY_MOBILE_EMOTION = "com.emokit.wear.key.mobile.emotion";
    /**错误类型代码*/
    public static final String KEY_MOBILE_ERROR_CODE = "com.emokit.wear.key.mobile.error.code";

    protected Context mContext;

    public MobileWearableReceive() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if(null != intent && ACTION_MOBILE_EMO.equals(intent.getAction())){
            if(intent.hasExtra(KEY_MOBILE_START_HEART_RATE)){
                onStartHeartRateListener();
            }else if(intent.hasExtra(KEY_MOBILE_CANCEL_HEART_RATE)){
                onCancelHeartRateListener();
            }else if(intent.hasExtra(KEY_MOBILE_ARRAY_HEART_RATE)){
                float[] array = intent.getFloatArrayExtra(KEY_MOBILE_ARRAY_HEART_RATE);
                onArrayHeartRateListener(array);
            }else if(intent.hasExtra(KEY_MOBILE_EMOTION)){
                String emotion = intent.getStringExtra(KEY_MOBILE_EMOTION);
                onEmotionListener(emotion);
            }else if(intent.hasExtra(KEY_MOBILE_ERROR_CODE)){
                String errCode = intent.getStringExtra(KEY_MOBILE_ERROR_CODE);
                onErrorListener(errCode);
            }
        }

    }

}
