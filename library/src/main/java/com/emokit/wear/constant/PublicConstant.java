package com.emokit.wear.constant;


/**
 * 
 * @ClassName: PublicConstant
 * @Description: 手机腕表两端公共的常量
 * @version V1.0.0
 */
public class PublicConstant {

	/**
	 * true表示debug版本，有打印信息和调试功能，false去掉打印信息和调试功能，调试功能长按关于键
	 */
	public static final boolean DEBUG = true;

	//-------------------------互传数据-------------------------
	/**手机和腕表通讯的前缀，为了解决多个应用冲突*/
	private static final String APP_TRANSFER_PREFIX = "/com_emokit_wear_";

	/**测试脉搏停止*/
	public static final String HEART_RATE_TEST_STOP_PATH = APP_TRANSFER_PREFIX + "heart_rate_test_stop_path";
	/**测试脉搏开始*/
	public static final String HEART_RATE_TEST_START_PATH = APP_TRANSFER_PREFIX + "heart_rate_test_start_path";

	/**心率数据传输路径*/
	public static final String HEART_RATE_DATA_PATH = APP_TRANSFER_PREFIX + "heart_rate_data_path";
	/**心率数据传输key*/
	public static final String HEART_RATE_ARRAY_KEY = "heart_rate_array_key";

	/**发送emokit 结果数据*/
	public static final String HEART_RATE_EMO_RESULT_PATH = APP_TRANSFER_PREFIX + "heart_rate_emo_result_path";
	/**发送emokit 结果数据*/
	public static final String HEART_RATE_EMO_RESULT_KEY = "heart_rate_emo_result_key";

	/**错误字符串的传输*/
	public static final String ERROR_PATH = APP_TRANSFER_PREFIX + "error_path";
	/**错误字符串的key*/
	public static final String ERROR_KEY = "error_key";
	//-------------------------互传数据-------------------------

	//-------------------------错误代码-------------------------
	/**获取情绪结果错误*/
	public static final String ERR_EMOTION_RESULT = "10000";
	/**获取心率错误，没有获取到心率*/
	public static final String ERR_HEART_RATE = "10001";
	/**
	 * 手机腕表没有建立链接，点击开始测试心率
	 * 1.手机端，没有链接到腕表
	 * 2.腕表端，没有链接到手机
	 */
	public static final String ERR_CONNECT_START_HEART_RATE = "10002";
	/**
	 * 手机腕表没有建立链接，停止测试心率
	 * 1.手机端，没有链接到腕表(只有手机端，腕表端没链接到手机也可以取消)
	 */
	public static final String ERR_CONNECT_STOP_HEART_RATE = "10003";
	/**
	 * 手机腕表没有建立链接，手机端发送情绪结果
	 * 1.没有链接到腕表
	 */
	public static final String ERR_CONNECT_SEND_EMO_RESULT = "10004";
	/**
	 * 手机腕表没有建立链接，手表端发送心率数组到手机
	 * 1.没有链接到手机
	 */
	public static final String ERR_CONNECT_SEND_HEART_RATE_ARRAY = "10005";
	//-------------------------错误代码-------------------------

	//-------------------------Service 启动的动作-------------------------
	/**
	 * 开始获取心率的情绪结果
	 */
	public static final String SERVICE_ACTION_START_HEART_RATE = "service_action_start_heart_rate";
	/**
	 * 停止获取心率的情绪结果
	 */
	public static final String SERVICE_ACTION_STOP_HEART_RATE = "service_action_stop_heart_rate";
	//-------------------------Service 启动的动作-------------------------
}
