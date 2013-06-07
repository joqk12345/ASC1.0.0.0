package com.asc.app.ui;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.net.TrafficStats;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.asc.app.R;

public class TrafficActivity extends TabActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.traffic);
		
//		Button return_btn = (Button) findViewById(R.id.return_btn);
//		return_btn.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});
		Resources res = this.getResources();
		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
		Intent intent;
		TabHost.TabSpec spec;
		tabHost.setup();
		
		intent = new Intent(this, TrafficDailyActivity.class);
		spec = tabHost.newTabSpec("daily").setIndicator(getString(R.string.traffic_monitor), res.getDrawable(R.drawable.stock)).setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent(this, TrafficSoftActivity.class);
		spec = tabHost.newTabSpec("soft").setIndicator(getString(R.string.traffic_rank), res.getDrawable(R.drawable.statistics)).setContent(intent);
		tabHost.addTab(spec);
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				if (tabId.equals("soft")) {
					if (TrafficStats.getUidRxBytes(1000) == TrafficStats.UNSUPPORTED && TrafficStats.getUidRxBytes(1000) == TrafficStats.UNSUPPORTED) {
						AlertDialog.Builder builder = new AlertDialog.Builder(TrafficActivity.this);
						builder.setTitle(R.string.tips);
						builder.setMessage(R.string.function_not_support);
						builder.setNegativeButton(R.string.yes, new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) { 
							}
						});
						builder.show();
					}
				}
				Log.e("tabId", tabId);
			}
		});
	}
}