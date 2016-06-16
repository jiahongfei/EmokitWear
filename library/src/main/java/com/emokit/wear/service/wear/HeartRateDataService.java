package com.emokit.wear.service.wear;

import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;

import java.util.ArrayList;

/**
 * 获取心率数据的Service
 *
 * @author jiahongfei
 */
public class HeartRateDataService implements SensorEventListener {

    private static final Object LOC_OBJ = new Object();
    /**
     * 脉搏成功之后的定时时间
     */
    private static long HEART_RATE_SUCCED_DURATION = 30 * 1000;
    /**
     * 超过这个时间还没有返回一次成功的心率调用错误监听停止获取心率
     */
    private static long HEART_RATE_TIME_OUT = 45 * 1000;
    private static HeartRateDataService sInstance = null;

    private Context mContext;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private IHeartRateDataListener mHeartRateDataListener;

    private ArrayList<Float> mHeartRateList = new ArrayList<>();

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {

                    if (null != mHeartRateDataListener) {
                        mHeartRateDataListener
                                .onArrayHeartRateListener(mHeartRateList
                                        .toArray(new Float[mHeartRateList.size()]));
                    }

                    stopHearRate();

                    break;
                }
                case 1: {
                    //超时报错
                    if (null != mHeartRateDataListener) {
                        mHeartRateDataListener.onHeartRateFailsListener();
                    }
                    stopHearRate();
                    break;
                }
                default:
                    break;
            }
            return false;
        }
    });

    public static final HeartRateDataService getInstance(Context context) {
        if (null == sInstance) {
            synchronized (LOC_OBJ) {
                if (null == sInstance) {
                    sInstance = new HeartRateDataService(context);
                }
            }
        }
        return sInstance;
    }

    private HeartRateDataService(Context context) {
        mContext = context;
        initSensor();
    }

    private void initSensor() {
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_HEART_RATE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (0 == event.values[0]) {
            // 如果要是有0就是没有数据
            return;
        }
        if (null != mHeartRateDataListener) {
            mHeartRateDataListener.onHeartRateValueListener(mHeartRateList.size(), event.values[0]);
        }
        if (0 == mHeartRateList.size()) {
            mHandler.sendMessageDelayed(
                    mHandler.obtainMessage(0, mHeartRateList), HEART_RATE_SUCCED_DURATION);
            //删除检测失败的定时器
            mHandler.removeMessages(1);
        }
        // 添加心率值
        mHeartRateList.add(event.values[0]);


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * 开始测试心率
     */
    public void startHearRate() {

        acquireWakeLock();

        stopHearRate();

        if (null != mHeartRateDataListener) {
            mHeartRateDataListener.onStartHeartRateDataListener();
        }
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
        //同时启动定时器，监听失败
        mHandler.sendEmptyMessageDelayed(1, HEART_RATE_TIME_OUT);
    }

    /**
     * 停止测试心率
     */
    public void stopHearRate() {
        mHandler.removeMessages(0);
        mHandler.removeMessages(1);
        mHeartRateList.clear();

        mSensorManager.unregisterListener(HeartRateDataService.this);

        releaseWakeLock();
    }

    private PowerManager.WakeLock wakeLock;

    /**
     * 获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
     */
    private void acquireWakeLock() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, getClass()
                    .getCanonicalName());
            if (null != wakeLock) {
                wakeLock.acquire();
            }
        }
    }

    // 释放设备电源锁
    private void releaseWakeLock() {
        if (null != wakeLock && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    /**
     * 设置获取心率的超时时间
     * 如果超过这个时间还没有接收到一个有用的心率值会自动停止获取心率
     *
     * @param milliseTimeOut
     */
    private void setHeartRateTimeOut(long milliseTimeOut) {
        HEART_RATE_TIME_OUT = milliseTimeOut;
    }

    /**
     * 设置获取心率的时间，在这个规定时间内获取心率
     * 在成功获取第一个心率值开始倒计时
     *
     * @param milliseDuration
     */
    private void setHeartRateDuration(long milliseDuration) {
        HEART_RATE_SUCCED_DURATION = milliseDuration;
    }

    /**
     * 设置发送脉搏数据的监听
     *
     * @param iHeartRateDataListener
     */
    public void setHeartRateDataListener(
            IHeartRateDataListener iHeartRateDataListener) {
        mHeartRateDataListener = iHeartRateDataListener;
    }

    public static class Builder {

        public Builder(Application application) {
            sInstance = getInstance(application);
        }

        /**
         * 设置获取心率的超时时间，超过这个时间还没有返回一次成功的心率调用错误监听停止获取心率
         *
         * @param milliseTimeOut
         * @return
         */
        public Builder setHeartRateTimeOut(long milliseTimeOut) {
            sInstance.setHeartRateTimeOut(milliseTimeOut);
            return this;
        }

        /**
         * 在这段时间内获取心率
         *
         * @param milliseDuration
         * @return
         */
        public Builder setHeartRateDuration(long milliseDuration) {
            sInstance.setHeartRateDuration(milliseDuration);
            return this;
        }

        /**
         * 设置获取心率数据监听
         *
         * @param iHeartRateDataListener
         * @return
         */
        public Builder setHeartRateDataListener(IHeartRateDataListener iHeartRateDataListener) {
            sInstance.setHeartRateDataListener(iHeartRateDataListener);
            return this;
        }

        /**
         * 根据配置创建对象
         * @return
         */
        public HeartRateDataService create() {
            return sInstance;
        }

    }

}
