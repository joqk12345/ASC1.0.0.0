package com.asc.app.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.cookie.DateUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.asc.app.R;
import com.asc.app.bean.request.WLFingerSetRequestBean;
import com.asc.app.bean.response.WlFingerSetResult;
import com.asc.app.database.DBManager;
import com.asc.app.json.JSONUtil;
import com.asc.app.service.RtRepService;
import com.asc.app.service.WifiConnService;

import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorJoiner.Result;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LocationUpdateActivity extends Activity {

	// wifi实例的声明
	private WifiConnService wifiConnService;
	protected static final String LOG_TAG = "LocationUpdateActivity";
	private EditText e1;
	private Context mContext;
	ReplyTask replyTask;
	private long groupNum;
	
	private static  boolean threadFlag = false;

	Button submit_btn,cancel_btn,serviceopen_btn,serviceclose_btn;
	
	@SuppressLint("ShowToast")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//添加没有标题栏的操作
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_update_layout);
		mContext=LocationUpdateActivity.this;
		
		//按钮的初始化操作
		this.buttonInit();
		
		// 当点击上报的时读取配置文件并上报信息

		// 上报的位置
		e1 = (EditText) findViewById(R.id.locationnamevalue);
		// 找到那个RSSI的信息

		// 点击上报按钮的声明
	
		submit_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
/*				serviceclose_btn.setClickable(false);
				serviceopen_btn.setClickable(false);
				submit_btn.setClickable(false);
				cancel_btn.setClickable(true);*/
				
				//如果进程执行上报任务那么进行   线程工作 可以进行   添加线程标记为
				LocationUpdateActivity.threadFlag=false;
				
				serviceclose_btn.setEnabled(false);
				serviceopen_btn.setEnabled(false);
				submit_btn.setEnabled(false);

				cancel_btn.setEnabled(true);//当为false是取消按钮不需要置成灰色的，按上去也没反应
				
				replyTask = new ReplyTask();
				replyTask.execute(null);
				Log.e(LOG_TAG,"开启指纹上报按钮");
			}	
		});
		
		
		cancel_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0)  {
				Log.e(LOG_TAG,"开启取消按钮");
				Log.e(LOG_TAG,"以前轮次清零");
				groupNum=0;
				//如果进程完成任务那么进行
				LocationUpdateActivity.threadFlag=true;
				Log.e(LOG_TAG,"设置线程标记位");
				if(replyTask.getStatus().equals(AsyncTask.Status.FINISHED)){
					Log.e(LOG_TAG,"上报按钮完成状态"+replyTask.getStatus());	
				/*	serviceclose_btn.setClickable(true);
					serviceopen_btn.setClickable(true);
					submit_btn.setClickable(true);
					cancel_btn.setClickable(false);*/
					serviceclose_btn.setEnabled(true);
					serviceopen_btn.setEnabled(true);
					submit_btn.setEnabled(true);
					cancel_btn.setEnabled(false);
				}else if(replyTask.getStatus().equals(AsyncTask.Status.PENDING)){
					Log.e(LOG_TAG,"上报按钮挂起状态"+replyTask.getStatus());
					//如果方法处于挂起状态，这可以通过cancel来尝试停止该方法
					replyTask.cancel(true);
					//如果任务停止 则执行  按钮状态变化操作
					if (replyTask.isCancelled()) {
						serviceclose_btn.setEnabled(true);
						serviceopen_btn.setEnabled(true);
						submit_btn.setEnabled(true);
						cancel_btn.setEnabled(false);
					}
					
				}else if(replyTask.getStatus().equals(AsyncTask.Status.RUNNING)){
					Log.e(LOG_TAG,"上报按钮执行状态"+replyTask.getStatus());	
					replyTask.cancel(true);
					//如果任务停止 则执行  按钮状态变化操作
					if (replyTask.isCancelled()) {
						serviceclose_btn.setEnabled(true);
						serviceopen_btn.setEnabled(true);
						submit_btn.setEnabled(true);
						cancel_btn.setEnabled(false);
					}
				}
			}	
			
		});
		
//		serviceopen_btn.setClickable(false);
		
		serviceopen_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				/*serviceclose_btn.setClickable(true);  //关闭实时上报按钮打开
				serviceopen_btn.setClickable(false);
				submit_btn.setClickable(false);
				cancel_btn.setClickable(false);*/
				
				Intent i  = new Intent();
				i.setClass(LocationUpdateActivity.this, RtRepService.class);
				mContext.startService(i);
				Log.e(LOG_TAG,"开启实时上报按钮");
				
				serviceopen_btn.setEnabled(false);//设置页面为灰色 
				serviceclose_btn.setEnabled(true);  
				submit_btn.setEnabled(false);
				cancel_btn.setEnabled(false);
			}
		});
		
		
		serviceclose_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			/*	serviceclose_btn.setClickable(false);
				serviceopen_btn.setClickable(true);//实时上传可以点击
				submit_btn.setClickable(true);//指纹采集可以点击
				cancel_btn.setClickable(true);*/
				
				Intent i  = new Intent();
				i.setClass(LocationUpdateActivity.this, RtRepService.class);
				mContext.stopService(i);
				Log.e(LOG_TAG,"关闭实时上报按钮");
				
				serviceopen_btn.setEnabled(true);//设置页面为灰色 
				serviceclose_btn.setEnabled(false);  
				submit_btn.setEnabled(true);
				cancel_btn.setEnabled(true);
			}
		});
		
		
		//返回按钮
		Button return_btn = (Button) findViewById(R.id.return_btn);
		return_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	private void  buttonInit(){
		
		//提交
		submit_btn = (Button) findViewById(R.id.locationbutton);
		//取消按钮
		cancel_btn =(Button) findViewById(R.id.locationCanelbutton);
		//开启实时上报按钮
		serviceopen_btn = (Button) findViewById(R.id.rtrepuploadstart);
		//关闭实时上报按钮
		serviceclose_btn = (Button) findViewById(R.id.rtrepuploadstop);
		
		cancel_btn.setEnabled(false);
		
		serviceclose_btn.setEnabled(false);
		
	}
	
	
	private class ReplyTask extends AsyncTask<String, WLFingerSetRequestBean, Result> {
		private String url = "";
 
		@Override
		protected Result doInBackground(String... params) { // 这里面是点击上报位置要做的事情
			//组号初始化 
			groupNum=0;

			// （1）读取配置文件 url 、ssid 、collect_period /collect_rate_vate
			url = "";
			String sSid = "";
			String collectPeriod = "";
			String collectRateValue = "";
			// 从数据库中取出数据
			DBManager dbManager = DBManager
					.getInstance(LocationUpdateActivity.this);
			dbManager.open();
			Cursor cursor = dbManager.executeSqlQuery("select serveURL,SSID,collect_period,collect_rate from wlfinger",null);
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				// 得到该四个值的信息
				Log.e(LOG_TAG, url + " " + sSid);
				if (url != null)
					url = cursor.getString(0);
				if (sSid != null)
					sSid = cursor.getString(1);
				if (collectPeriod != null)
					collectPeriod = cursor.getString(2);
				if (url != null)
					collectRateValue = cursor.getString(3);
			}
			// (2)打开wifiManager 管理器进行设置 并且进行过滤ssid 得到相应的信号值 （未加循环）

			if (collectPeriod != null && collectRateValue != null) {
				for (int i = 1; i <= Integer.parseInt(collectRateValue); i++) {
					//设置线程 如果线程标记为处于结束状态直接 返回
					if(LocationUpdateActivity.threadFlag){
						return null;
					}else{
						
					// 如果线程  标记为 为 false  则 可以执行线程    线程等待 周期
					try {
//						Thread.sleep(Integer.parseInt(collectPeriod) * 1000);
						Thread.sleep((long) (Float.parseFloat(collectPeriod) * 1000));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					wifiConnService = WifiConnService.getInstance(getApplicationContext());
					// 如果wifi没有打开 则打开wifi连接
					if (!wifiConnService.isWifiEnabled()) {
						wifiConnService.enableWifi();
					}
					// 获取开放的SSID
					wifiConnService.startWifiManager();//开始监控
					List<ScanResult> scanResults = wifiConnService.getResults();
					List<WLFingerSetRequestBean> wlFingerlist =new ArrayList<WLFingerSetRequestBean>();
					for (ScanResult scanResult : scanResults) {
						//添加是否的判断（对于多个SSID的处理）
						String [] ssidResult = sSid.split(",");
						for(int j=0;j<ssidResult.length;j++){
							if (scanResult.SSID.equalsIgnoreCase(ssidResult[j])) {
								System.out.println(scanResult.SSID + "---bssid---"+ scanResult.BSSID+"--rssi--"+scanResult.level);
								//this.publishProgress(scanResult.SSID,scanResult.BSSID, scanResult.level + "",url);
								// 导入要传的中间类
								WLFingerSetRequestBean wlFingerSetVo = new WLFingerSetRequestBean();
								// 插入时间
								java.util.Date date = new java.util.Date();
								// java.sql.Date data1=new java.sql.Date(date.getTime());
								wlFingerSetVo.setCollTime(DateUtils.formatDate(date,"yyyy-MM-dd HH:mm:ss"));
								// 插入位置
								wlFingerSetVo.setPointAlias(e1.getText().toString());
								// 插入信号强度
								// 在SSID下面下面的信号强度
								wlFingerSetVo.setRSSI(scanResult.level);
								// 插入ssid
								wlFingerSetVo.setESSID(sSid);
								// 插入apmac
								wlFingerSetVo.setApMac(scanResult.BSSID);
								wlFingerlist.add(wlFingerSetVo);
							}
						}
					}
					WLFingerSetRequestBean[] array=new WLFingerSetRequestBean[wlFingerlist.size()];
					wlFingerlist.toArray(array);
					this.publishProgress(array);
				}
				//添加提示信息
//				Toast.makeText(LocationUpdateActivity.this,	R.string.upload_success, Toast.LENGTH_SHORT).show();
			}
				
			}
			return null;
		}
		@Override
		protected void onProgressUpdate(WLFingerSetRequestBean... values) {
			//轮次累加
			groupNum++;
//			for (WLFingerSetRequestBean wlFingerSetRequestBean : values) {
//				//JSON
//				JSONObject jsonReqObj = new JSONObject();
//				
//				try {
//					String result="{";
//					result=result+wlFingerSetRequestBean.toJSONString()+",";
//					
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//				
//				
//			}
		//	String result = "[";
			StringBuffer reBuffer=new StringBuffer();
			JSONObject jsonReqObj = new JSONObject();
			JSONArray jsonArray=new JSONArray();
			JSONObject jsonObject=null;
			for (int i = 0; i < values.length; i++) {
				System.out.println(values[i]);
				jsonObject=new JSONObject();
				try {
					/*jsonObject.put("apMac",values[i].getApMac()  );
					jsonObject.put("collTime",values[i].getCollTime()  );
					jsonObject.put("pointAlias",values[i].getPointAlias()  );
					jsonObject.put("RSSI",values[i].getRSSI() );
					jsonObject.put("TOA",values[i].getTOA() );
					jsonObject.put("rate",values[i].getRate()  );
					jsonObject.put("ESSID",values[i].getESSID()  );
					jsonObject.put("mode",values[i].getMode()  );*/
					
					jsonObject.put("apMac",values[i].getApMac() == null ? "" :values[i].getApMac());
					jsonObject.put("collTime",values[i].getCollTime() == null ? "" :values[i].getCollTime());
					jsonObject.put("pointAlias",values[i].getPointAlias() == null ? "" :values[i].getPointAlias());
					jsonObject.put("RSSI",values[i].getRSSI()+"" == null ? "" :values[i].getRSSI());
					jsonObject.put("TOA",values[i].getTOA()+"" == null ? "" :values[i].getTOA());
					jsonObject.put("rate",values[i].getRate()+"" == null ? "" :values[i].getRate());
					jsonObject.put("ESSID",values[i].getESSID()== null ? "" :values[i].getESSID());
					jsonObject.put("mode",values[i].getMode() == null ? "" :values[i].getMode());
					
					Log.e(LOG_TAG,"显示组号信息");
					//在这里显示组号 和  编号  
//					Toast.makeText(LocationUpdateActivity.this,	"第"+groupNum+"轮,"+"第"+(++i)+"个,"+"采集点为:"+e1.getText().toString()+".", Toast.LENGTH_SHORT).show();
					Log.e(LOG_TAG,"第"+groupNum+"轮"+"第"+i+"个"+"采集点"+e1.getText().toString());
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				jsonArray.put(jsonObject);
			}
			//reBuffer.insert(0, "[").append("]");
			try {
				jsonReqObj.put("result", jsonArray);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			String result=jsonReqObj.toString();
			System.out.println("result-->"+result);
			JSONUtil.setServicePostionByHttp(url);
			JSONUtil jsonUtil = new JSONUtil();
			WlFingerSetResult wlFingerSetResult = jsonUtil.reportLocationRSSIListString(result);
			
		/*	System.out.println("Progress---" + values[0] + "--" + values[1]
					+ "--" + values[3]);
			// 封装信息并且做打包成json插入数据库
			// 导入要传的中间类
			WLFingerSetRequestBean wlFingerSetVo = new WLFingerSetRequestBean();
			// 插入时间
			java.util.Date date = new java.util.Date();
			// java.sql.Date data1=new java.sql.Date(date.getTime());
			wlFingerSetVo.setCollTime(DateUtils.formatDate(date,
					"yyyy-MM-dd HH:mm:ss"));
			// 插入位置
			wlFingerSetVo.setPointAlias(e1.getText().toString());
			// 插入信号强度
			// 在SSID下面下面的信号强度

			wlFingerSetVo.setRSSI(Integer.parseInt(values[2]));
			// 插入ssid
			wlFingerSetVo.setESSID(values[0]);
			// 插入apmac
			wlFingerSetVo.setApMac(values[1]);
			// （3）调用servlet接口循环插入到数据库中去 构造第五个数据 及 插入时间的数据   上报将信息获取一起上报
			// 初始化提交url 这次只用写ip地址即可
			JSONUtil.setServicePostionByHttp(values[3]);
			JSONUtil jsonUtil = new JSONUtil();
			WlFingerSetResult wlFingerSetResult = jsonUtil.reportLocationRSSI(wlFingerSetVo);*/
			if(groupNum%5==0){
				Toast.makeText(LocationUpdateActivity.this,	"第"+groupNum+"轮,上报结束", Toast.LENGTH_SHORT).show();
			}
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.location_update_layout, menu);
		return true;
	}
}
