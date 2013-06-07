package com.asc.app.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.asc.app.R;
import com.asc.app.service.WifiConnService;
import com.asc.app.util.IpUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SignalListActivity extends Activity {
	private WifiConnService wifiConnService;
	private IntentFilter mFilter;
	
	private List<Map<String, Object>> itemList = new ArrayList<Map<String,Object>>();
	private Map<String, ScanResult> resultMap = new HashMap<String, ScanResult>();   //SSID 和 扫描结果的映射关系
	private SimpleAdapter adapter;
	
	private ListView apList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signal_list);
		//获取wifi实例
		wifiConnService = WifiConnService.getInstance(this);
		//从界面中获取aplist列表
		apList = (ListView) findViewById(R.id.ap_list);
		if (!wifiConnService.isWifiEnabled()) {
			wifiConnService.enableWifi();
			Toast.makeText(this, getString(R.string.open_wifi_waiting), Toast.LENGTH_LONG);
		}
		//Item之中填充值
		setItemList(wifiConnService.getResults());
		
		adapter = new SimpleAdapter(this, itemList, R.layout.signal_info,
				new String[] { "SSID", "signal", "capabilities", "frequency","star1","star2","star3","star4","star5"},
				new int[] { R.id.ssid, R.id.signal, R.id.capabilities, R.id.frequency, R.id.star1, R.id.star2, R.id.star3, R.id.star4, R.id.star5});
		apList.setAdapter(adapter);
		
		// 选中AP时根据认证类型，弹出窗口
		apList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Map<String, Object> map = (Map<String, Object>) apList.getItemAtPosition(position);
				Intent intent = new Intent(SignalListActivity.this, SignalGraphActivity.class);
				intent.putExtra("ssid", (String)map.get("SSID"));
				startActivity(intent);
			}
		}) ;
		apList.setCacheColorHint(0);
		
		// 定义WIFI状态的Receiver过滤器
		mFilter = new IntentFilter();
        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        
        Button button = (Button) findViewById(R.id.return_btn);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	public List<Map<String, Object>> getItemList() {
		return itemList;
	}

	public void setItemList(List<ScanResult> scanResults) {
		itemList.clear();
		if (null == scanResults) {
			return;
		}
		Map<String, Object> map;
//		WifiInfo wifiInfo = wifiConnService.getCurrentConnInfo();
		for(ScanResult apInfo : scanResults){
			map = new HashMap<String, Object>();
			map.put("SSID", apInfo.SSID);
			
			String auth = getString(wifiConnService.getAuthString(apInfo.capabilities));
		    map.put("capabilities", auth);     // 设置验证方式
			
//		    boolean isLocked = !(map.get("capabilities").equals(getString(R.string.wifi_open)));
			map.put("signal", apInfo.level+" dBm");
			map.put("frequency", apInfo.frequency +" MHz");
			switch (wifiConnService.getLevel(apInfo.level, 5)) {
			case 0:
				map.put("star1", R.drawable.star);
				map.put("star2", R.drawable.star_none);
				map.put("star3", R.drawable.star_none);
				map.put("star4", R.drawable.star_none);
				map.put("star5", R.drawable.star_none);
				break;
			case 1:
				map.put("star1", R.drawable.star);
				map.put("star2", R.drawable.star);
				map.put("star3", R.drawable.star_none);
				map.put("star4", R.drawable.star_none);
				map.put("star5", R.drawable.star_none);
				break;
			case 2:
				map.put("star1", R.drawable.star);
				map.put("star2", R.drawable.star);
				map.put("star3", R.drawable.star);
				map.put("star4", R.drawable.star_none);
				map.put("star5", R.drawable.star_none);
				break;
			case 3:
				map.put("star1", R.drawable.star);
				map.put("star2", R.drawable.star);
				map.put("star3", R.drawable.star);
				map.put("star4", R.drawable.star);
				map.put("star5", R.drawable.star_none);
				break;
			case 4:
				map.put("star1", R.drawable.star);
				map.put("star2", R.drawable.star);
				map.put("star3", R.drawable.star);
				map.put("star4", R.drawable.star);
				map.put("star5", R.drawable.star);
				break;
			default:
				break;
			}
			
			itemList.add(map);
			resultMap.put(apInfo.SSID, apInfo);
		}
		if (null != adapter) {
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
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
		}
	};
}
