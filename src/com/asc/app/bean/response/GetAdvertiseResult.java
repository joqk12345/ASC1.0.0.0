package com.asc.app.bean.response;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class GetAdvertiseResult implements Serializable{
	private static final long serialVersionUID = -786594087116288348L;
	
	private String operateID;
	private int resultCode;
	private String errorInfo;
	private String location;
	private String advertiseUrl;
	private String advertiseInfo;

	public String getAdvertiseInfo() {
		return advertiseInfo;
	}

	public void setAdvertiseInfo(String advertiseInfo) {
		this.advertiseInfo = advertiseInfo;
	}

	public String getAdvertiseUrl() {
		return advertiseUrl;
	}

	public void setAdvertiseUrl(String advertiseUrl) {
		this.advertiseUrl = advertiseUrl;
	}

	public String getOperateID() {
		return operateID;
	}

	public void setOperateID(String operateID) {
		this.operateID = operateID;
	}

	/**
	 * @return
	 */
	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public static GetAdvertiseResult parseObject(String jsonString) throws JSONException{
		JSONObject jsonRespObj = new JSONObject(jsonString);
		GetAdvertiseResult result = new GetAdvertiseResult();
		result.setOperateID(jsonRespObj.getString("operateID"));
		result.setErrorInfo(jsonRespObj.getString("errorInfo"));
		result.setResultCode(jsonRespObj.getInt("resultCode"));
		result.setAdvertiseInfo(jsonRespObj.getString("advertiseInfo"));
		result.setAdvertiseUrl(jsonRespObj.getString("advertiseUrl"));
		result.setLocation(jsonRespObj.getString("location"));
		return result;
	}
}