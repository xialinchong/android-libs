package com.yidianhulian.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
/**
 * key-value 数据库存储处理
 * 
 * @author leeboo
 *
 */
public class KVHandler extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "favorite_addresses";
    private static final String TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                		BaseColumns._ID + " INTEGER, " +
                " name VARCHAR(45)," +
                " value TEXT);";
    private static final String UPGRADE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
            		BaseColumns._ID + " INTEGER, " +
            " name VARCHAR(45)," +
            " value TEXT);";
    
	public KVHandler(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		 db.execSQL(TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
//		if(newVer > oldVer){
//			db.execSQL(UPGRADE_CREATE);
//		}
	}

	/**
	 * 返回name的所有的值
	 * 
	 * @param context
	 * @param name
	 * @return
	 */
	public static List<String> getValues(Context context, String name){
		KVHandler kvh = new KVHandler(context, "dache", null, DATABASE_VERSION);
		SQLiteDatabase db = kvh.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, new String[]{BaseColumns._ID, "name","value"}, "name=?", new String[]{name}, null, null, null);
		List<String> list = new ArrayList<String>();
		while (cursor.moveToNext()) {  
			list.add(cursor.getString(cursor.getColumnIndex("value")));  
        }
		cursor.close();
		db.close();
		return list;
	}
	
	/**
	 * 增加一个name的value
	 * 
	 * @param context
	 * @param name
	 * @param value
	 */
	public static void addValue(Context context, String name, String value){
		KVHandler kvh = new KVHandler(context, "dache", null, DATABASE_VERSION);

		List<String> exists = KVHandler.getValues(context, name);
		if(exists.contains(value)){
			return;
		}
		
		SQLiteDatabase wdb = kvh.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("value", value);
		cv.put("name", name);
    	wdb.insert(TABLE_NAME, null, cv);
		wdb.close();
	}
}
