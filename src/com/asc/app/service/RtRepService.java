package com.asc.app.service;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.cookie.DateUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.asc.app.bean.request.DeviceInfoRequestBean;
import com.asc.app.bean.request.LocationVoRequestBean;
import com.asc.app.bean.request.WLFingerSetRequestBean;
import com.asc.app.bean.response.OperateResult;
import com.asc.app.bean.response.WlFingerSetResult;
import com.asc.app.database.DBManager;
import com.asc.app.json.JSONUtil;
import com.asc.app.ui.ASCActivity;
import com.asc.app.ui.LocationUpdateActivity;
import com.asc.app.util.UpdateUtil;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

/**
 * @joqk
 * 
 * 
 * @date   2013年4月27日11:02:12
 * 
 * Real-time reporting Service   实时位置上报服务
 * 
 * */

public class RtRepService extends Service {
	
	
	//定义个一个Tag标签
	private static final String TAG = "MyService";
	//这里定义吧一个Binder类，用在onBind()有方法里，这样Activity那边可以获取到
	private MyBinder mBinder = new MyBinder();
	//定时上传服务的周期从数据库中读取
	private String url="";
	private String sSid="";
	private String collectPeriod="";
	private String collectRateValue="";
	//wifi连接类
	private WifiConnService wifiConnService;
	
	private Thread t;
	private boolean se=false;
	
	/**
	 * 用于与activity 交互使用
	 * 
	 * */

	@Override
	public IBinder onBind(Intent intent) {
		Log.e(TAG, "start IBinder~~~");
		return mBinder;
	}
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		//初始化数据库 配置信息  读取循环周期
		// 从数据库中取出数据
		DBManager dbManager = DBManager.getInstance(RtRepService.this);
		dbManager.open();
		Cursor cursor = dbManager.executeSqlQuery("select serveURL,SSID,collect_period,collect_rate from wlfinger",	null);
		if (cursor.getCount() > 0 && cursor.moveToFirst()) {
			// 得到该四个值的信息
			if (url != null)
				url = cursor.getString(0);
			if (sSid != null)
				sSid = cursor.getString(1);
			if (collectPeriod != null)
				collectPeriod = cursor.getString(2);
			if (url != null)
				collectRateValue = cursor.getString(3);
		}
		Log.e(TAG, "start onCreate~~~");
		se = true;
		
	}
	
	/*@Override
	public void onStart(Intent intent, int startId) {
		Log.e(TAG, "start onStart~~~");
		super.onStart(intent, startId);	
	}
	*/
	
	
	@Override
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		//线程中进行实时上报
		Log.e(TAG, "start onStartCommand~~~");
		
		this.serviceTask();
		return super.onStartCommand(intent, flags, startId);
	
	}


	@Override
	public void onDestroy() {
		Log.e(TAG, "start onDestroy~~~");
		//销毁线程操作
		se = false;
		super.onDestroy();
	}
	
	//可以根据i的正负来控制线程的启动与关闭
	private void serviceTask(){
			//在这里进行wifi信号检测 并且上传到服务器
		t =  new Thread(new Runnable() {
				@Override
				public void run() {
					while(se){
					//周期
					if (collectPeriod != null && collectRateValue != null) {
					
							// 线程等待 周期
							try {
								Thread.sleep(Integer.parseInt(collectPeriod) * 1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							wifiConnService = WifiConnService.getInstance(getApplicationContext());
							// 如果wifi没有打开 则打开wifi连接
							if (!wifiConnService.isWifiEnabled()) {
								wifiConnService.enableWifi();
								// 提示用户进行wifi连接
								// Toast.makeText(LocationUpdateActivity.this,
								// R.string.open_wifi_waiting,
								// Toast.LENGTH_LONG).show();
							}
							// 获取开放的SSID
							wifiConnService.startWifiManager();//开始监控
							List<ScanResult> scanResults = wifiConnService.getResults();
							WifiInfo info = wifiConnService.getConnInfo();
							List<LocationVoRequestBean> LocationVolist =new ArrayList<LocationVoRequestBean>();
						
							for (ScanResult scanResult : scanResults) {
								//添加是否的判断（对于多个SSID的处理）
								String [] ssidResult = sSid.split(",");
								for(int j=0;j<ssidResult.length;j++){
									if (scanResult.SSID.equalsIgnoreCase(ssidResult[j])) {
										System.out.println(scanResult.SSID + "---bssid---"+ scanResult.BSSID+"--rssi--"+scanResult.level);
										//this.publishProgress(scanResult.SSID,scanResult.BSSID, scanResult.level + "",url);
										// 导入要传的中间类
										LocationVoRequestBean locationVo = new LocationVoRequestBean();
										// 插入时间
										java.util.Date date = new java.util.Date();
										// java.sql.Date data1=new java.sql.Date(date.getTime());
										locationVo.setCollTime(DateUtils.formatDate(date,"yyyy-MM-dd HH:mm:ss"));
										// mac station mac地址  在这里获取Stationmac
										locationVo.setStaMac(info.getMacAddress());										
										// 插入信号强度
										// 在SSID下面下面的信号强度
										locationVo.setRSSI(scanResult.level);
										// 插入ssid
										locationVo.setESSID(sSid);
										// 插入apmac
										locationVo.setApMac(scanResult.BSSID);
										LocationVolist.add(locationVo);
									}
								}
							}
							LocationVoRequestBean[] array=new LocationVoRequestBean[LocationVolist.size()];
							LocationVolist.toArray(array);
//							this.publishProgress(array);
							
							StringBuffer reBuffer=new StringBuffer();
							JSONObject jsonReqObj = new JSONObject();
							
							JSONArray jsonArray=new JSONArray();
							JSONObject jsonObject=null;
							for (int k = 0; k < array.length; k++) {
								System.out.println(array[k]);
								jsonObject=new JSONObject();
								try {
									jsonObject.put("apMac",array[k].getApMac() == null ? "" :array[k].getApMac());
									jsonObject.put("collTime",array[k].getCollTime() == null ? "" :array[k].getCollTime());
									jsonObject.put("staMac",array[k].getStaMac() == null ? "" :array[k].getStaMac());
									jsonObject.put("RSSI",array[k].getRSSI()+"" == null ? "" :array[k].getRSSI());
									jsonObject.put("TOA",array[k].getTOA()+"" == null ? "" :array[k].getTOA());
									jsonObject.put("rate",array[k].getRate()+"" == null ? "" :array[k].getRate());
									jsonObject.put("ESSID",array[k].getESSID()== null ? "" :array[k].getESSID());
									jsonObject.put("mode",array[k].getMode() == null ? "" :array[k].getMode());
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								jsonArray.put(jsonObject);
							}
							//reBuffer.insert(0, "[").append("]");
							try {
								jsonReqObj.put("resultLocation", jsonArray);
								
							} catch (JSONException e) {
								e.printStackTrace();
							}
							String result=jsonReqObj.toString();
							System.out.println("resultLocation-->"+result);
							JSONUtil.setServicePostionByHttp(url);
							JSONUtil jsonUtil = new JSONUtil();
							jsonUtil.reportLocationRSSIListString(result);
						}
						//添加提示信息
//						Toast.makeText(LocationUpdateActivity.this,	R.string.upload_success, Toast.LENGTH_SHORT).show();
					}
				}
			});
	//开启线程
		t.start();
		}
		
	@Override
	public boolean onUnbind(Intent intent) {
		Log.e(TAG, "start onUnbind~~~");
		return super.onUnbind(intent);
	}
	
	public class MyBinder extends Binder{
		public RtRepService getService()
		{
			return RtRepService.this;
		}
	}

}
