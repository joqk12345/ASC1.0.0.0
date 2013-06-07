package com.asc.app.ui;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.DialRenderer;
import org.achartengine.renderer.DialRenderer.Type;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.asc.app.R;
import com.asc.app.service.WifiConnService;

public class SignalDashboardActivity extends Activity {
	private CategorySeries category = new CategorySeries("");
	private GraphicalView mChartView;
//	private DialRenderer renderer;
//	private IntentFilter mFilter;
	private String ssid;
	private ValuesThread valuesThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		ssid = (String) getIntent().getExtras().get("ssid");
		setContentView(R.layout.wifi_signal_dashboard);
		startChartThread();
		Button return_btn = (Button) findViewById(R.id.return_btn);
		return_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				valuesThread.setRunning(false);
				valuesThread.interrupt();
				finish();
			}
		});
//		mFilter = new IntentFilter();
//        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//        mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        category.add("", -100);
        
        valuesThread = new ValuesThread();
        valuesThread.start();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (null == valuesThread) {
			valuesThread = new ValuesThread();
		    valuesThread.start();
		}
		mChartView = ChartFactory.getDialChartView(SignalDashboardActivity.this, category, getDialRenderer());
		LinearLayout layout = (LinearLayout) findViewById(R.id.wifi_signal_graph);
		layout.addView(mChartView);
		WifiManager mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE); 
		mWifiManager.startScan();
	}
	
	@Override
	protected void onPause() {
		valuesThread.setRunning(false);
		valuesThread.interrupt();
		valuesThread = null;
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	private DialRenderer getDialRenderer() {
		DialRenderer renderer = new DialRenderer();
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);// 说明字体的大小
		SimpleSeriesRenderer r = new SimpleSeriesRenderer();
		r.setColor(Color.RED);// 指针的颜色
		r.setStroke(new BasicStroke(Cap.BUTT, Join.ROUND, 10.0f, new float[]{}, 200f));
//		r.setChartValuesTextAlign();

		renderer.addSeriesRenderer(r);
		renderer.setLabelsTextSize(10);// 仪表盘字体大小
		renderer.setLabelsColor(Color.WHITE);// 仪表盘格子的颜色
		renderer.setShowLabels(true);
		renderer.setShowLegend(false);
		// 设置指针样式（默认为三角形）此为箭头
		renderer.setVisualTypes(new DialRenderer.Type[] { Type.NEEDLE });
		renderer.setMinValue(-100);// 仪表盘最小值
		renderer.setMaxValue(-20);// 仪表盘最大值
		renderer.setChartTitle(ssid);
		
		renderer.setPanEnabled(false);
		renderer.setZoomEnabled(false);
		return renderer;
	}
	
	private void startChartThread(){
		timer.schedule(task, 0, 1000 * 3); // 启动timer,5秒绘制一次
//		new ValuesThread().start();
	}
	
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				WifiConnService wifiService = WifiConnService.getInstance(SignalDashboardActivity.this);
				List<ScanResult> scanResults = wifiService.getResults();
				if (null == scanResults) {
					return;
				}
				for (ScanResult result : scanResults) {
					if (result.SSID.equals(ssid)) {
						category.clear();
						Log.e("category", result.level +" dBm");
						category.add(result.level);
						return;
					}
				}
			}
		}
	};

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mChartView.invalidate();
				break;
			}
			super.handleMessage(msg);
		}
	};

	Timer timer = new Timer();
	TimerTask task = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	};
	
	private class ValuesThread extends Thread {
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
					if (category == null) {
						continue;
					}
					WifiConnService wifiService = WifiConnService.getInstance(SignalDashboardActivity.this);
					List<ScanResult> scanResults = wifiService.getResults();
					if (null == scanResults) {
						Log.e("ValuesThread", "results is null");
						continue;
					}
					for (ScanResult result : scanResults) {
						if (result.SSID.equals(ssid)) {
							category.clear();
							category.add(result.level);
							Log.e("category", result.level +" dBm");
							break;
						}
					}
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Log.e("ValuesThread","Thread is interruped!");
				}
			}
		}
	}
}
