package com.asc.app.ui;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.asc.app.R;

public class SignalGraphActivity extends TabActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signal_graph);
		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
		Intent intent;
		TabHost.TabSpec tabSpec;
		tabHost.setup();
		
		intent = new Intent(SignalGraphActivity.this, SignalTrendActivity.class);
		intent.putExtra("ssid", (String)getIntent().getExtras().get("ssid"));
		tabSpec = tabHost.newTabSpec("trend").setIndicator(getString(R.string.graph_trend),getResources().getDrawable(R.drawable.monitor)).setContent(intent);
		tabHost.addTab(tabSpec);
		
		intent = new Intent(SignalGraphActivity.this, SignalDashboardActivity.class);
		intent.putExtra("ssid", (String)getIntent().getExtras().get("ssid"));
		tabSpec = tabHost.newTabSpec("dashboard").setIndicator(getString(R.string.graph_dashborad),getResources().getDrawable(R.drawable.dashboard)).setContent(intent);
		tabHost.addTab(tabSpec);
	}
}
