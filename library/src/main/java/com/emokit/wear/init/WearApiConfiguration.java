package com.emokit.wear.init;

/**
 * 腕表端api配置类
 * Created by jiahongfei on 16/3/17.
 */
public class WearApiConfiguration {

    /**获取心率期间最小时间10秒*/
    private static final long HEART_RATE_DURATION_MIN = 10*1000;
    /**获取心率超时时间最小20秒*/
    private static final long HEART_RATE_TIMEOUT_MIN = 20*1000;

    public final long mHeartRateDuration;
    public final long mHeartRateTimeOut;

    private WearApiConfiguration(Builder builder){
        mHeartRateDuration = builder.mHeartRateDuration;
        mHeartRateTimeOut = builder.mHeartRateTimeOut;
    }

    public static final class Builder{
        public long mHeartRateDuration = 45 * 1000;
        public long mHeartRateTimeOut = 30 * 1000;

        /**
         * 设置获取心率的超时时间
         * 如果超过这个时间还没有接收到一个有用的心率值会自动停止获取心率
         * 最小HEART_RATE_TIMEOUT_MIN秒
         * @param milliseTimeOut
         */
        public Builder setHeartRateTimeOut(int milliseTimeOut) {
            if(milliseTimeOut < HEART_RATE_TIMEOUT_MIN){
                mHeartRateTimeOut = HEART_RATE_TIMEOUT_MIN;
            }else {
                mHeartRateTimeOut = milliseTimeOut;
            }
            return this;
        }

        /**
         * 设置获取心率的时间，在这个规定时间内获取心率
         * 在成功获取第一个心率值开始倒计时
         * 最小HEART_RATE_DURATION_MIN秒
         * @param milliseDuration
         */
        public Builder setHeartRateDuration(int milliseDuration) {
            if(milliseDuration < HEART_RATE_DURATION_MIN){
                mHeartRateDuration = HEART_RATE_DURATION_MIN;
            }else {
                mHeartRateDuration = milliseDuration;
            }
            return this;
        }

        public WearApiConfiguration create(){
            return new WearApiConfiguration(this);
        }

    }
}
