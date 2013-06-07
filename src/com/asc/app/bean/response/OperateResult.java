package com.asc.app.bean.response;

import java.io.Serializable;

import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Webservice返回消息
 * @author zhanglei
 *
 */
public class OperateResult implements Serializable{
	private String operateID;
	private int resultCode;
	private String errorInfo;

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
	
	public static OperateResult parseObject(String jsonString) throws JSONException{
		JSONObject jsonRespObj = new JSONObject(jsonString);
		OperateResult result = new OperateResult();
		result.setOperateID(jsonRespObj.getString("operateID"));
		result.setResultCode(jsonRespObj.getInt("resultCode"));
		result.setErrorInfo(jsonRespObj.getString("errorInfo"));
		return result;
	}

}
