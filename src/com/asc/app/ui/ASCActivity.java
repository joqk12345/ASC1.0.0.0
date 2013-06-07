package com.asc.app.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.Toast;



import com.asc.app.R;
import com.asc.app.bean.request.BaseRequestBean;
import com.asc.app.bean.response.GetVersionResult;
import com.asc.app.database.DBManager;
import com.asc.app.json.JSONUtil;
import com.asc.app.ui.base.BaseActivity;
import com.asc.app.util.IntentUtil;
import com.asc.app.util.IpUtil;
import com.asc.app.util.UpdateUtil;

/**
 * @author zhanglei
 * 
 * @updater joqk
 * 
 * @date 2013年6月5日15:33:28
 *
 */
public class ASCActivity extends BaseActivity implements OnClickListener,OnDrawerOpenListener,OnDrawerCloseListener {
	
	protected static final String LOG_TAG = "ASCActivity";
	
	private Button settings_btn;
	private Button message_btn;
	private Button record_btn;
	private Button traffic_btn;
	
	//	快捷键显示
	//最新公告
	private Button quick_message_btn1;
	private Button quick_message_btn2;
	//wifi快速设置
	private Button quick_wifisetting1;
	private Button quick_wifisetting2;
	//信号检测
	private Button quick_signal_btn1;
	private Button quick_signal_btn2;
	//网络工具
	private Button network_tools_btn1;
	private Button network_tools_btn2;
	//服务器配置
	private Button quick_serversetting1;
	private Button quick_serversetting2;
	//位置上报
	private Button location_updatesetting1;
	private Button location_updatesetting2;
	//检查更新
	private Button check_update_btn1;
	private Button check_update_btn2;
	//抽屉菜单
	private SlidingDrawer slidingMenu;
	
	/**************/
	private ProgressDialog progressDialog;
	private ImageView imageView =null;
//	private static final String LOG_TAG = "ASCActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//初始化控件
		initControl();
		
	}
	/**
	 * 初始化控件
	 */
	private void initControl() {
		//系统设置
		settings_btn = (Button) findViewById(R.id.settings_btn);
		settings_btn.setOnClickListener(this);
		//流量监控
		traffic_btn = (Button) findViewById(R.id.traffic_btn);
		traffic_btn.setOnClickListener(this);
		//历史广告记录
		record_btn = (Button) findViewById(R.id.record_btn);
		record_btn.setOnClickListener(this);
		//最新公告
		message_btn = (Button) findViewById(R.id.message_btn);
		message_btn.setOnClickListener(this);
		// 最新公告-快捷键
		quick_message_btn1 = (Button) findViewById(R.id.quick_message_btn1);
		quick_message_btn1.setOnClickListener(this);
		quick_message_btn2 = (Button) findViewById(R.id.quick_message_btn2);
		quick_message_btn2.setOnClickListener(this);
		// WIFI快速设置-快捷键
		quick_wifisetting1 = (Button) findViewById(R.id.quick_wifisetting1);
		quick_wifisetting1.setOnClickListener(this);
		quick_wifisetting2 = (Button) findViewById(R.id.quick_wifisetting2);
		quick_wifisetting2.setOnClickListener(this);
		// 信号监测-快捷键
		quick_signal_btn1 = (Button) findViewById(R.id.quick_signal_btn1);
		quick_signal_btn1.setOnClickListener(this);
		quick_signal_btn2 = (Button) findViewById(R.id.quick_signal_btn2);
		quick_signal_btn2.setOnClickListener(this);
		//网络工具
		network_tools_btn1=(Button) findViewById(R.id.quick_network_btn1);
		network_tools_btn1.setOnClickListener(this);
		network_tools_btn2=(Button) findViewById(R.id.quick_network_btn2);
		network_tools_btn2.setOnClickListener(this);
		// 服务器配置-快捷键
		quick_serversetting1 = (Button) findViewById(R.id.setting_server_btn1);
		quick_serversetting1.setOnClickListener(this);
		quick_serversetting2 = (Button) findViewById(R.id.setting_server_btn2);
		quick_serversetting2.setOnClickListener(this);
		// 位置上报-快捷键
		location_updatesetting1 = (Button) findViewById(R.id.location_update_btn1);
		location_updatesetting1.setOnClickListener(this);
		location_updatesetting2 = (Button) findViewById(R.id.location_update_btn2);
		location_updatesetting2.setOnClickListener(this);
		//检查更新快捷键
		check_update_btn1 = (Button) findViewById(R.id.check_update_btn1);
		check_update_btn1.setOnClickListener(this);
		check_update_btn2 = (Button) findViewById(R.id.check_update_btn2);
		check_update_btn2.setOnClickListener(this);
		//抽屉菜单
		slidingMenu=(SlidingDrawer) findViewById(R.id.sliding);
		slidingMenu.setOnClickListener(this);
		slidingMenu.setOnDrawerOpenListener(this);
		slidingMenu.setOnDrawerCloseListener(this);
		imageView =(ImageView) findViewById(R.id.imageViewIcon);
		imageView.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		 switch (v.getId()) {
		 //一般键设置
         case R.id.settings_btn:
        	 	IntentUtil.start_activity(ASCActivity.this,SettingActivity.class);
             break;
         case R.id.traffic_btn:
        	 	IntentUtil.start_activity(this,TrafficActivity.class);
             break;
         case R.id.record_btn:
        		IntentUtil.start_activity(this,RecordActivity.class);
             break;
         case R.id.message_btn:
        	 toSeeAnnouncement();
             break;
         //快捷键设置
         case R.id.quick_message_btn1:
        	 toSeeAnnouncement();
          break;
         case R.id.quick_message_btn2:
        	 toSeeAnnouncement();
           break;
         case R.id.quick_signal_btn1 :
        	 startActivity(new Intent(ASCActivity.this, SignalListActivity.class));
            break;
         case R.id.quick_signal_btn2:
        	 startActivity(new Intent(ASCActivity.this, SignalListActivity.class));
            break;
         case R.id.quick_wifisetting1 :
        	 startActivity(new Intent(ASCActivity.this, AccessPointActivity.class));
            break;
         case R.id.quick_wifisetting2:
        	 startActivity(new Intent(ASCActivity.this, AccessPointActivity.class));
            break;
         case R.id.quick_network_btn1 :
        	 IntentUtil.start_activity(this, NetWorkTestActivity.class);
            break;
         case R.id.quick_network_btn2:
        	 IntentUtil.start_activity(this, NetWorkTestActivity.class);
            break;
         case R.id.setting_server_btn1 :
        	 toSetserverConfig();
            break;
         case R.id.setting_server_btn2:
        	 toSetserverConfig();
            break;
         case R.id.location_update_btn1 :
        	 IntentUtil.start_activity(this, LocationUpdateActivity.class);
            break;
         case R.id.location_update_btn2:
        	 IntentUtil.start_activity(this, LocationUpdateActivity.class);
            break;
         case R.id.check_update_btn1 :
        	 checkupdate();
            break;
         case R.id.check_update_btn2:
        	 checkupdate();
            break;
		 }
	}
	
	/**
	 * 设置抽屉菜单的开关状态
	 * */
	@Override
	public void onDrawerClosed() {
		imageView.setImageResource(R.drawable.bar1);
	}
	@Override
	public void onDrawerOpened() {
		imageView.setImageResource(R.drawable.bar);		
	}
	
	/**
	 * 最新公告 
	 */
	private void toSeeAnnouncement(){
		String url = "";
		String location = "";
		DBManager dbManager = DBManager.getInstance(ASCActivity.this);
		dbManager.open();
		Cursor cursor = dbManager.executeSqlQuery("select adUrl,location from ad_record where id =(select max(id) from ad_record)", null);
		if (cursor.getCount() > 0 && cursor.moveToFirst()) {
			url = cursor.getString(0);
			location = cursor.getString(1); 
		}
		if (!url.equals("")) {
			IntentUtil.start_activity(this, ADActivity.class, new BasicNameValuePair("adUrl", url), 
															  new BasicNameValuePair("location", location));
		} else {
			Toast.makeText(ASCActivity.this, R.string.no_message, Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 设置服务配置
	 * joqk
	 */
	private void toSetserverConfig(){
		String url="";
		String sSid="";
		String collectPeriod="";
		String collectRateValue="";
		//从数据库中取出数据
		DBManager dbManager = DBManager.getInstance(ASCActivity.this);
		dbManager.open();
		Cursor cursor = dbManager.executeSqlQuery("select serveURL,SSID,collect_period,collect_rate from wlfinger", null);
		if (cursor.getCount() > 0 && cursor.moveToFirst()) {
			if(url!=null)
				url= cursor.getString(0);	
			if(sSid!=null)
				sSid = cursor.getString(1);
			if(collectPeriod!=null)
				collectPeriod = cursor.getString(2);
			if(url!=null)
				collectRateValue = cursor.getString(3);
		}
		//如果数据 不为空 不用设置  
		if (null!=url && !url.equals("")) {
			IntentUtil.start_activity(this, ServerSettingActivity.class,  new BasicNameValuePair("url", url), 
																		  new BasicNameValuePair("sSid", sSid), 
																		  new BasicNameValuePair("collectPeriod", collectPeriod),
																		  new BasicNameValuePair("collectRateValue", collectRateValue));
			//Toast.makeText(ASCActivity.this, R.string.server_setting_promot2, Toast.LENGTH_SHORT).show();
		} else {
			 IntentUtil.start_activity(this, ServerSettingActivity.class);
		}
	}
	
	/**
	 * 检查服务更新情况
	 */
	private void checkupdate(){
			ConnectivityManager connManger = (ConnectivityManager) ASCActivity.this.getSystemService( Context.CONNECTIVITY_SERVICE );
			if (connManger.getActiveNetworkInfo() == null) {
				Toast.makeText(ASCActivity.this, getString(R.string.network_not_avilable), Toast.LENGTH_LONG).show();
				return;
			}
			
			progressDialog = new ProgressDialog(ASCActivity.this);
			progressDialog.setTitle(R.string.software_update);
			progressDialog.setMessage(getString(R.string.update_check_wait));
			progressDialog.show();
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					JSONUtil jsonUtil = new JSONUtil();
					BaseRequestBean baseRequest = new BaseRequestBean();
					baseRequest.setOperateID(UpdateUtil.generateId());
					baseRequest.setMethod("getVersion");
					GetVersionResult version = jsonUtil.getVersion(baseRequest);
					if (null == version) {
						return;
					}
					int oldVersion = UpdateUtil.getVersionCode(ASCActivity.this);
					Log.e("ASCActivity", "old: " + oldVersion + " new: " + version.getVersionCode());
					Message message = new Message();
					if (version.getVersionCode() > oldVersion) {
						message.what = 1;
					}else {
						message.what = 2;
					}
					handler.sendMessage(message);
				}
			}).start();
	}
	
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			AlertDialog.Builder builder;
			switch (msg.what) {
			case 1:
				progressDialog.cancel();
				builder = new Builder(ASCActivity.this);
				builder.setTitle(getString(R.string.software_update));
				builder.setIcon(R.drawable.menu);
				builder.setMessage(R.string.update_available);
				builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						progressDialog = new ProgressDialog(ASCActivity.this);
						progressDialog.setTitle(R.string.software_update);
						progressDialog.setMessage(getString(R.string.update_wait));
						getFile();
					}
				});
				builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				});
				builder.show();
				break;
			case 2:
				builder = new Builder(ASCActivity.this);
				builder.setTitle(getString(R.string.software_update));
				builder.setIcon(R.drawable.menu);
				builder.setMessage(R.string.no_update);
				builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				});
				builder.show();
				break;
			default:
				break;
			}
		};
	};
	

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.exit).setIcon(R.drawable.close);
		return super.onCreateOptionsMenu(menu);
	}
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case 1:
//			this.stopService(new Intent(this, AdvertiseService.class));
//			this.stopService(new Intent(this, TrafficService.class));
			ASCActivity.this.finish();
			System.exit(0);
			break;
		default:
			break;
		}
    	return super.onOptionsItemSelected(item);
    }
    
    private void getFile(){
    	progressDialog.show();
    	new Thread(new Runnable() {
			@Override
			public void run() {
				InputStream is = null;
				FileOutputStream fos = null;
				try {
					WifiManager mWifiManager = (WifiManager) ASCActivity.this.getSystemService(Context.WIFI_SERVICE);
					String server = IpUtil.intToIp(mWifiManager.getDhcpInfo().serverAddress);
					UpdateUtil.setServerPostion(server);
					UpdateUtil updateConfig = new UpdateUtil();
					is = updateConfig.getUpdateFile();
					if (is == null) 
			        {  
			          Log.e("updateAPK","input stream is null");
			          progressDialog.cancel();
			          return;
			        }
					File myTempFile = File.createTempFile(UpdateUtil.SAVED_APKPREFIX, "."+UpdateUtil.SAVED_APKSUFFIX); 
			        myTempFile.getAbsolutePath(); 
			        Log.e("updateAPK","AbsolutePath(): " + myTempFile.getAbsolutePath());
			        /*将文件写入临时盘*/ 
			        fos = new FileOutputStream(myTempFile); 
			        byte buf[] = new byte[128];   
			        int byteCount;
			        while ((byteCount = is.read(buf)) > 0) {
						fos.write(buf, 0, byteCount);
					}
			        updateConfig.destroy();
			        openFile(myTempFile);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					if (null != is) {
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (null != fos) {
						try {
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
    }
    
	private void openFile(File file) {
		progressDialog.cancel();
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		startActivity(intent);
	}

}