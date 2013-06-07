package com.asc.app.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.asc.app.R;
import com.asc.app.service.AdvertiseService;
import com.asc.app.service.TrafficService;
import com.asc.app.service.WifiConnService;

/**
 * @author zhanglei
 *
 */
public class SettingActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private CheckBoxPreference ad_switch;
	private CheckBoxPreference wlan_switch;
	private Preference wlan_network;
	private Preference more_settings;
	private CheckBoxPreference traffic_monitor_switch;
	private ListPreference traffic_timeout_period;
	private WifiConnService wifiConnService;
	public static final String ADVERTISE_CHECKBOX = "ad_switch";
	public static final String WLAN_CHECKBOX = "wlan_switch";//自定义checkboxPreference
	public static final String WLAN_AUTO_CHECKBOX = "wlan_auto_switch";//自定义checkboxPreference
	public static final String TRAFFIC_CHECKBOX = "traffic_monitor_switch";//自定义checkboxPreference
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		setContentView(R.layout.settings);
		
		ListView listView = (ListView) findViewById(android.R.id.list);
		listView.setCacheColorHint(0);
		
		Button return_btn = (Button) findViewById(R.id.return_btn);
		return_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		wifiConnService = WifiConnService.getInstance(getApplicationContext());
		
		ad_switch = (CheckBoxPreference) findPreference(ADVERTISE_CHECKBOX);
		ad_switch.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if (!Boolean.parseBoolean(newValue.toString())) {
					Log.e("ADVERTISE_CHECKBOX", "advertise service is stopped...");
					getApplicationContext().stopService(new Intent(getApplicationContext(), AdvertiseService.class));
				}
				return true;
			}
		});
		if (ad_switch.isChecked()) {
			ad_switch.setLayoutResource(R.layout.check_preference_yes_layout);
		}else {
			ad_switch.setLayoutResource(R.layout.check_preference_no_layout);
		}
		
		wlan_switch = (CheckBoxPreference) findPreference(WLAN_CHECKBOX);
		wlan_switch.setChecked(wifiConnService.isWifiEnabled());
		wlan_switch.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if (Boolean.parseBoolean(newValue.toString())) {
					wifiConnService.enableWifi();
				}else {
					wifiConnService.disableWifi();
				}
				return true;
			}
		});
		if (wlan_switch.isChecked()) {
			wlan_switch.setLayoutResource(R.layout.check_preference_yes_layout);
		}else {
			wlan_switch.setLayoutResource(R.layout.check_preference_no_layout);
		}
		
		CheckBoxPreference wlan_auto_switch = (CheckBoxPreference) findPreference(WLAN_AUTO_CHECKBOX);
		if (wlan_auto_switch.isChecked()) {
			wlan_auto_switch.setLayoutResource(R.layout.check_preference_yes_layout);
		}else {
			wlan_auto_switch.setLayoutResource(R.layout.check_preference_no_layout);
		}
		
		wlan_network = findPreference("wlan_network");
		wlan_network.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(getApplicationContext(), AccessPointActivity.class);
				startActivity(intent);
				return true;
			}
		});
		
		more_settings = findPreference("more_settings");
		more_settings.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
				startActivity(intent);
				return true;
			}
		});
		
		traffic_monitor_switch = (CheckBoxPreference) findPreference(TRAFFIC_CHECKBOX);
		traffic_monitor_switch.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if (!Boolean.parseBoolean(newValue.toString())) {
					getApplicationContext().stopService(new Intent(getApplicationContext(), TrafficService.class));
				}
				return true;
			}
		});
		if (traffic_monitor_switch.isChecked()) {
			traffic_monitor_switch.setLayoutResource(R.layout.check_preference_yes_layout);
		}else {
			traffic_monitor_switch.setLayoutResource(R.layout.check_preference_no_layout);
		}
		
		traffic_timeout_period = (ListPreference) findPreference("traffic_timeout_period");
		traffic_timeout_period.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				SettingActivity.this.stopService(new Intent(SettingActivity.this, TrafficService.class));
				SettingActivity.this.startService(new Intent(SettingActivity.this, TrafficService.class));
				return true;
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		wlan_switch.setChecked(wifiConnService.isWifiEnabled());
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		if (preference.getKey().equals(ADVERTISE_CHECKBOX)) {
			CheckBoxPreference advertise_check = (CheckBoxPreference)preference;
			if (advertise_check.isChecked()) {
				preference.setLayoutResource(R.layout.check_preference_yes_layout);
				preferenceScreen.removePreference(preference);
			}else {
				preference.setLayoutResource(R.layout.check_preference_no_layout);
				preferenceScreen.removePreference(preference);
			}
			getPreferences(MODE_WORLD_WRITEABLE).edit().putBoolean(ADVERTISE_CHECKBOX, advertise_check.isChecked());
		}
        if(preference.getKey().equals(WLAN_CHECKBOX)){
			CheckBoxPreference wlanPreference = (CheckBoxPreference)preference;
			if (wlanPreference.isChecked()) {
				preference.setLayoutResource(R.layout.check_preference_yes_layout);
				preferenceScreen.removePreference(preference);
			}else {
				preference.setLayoutResource(R.layout.check_preference_no_layout);
				preferenceScreen.removePreference(preference);
			}
			getPreferences(MODE_WORLD_WRITEABLE).edit().putBoolean(WLAN_CHECKBOX, wlanPreference.isChecked());
		}
        if (preference.getKey().equals(WLAN_AUTO_CHECKBOX)) {
			CheckBoxPreference wlanAutoPreference = (CheckBoxPreference)preference;
			if (wlanAutoPreference.isChecked()) {
				preference.setLayoutResource(R.layout.check_preference_yes_layout);
				preferenceScreen.removePreference(preference);
			}else {
				preference.setLayoutResource(R.layout.check_preference_no_layout);
				preferenceScreen.removePreference(preference);
			}
			getPreferences(MODE_WORLD_WRITEABLE).edit().putBoolean(WLAN_AUTO_CHECKBOX, wlanAutoPreference.isChecked());
		}
        if (preference.getKey().equals(TRAFFIC_CHECKBOX)) {
			CheckBoxPreference trafficPreference = (CheckBoxPreference)preference;
			if (trafficPreference.isChecked()) {
				preference.setLayoutResource(R.layout.check_preference_yes_layout);
				preferenceScreen.removePreference(preference);
			}else {
				preference.setLayoutResource(R.layout.check_preference_no_layout);
				preferenceScreen.removePreference(preference);
			}
			getPreferences(MODE_WORLD_WRITEABLE).edit().putBoolean(TRAFFIC_CHECKBOX, trafficPreference.isChecked());
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
}