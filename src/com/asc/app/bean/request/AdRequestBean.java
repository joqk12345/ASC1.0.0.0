package com.asc.app.bean.request;

import org.json.JSONException;
import org.json.JSONObject;

public class AdRequestBean extends BaseRequestBean{
	String bssid;
	String essid;
	String area;

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public String getEssid() {
		return essid;
	}

	public void setEssid(String essid) {
		this.essid = essid;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}
	
	public String toJSONString() throws JSONException{
		JSONObject jsonReqObj = new JSONObject();
		jsonReqObj.put("operateID", this.operateID == null ? "" : this.operateID);
		jsonReqObj.put("bssid", this.bssid == null ? "" : this.bssid);
		jsonReqObj.put("essid", this.essid == null ? "" : this.essid);
		jsonReqObj.put("area", this.area == null ? "" : this.area);
		jsonReqObj.put("method", this.method == null ? "" : this.method);
		return jsonReqObj.toString();
	}

}
