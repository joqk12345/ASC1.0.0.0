package com.asc.app.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import com.asc.app.database.DBManager;

public class TrafficService extends Service{
	private int timeout_period;
	private TrafficThread trafficThread;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		trafficThread = new TrafficThread();
		if (!trafficThread.isAlive()) {
			trafficThread.start();
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String timeout_periodString = this.getSharedPreferences("com.asc_preferences", Context.MODE_PRIVATE).getString("traffic_timeout_period", "30");
		timeout_period = Integer.parseInt(timeout_periodString);
		Log.e("TrafficService", "service is started...");
		return super.onStartCommand(intent, flags, startId);
	}
	
	class TrafficThread extends Thread {
		private long phoneRx;
		private long phoneTx;
		private long wifiRx;
		private long wifiTx;
		private boolean isRunning = true;
		
		public boolean isRunning() {
			return isRunning;
		}
		public void setRunning(boolean isRunning) {
			this.isRunning = isRunning;
		}

		@Override
		public void run() {
			while (isRunning()) {
				try {
					phoneRx = TrafficStats.getMobileRxBytes() == TrafficStats.UNSUPPORTED ? 0 : TrafficStats.getMobileRxBytes();
					phoneTx = TrafficStats.getMobileTxBytes() == TrafficStats.UNSUPPORTED ? 0 : TrafficStats.getMobileTxBytes();
					wifiRx = TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() - phoneRx);
					wifiTx = TrafficStats.getTotalTxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalTxBytes() - phoneTx);
					if (timeout_period <= 0) {
						timeout_period = 10;
					}
					
					Log.e("new timeout_period value: ", timeout_period +"");
					
					Thread.sleep(timeout_period * 60 * 1000);
					
					long newPhoneRx = TrafficStats.getMobileRxBytes() == TrafficStats.UNSUPPORTED ? 0 : TrafficStats.getMobileRxBytes();
					long newPhoneTx = TrafficStats.getMobileTxBytes() == TrafficStats.UNSUPPORTED ? 0 : TrafficStats.getMobileTxBytes();
					long newWifiRx = TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() - newPhoneRx);
					long newWifiTx = TrafficStats.getTotalTxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalTxBytes() - newPhoneTx);
					
					Log.e("wifi received: ", newWifiRx - wifiRx + "");
					Log.e("wifi transmit: ", newWifiTx - wifiTx + "");
					if ((newWifiRx - wifiRx) < 100 || newWifiTx - wifiTx < 1024) {
						WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
						wifiManager.setWifiEnabled(false);
						Log.e("TrafficService", "wifi is disabled!!!!!!!!!!! current id: " + this.getId());
					}
				} catch (InterruptedException e) {
					Log.e("TrafficService", isRunning() +" Thread is interrupted! id: " + this.getId());
				}
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		trafficThread.setRunning(false);
		trafficThread.interrupt();
	}
	
	public static void insertOrUpdateTraffic(String trafficType, Context cxt, long newTraffic){
		DBManager dbManager = DBManager.getInstance(cxt);
		dbManager.open();
		Date nowDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String nowDateString = dateFormat.format(nowDate);
		// 手机流量记录
		if (trafficType.equals("mobile")) {
			Cursor cursor = dbManager.executeSqlQuery("select id,phoneTraffic from traffic_daily where createDate = ?", new String[] { nowDateString });
			// 有今天的手机流量记录
			if (cursor.moveToFirst()) {
				int id = cursor.getInt(0);
				long traffic = cursor.getLong(1);
				traffic = traffic + newTraffic;
				dbManager.executeSql("update traffic_daily set phoneTraffic = " + traffic +" where id = " + id);
			}
			// 无今天的手机流量记录
			else {
				ContentValues contentValues = new ContentValues();
				contentValues.put("phoneTraffic", newTraffic);
				contentValues.put("createDate", nowDateString);
				try {
					dbManager.insert("traffic_daily", "id", contentValues);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// WIFI流量记录
		else {
			Cursor cursor = dbManager.executeSqlQuery("select id,wifiTraffic from traffic_daily where createDate = ?", new String[] { nowDateString });
			// 有今天的WIFI流量记录
			if (cursor.moveToFirst()) {
				int id = cursor.getInt(0);
				long traffic = cursor.getLong(1);
				traffic = traffic + newTraffic;
				dbManager.executeSql("update traffic_daily set wifiTraffic = " + traffic +" where id = " + id);
			}
			// 无今天的WIFI流量记录
			else {
				ContentValues contentValues = new ContentValues();
				contentValues.put("wifiTraffic", newTraffic);
				contentValues.put("createDate", nowDateString);
				try {
					dbManager.insert("traffic_daily", "id", contentValues);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		dbManager.close();
	}

}
