package com.emokit.wear.receive;

/**
 * 心率测情绪监听
 */
interface IHeartRateEmoResultListener {

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
