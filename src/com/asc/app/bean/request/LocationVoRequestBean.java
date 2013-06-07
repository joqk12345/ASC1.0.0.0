package com.asc.app.bean.request;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LocationVoRequestBean implements Serializable{
	private static final long serialVersionUID = -1104921622958421405L;
	private String apMac;// apMac 暂时用BSSID代替
	private String staMac;//终端MAC地址
	private int RSSI;// RSSI
	private int TOA;// TOA
	private String collTime;// 采集时间
	private int rate ;
	private String mode;
	private String ESSID;

	public static List<LocationVoRequestBean> parseObject(JSONArray jSONArray) throws JSONException {
		List<LocationVoRequestBean> list = new ArrayList<LocationVoRequestBean>();
		Iterator iterator = ((List<LocationVoRequestBean>) jSONArray).iterator();
		while(iterator.hasNext()){
			JSONObject jsonReqObj = (JSONObject)iterator.next();
			LocationVoRequestBean locationVo = new LocationVoRequestBean();
			locationVo.setApMac(jsonReqObj.getString("apMac"));
			locationVo.setStaMac(jsonReqObj.getString("staMac"));
			locationVo.setCollTime(jsonReqObj.getString("collTime"));
			locationVo.setRSSI(jsonReqObj.getInt("RSSI"));
			locationVo.setTOA(jsonReqObj.getInt("TOA"));
			locationVo.setRate(jsonReqObj.getInt("rate"));
			locationVo.setESSID(jsonReqObj.getString("ESSID"));
			locationVo.setMode(jsonReqObj.getString("mode"));
			list.add(locationVo);
		}
		return list;
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
	
	public String getStaMac() {
		return staMac;
	}

	public void setStaMac(String staMac) {
		this.staMac = staMac;
	}
}
