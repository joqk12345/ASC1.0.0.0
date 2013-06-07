package com.asc.app.bean.request;

import org.json.JSONException;
import org.json.JSONObject;

public class DeviceInfoRequestBean extends BaseRequestBean{
	String staMac;
	String staType;
	String deviceManufacturer;
	String deviceModel;
	String osVersion;

	public String getStaMac() {
		return staMac;
	}

	public void setStaMac(String staMac) {
		this.staMac = staMac;
	}

	public String getStaType() {
		return staType;
	}

	public void setStaType(String staType) {
		this.staType = staType;
	}

	public String getDeviceManufacturer() {
		return deviceManufacturer;
	}

	public void setDeviceManufacturer(String deviceManufacturer) {
		this.deviceManufacturer = deviceManufacturer;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String toJSONString() throws JSONException{
		JSONObject jsonReqObj = new JSONObject();
		jsonReqObj.put("method", this.method);
		jsonReqObj.put("operateID", this.operateID);
		jsonReqObj.put("staMac", this.staMac);
		jsonReqObj.put("staType", this.staType);
		jsonReqObj.put("deviceManufacturer", this.deviceManufacturer);
		jsonReqObj.put("deviceModel", this.deviceModel);
		jsonReqObj.put("osVersion", this.osVersion);
		return jsonReqObj.toString();
	}
}
