package com.asc.app.service;

import java.util.List;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.asc.app.R;
import com.asc.app.util.Summary;

public class WifiConnService {
	private WifiManager mWifiManager;

	public static final int SECURITY_NONE = 0;
	public static final int SECURITY_WEP = 1;
	public static final int SECURITY_PSK = 2;
	public static final int SECURITY_EAP = 3;

	private static  WifiConnService wifiTool = null;

	private Context context = null;

	private WifiConnService(Context context) {
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		this.context = context;
	};

	public static  WifiConnService getInstance(Context context) {
//		if (null == wifiTool) {
			wifiTool = new WifiConnService(context);
//		}
		return wifiTool;
	}

	public boolean isWifiEnabled() {
		return mWifiManager.isWifiEnabled();
	}

	public boolean isWifiOpened() {
		return mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED
				|| mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING;
	}

	public void enableWifi() {
		if (!this.isWifiOpened()) {
			mWifiManager.setWifiEnabled(true);
			mWifiManager.startScan();
		}
	}

	public void disableWifi() {
		if (this.isWifiOpened()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	public WifiConfiguration findConfiguredNetwork(String SSID) {
		List<WifiConfiguration> configList = mWifiManager
				.getConfiguredNetworks();
		for (WifiConfiguration config : configList) {
			if (config.SSID.contains(SSID)) {
				Log.e("findConfiguredNetwork: ", "SSID: " + SSID);
				return config;
			}
		}
		return null;
	}

	public int getNetworkId(ScanResult scanResult, List<View> inputViews) {
		if (null == scanResult) {
			return 0;
		}
		Log.d("AuthParam", "添加 " + scanResult.SSID);
		WifiConfiguration wifiConfig = findConfiguredNetwork(scanResult.SSID);
		if (null != wifiConfig) { // 如果SSID已在配置网络列表中，则返回networkId
			return wifiConfig.networkId;
		}

		if (inputViews == null) { // 忘记网络操作
			return 0;
		}

		WifiConfiguration config = new WifiConfiguration(); //
		// 如果SSID未配置过，则创建并加入配置列表中
		config.networkId = 1;
		config.SSID = "\"" + scanResult.SSID + "\"";
		config.hiddenSSID = false;

		switch (getSecurity(scanResult)) {
		case WifiConnService.SECURITY_NONE:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			break;
		case WifiConnService.SECURITY_WEP:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
			EditText mPassword = (EditText) inputViews.get(0);
			if (mPassword.length() != 0) {
				int length = mPassword.length();
				String password = mPassword.getText().toString();
				// WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
				if ((length == 10 || length == 26 || length == 58)
						&& password.matches("[0-9A-Fa-f]*")) {
					config.wepKeys[0] = password;
				} else {
					config.wepKeys[0] = '"' + password + '"';
				}
			}
			break;
		case WifiConnService.SECURITY_PSK:
			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			EditText pwd_psk = (EditText) inputViews.get(0);
			if (pwd_psk.length() != 0) {
				String password = pwd_psk.getText().toString();
				if (password.matches("[0-9A-Fa-f]{64}")) {
					config.preSharedKey = password;
				} else {
					config.preSharedKey = '"' + password + '"';
				}
			}
			break;
		case WifiConnService.SECURITY_EAP:
			config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
			config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);

			Button eap_method = (Button) inputViews.get(0);
			Button phase2 = (Button) inputViews.get(1);
			Button ca_cert = (Button) inputViews.get(2);
			Button client_cert = (Button) inputViews.get(3);
			EditText identity_txt = (EditText) inputViews.get(4);
			EditText anonymous_identity_txt = (EditText) inputViews.get(5);
			EditText private_key_txt = (EditText) inputViews.get(6);

			config.eap.setValue(eap_method.getText().toString());
			if (!phase2.getText().toString().equals("无")) {
				config.phase2.setValue(phase2.getText().toString());
			}
			if (!ca_cert.getText().toString().equals("不明确的")) {
				config.ca_cert.setValue(ca_cert.getText().toString());
			}
			if (!client_cert.getText().toString().equals("不明确的")) {
				config.client_cert.setValue(client_cert.getText().toString());
			}
			if (!identity_txt.getText().toString().trim().equals("")) {
				config.identity.setValue(identity_txt.getText().toString());
			}
			if (!anonymous_identity_txt.getText().toString().trim().equals("")) {
				config.anonymous_identity.setValue(anonymous_identity_txt
						.getText().toString());
			}
			if (!private_key_txt.getText().toString().trim().equals("")) {
				config.private_key.setValue(private_key_txt.getText()
						.toString());
			}
			break;
		default:
			break;
		}
		Log.d("AuthParam", config.toString());
		int networkId = mWifiManager.addNetwork(config);
		return networkId;
	}

	public void connect(int netId) {
		if (-1 != netId) {
			mWifiManager.enableNetwork(netId, true);
		}
	}

	/**
	 * 返回认证类型
	 * 
	 * @param result
	 * @return
	 */
	public int getSecurity(ScanResult result) {
		if (result.capabilities.contains("WEP")) {
			return SECURITY_WEP;
		} else if (result.capabilities.contains("PSK")) {
			return SECURITY_PSK;
		} else if (result.capabilities.contains("EAP")) {
			return SECURITY_EAP;
		}
		return SECURITY_NONE;
	}

	/**
	 * 返回认证方式
	 * 
	 * @param capabilities
	 * @return
	 */
	public int getAuthString(String capabilities) {
		if (capabilities.contains("WEP")) {
			return R.string.wifi_wep;
		} else if (capabilities.contains("PSK")) {
			return R.string.wifi_psk;
		} else if (capabilities.contains("EAP")) {
			return R.string.wifi_eap;
		} else {
			return R.string.wifi_open;
		}
	}

	/**
	 * 根据认证方式和信号强度显示信号图片
	 * 
	 * @param isLocked
	 * @param level
	 * @return
	 */
	// public int getSignalPic(boolean isLocked, int level) {
	// switch (level) {
	// case 0:
	// return isLocked ? R.drawable.ic_wifi_lock_signal_1 :
	// R.drawable.ic_wifi_signal_1;
	// case 1:
	// return isLocked ? R.drawable.ic_wifi_lock_signal_2 :
	// R.drawable.ic_wifi_signal_2;
	// case 2:
	// return isLocked ? R.drawable.ic_wifi_lock_signal_3 :
	// R.drawable.ic_wifi_signal_3;
	// case 3:
	// return isLocked ? R.drawable.ic_wifi_lock_signal_4 :
	// R.drawable.ic_wifi_signal_4;
	// default:
	// return isLocked ? R.drawable.ic_wifi_lock_signal_1 :
	// R.drawable.ic_wifi_signal_1;
	// }
	// }

	public boolean isConfiged(String ssid) {
		List<WifiConfiguration> configs = wifiTool.getConfigs();
		StringBuffer ssids = new StringBuffer();
		for (WifiConfiguration config : configs) {
			ssids.append(config.SSID);
			ssids.append(",");
		}
		return ssids.toString().contains(ssid);
	}

	public boolean isConnected(String ssid) {
		if (null != mWifiManager.getConnectionInfo().getSSID()) {
			return mWifiManager.getConnectionInfo().getSSID().contains(ssid);
		}
		return false;
	}

	public boolean disconnnect() {
		return mWifiManager.disconnect();
	}

	public boolean disableNetwork(int netId) {
		mWifiManager.removeNetwork(netId);
		return mWifiManager.saveConfiguration();
	}

	public WifiInfo getConnInfo() {
		if (mWifiManager.getConnectionInfo().getNetworkId() == -1) {
			return null;
		} else {
			return mWifiManager.getConnectionInfo();
		}
	}

	public int getWifiState() {
		return mWifiManager.getWifiState();
	}

	public List<ScanResult> getResults() {
		return mWifiManager.getScanResults();
	}
	public void startWifiManager(){
		if(mWifiManager!=null){
			mWifiManager.startScan();
		}
		
	}
	
	public void stopWifiManager(){
		if(mWifiManager!=null){
		//	mWifiManager.d();
		}
		
	}
	

	public List<WifiConfiguration> getConfigs() {
		return mWifiManager.getConfiguredNetworks();
	}

	public WifiInfo getCurrentConnInfo() {
		return mWifiManager.getConnectionInfo();
	}

	public String getWifiLevelString(ScanResult scanResult) {
		String level = "";
		switch (wifiTool.getLevel(scanResult.level, 4)) {
		case 0:
			level = context.getString(R.string.wifi_level_1);
			break;
		case 1:
			level = context.getString(R.string.wifi_level_2);
			break;
		case 2:
			level = context.getString(R.string.wifi_level_3);
			break;
		case 3:
			level = context.getString(R.string.wifi_level_4);
			break;
		default:
			break;
		}
		return level;
	}

	public int getLevel(int mRssi, int numLevels) {
		if (mRssi == Integer.MAX_VALUE) {
			return -1;
		}
		return WifiManager.calculateSignalLevel(mRssi, numLevels);
	}

	public String getLevelString(int level) {
		String levelString = "";
		switch (level) {
		case 0:
			levelString = context.getString(R.string.wifi_level_1);
			break;
		case 1:
			levelString = context.getString(R.string.wifi_level_2);
			break;
		case 2:
			levelString = context.getString(R.string.wifi_level_3);
			break;
		case 3:
			levelString = context.getString(R.string.wifi_level_4);
			break;
		default:
			break;
		}
		return levelString;
	}

	/**
	 * 处理网络连接改变
	 * 
	 * @param networkInfo
	 */
	public String handleNetworkStateChanged(NetworkInfo networkInfo) {
		if (wifiTool.isWifiEnabled() && wifiTool.getConnInfo() != null) { // 显示当前AP连接状态
			return Summary.get(context, wifiTool.getConnInfo().getSSID(), networkInfo.getDetailedState());
		}
		return context.getString(R.string.wifi_disconnected);
	}

	public String handleWifiStateChanged(int wifiState) {
		String wifiStateText;
		switch (wifiState) {
		case WifiManager.WIFI_STATE_ENABLING:
			wifiStateText = context.getString(R.string.wifi_state_enabling);
			break;
		case WifiManager.WIFI_STATE_DISABLING:
			wifiStateText = context.getString(R.string.wifi_state_disabling);
			break;
		case WifiManager.WIFI_STATE_ENABLED:
			wifiStateText = context.getString(R.string.wifi_state_enabled);
			break;
		case WifiManager.WIFI_STATE_DISABLED:
			wifiStateText = context.getString(R.string.wifi_state_disabled);
			break;
		case WifiManager.WIFI_STATE_UNKNOWN:
			wifiStateText = context.getString(R.string.wifi_state_unknown);
			break;
		default:
			wifiStateText = "BAD";
			break;
		}
		return wifiStateText;
	}
}
