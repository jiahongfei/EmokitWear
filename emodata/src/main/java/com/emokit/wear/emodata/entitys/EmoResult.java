package com.emokit.wear.emodata.entitys;

import java.io.Serializable;

/**
 * 根据心率返回Emokit结果
 */
public class EmoResult implements Serializable {

	/**
	 * 序列化的时候标志着这是同一个类
	 */
	private static final long serialVersionUID = 1L;

	private String exciting_trend;
	private String exciting;
	private String rc_main_value;
	private String resultcode;
	private String rc_main;
	private String servertime;
	private String result_id;


	public String getExciting_trend() {
		return exciting_trend;
	}

	public void setExciting_trend(String exciting_trend) {
		this.exciting_trend = exciting_trend;
	}

	public String getExciting() {
		return exciting;
	}

	public void setExciting(String exciting) {
		this.exciting = exciting;
	}

	public float excitability(){
		//LA:过于低迷
		//LV:较为低迷
		//CH:较为兴奋
		//HO:过于兴奋
		if("LA".equals(getExciting())){
//			return 2.5f;
			return 3f;
		}else if("LV".equals(getExciting())){
			return 5f;
		}else if("CH".equals(getExciting())){
//			return 7.5f;
			return 8f;
		}else if("HO".equals(getExciting())){
			return 10f;
		}else {
			return 0f;
		}
	}

	public String getRc_main_value() {
		return rc_main_value;
	}

	public void setRc_main_value(String rc_main_value) {
		this.rc_main_value = rc_main_value;
	}

	public String getResultcode() {
		return resultcode;
	}

	public void setResultcode(String resultcode) {
		this.resultcode = resultcode;
	}

	public String getRc_main() {
		return rc_main;
	}

	public void setRc_main(String rc_main) {
		this.rc_main = rc_main;
	}

	public String getServertime() {
		return servertime;
	}

	public void setServertime(String servertime) {
		this.servertime = servertime;
	}

	public String getResult_id() {
		return result_id;
	}

	public void setResult_id(String result_id) {
		this.result_id = result_id;
	}
}
