package com.asc.app.bean.request;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationRequestBean extends BaseRequestBean {
	private String ssid;
	private String mac;
	private double latitude;
	private double longitude;
	private float accuracy;

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	
	public String getMac() {
		return mac;
	}
	
	public void setMac(String mac) {
		this.mac = mac;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}
	
	public String toJSONString() throws JSONException{
		JSONObject jsonReqObj = new JSONObject();
		jsonReqObj.put("method", this.method);
		jsonReqObj.put("operateID", this.operateID);
		jsonReqObj.put("ssid", this.ssid);
		jsonReqObj.put("latitude", this.latitude);
		jsonReqObj.put("longitude", this.longitude);
		jsonReqObj.put("accuracy", this.accuracy);
		return jsonReqObj.toString();
	}

}
