package com.asc.app.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.asc.app.R;
import com.asc.app.service.WifiConnService;
import com.asc.app.util.IpUtil;

public class AccessPointActivity extends Activity {
	private WifiConnService wifiConnService;
	private IntentFilter mFilter;
	
	private List<Map<String, Object>> itemList = new ArrayList<Map<String,Object>>();
	private Map<String, ScanResult> resultMap = new HashMap<String, ScanResult>();   //SSID 和 扫描结果的映射关系
	private SimpleAdapter adapter;
	
	private ListView apList;
	private TextView wlanState;
	private ToggleButton wlanSwitch;
	
	private List<View> inputViews = new ArrayList<View>();	
	private Button eap_method_btn;
	private Button phase2_btn;
	private Button ca_cert_btn;
	private Button client_cert_btn;
	
	private final int EAP_METHOD_DIALOG = 1;  
	private final int PHASE2_DIALOG = 2;
	private final int CA_CERT_DIALOG = 3;
	private final int CLIENT_CERT_DIALOG = 4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ap_list);
		
		wifiConnService = WifiConnService.getInstance(this);
		wlanState = (TextView) findViewById(R.id.wlan_state);
		wlanSwitch = (ToggleButton) findViewById(R.id.wlan_switch);
		apList = (ListView) findViewById(R.id.ap_list);
		//如果wifi服务开启直接就填充listView
		if (wifiConnService.isWifiEnabled()) {
			setItemList(wifiConnService.getResults());
		}
		
		adapter = new SimpleAdapter(this, itemList, R.layout.access_point,
				new String[] { "SSID", "signal", "capabilities", "status" },
				new int[] { R.id.ssid, R.id.signal, R.id.capabilities, R.id.status });
		apList.setAdapter(adapter);
		
		// 选中AP时根据认证类型，弹出窗口
		apList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Map<String, Object> map = (Map<String, Object>) apList.getItemAtPosition(position);
				showAlertDialog(AccessPointActivity.this, map);
			}
		}) ;
		apList.setCacheColorHint(0);
		
		// 定义WIFI状态的Receiver过滤器
		mFilter = new IntentFilter();
        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        
        wlanSwitch.setChecked(wifiConnService.isWifiEnabled());
        wlanSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					Toast.makeText(AccessPointActivity.this, getString(R.string.open_wifi_waiting), Toast.LENGTH_LONG).show();
					wifiConnService.enableWifi();
					setItemList(wifiConnService.getResults());
				}else {
					Toast.makeText(AccessPointActivity.this, getString(R.string.close_wifi_waiting), Toast.LENGTH_LONG).show();
					wifiConnService.disableWifi();
					setItemList(null);
				}
			}
		});
        
        Button return_btn = (Button) findViewById(R.id.return_btn);
        return_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	public List<Map<String, Object>> getItemList() {
		return itemList;
	}

	public void setItemList(List<ScanResult> scanResults) {
		
		itemList.clear();
		resultMap.clear();
		
		Map<String, Object> map;
		WifiInfo wifiInfo = wifiConnService.getCurrentConnInfo();
		
		if (null == scanResults) {
			if (null != adapter) {
				adapter.notifyDataSetChanged();
			}
			return;
		}
		
		for(ScanResult apInfo : scanResults){
			if (resultMap.containsKey(apInfo.SSID)) {
				continue;
			}
			
			Log.e("AccessPointActiviy", "ssid: " + apInfo.SSID +" ap bssid: " + apInfo.BSSID);
			
			String auth=null;
			
			map = new HashMap<String, Object>();
			map.put("SSID", apInfo.SSID);
			
			auth = getString(wifiConnService.getAuthString(apInfo.capabilities));
		    map.put("capabilities", auth);     // 设置验证方式
			
//		    boolean isLocked = !(map.get("capabilities").equals(getString(R.string.wifi_open)));
			map.put("signal", wifiConnService.getLevelString(wifiConnService.getLevel(apInfo.level, 4)));
			map.put("status", " ");
			map.put("sortLevel", wifiConnService.getLevel(apInfo.level, 4));
			
			if (wifiConnService.isConfiged(apInfo.SSID)) {   // 判断是否配置过
				map.remove("status");
				map.put("status", getString(R.string.wifi_configed));
			}
			
			if (null != wifiInfo.getSSID() && wifiInfo.getSSID().equals(apInfo.SSID)) {  // 判断是否已连接
				map.remove("status");
				map.put("status", getString(R.string.wifi_connected));
				map.put("sortLevel", 5);
			}
			
			itemList.add(map);
			
			Collections.sort(itemList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> map1, Map<String, Object> map2) {
					return (Integer) map2.get("sortLevel") - (Integer) map1.get("sortLevel");
				}
			});
			
			resultMap.put(apInfo.SSID, apInfo);
		}
		if (null != adapter) {
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
		wlanState = (TextView) findViewById(R.id.wlan_state);
		
		registerReceiver(receiver, mFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		unregisterReceiver(receiver);
	}
	
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			
			if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				setItemList(wifiConnService.getResults());
			}
			if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
				String wifiState = wifiConnService.handleWifiStateChanged(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)); //处理WIFI开关状态改变
				wlanState.setText(wifiState);
			}
			if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				String networkState = wifiConnService.handleNetworkStateChanged((NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)); // 处理网络连接状态改变
				wlanState.setText(networkState);
			}
			
		}
	};
	
	private void showAlertDialog(Context context, final Map<String, Object> itemMap){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		ScanResult scanResult = resultMap.get(itemMap.get("SSID"));
		
		//原有网络中是否已经存在原来的网络配置
		if (wifiConnService.isConfiged(scanResult.SSID)) {
			builder.setNeutralButton("忘记", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					ScanResult result = resultMap.get(itemMap.get("SSID"));
					int networkId = wifiConnService.getNetworkId(result, null);
					boolean disabled = wifiConnService.disableNetwork(networkId);
					
					setItemList(wifiConnService.getResults());
					
					Log.e("disableNetwork", "networkId: "+ networkId +" result: " + disabled);
				}
			});
			
			//当前ssid 连接时候的处理
			if (wifiConnService.isConnected(scanResult.SSID)) {
				View connectedView = LayoutInflater.from(this).inflate(R.layout.ap_connected, null);
				ViewGroup group = (ViewGroup) connectedView.findViewById(R.id.ap_info);
				
				addRow(group, R.string.wifi_security, getString(wifiConnService.getAuthString(scanResult.capabilities)));
				addRow(group, R.string.wifi_signal, wifiConnService.getWifiLevelString(scanResult));
				addRow(group, R.string.wifi_speed, wifiConnService.getConnInfo().getLinkSpeed() + "Mbps");
				addRow(group, R.string.wifi_ip, IpUtil.intToIp(wifiConnService.getConnInfo().getIpAddress()));
				
				builder.setView(connectedView);
			}
		}else {
			View authView = showSecurityFields(scanResult);  // 根据不同的认证类型，显示用户接口
			
			builder.setView(authView);
		}
		
		builder.setIcon(R.drawable.menu);
		builder.setTitle((String)itemMap.get("SSID"));
		//当前ssid没有连接时候处理 
		if (!wifiConnService.isConnected(scanResult.SSID)) {
			builder.setPositiveButton("连接", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ScanResult result = resultMap.get(itemMap.get("SSID"));
					int networkId = wifiConnService.getNetworkId(result, inputViews);
					
					wifiConnService.connect(networkId);
					setItemList(wifiConnService.getResults());
				}
			});
		}
		
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.show();
	}
	
	 private View showSecurityFields(ScanResult result) {
		 View authView = LayoutInflater.from(this).inflate(R.layout.ap_dialog, null);
		 
		 ViewGroup group = (ViewGroup) authView.findViewById(R.id.info);
		 addRow(group, R.string.wifi_security, getString(wifiConnService.getAuthString(result.capabilities)));
		 addRow(group, R.string.wifi_signal, wifiConnService.getWifiLevelString(result));
		 
		 switch (wifiConnService.getSecurity(result)) {
			case WifiConnService.SECURITY_NONE:
				break;
			case WifiConnService.SECURITY_WEP:
				inputViews.clear();
				//如果是wep认证的话设置wep text 可以显示
			    authView.findViewById(R.id.wep_field).setVisibility(View.VISIBLE);
			    final EditText pwd_wep = (EditText) authView.findViewById(R.id.pwd_wep);
			    inputViews.add(pwd_wep);
			    
			    CheckBox show_pwd_wep = (CheckBox) authView.findViewById(R.id.show_pwd_wep);
			    
			    show_pwd_wep.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							pwd_wep.setInputType(isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
									: InputType.TYPE_CLASS_TEXT
											| InputType.TYPE_TEXT_VARIATION_PASSWORD);
						}
				});
				break;
			case WifiConnService.SECURITY_EAP:
				authView.findViewById(R.id.eap_field).setVisibility(View.VISIBLE);
				inputViews.clear();
				
			    eap_method_btn = (Button) authView.findViewById(R.id.eap_method);
				eap_method_btn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showDialog(EAP_METHOD_DIALOG);
					}
				});
				inputViews.add(eap_method_btn);
				
				phase2_btn = (Button) authView.findViewById(R.id.phase2);
				phase2_btn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showDialog(PHASE2_DIALOG);
					}
				});
				inputViews.add(phase2_btn);
				
				ca_cert_btn = (Button) authView.findViewById(R.id.ca_cert);
				ca_cert_btn.setText("不明确的");
				ca_cert_btn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showDialog(CA_CERT_DIALOG);
					}
				});
				inputViews.add(ca_cert_btn);
				
				client_cert_btn = (Button) authView.findViewById(R.id.client_cert);
				client_cert_btn.setText("不明确的");
				client_cert_btn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showDialog(CLIENT_CERT_DIALOG);
					}
				});
				inputViews.add(client_cert_btn);
				
				EditText identity_txt = (EditText) authView.findViewById(R.id.identity);
				inputViews.add(identity_txt);
				
				EditText anonymous_identity_txt = (EditText) authView.findViewById(R.id.anonymous_identity);
				inputViews.add(anonymous_identity_txt);
				
				final EditText private_key = (EditText) authView.findViewById(R.id.private_key);
				inputViews.add(private_key);
				//密码是否显示
				CheckBox show_pwd_eap = (CheckBox) authView.findViewById(R.id.show_pwd_eap);
				show_pwd_eap.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							private_key.setInputType(isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
											: InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
						}
				});
				break;
				
			case WifiConnService.SECURITY_PSK:
				inputViews.clear();
				
				authView.findViewById(R.id.psk_field).setVisibility(View.VISIBLE);
				final EditText password = (EditText) authView.findViewById(R.id.pwd_psk);
				inputViews.add(password);
				
				CheckBox show_pwd_psk = (CheckBox) authView.findViewById(R.id.show_pwd_psk);
				show_pwd_psk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
						    password.setInputType(isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
											: InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
						}
				});
				
//				Button net_type_btn = (Button) authView.findViewById(R.id.net_type);
//				net_type_btn.setText("WPA/WPA2");
//				net_type_btn.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						showDialog(NET_TYPE_DIALOG);
//					}
//				});
//				inputViews.add(net_type_btn);
				break;
			default:
				break;
		 }
		 return authView;
	 }
	 
	private void addRow(ViewGroup group, int name, String value) {
		View row = getLayoutInflater().inflate(R.layout.ap_dialog_row, group, false);
		((TextView) row.findViewById(R.id.name)).setText(name);
		((TextView) row.findViewById(R.id.value)).setText(value);
		group.addView(row);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case EAP_METHOD_DIALOG:
			Builder builder = new AlertDialog.Builder(this);  
			builder.setTitle("EAP方法");  
			DialogInterface.OnClickListener btnListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int length = getResources().getStringArray(R.array.wifi_eap_type).length;
					if (which < length && which >= 0) {
						String eap_method_selected = getResources().getStringArray(R.array.wifi_eap_type)[which];
						eap_method_btn.setText(eap_method_selected);
					}
				}
			};
			builder.setSingleChoiceItems(R.array.wifi_eap_type, 0, btnListener); 
			builder.setPositiveButton("确定", btnListener);  
			dialog = builder.create();  
			break;
		case PHASE2_DIALOG:
			Builder builder2 = new AlertDialog.Builder(this);  
			builder2.setTitle("阶段2身份认证");  
			DialogInterface.OnClickListener btnListener2 = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int length = getResources().getStringArray(R.array.phase2_type).length;
					if (which < length && which >= 0) {
						String eap_method_selected = getResources().getStringArray(R.array.phase2_type)[which];
						phase2_btn.setText(eap_method_selected);
					}
				}
			};
			builder2.setSingleChoiceItems(R.array.phase2_type, 0, btnListener2); 
			builder2.setPositiveButton("确定", btnListener2);  
			dialog = builder2.create();
			break;
		case CA_CERT_DIALOG:
			Builder builder3 = new AlertDialog.Builder(this);  
			builder3.setTitle("CA证书");  
			DialogInterface.OnClickListener btnListener3 = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int length = getResources().getStringArray(R.array.ca_cert).length;
					if (which < length && which >= 0) {
						String eap_method_selected = getResources().getStringArray(R.array.ca_cert)[which];
						ca_cert_btn.setText(eap_method_selected);
					}
				}
			};
			builder3.setSingleChoiceItems(R.array.ca_cert, 0, btnListener3); 
			builder3.setPositiveButton("确定", btnListener3);  
			dialog = builder3.create();  
			break;
		case CLIENT_CERT_DIALOG:
			Builder builder4 = new AlertDialog.Builder(this);  
			builder4.setTitle("用户证书");  
			DialogInterface.OnClickListener btnListener4 = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int length = getResources().getStringArray(R.array.client_cert).length;
					if (which < length && which >= 0) {
						String eap_method_selected = getResources().getStringArray(R.array.client_cert)[which];
						client_cert_btn.setText(eap_method_selected);
					}
				}
			};
			builder4.setSingleChoiceItems(R.array.client_cert, 0, btnListener4); 
			builder4.setPositiveButton("确定", btnListener4);  
			dialog = builder4.create();  
			break;
		default:
			break;
		}
		return dialog;
	}
		
		
}
