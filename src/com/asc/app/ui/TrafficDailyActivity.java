package com.asc.app.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.net.TrafficStats;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asc.app.R;
import com.asc.app.database.DBManager;

public class TrafficDailyActivity extends Activity {
	private long maxTraffic = 100;
	private long todayMobileTraffic = 0;
	private long todayWifiTraffic = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.traffic_daily_graph);
		String netType = "wifi";
		long phoneRx;
		long phoneTx;
		long wifiRx;
		long wifiTx;
		phoneRx = TrafficStats.getMobileRxBytes() == TrafficStats.UNSUPPORTED ? 0 : TrafficStats.getMobileRxBytes();
		phoneTx = TrafficStats.getMobileTxBytes() == TrafficStats.UNSUPPORTED ? 0 : TrafficStats.getMobileTxBytes();
		wifiRx = TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() - phoneRx);
		wifiTx = TrafficStats.getTotalTxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalTxBytes() - phoneTx);
		
		todayWifiTraffic = (wifiRx + wifiTx) / 1024;
		todayMobileTraffic = (phoneRx + phoneTx) / 1024;
		LinearLayout wifiLayout = (LinearLayout) findViewById(R.id.wifi_traffic_graph);
		View wifiChartView = ChartFactory.getBarChartView(this, getDataset(netType), getBarRenderer(netType), Type.DEFAULT);
		wifiLayout.addView(wifiChartView);
		
		netType = "mobile";
		LinearLayout mobileLayout = (LinearLayout) findViewById(R.id.mobile_traffic_graph);
		View mobileView = ChartFactory.getBarChartView(this, getDataset(netType), getBarRenderer(netType), Type.DEFAULT);
		mobileLayout.addView(mobileView);
		
		TextView wifi_traffic_daily = (TextView) findViewById(R.id.wifi_traffic_daily);
		wifi_traffic_daily.setText("" + todayWifiTraffic + "KB");
		TextView mobile_traffic_daily = (TextView) findViewById(R.id.mobile_traffic_daily);
		mobile_traffic_daily.setText("" + todayMobileTraffic + "KB");
	}
	
	private XYMultipleSeriesDataset getDataset(String netType) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		CategorySeries series = new CategorySeries(""); // 声明一个柱形图
		
		DBManager dbManager = DBManager.getInstance(getApplicationContext());
		dbManager.open();
		Date nowDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
		String nowMonth = dateFormat.format(nowDate);
		String sql = "";
		if (netType.equals("wifi")) {
			sql = "select wifiTraffic,createDate from traffic_daily where createDate like '"+ nowMonth +"%'";
		}else if (netType.equals("mobile")) {
			sql = "select phoneTraffic,createDate from traffic_daily where createDate like '"+ nowMonth +"%'";
		}
		
		Cursor cursor = dbManager.executeSqlQuery(sql, null);
		
		Calendar calendar = Calendar.getInstance();
		int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		for (int i = 1; i <= maxDay; i++) {
			series.add(0);                        // 初始化每天记录为0
		}
		int date = 0;
		if (cursor.moveToFirst()) {
			for (int j = 0; j < cursor.getCount(); j++) {
				long traffic = cursor.getLong(0);
				date = Integer.parseInt(cursor.getString(1).substring(8));
				Log.e("date", date+"");
				if (traffic > maxTraffic) {
					maxTraffic = traffic;
				}
				if (date == calendar.get(Calendar.DATE)) {
					if (netType.equals("wifi")) {
						todayWifiTraffic = traffic + todayWifiTraffic;
					}else if (netType.equals("mobile")) {
						todayMobileTraffic = traffic + todayMobileTraffic;
					}
				}
				series.set(date - 1, "test", traffic);   // 设置有记录的日期
				cursor.moveToNext();
			}
		}
		dbManager.close();
		if (netType.equals("wifi")) {
			series.set(calendar.get(Calendar.DATE) - 1, "test", todayWifiTraffic);
		}else if (netType.equals("mobile")) {
			series.set(calendar.get(Calendar.DATE) - 1, "test", todayMobileTraffic);
		}
		dataset.addSeries(series.toXYSeries());// 添加该柱形图到数据设置列表
		return dataset;

	}

	@SuppressWarnings("deprecation")
	public XYMultipleSeriesRenderer getBarRenderer(String netType) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		SimpleSeriesRenderer r = new SimpleSeriesRenderer();
		r.setColor(Color.WHITE);// 颜色
		renderer.addSeriesRenderer(r);
		if (netType.equals("wifi")) {
			renderer.setChartTitle("WIFI"); // 设置标题
		}else if (netType.equals("mobile")) {
			renderer.setChartTitle("2G/3G"); // 设置标题
		}
		renderer.setYTitle("流量(KB)"); // y轴标题
		renderer.setXTitle("日期");// x轴标题
		renderer.setAxisTitleTextSize(10);
		renderer.setChartTitleTextSize(10);
		renderer.setXAxisMin(0.5);// x轴最小值
		renderer.setXAxisMax(10.5);
		renderer.setYAxisMin(0);// y轴最小值
		renderer.setDisplayChartValues(true);// 是否在图上中显示值
		renderer.setChartValuesTextSize(10);
		renderer.setShowGrid(true);// 显示网格
		renderer.setFitLegend(true);// 调整合适的位置
		renderer.setXLabels(0);
		renderer.setShowLegend(false);
		Calendar calendar = Calendar.getInstance();
		int days = calendar.getActualMaximum(Calendar.DATE);
		for (int i = 1; i <= days; i++) {
			renderer.addTextLabel(i ,calendar.get(Calendar.MONTH) + 1 + "."+i);
		}
		renderer.setBarSpacing(1);
		return renderer;
	}
}
