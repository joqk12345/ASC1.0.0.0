package com.asc.app.ui;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.LinearLayout;

import com.asc.app.R;
import com.asc.app.service.WifiConnService;

public class SignalTrendActivity extends Activity {
	private GraphicalView mChartView;
	private XYSeries series;
	private XYMultipleSeriesDataset dataset;
	private String ssid;
	private int count = 2;
//	private IntentFilter mFilter;
	private ValuesThread valuesThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_signal_trend);
		Button returnBtn = (Button) findViewById(R.id.return_btn);
		returnBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				valuesThread.setRunning(false);
				valuesThread.interrupt();
				finish();
			}
		});
		ssid = (String) getIntent().getExtras().get("ssid");
//		mFilter = new IntentFilter();
//        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//        mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        valuesThread = new ValuesThread();
        timer.schedule(timerTask, 0, 1000);
        valuesThread.start();
	}

	@Override
	
	protected void onResume() {
		if (null == valuesThread) {
			valuesThread = new ValuesThread();
		    valuesThread.start();
		}
		handler.setReceive(true);
//		registerReceiver(receiver, mFilter);
		LinearLayout layout = (LinearLayout) findViewById(R.id.wifi_signal_graph);
		mChartView = ChartFactory.getLineChartView(SignalTrendActivity.this, getDataset(), getLineRender());
		
		
		layout.addView(mChartView);
		WifiManager mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE); 
		mWifiManager.startScan();
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
//		unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		Log.e("onPause", "=========onPause===========");
		handler.setReceive(false);
		valuesThread.setRunning(false);
		valuesThread.interrupt();
		valuesThread = null;
		super.onPause();
	}
	
	class MyHandler extends Handler{
		private volatile boolean isReceive = true;
		
		public void setReceive(boolean isReceive) {
			this.isReceive = isReceive;
		}

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (isReceive) {
					Log.e("MyHandler", "invalidate" + isReceive);
					mChartView.invalidate();
				}
				break;
			case 2:
				setReceive(false);
				Log.e("MyHandler", "setReceive" + isReceive);
				break;
			}
			
		};
	}
	
	final MyHandler handler = new MyHandler();
	
	@Override
	protected void onStop() {
		Log.e("onStop", "===========onStop============");
		super.onStop();
	}
	
	private XYMultipleSeriesDataset getDataset() {
		dataset = new XYMultipleSeriesDataset();
		series = new XYSeries("", 0);
		dataset.addSeries(series);
		return dataset;
	}
	
	private XYMultipleSeriesRenderer getLineRender() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(5f);
		renderer.setXLabels(0);
		renderer.setYLabels(20);
		renderer.setXAxisMin(0);
		renderer.setXAxisMax(100);
		renderer.setYAxisMax(-20, 0);
		renderer.setYAxisMin(-100);
		renderer.setShowGrid(true);
		renderer.setXLabelsAlign(Align.RIGHT);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setZoomButtonsVisible(false);
		renderer.setShowLegend(false);
		renderer.setChartTitle(ssid);
		renderer.setYTitle(getString(R.string.signal_unit));
//		renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
//		renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(Color.CYAN);// 颜色
		r.setLineWidth(5f);
		renderer.addSeriesRenderer(r);
		renderer.setPanLimits(new double[]{0,0,0,0});
		renderer.setPanEnabled(false, false);
	    renderer.setZoomEnabled(true, true);
		return renderer;
	}
	
	Timer timer = new Timer();
	TimerTask timerTask = new TimerTask() {
		@Override
		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	};
	
//	private final BroadcastReceiver receiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
//				WifiConnService wifiService = WifiConnService.getInstance(SignalTrendActivity.this);
//				List<ScanResult> scanResults = wifiService.getResults();
//				if (null == scanResults) {
//					return;
//				}
//				for (ScanResult result : scanResults) {
//					if (result.SSID.equals(ssid)) {
//						Log.e("receiver", result.level +" dBm");
//						series.add(count, result.level);
//						count += 2;
//						return;
//					}
//				}
//			}
//		}
//	};
	
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
					if (series == null) {
						continue;
					}
					WifiConnService wifiService = WifiConnService.getInstance(SignalTrendActivity.this);
					List<ScanResult> scanResults = wifiService.getResults();
					if (null == scanResults) {
						Log.e("ValuesThread", "results is null");
						continue;
					}
					for (ScanResult result : scanResults) {
						if (result.SSID.equals(ssid)) {
							series.add(count, result.level);
							count += 2;
							Log.e("ValuesThread", result.level +" dBm");
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
