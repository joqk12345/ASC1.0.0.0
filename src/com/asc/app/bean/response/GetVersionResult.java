package com.asc.app.bean.response;

import org.json.JSONException;
import org.json.JSONObject;

public class GetVersionResult {
	private String operateID;
	private int resultCode;
	private String errorInfo;
	private int versionCode;
	private String versionName;

	public String getOperateID() {
		return operateID;
	}

	public void setOperateID(String operateID) {
		this.operateID = operateID;
	}

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

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	
	public static GetVersionResult parseObject(String jsonString) throws JSONException{
		JSONObject jsonRespObj = new JSONObject(jsonString);
		GetVersionResult result = new GetVersionResult();
		result.setOperateID(jsonRespObj.getString("operateID"));
		result.setErrorInfo(jsonRespObj.getString("errorInfo"));
		result.setResultCode(jsonRespObj.getInt("resultCode"));
		result.setVersionCode(jsonRespObj.getInt("versionCode"));
		result.setVersionName(jsonRespObj.getString("versionName"));
		return result;
	}

}