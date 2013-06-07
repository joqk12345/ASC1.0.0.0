package com.asc.app.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.asc.app.database.DBManager;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.TrafficStats;
import android.util.Log;
import android.widget.ImageView;

public class SoftwareTrafficService {
	private static SoftwareTrafficService mobileTrafficService = null;
	private TrafficThread trafficThread;
	private static Context context;

	private SoftwareTrafficService() {
		trafficThread = new TrafficThread();
		trafficThread.start();
	}

	public static SoftwareTrafficService getInstance(Context cxt) {
		context = cxt;
		if (mobileTrafficService == null) {
			mobileTrafficService = new SoftwareTrafficService();
		}
		return mobileTrafficService;
	}

	class TrafficThread extends Thread {
		@Override
		public void run() {
			PackageManager pm = context.getPackageManager();
			List<ApplicationInfo> applicationInfos = pm.getInstalledApplications(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
			for (ApplicationInfo appInfo : applicationInfos) {
				int uid = appInfo.uid;
				PackageInfo packageInfo;
				try {
					packageInfo = pm.getPackageInfo(appInfo.packageName, PackageManager.GET_PERMISSIONS);
					String[] permissions = packageInfo.requestedPermissions;

					if (permissions == null) {
						continue;
					}
					for (int i = 0; i < permissions.length; i++) {
						if (!"android.permission.INTERNET".equals(permissions[i])) {
							continue;
						}
						long uidRxTraffic = TrafficStats.getUidRxBytes(uid);
						long uidTxTraffic = TrafficStats.getUidTxBytes(uid);
						if (uidRxTraffic == TrafficStats.UNSUPPORTED && uidTxTraffic == TrafficStats.UNSUPPORTED) {
							continue;
						}
						if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
							continue;
						}
						Log.e("TrafficService", "uid: " + uid
								+ " uidRxTraffic: " + uidRxTraffic
								+ " uidTxTraffic: " + uidTxTraffic + " " + appInfo.icon);
						DBManager dbManager = DBManager.getInstance(context);
						dbManager.open();
						Cursor cursor = dbManager.executeSqlQuery(
										"select mobileTraffic from traffic_soft where softUid = ?",
										new String[] { String.valueOf(uid) });
						if (cursor.moveToFirst()) {
							long trafficRecord = cursor.getLong(0);
							trafficRecord = trafficRecord + uidRxTraffic + uidTxTraffic;
							dbManager.executeSql("update traffic_soft set mobileTraffic = "
											+ (trafficRecord / 1024)
											+ " where softUid = " + uid);
						} else {
							ContentValues contentValues = new ContentValues();
							contentValues.put("softUid", uid);
							contentValues.put("softName", appInfo.loadLabel(pm).toString());
							contentValues.put("softLogo", appInfo.icon);
							contentValues.put("mobileTraffic", (uidRxTraffic + uidTxTraffic) / 1024);
							Date nowDate = new Date();
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
							String monthDate = dateFormat.format(nowDate);
							contentValues.put("createDate", monthDate);
							try {
								dbManager.insert("traffic_soft", "id", contentValues);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						dbManager.close();
					}
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
