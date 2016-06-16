package com.emokit.wear.receive;

/**
 * wear传输数据对外的回掉
 * Created by jiahongfei on 15/12/9.
 */
interface IWearDataTransferListener {

    /**
     * 开始测试心率
     */
    public void onStartHeartRateListener();

    /**
     * 取消测试心率
     */

    public void onCancelHeartRateListener();

    /**
     * 收到心率数组
     * @param heartRates
     */
    public void onArrayHeartRateListener(float[] heartRates);

    /**
     * 获取到的心率
     * @param index 获取第index个心率
     * @param value 获取的心率值
     */
    public void onHeartRateValueListener(int index, float value);

    /**
     * 获取情绪结果
     * @param emoJson
     */
    public void onEmotionListener(String emoJson);

    /**
     * 出错监听
     * @param errCode
     */
    public void onErrorListener(String errCode);

}
