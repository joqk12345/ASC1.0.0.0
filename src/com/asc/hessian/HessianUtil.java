package com.asc.app.hessian;

import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import com.asc.bean.request.AdRequestBean;
import com.asc.bean.request.ReportRequestBean;
import com.asc.bean.response.GetAdvertiseResult;
import com.asc.bean.response.OperateResult;
import com.caucho.hessian.client.HessianProxyFactory;

public class HessianUtil {
	private static String HESSIAN_URL = "https://192.168.20.159:8443/RadiusWeb/hessian/HessianServlet";
	private static IASCServiceHessian ascServiceHessian;
	
	private static TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
            }
        };
	
	private static void init() {
		try {
			SSLContext sContext = SSLContext.getInstance("SSL");
			sContext.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sContext.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new AllowAllHostnameVerifier());
			HessianProxyFactory factory = new HessianProxyFactory();
			factory.setHessian2Reply(false);
			ascServiceHessian = (IASCServiceHessian) factory.create(IASCServiceHessian.class, HESSIAN_URL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}
	
	public static void setServicePostion(String serverIp) {
		HESSIAN_URL = "https://" + serverIp + ":31658/AuteView2RadiusWeb/hessian/HessianServlet";
	}
	
	/**
	 * @param reportRequest
	 * @return OperateResult
	 */
	public static OperateResult reportDeviceInfo(ReportRequestBean reportRequest){
		init();
		return ascServiceHessian.reportDeviceInfo(reportRequest);
	}
	
	/**
	 * @param advertiseRequest
	 * @return GetAdvertiseResult
	 */
	public static GetAdvertiseResult getAdvertise(AdRequestBean advertiseRequest){
		init();
		return ascServiceHessian.getAdvertise(advertiseRequest);
	}
}
