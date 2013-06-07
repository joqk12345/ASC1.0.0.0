package com.asc.hessian;

import com.asc.bean.request.AdRequestBean;
import com.asc.bean.request.ReportRequestBean;
import com.asc.bean.response.GetAdvertiseResult;
import com.asc.bean.response.OperateResult;

public interface IASCServiceHessian {
	public OperateResult reportDeviceInfo(ReportRequestBean reportRequest);

	public GetAdvertiseResult getAdvertise(AdRequestBean advertiseRequest);
}
