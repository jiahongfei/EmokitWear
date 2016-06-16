package com.emokit.wear.service.wear;

/**
 * 测试心率对外的回掉
 * Created by jiahongfei on 15/12/9.
 */
interface IHeartRateDataListener {

    /**
     * 开始获取心率数据
     */
    public void onStartHeartRateDataListener();

    /**
     * 获取到的心率
     * @param index 获取第index个心率
     * @param value 获取的心率值
     */
    public void onHeartRateValueListener(int index, Float value);

    /**
     * 获取心率完成，成功返回一连串的心率值
     * @param arrayHeartRate
     */
    public void onArrayHeartRateListener(Float[] arrayHeartRate);

    /**
     * 测试脉搏失败，超时等原因
     */
    public void onHeartRateFailsListener();

}
