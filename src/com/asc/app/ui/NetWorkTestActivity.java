package com.asc.app.ui;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.database.CursorJoiner.Result;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.asc.app.R;



public class NetWorkTestActivity extends Activity {
	
	private  Editable replyEditable=null;
	private  EditText commandInput = null;
	private  ReplyTask replyTask=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_work_test);
        
        
        //返回
        Button returnButton=(Button) findViewById(R.id.return_btn);
        returnButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(replyTask!=null&&!replyTask.isCancelled()){
					replyTask.cancel(true);
				}
				startActivity(new Intent(NetWorkTestActivity.this, ASCActivity.class));
			}
		});
        
        
        
        commandInput = (EditText) findViewById(R.id._text_command);
        Button actionButton=(Button) findViewById(R.id._btn_execute);
        EditText replyArea=(EditText) findViewById(R.id._reply_area);
		replyEditable=replyArea.getEditableText();
		
		
        actionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 Editable editable = commandInput.getEditableText();
				 String command=  editable.toString();
				 NetWorkTestActivity.this.startCommandJob(command);
			}
		});
        
        
        
        
       
        //终止命令
        Button abortButton= (Button) findViewById(R.id._btn_abort);
        abortButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 if (replyTask!=null&&!replyTask.isCancelled()) {
					 replyTask.cancel(true);
				 }
			}
		});
        
        //清除控制台输出
        /*Button clearConsoleButton= (Button) findViewById(R.id._reply_clear);
        clearConsoleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				replyEditable.clear();
			}
		});*/
        
        replyArea.setKeepScreenOn(true);
        
        replyArea.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int code, KeyEvent event) {
				Log.d("s", code+"");
				if(KeyEvent.KEYCODE_ENTER==code&&event.getAction()==KeyEvent.ACTION_DOWN){	 
					 String replyContent=replyEditable.toString();
					 String []replylines=replyContent.trim().split("\n");
					 if(replylines.length>0){
						 String command=replylines[replylines.length-1];
						 Log.d("command", command);
						 startCommandJob(command+"\n");
						 return true;
					 }
				}
				return false;
			}
		});
        
    }

   
    protected void startCommandJob(String command) {
    	if (command==null||(command!=null&&command.trim().length()==0)) {
    		Toast.makeText(NetWorkTestActivity.this, R.string.command_alert, Toast.LENGTH_SHORT).show();
			return;
		}
    	
    	
    	 if (replyTask!=null&&!replyTask.isCancelled()) {
			 replyTask.cancel(true);
		 }
		 replyTask=new ReplyTask();
		 replyTask.execute(command);
	}
    
	private class ReplyTask extends AsyncTask<String , String, Result>{
    	
    	private Process process =null;
		@Override
		protected Result doInBackground(String... params) {
			try {
				Log.i("NetWorkTestActivity", params[0]);
				process = Runtime.getRuntime().exec(params[0]);
				InputStream reply = process.getInputStream();
				
				Log.d("Thread", "start");
				byte[] buffer=new byte[1024];
				int len=-1;
				try {
					String replyLine=null;
					this.publishProgress("\n\n");
					while((len=reply.read(buffer))!=-1){
					    replyLine=new String(buffer, 0, len);
						Log.d("NetWorkTestActivity", replyLine);
						if (isCancelled()) {
							break;
						}
						this.publishProgress(replyLine);
					}
					this.publishProgress("\n");
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					if (reply!=null) {
						try {
							reply.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			replyEditable.append(values[0]);
		}

		@Override
		protected void onCancelled() {
			Log.i("NetWorkTestActivity","############cancel!####onCancelled###############");
			process.destroy();
		}
    	
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_net_work_test, menu);
        return super.onCreateOptionsMenu(menu);
    }


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int itemId=item.getItemId();
		Log.d("MENU", itemId+"");
		switch (itemId) {
		case R.id.clear_console:
			replyEditable.clear();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
    
    
    
}
