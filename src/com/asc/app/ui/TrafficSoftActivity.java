package com.asc.app.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.drawable;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

import com.asc.app.R;
import com.asc.app.database.DBManager;

public class TrafficSoftActivity extends Activity {
	private ListView soft_traffic_list;
	private SimpleAdapter adapter;
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.traffic_soft_list);
		soft_traffic_list = (ListView) findViewById(R.id.soft_traffic_list);
		setDataList();
		adapter = new SimpleAdapter(this, dataList, R.layout.traffic_soft,
				new String[] { "icon", "label", "traffic" }, new int[] {R.id.soft_icon, R.id.soft_label, R.id.traffic_value });
		soft_traffic_list.setAdapter(adapter);
		soft_traffic_list.setCacheColorHint(0);
		adapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data, String textRepresentation) {
				if (view instanceof ImageView && data instanceof Drawable) {
					ImageView imageView = (ImageView) view;
					imageView.setImageDrawable((Drawable) data);
					return true;
				}
				return false;
			}
		});
	}


	public void setDataList() {
		DBManager dbManager = DBManager.getInstance(TrafficSoftActivity.this);
		dbManager.open();
		Date currentMonthDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
		String currentMonth = dateFormat.format(currentMonthDate);
		Cursor cursor = dbManager.executeSqlQuery("select softUid,mobileTraffic,softName from traffic_soft where createDate = ?", new String[] { currentMonth });
		if (cursor.moveToFirst()) {
			PackageManager pm = TrafficSoftActivity.this.getPackageManager();
			List<ApplicationInfo> applicationInfos = pm.getInstalledApplications(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
			for (int i = 0; i < cursor.getCount(); i++) {
				int softUid = cursor.getInt(0);
				long trafficKB = cursor.getLong(1);
				String softName = cursor.getString(2);
				Map<String, Object> dataMap = new HashMap<String, Object>();
				
				for (ApplicationInfo appInfo : applicationInfos) {
					int uid = appInfo.uid;
					String label = appInfo.loadLabel(pm).toString();
					PackageInfo packageInfo = null;
					try {
						packageInfo = pm.getPackageInfo(appInfo.packageName, PackageManager.GET_PERMISSIONS);
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					String[] permissions = packageInfo.requestedPermissions;
					if (permissions == null) {
						continue;
					}
					boolean isInternet = false;
					for (int j = 0; j < permissions.length; j++) {
						if ("android.permission.INTERNET".equals(permissions[j])) {
							isInternet = true;
						}
					}
					if (!isInternet) {
						continue;
					}
					if (uid == softUid && softName.equals(label)) {
						Log.e("Traffic",trafficKB + "---" + TrafficStats.getUidRxBytes(uid) + "---" + TrafficStats.getUidTxBytes(uid));
						trafficKB = trafficKB + (TrafficStats.getUidRxBytes(uid) + TrafficStats.getUidTxBytes(uid))/1024;
						Log.e("Traffic", appInfo.loadLabel(pm).toString() + " uid: " + appInfo.uid);
						dataMap.put("icon", appInfo.loadIcon(pm));
						dataMap.put("label", appInfo.loadLabel(pm).toString());
						String trafficString = "";
						if (trafficKB == 0) {
							trafficString = "<1KB";
						}
						else if (trafficKB > 1024) {
							trafficString = trafficKB / 1024 + "MB";
						} else {
							trafficString = trafficKB + "KB";
						}
						dataMap.put("traffic", trafficString);
						dataList.add(dataMap);
					}
				}
				cursor.moveToNext();
			}
			Collections.sort(dataList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> map1, Map<String, Object> map2) {
					String traffic1 = (String) map1.get("traffic");
					String traffic2 = (String) map2.get("traffic");
					traffic1 = traffic1.contains("<") ? "0" : traffic1.substring(0, traffic1.length() - 2);
					traffic2 = traffic2.contains("<") ? "0" : traffic2.substring(0, traffic2.length() - 2);
					
					if (Long.parseLong(traffic1) > Long.parseLong(traffic2)) {
						return -1;
					}else if (Long.parseLong(traffic1) == Long.parseLong(traffic2)) {
						return 0;
					}else return 1;
				}
			});
		}
		dbManager.close();
	}
	
}
