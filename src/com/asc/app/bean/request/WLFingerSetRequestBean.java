package com.asc.app.bean.request;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author joqk
 *
 */
public class WLFingerSetRequestBean implements Serializable{
	private static final long serialVersionUID = -1104921622958421405L;
	private String pointAlias;// 采集点别名
	private String apMac;// apMac 暂时用BSSID代替
	private int RSSI;// RSSI
	private int TOA;// TOA
	private String collTime;// 采集时间
	private int rate ;
	private String mode;
	private String ESSID;

	
	public String getPointAlias() {
		return pointAlias;
	}

	public void setPointAlias(String pointAlias) {
		this.pointAlias = pointAlias;
	}

	public String getApMac() {
		return apMac;
	}

	public void setApMac(String apMac) {
		this.apMac = apMac;
	}

	public int getRSSI() {
		return RSSI;
	}

	public void setRSSI(int rssi) {
		RSSI = rssi;
	}

	public int getTOA() {
		return TOA;
	}

	public void setTOA(int toa) {
		TOA = toa;
	}

	public String getCollTime() {
		return collTime;
	}

	public void setCollTime(String collTime) {
		this.collTime = collTime;
	}
	
	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getESSID() {
		return ESSID;
	}

	public void setESSID(String essid) {
		ESSID = essid;
	}

	/**
	 * 将该类转换成json格式
	 * @return
	 * @throws JSONException
	 */
	public String toJSONString() throws JSONException{
		JSONObject jsonReqObj = new JSONObject();
		jsonReqObj.put("apMac", this.apMac == null ? "" : this.apMac);
		jsonReqObj.put("collTime", this.collTime == null ? "" : this.collTime);
		jsonReqObj.put("pointAlias", this.pointAlias == null ? "" : this.pointAlias);
		jsonReqObj.put("RSSI", this.RSSI+"" == null ? "" : this.RSSI);
		jsonReqObj.put("TOA", this.TOA+"" == null ? "" : this.TOA);
		jsonReqObj.put("rate", this.rate+"" == null ? "" : this.rate);
		jsonReqObj.put("ESSID", this.ESSID== null ? "" : this.ESSID);
		jsonReqObj.put("mode", this.mode == null ? "" : this.mode);
		return jsonReqObj.toString();
	}
	
	/**
	 * 解析类 在这里没用
	 * */
	public static WLFingerSetRequestBean parseObject(String jsonRequest) throws JSONException {
		System.out.println("request json: " + jsonRequest);
		JSONObject jsonReqObj = new JSONObject(jsonRequest);
		WLFingerSetRequestBean wLFingerSetVo = new WLFingerSetRequestBean();
		wLFingerSetVo.setApMac(jsonReqObj.getString("apMac"));
		wLFingerSetVo.setCollTime(jsonReqObj.getString("collTime"));
		wLFingerSetVo.setPointAlias(jsonReqObj.getString("pointAlias"));
		wLFingerSetVo.setRSSI(jsonReqObj.getInt("RSSI"));
		wLFingerSetVo.setTOA(jsonReqObj.getInt("TOA"));
		wLFingerSetVo.setRate(jsonReqObj.getInt("rate"));
		wLFingerSetVo.setESSID(jsonReqObj.getString("ESSID"));
		wLFingerSetVo.setMode(jsonReqObj.getString("mode"));
		return wLFingerSetVo;
	}

	
}
