package com.asc.app.bean.request;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class BaseRequestBean implements Serializable {
	private static final long serialVersionUID = 1270612211161735706L;
	String method;
	String operateID;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getOperateID() {
		return operateID;
	}

	public void setOperateID(String operateID) {
		this.operateID = operateID;
	}

	public String toJSONString() throws JSONException{
		JSONObject jsonReqObj = new JSONObject();
		jsonReqObj.put("method", this.method);
		jsonReqObj.put("operateID", this.operateID);
		return jsonReqObj.toString();
	}
}
