package com.asc.app.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import android.util.Log;

import com.asc.app.bean.request.AdRequestBean;
import com.asc.app.bean.request.BaseRequestBean;
import com.asc.app.bean.request.DeviceInfoRequestBean;
import com.asc.app.bean.request.LocationRequestBean;
import com.asc.app.bean.request.WLFingerSetRequestBean;
import com.asc.app.bean.response.GetAdvertiseResult;
import com.asc.app.bean.response.GetVersionResult;
import com.asc.app.bean.response.OperateResult;
import com.asc.app.bean.response.WlFingerSetResult;

/**
 * 
 * @author zhanglei
 *
 */
public class JSONUtil {
	
	private final static String LOG_TAG = "JSONUtil";
//	private static String JSON_URL = "https://12.1.1.1:31658/MonitorWeb/json/JsonServlet";
	private static String JSON_URL = "http://192.168.80.16:8080/AuteView2072After/wl/WLFingerSetServlet";
	private HttpClient httpClient;
	private HttpPost httpPost;
	
	public static void setServicePostion(String serverIp) {
		JSON_URL = "https://" + serverIp + ":31658/MonitorWeb/json/JsonServlet";
	}
	
	/*public static void setServicePostionByHttp(String serverIp) {
		JSON_URL = "http://" + serverIp + ":8080/AuteView2072After/wl/WLFingerSetServlet";
	}*/
	
	public static void setServicePostionByHttp(String serverIp) {
		JSON_URL = "http://" + serverIp + "/wl/WLFingerSetServlet";
	}

	
	public static void setServicePostionByHttpALL(String serverServletLocation) {
		JSON_URL = serverServletLocation;
	}
	
	public void init(){
		SchemeRegistry schemeRegistry = new SchemeRegistry();
	    schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 8443));
	    schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	    
	    HttpParams params = new BasicHttpParams();
	    params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
	    params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
	    params.setParameter(ConnManagerPNames.TIMEOUT, 1000*5L);
	    params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
	    
	    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	    
		ClientConnectionManager connManager = new SingleClientConnManager(params, schemeRegistry);
		httpClient = new DefaultHttpClient(connManager, params);
		
//		httpClient = new DefaultHttpClient();
		httpPost = new HttpPost(JSON_URL);
		Log.e(LOG_TAG, JSON_URL);
	}
	
	public void test(){
		try {
			HttpGet httpGet = new HttpGet("http://1.1.1.1");
			HttpResponse response = httpClient.execute(httpGet);
			String value = "--";
			if (httpGet.getFirstHeader("location") != null ) {
				value = httpGet.getFirstHeader("location").getValue();
			}
			Log.e(LOG_TAG, "value: " + value);
			InputStreamReader inputReader = new InputStreamReader(response.getEntity().getContent());
			BufferedReader bReader = new BufferedReader(inputReader);
			String result = "";
			while ((result = bReader.readLine()) != null) {
				Log.e(LOG_TAG, "result:  "+result );
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void destroy(){
		httpPost.abort();
		httpClient.getConnectionManager().shutdown();
	}
	//sqlite3 /data/data/com.android.providers.settings/databases/settings.db "INSERT INTO system VALUES(99,'http_proxy','124.193.109.117:80')"
	/**
	 * 上报设备信息
	 * @param reportRequest
	 * @return OperateResult
	 */
	public OperateResult reportDeviceInfo(DeviceInfoRequestBean reportRequest){
		try {
			init();
			String jsonString = reportRequest.toJSONString();
//			httpPost.setEntity(new StringEntity(ReportRequestBean.toJSONString(reportRequest)));
			Log.e(LOG_TAG, jsonString);
			httpPost.setEntity(new StringEntity(jsonString, "UTF-8"));
			HttpResponse response = httpClient.execute(httpPost);
			String responseJson = EntityUtils.toString(response.getEntity(),"UTF-8");
			Log.e("JSONUtil", responseJson);
			destroy();
			return OperateResult.parseObject(responseJson);
		} catch (UnsupportedEncodingException e) {
			Log.e("reportDeviceInfo", e.getMessage());
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			Log.e("reportDeviceInfo", e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("reportDeviceInfo", e.getMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			Log.e("reportDeviceInfo", e.getMessage());
			e.printStackTrace();
		} catch (JSONException e) {
			Log.e("reportDeviceInfo", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 上报位置信息
	 * @param locationRequest
	 * @return
	 */
	public OperateResult reportLocation(LocationRequestBean locationRequest){
		try {
			init();
			String jsonString = locationRequest.toJSONString();
			Log.e(LOG_TAG, jsonString);
			httpPost.setEntity(new StringEntity(jsonString, "UTF-8"));
			HttpResponse response = httpClient.execute(httpPost);
			String responseJson = EntityUtils.toString(response.getEntity(),"UTF-8");
			Log.e("JSONUtil", responseJson);
			destroy();
			return OperateResult.parseObject(responseJson);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取广告地址
	 * @param advertiseRequest
	 * @return GetAdvertiseResult
	 */
	public GetAdvertiseResult getAdvertise(AdRequestBean advertiseRequest){
		try {
			init();
			String jsonString = advertiseRequest.toJSONString();
			Log.e(LOG_TAG, jsonString);
			httpPost.setEntity(new StringEntity(jsonString,"UTF-8"));
			HttpResponse response = httpClient.execute(httpPost);
			String responseJson = EntityUtils.toString(response.getEntity(),"UTF-8");
			Log.e("JSONUtil", responseJson);
			destroy();
			return GetAdvertiseResult.parseObject(responseJson);
		} catch (UnsupportedEncodingException e) {
			Log.e(LOG_TAG, e.getMessage());
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			Log.e(LOG_TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			Log.e(LOG_TAG, e.getMessage());
			e.printStackTrace();
		} catch (JSONException e) {
			Log.e(LOG_TAG, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取软件版本
	 * @param baseRequest
	 * @return
	 */
	public GetVersionResult getVersion(BaseRequestBean baseRequest){
		try {
			init();
			String jsonString = baseRequest.toJSONString();
			Log.e(LOG_TAG, jsonString);
			httpPost.setEntity(new StringEntity(jsonString, "UTF-8"));
			HttpResponse response = httpClient.execute(httpPost);
			String responseJson = EntityUtils.toString(response.getEntity(),"UTF-8");
			Log.e("JSONUtil", responseJson);
			destroy();
			return GetVersionResult.parseObject(responseJson);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 位置信号信息上报
	 * @param locationRequest
	 * @return
	 */
	public WlFingerSetResult reportLocationRSSI(WLFingerSetRequestBean wlFingerSetRequestbean){
	/*	try {*/
			try {
				init();
				//将信息转换成jsonString 格式
				String jsonString = wlFingerSetRequestbean.toJSONString();
				Log.e(LOG_TAG, jsonString);
				//使用httpPost方式请求发送数据
				httpPost.setEntity(new StringEntity(jsonString, "UTF-8"));
				HttpResponse response = httpClient.execute(httpPost);
				String responseJson = EntityUtils.toString(response.getEntity(),"UTF-8");
				Log.e("JSONUtil", responseJson);
				destroy();
				return WlFingerSetResult.parseObject(responseJson);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		/*} catch (Exception e) {
		    e.printStackTrace();
		}*/
		return null;
	}
	
	/**
	 * 位置信号信息上报
	 * @param locationRequest
	 * @return
	 */
	public WlFingerSetResult reportLocationRSSIListString(String listString){
	/*	try {*/
			try {
				init();
				//将信息转换成jsonString 格式
				String jsonString = listString;
				Log.e(LOG_TAG, jsonString);
				//使用httpPost方式请求发送数据
				httpPost.setEntity(new StringEntity(jsonString, "UTF-8"));
				HttpResponse response = httpClient.execute(httpPost);
				String responseJson = EntityUtils.toString(response.getEntity(),"UTF-8");
				Log.e("JSONUtil", responseJson);
				destroy();
				return WlFingerSetResult.parseObject(responseJson);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		/*} catch (Exception e) {
		    e.printStackTrace();
		}*/
		return null;
	}
}
