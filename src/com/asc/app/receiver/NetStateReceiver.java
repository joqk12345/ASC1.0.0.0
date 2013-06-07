package com.asc.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.asc.app.service.AdvertiseService;
import com.asc.app.service.SoftwareTrafficService;
import com.asc.app.service.TrafficService;

/**
 * @author zhanglei
 *
 */
public class NetStateReceiver extends BroadcastReceiver {
	private static String latestNetType = "";
	
	private void recordWifiTraffic(Context cxt){
		if (latestNetType.equals("wifi")) {
			Log.e("wifi is disconnected","wifi: "+ (TrafficStats.getTotalRxBytes() - TrafficStats.getMobileRxBytes()) + "  " + (TrafficStats.getTotalTxBytes() - TrafficStats.getMobileTxBytes()));
			latestNetType = "";
			long newTraffic = (TrafficStats.getTotalRxBytes() - TrafficStats.getMobileRxBytes()) + (TrafficStats.getTotalTxBytes() - TrafficStats.getMobileTxBytes());
			newTraffic = newTraffic / 1024;
			TrafficService.insertOrUpdateTraffic("wifi", cxt, newTraffic);
		}
		cxt.stopService(new Intent(cxt, TrafficService.class));
	}
	
    private void recordMobileTraffic(Context cxt){
    	if (latestNetType.equals("mobile")) {
			Log.e("mobile is closed", "mobile: " + TrafficStats.getMobileRxBytes() + "  " + TrafficStats.getMobileTxBytes());
			latestNetType = "";
			long newTraffic = TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes();
			newTraffic = newTraffic / 1024;
			TrafficService.insertOrUpdateTraffic("mobile", cxt, newTraffic);
		}
	}
	
	@Override
	public void onReceive(Context cxt, Intent intent) {
		SharedPreferences preferences = cxt.getSharedPreferences("com.asc_preferences", Context.MODE_PRIVATE);
		
		boolean traffic_switch = preferences.getBoolean("traffic_monitor_switch", true);
		
		if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)&& traffic_switch) {
			int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,WifiManager.WIFI_STATE_UNKNOWN);
			switch (state) {
			case WifiManager.WIFI_STATE_DISABLING:
				recordWifiTraffic(cxt);
				break;
			case WifiManager.WIFI_STATE_DISABLED:
				recordWifiTraffic(cxt);
				break;
			default:
				break;
			}
		}
		// WIFI连接状态变化
		if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			NetworkInfo networkInfo = (NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			// WIFI已连接上
			if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {                 
				boolean advertiseSwitch = preferences.getBoolean("ad_switch", true);             
				// 如果开启商场信息推送开关
				if (advertiseSwitch) {                                                               
					Intent adIntent = new Intent(cxt, AdvertiseService.class);
//					adIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					cxt.startService(adIntent);
				}
				// 如果开启流量监控开关
				if (traffic_switch) {
					// 如果上次连接为mobile
					recordMobileTraffic(cxt);
					latestNetType = "wifi";
					
					Intent trafficIntent = new Intent(cxt, TrafficService.class);
//					trafficIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					cxt.startService(trafficIntent);
				}
				
			} 
			// WIFI连接正在关闭
			if (networkInfo.getState().equals(NetworkInfo.State.DISCONNECTED) && traffic_switch) {
				recordWifiTraffic(cxt);
			}
		}
		// GPRS连接状态变化
		else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			boolean wlan_auto_switch = preferences.getBoolean("wlan_auto_switch", false);
			ConnectivityManager connectivityManager = (ConnectivityManager) cxt.getSystemService( Context.CONNECTIVITY_SERVICE );
			NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
			if (null == activeNetworkInfo || activeNetworkInfo.getExtraInfo() == null) {  // 没有任何GPRS连接
				if (traffic_switch) {
					recordMobileTraffic(cxt);
				}
				return;
			}
			// WLAN自控制开关
			if (wlan_auto_switch) {                                                         
				String netTypeName = activeNetworkInfo.getExtraInfo();
				if (netTypeName.equals("cmnet") || !netTypeName.equals("WIFI")) {
					WifiManager wifiManager = (WifiManager) cxt.getSystemService(Context.WIFI_SERVICE);
					wifiManager.setWifiEnabled(true);
				}
			}
			// 流量统计开关
			if (traffic_switch) {     
				recordWifiTraffic(cxt);
				latestNetType = activeNetworkInfo.getTypeName().toLowerCase();
			}
		}
		else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
			SoftwareTrafficService.getInstance(cxt);
		}
	}
}