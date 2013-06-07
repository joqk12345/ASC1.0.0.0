package com.asc.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import com.asc.app.json.EasySSLSocketFactory;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class UpdateUtil {
	private static String UPDATE_URL = "https://12.1.1.1:31658/MonitorWeb/uploadfiles/autelan-asc.apk";
	public static final String SAVED_APKPREFIX = "update-asc";
	public static final String SAVED_APKSUFFIX = "apk";
	private HttpClient httpClient;
	private HttpGet httpGet;
	
	
	public static void setServerPostion(String serverIp){
		UPDATE_URL = "https://" + serverIp + ":31658/MonitorWeb/uploadfiles/autelan-asc.apk";
	}
	
	public void init(){
		SchemeRegistry schemeRegistry = new SchemeRegistry();
	    schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 8443));
	    schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	    
	    HttpParams params = new BasicHttpParams();
	    params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
	    params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
	    params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
	    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	    
		ClientConnectionManager connManager = new SingleClientConnManager(params, schemeRegistry);
		httpClient = new DefaultHttpClient(connManager, params);
		httpGet = new HttpGet(UPDATE_URL);
	}
	
	public InputStream getUpdateFile(){
		try {
			init();
			HttpResponse response = httpClient.execute(httpGet);
			return response.getEntity().getContent();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void destroy(){
		httpGet.abort();
		httpClient.getConnectionManager().shutdown();
	}
	
	public static String getVersionName(Context context){
		try {
			return context.getPackageManager().getPackageInfo("com.asc", 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "1.0";
	}
	
	public static int getVersionCode(Context context){
		try {
			return context.getPackageManager().getPackageInfo("com.asc", 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}
	
	public static String generateId() {
		try {
			String base = "0123456789";
			Random random = new Random();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < 10; i++) {
				int number = random.nextInt(base.length());
				sb.append(base.charAt(number));
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
}
