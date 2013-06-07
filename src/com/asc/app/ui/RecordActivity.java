package com.asc.app.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.asc.app.R;
import com.asc.app.database.DBManager;
/**
 * @author zhanglei
 *
 */
public class RecordActivity extends ListActivity {
	private ListView messageListView;
	private final int MENU_VIEW = 1;
	private final int MENU_DELETE = 2;
	private List<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
	private MyAdapter myAdapter;
	DBManager dbManager = DBManager.getInstance(RecordActivity.this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message);
		
		Button return_btn = (Button) findViewById(R.id.return_btn);
		return_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		messageListView = (ListView) findViewById(android.R.id.list);
		
		getData();                              
		myAdapter = new MyAdapter(this);
		messageListView.setAdapter(myAdapter);
		messageListView.setCacheColorHint(0);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case MENU_VIEW:
			Intent intentAD = new Intent(this, ADActivity.class);
			intentAD.putExtra("adUrl", dataList.get(menuInfo.position).get("ad_url").toString());
			intentAD.putExtra("location", dataList.get(menuInfo.position).get("message_text").toString());
			startActivity(intentAD);
			break;
		case MENU_DELETE:
			dbManager.open();
			String sql = "delete from ad_record where adUrl = '"
					+ dataList.get(menuInfo.position).get("ad_url").toString() + "'";
			dbManager.executeSql(sql);
			getData();
			myAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	/**
	 * 
	 */
	private void getData() {
		dataList.clear();
		dbManager.open();
		Cursor cursor = null;
		try {
			cursor = dbManager.findAll("ad_record", null);
			if (cursor.moveToFirst()) {
				for (int i = 0; i < cursor.getCount(); i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("message_title", cursor.getString(1));
					map.put("message_text", cursor.getString(2));
					map.put("message_date", cursor.getString(3));
					map.put("ad_url", cursor.getString(4));
					cursor.moveToNext();
					dataList.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbManager.close();
		}
	}
	
	private class MyAdapter extends BaseAdapter{
		private LayoutInflater layoutInflater;
		
		public MyAdapter(Context context) {
			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {	
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.message_list, null);
				holder.title = (TextView) convertView.findViewById(R.id.message_title);
				holder.text = (TextView) convertView.findViewById(R.id.message_text);
				holder.date = (TextView) convertView.findViewById(R.id.message_date);
				holder.deleteBtn = (Button) convertView.findViewById(R.id.delete_btn);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.title.setText((String)dataList.get(position).get("message_title"));
			holder.text.setText((String)dataList.get(position).get("message_text"));
			holder.date.setText((String)dataList.get(position).get("message_date"));
			
			final int mPostion = position;
			holder.deleteBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
					builder.setTitle(R.string.tips);
					builder.setMessage(R.string.sure_to_delete);
					builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {}
					});
					builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dbManager.open();
							String sql = "delete from ad_record where adUrl = '" + dataList.get(mPostion).get("ad_url").toString() + "'";
							dbManager.executeSql(sql);
							getData();
							myAdapter.notifyDataSetChanged();
						}
					});
					builder.show();
				}
			});
			convertView.setOnClickListener(new OnItemClickListener(position));
			return convertView;
		}
	}
	
	private class OnItemClickListener implements OnClickListener {
		private int mPosition;

		OnItemClickListener(int position) {
			mPosition = position;
		}

		@Override
		public void onClick(View view) {
			Intent intentAD = new Intent(RecordActivity.this, ADActivity.class);
			intentAD.putExtra("adUrl", dataList.get(mPosition).get("ad_url").toString());
			intentAD.putExtra("location", dataList.get(mPosition).get("message_text").toString());
			startActivity(intentAD);
		}
	}
	
	private final class ViewHolder {  
        public TextView title;  
        public TextView text;  
        public TextView date;
        public Button deleteBtn;  
    }  
}