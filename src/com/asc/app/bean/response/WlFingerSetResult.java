package com.asc.app.bean.response;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class WlFingerSetResult implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int resultCode;
	private String errorInfo;
	
	
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


	public static WlFingerSetResult parseObject(String jsonString) throws JSONException{
		JSONObject jsonRespObj = new JSONObject(jsonString);
		WlFingerSetResult result = new WlFingerSetResult();
		result.setResultCode(jsonRespObj.getInt("resultCode"));
		result.setErrorInfo(jsonRespObj.getString("errorInfo"));
		return result;
	}

}
