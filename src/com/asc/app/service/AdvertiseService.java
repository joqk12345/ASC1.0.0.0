package com.asc.app.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.asc.app.R;
import com.asc.app.bean.request.AdRequestBean;
import com.asc.app.bean.request.DeviceInfoRequestBean;
import com.asc.app.bean.request.LocationRequestBean;
import com.asc.app.bean.response.GetAdvertiseResult;
import com.asc.app.bean.response.OperateResult;
import com.asc.app.database.DBManager;
import com.asc.app.json.JSONUtil;
import com.asc.app.ui.ADActivity;
import com.asc.app.util.IpUtil;
import com.asc.app.util.UpdateUtil;

/**
 * @author zhanglei
 *
 */
@SuppressLint({ "NewApi", "NewApi" })
public class AdvertiseService extends Service{
	private static String LOG_TAG = "AdvertiseService";
	private String adUrl;
	private String bssid;
	private String essid;
	private LocationManager locationManager;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		adUrl = "";
		
		WifiManager mWifiManager = (WifiManager) AdvertiseService.this.getSystemService(Context.WIFI_SERVICE);
		
	    final WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		if (null == wifiInfo || wifiInfo.getBSSID() == null) {
			return super.onStartCommand(intent, flags, startId); 
		}
		bssid = wifiInfo.getBSSID().toUpperCase(); //"00:1F:64:E0:00:B8";
		essid = wifiInfo.getSSID();

		String gateWay = IpUtil.intToIp(mWifiManager.getDhcpInfo().gateway);
		String dhcpServer = IpUtil.intToIp(mWifiManager.getDhcpInfo().serverAddress);
//		WebServiceUtil.setGateWay(gateWay);
		Log.e(LOG_TAG, bssid);
		Log.e(LOG_TAG, essid);
		Log.e(LOG_TAG, gateWay);
		Log.e(LOG_TAG, dhcpServer);
		JSONUtil.setServicePostion(dhcpServer);
		
		SharedPreferences preferences = AdvertiseService.this.getSharedPreferences("com.asc_preferences", Context.MODE_PRIVATE);
		if (!preferences.contains("isLocationReported")) {
			registerLocationListener();
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				SharedPreferences preferences = AdvertiseService.this.getSharedPreferences("com.asc_preferences", Context.MODE_PRIVATE);
				if (!preferences.contains("isDeviceInfoReported")) {
					Editor editor = preferences.edit();
					editor.putBoolean("isDeviceInfoReported", true);
					editor.commit();
					
//					WifiManager mWifiManager = (WifiManager) AdvertiseService.this.getSystemService(Context.WIFI_SERVICE);
					String staMac = wifiInfo.getMacAddress();
					
					DeviceInfoRequestBean reportRequest = new DeviceInfoRequestBean();
					reportRequest.setOperateID(UpdateUtil.generateId());
					reportRequest.setMethod("reportDeviceInfo");
					reportRequest.setDeviceManufacturer(Build.MANUFACTURER);
					reportRequest.setDeviceModel(Build.MODEL);
					reportRequest.setOsVersion("Android " + Build.VERSION.RELEASE);
					reportRequest.setStaMac(staMac);
					reportRequest.setStaType("mobile");
					
					JSONUtil jsonUtil = new JSONUtil();
					OperateResult result = jsonUtil.reportDeviceInfo(reportRequest);
					if (null == result) {
						return;
					}
					if (result.getResultCode() == 1) {
						Log.e(LOG_TAG, result.getErrorInfo());
					}
				}
			}
		}).start();
		                                                      
		new Thread(new Runnable() {                                                     
			
			public void run() {
				
				AdRequestBean advertiseRequest = new AdRequestBean();
				advertiseRequest.setOperateID(UpdateUtil.generateId());
				advertiseRequest.setMethod("getAdvertise");
				advertiseRequest.setBssid(bssid);
				advertiseRequest.setEssid(essid);
				advertiseRequest.setArea("");
				
				JSONUtil jsonUtil = new JSONUtil();
				GetAdvertiseResult adResult = jsonUtil.getAdvertise(advertiseRequest);
				if (null == adResult) {
					return;
				}
				adUrl = adResult.getAdvertiseUrl();
					
					if (adResult.getResultCode() == 0) {
						DBManager dbManager = DBManager.getInstance(AdvertiseService.this);
						dbManager.open();
						
						try {
//							String sql = "select id from ad_record where (select adUrl from ad_record where id = "
//											+ "(select max(id) from ad_record)) = '" + adUrl + "'";
//							Cursor cursor = dbManager.executeSqlQuery(sql, null);
//							cursor.moveToFirst();
//							if (cursor.getCount() > 0) {                                                   
//								Log.e(LOG_TAG, "advertise has exists...  ");
//								adUrl = "";
//								return;
//							}
							String deleteSql = "delete from ad_record where adUrl = '"+ adUrl +"'";
							dbManager.executeSql(deleteSql);   
							
							ContentValues contentValues = new ContentValues();
							contentValues.put("adUrl", adUrl);
							contentValues.put("bssid", bssid);
							contentValues.put("adInfo", adResult.getAdvertiseInfo());
							contentValues.put("location", adResult.getLocation());
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
							contentValues.put("createDate", dateFormat.format(new Date()));
							
							dbManager.insert("ad_record", "id", contentValues); 
							
							NotificationManager notifyManager = (NotificationManager)AdvertiseService.this.getSystemService(Context.NOTIFICATION_SERVICE);
							Notification notify = new Notification(R.drawable.ic_launcher, getString(R.string.you_have_new_message), System.currentTimeMillis());  
							notify.flags = Notification.FLAG_ONGOING_EVENT; 
							notify.defaults = Notification.DEFAULT_VIBRATE;
							
							Intent intentAD = new Intent(AdvertiseService.this, ADActivity.class);
							intentAD.putExtra("adUrl", adUrl);
							intentAD.putExtra("location", adResult.getLocation());
							intentAD.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
							
							PendingIntent contentIntent = PendingIntent.getActivity(AdvertiseService.this, 0, intentAD, PendingIntent.FLAG_UPDATE_CURRENT);
							notify.setLatestEventInfo(AdvertiseService.this, "Autelan Smart Client", getString(R.string.you_have_new_message), contentIntent);
							notifyManager.notify(R.string.app_name, notify);                
						} catch (Exception e) {
							Log.e(LOG_TAG, e.getMessage());
							e.printStackTrace();
						} finally {
							dbManager.close();
						}
					}
					else {
						Log.e(LOG_TAG, adResult.getErrorInfo());
					}
			}
		}).start();
		
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	private class MyLocationListner implements LocationListener{

		public void onLocationChanged(Location location) {
			new Thread(){
				public void run() {
					
				};
			}.start();
			Log.e("GPSTEST", "Got New Location of provider:" + location.getProvider());
			//纬度
			Log.e("GPSTEST", "Latitude:" + location.getLatitude());
//			latitude.setText("纬度："+String.valueOf(location.getLatitude()));
			//经度
			Log.e("GPSTEST", "Longitude:" + location.getLongitude());
//			longtitude.setText("经度："+String.valueOf(location.getLongitude()));
			//精确度
			Log.e("GPSTEST", "Accuracy:" + location.getAccuracy());
//			longtitude.setText("精确度："+String.valueOf(location.getAccuracy()));
			
			SharedPreferences preferences = AdvertiseService.this.getSharedPreferences("com.asc_preferences", Context.MODE_PRIVATE);
			if (!preferences.contains("isLocationReported")) {
				Editor editor = preferences.edit();
				editor.putBoolean("isLocationReported", true);
				editor.commit();
				Log.e(LOG_TAG, "=============Location===============");
				LocationRequestBean locationRequest = new LocationRequestBean();
				locationRequest.setMethod("reportLocation");
				locationRequest.setOperateID(UpdateUtil.generateId());
				locationRequest.setSsid(essid);
				locationRequest.setLatitude(location.getLatitude());
				locationRequest.setLongitude(location.getLongitude());
				locationRequest.setAccuracy(location.getAccuracy());
				
				JSONUtil jsonUtil = new JSONUtil();
				OperateResult result = jsonUtil.reportLocation(locationRequest);
				if (null == result) {
					return;
				}
				if (result.getResultCode() == 1) {
					Log.e(LOG_TAG, result.getErrorInfo());
				}
			}
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.e("GPSTEST", "onStatusChanged: " + provider);
		}

		public void onProviderEnabled(String provider) {
			Log.e("GPSTEST", "onProviderEnabled: " + provider);
		}

		public void onProviderDisabled(String provider) {
			Log.e("GPSTEST", "onProviderDisabled: " + provider);
		}
	}
	
	private LocationListener gpsListener = null;
	private LocationListener networkListner=null;
	
	private void registerLocationListener() {
		if (null == locationManager) {
			locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		}
		networkListner = new MyLocationListner();
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, networkListner);
		
		gpsListener = new MyLocationListner();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, gpsListener);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (null != locationManager) {
			locationManager.removeUpdates(networkListner);
			locationManager.removeUpdates(gpsListener);
		}
	}
	
}
