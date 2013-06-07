package com.asc.app.ui;

import com.asc.app.R;
import com.asc.app.R.layout;
import com.asc.app.R.menu;
import com.asc.app.bean.request.WLFingerSetRequestBean;
import com.asc.app.database.DBManager;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ServerSettingActivity extends Activity {
	
	protected static final String LOG_TAG = "ServerSettingActivity";
	private EditText e1,e2,e3,e4;
	
	
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//添加没有标题栏的操作
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_setting);
        
        //获取数据库信息 并且插入数据库中（sqlite中去）
        e1=(EditText)findViewById(R.id.avURlvalue);
        e2=(EditText)findViewById(R.id.ssidvalue);
        e3=(EditText)findViewById(R.id.collect_period_value);
        e4=(EditText)findViewById(R.id.collect_rate_value);
        
        Button getInfo_btn = (Button) findViewById(R.id.serversettingbutton);
      
        //从activity中获取数据
        if(null!=this.getIntent().getExtras()){
            String urltmp = this.getIntent().getExtras().getString("url");
    		String sSidtmp = this.getIntent().getExtras().getString("sSid");
    		String collectPeriodtmp = this.getIntent().getExtras().getString("collectPeriod");
    		String collectRateValuetmp = this.getIntent().getExtras().getString("collectRateValue");
    		Log.e(LOG_TAG, urltmp + " " + sSidtmp);
    		
    		//并且为其田中值
    		e1.setText(urltmp);
    		e2.setText(sSidtmp);
    		e3.setText(collectPeriodtmp);
    		e4.setText(collectRateValuetmp);
    		
        }
   
        
        //服务器配置按钮
        getInfo_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				String url,sSid,collectPeriod,collectRateValue;
				//将信息插入数据库中去
				DBManager dbManager = DBManager.getInstance(ServerSettingActivity.this);
				dbManager.open();
				
				//数据库存储
				ContentValues contentValue=new ContentValues();
				contentValue.put("serveURL", e1.getText().toString());
				contentValue.put("SSID", e2.getText().toString());
				contentValue.put("collect_period", e3.getText().toString());
				contentValue.put("collect_rate", e4.getText().toString());
				try {
					//删除整表在插入
					dbManager.executeSql("delete from wlfinger");
					Long id = dbManager.insert("wlfinger",null,contentValue);
					if(id>0){
						//跳到主页面
						Intent intentAD = new Intent(ServerSettingActivity.this, ASCActivity.class);
						startActivity(intentAD);
					}else{
						//插入失败
						Toast.makeText(ServerSettingActivity.this, R.string.server_setting_promot, Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}		
			
		});
        
        //打开实时服务按钮
        
        
        
        
        //关闭实时服务按钮
        
        
        
        //返回页面按钮
        Button return_btn = (Button) findViewById(R.id.return_btn);
        return_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_server_setting, menu);
        return true;
    }
}
