package com.asc.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author zhanglei
 * 
 */
public class DBManager {
	private static final String DB_NAME = "asc.db";

	private static final int DB_VERSION = 1;

	private SQLiteDatabase mSQLiteDatabase = null;

	private DatabaseHelper mDatabaseHelper = null;

	private Context mContext = null;
	
	private static DBManager dbConn= null;
	
	private Cursor cursor;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE wifi_user(account TEXT PRIMARY KEY, password TEXT, isDefault INTEGER, balance DECIMAL(10,2), expireTime TEXT, leftTime LONG, userState INTEGER, productId INTEGER);");
			db.execSQL("CREATE TABLE product(id INTEGER PRIMARY KEY, productName TEXT, billTime INTEGER, timeUnit TEXT, moneyCount DOUBLE(12,3), baseFee INTEGER, maxFee INTEGER, isLimitFee INTEGER, maxUpBytes INTEGER, maxDownByte INTEGER);");
			db.execSQL("CREATE TABLE charge_record(id INTEGER PRIMARY KEY, account TEXT, chargeCount DECIMAL(10,2), chargeTime TEXT, username TEXT);");
			db.execSQL("CREATE TABLE bill(id INTEGER PRIMARY KEY, account TEXT,billTime LONG, billCount DECIMAL(10,2), totalBytes LONG, createDate TEXT);");
			db.execSQL("CREATE TABLE traffic_soft(id INTEGER PRIMARY KEY, softUid INTEGER,softName TEXT, softLogo TEXT, mobileTraffic LONG, createDate TEXT);");
			db.execSQL("CREATE TABLE traffic_daily(id INTEGER PRIMARY KEY, phoneTraffic LONG, wifiTraffic LONG, createDate TEXT);");
			db.execSQL("CREATE TABLE ad_record(id INTEGER PRIMARY KEY, adInfo TEXT, location TEXT, createDate TEXT, adUrl TEXT, bssid TEXT);");
			//创建位置上报的sql语句
			db.execSQL("CREATE TABLE wlfinger(id INTEGER PRIMARY KEY, serveURL TEXT, SSID TEXT, collect_period TEXT, collect_rate TEXT, creDate TEXT);");
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS wifi_user");
			db.execSQL("DROP TABLE IF EXISTS product");
			db.execSQL("DROP TABLE IF EXISTS charge_record");
			db.execSQL("DROP TABLE IF EXISTS bill");
			db.execSQL("DROP TABLE IF EXISTS traffic_soft");
			db.execSQL("DROP TABLE IF EXISTS traffic_daily");
			db.execSQL("DROP TABLE IF EXISTS ad_record");
			//删除位置信息表结构
			db.execSQL("DROP TABLE IF EXISTS wlfinger");
			onCreate(db);
		}
	}

	private DBManager(Context mContext) {
		super();
		this.mContext = mContext;
	}
	
	public static DBManager getInstance(Context mContext){
		if (null == dbConn) {
			dbConn = new DBManager(mContext);
		}
		return dbConn;
	}

	public void open() {
		if (null == mSQLiteDatabase || !mSQLiteDatabase.isOpen()) {
			mDatabaseHelper = new DatabaseHelper(mContext);
			mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
		}
	}

	public void close() {
		if (null != cursor && !cursor.isClosed()) {
			cursor.close();
		}
		if (mSQLiteDatabase != null && mSQLiteDatabase.isOpen()) {
			mSQLiteDatabase.close();
		}
		if (null != mDatabaseHelper) {
			mDatabaseHelper.close();
		}
	}

	/**
	 * 
	 * @param tableName
	 * @param nullColumn
	 * @param contentValues
	 * @return
	 * @throws Exception
	 */
	public long insert(String tableName, String nullColumn,
			ContentValues contentValues) throws Exception {
		try {
			return mSQLiteDatabase.insert(tableName, nullColumn, contentValues);
		} catch (Exception e) {
			throw e;
		}
	}

	public long delete(String tableName, String key, int id) throws Exception {
		try {
			return mSQLiteDatabase.delete(tableName, key + " = " + id, null);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public Cursor findAll(String tableName, String [] columns) throws Exception{
		try {
			cursor = mSQLiteDatabase.query(tableName, columns, null, null, null, null, null);
			cursor.moveToFirst();
			return cursor;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public Cursor findById(String tableName, String key, int id, String [] columns) throws Exception {
		try {
			return mSQLiteDatabase.query(tableName, columns, key + " = " + id, null, null, null, null);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public Cursor find(String tableName, String [] names, String [] values, String [] columns, String orderColumn, String limit) throws Exception{
		try {
			StringBuffer selection = new StringBuffer();
			for (int i = 0; i < names.length; i++) {
				selection.append(names[i]);
				selection.append(" = ?");
				if (i != names.length - 1) {
					selection.append(",");
				}
			}
			cursor = mSQLiteDatabase.query(true, tableName, columns, selection.toString(), values, null, null, orderColumn, limit);
			cursor.moveToFirst();
			return cursor;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * Convenience method for updating rows in the database.
	 * @param tableName
	 * @param names
	 * @param values
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public boolean update(String tableName, String [] names, String [] values, ContentValues args) throws Exception{
		try {
			StringBuffer selection = new StringBuffer();
			for (int i = 0; i < names.length; i++) {
				selection.append(names[i]);
				selection.append(" = ?");
				if (i != names.length - 1) {
					selection.append(",");
				}
			}
			return mSQLiteDatabase.update(tableName, args, selection.toString(), values) > 0;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * CREATE TABLE, DELETE, INSERT
	 * @param sql
	 */
	public void executeSql(String sql) {
		mSQLiteDatabase.execSQL(sql);
	}
	
	/**
	 * Runs the provided SQL and returns a Cursor over the result set.
	 * @param sql
	 * @param selectionArgs
	 * @return
	 */
	public Cursor executeSqlQuery(String sql, String [] selectionArgs){
		return mSQLiteDatabase.rawQuery(sql, selectionArgs);
	}

}
