package com.growatt.shinetools.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.constant.GlobalConstant;

import com.growatt.shinetools.utils.LogUtil;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class SqliteUtil {
	public static String inquiryIs(){
		String times="";
		DateSqlite dataSQiLte=new DateSqlite(ShineToosApplication.getContext());
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		Cursor c = base.query("islogin",null,null,null,null,null,null);
		if(c.getCount()>0){
			if(c.moveToFirst()){
				for(int i=0;i<c.getCount();i++){
					times=c.getString(c.getColumnIndex("falg"));
					c.moveToNext();
				}
			}
		}
		c.close();
		base.close();
		return times;
	}
	public static void islogin(String time){
		DateSqlite dataSQiLte=new DateSqlite(ShineToosApplication.getContext());
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		base.delete("islogin",null, null);
		ContentValues values = new ContentValues();
		values.put("falg", time);
		base.insert("islogin", null, values);
		base.close();
	}
	public static String inquirytime(){
		String times = "";
		DateSqlite dataSQiLte=new DateSqlite(ShineToosApplication.getContext());
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		Cursor c = base.query("times",null,null,null,null,null,null);
		if(c.getCount()>0){
			if(c.moveToFirst()){
				for(int i=0;i<c.getCount();i++){
					times=c.getString(c.getColumnIndex("time"));
					c.moveToNext();
				}
			}
		}
		c.close();
		base.close();
		return times;
	}
	public static String inquiryplant(){
		String times = "";
		DateSqlite dataSQiLte=new DateSqlite(ShineToosApplication.getContext());
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		Cursor c = base.query("plant",null,null,null,null,null,null);
		if(c.getCount()>0){
			if(c.moveToFirst()){
				for(int i=0;i<c.getCount();i++){
					times=c.getString(c.getColumnIndex("plant"));
					c.moveToNext();
				}
			}
		}
		c.close();
		base.close();

		return times;
	}




    /**
     * 查询所有用户登录用户名和密码:map
     * @return
     */
	public static Map<String, String> inquiryloginAll(){
		Map<String, String> map = new LinkedHashMap<>();
		DateSqlite dataSQiLte=new DateSqlite(ShineToosApplication.getContext());
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		Cursor c = base.query("login",null,null,null,null,null,null);
		if(c.getCount()>0){
			if (c.moveToLast()){
				for (int i = 0; i < c.getCount(); i++) {
					map.put(c.getString(c.getColumnIndex("name")),c.getString(c.getColumnIndex("pwd")));
					c.moveToPrevious();
				}
			}
		}
		c.close();
		base.close();
		return map;
	}


	public static void time(String time){
		DateSqlite dataSQiLte=new DateSqlite(ShineToosApplication.getContext());
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		base.delete("times",null, null);
		ContentValues values = new ContentValues();
		values.put("time", time);
		base.insert("times", null, values);
		base.close();
	}


	public static void deleteUser(){
		DateSqlite dataSQiLte=new DateSqlite(ShineToosApplication.getContext());
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		base.delete("login",null, null);
		base.close();
	}
	public static void plant(String time){
		DateSqlite dataSQiLte=new DateSqlite(ShineToosApplication.getContext());
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		base.delete("plant",null, null);
		ContentValues values = new ContentValues();
		values.put("plant", time);
		base.insert("plant", null, values);
		base.close();
	}

	public static void ids(Map<String, Object> map){
		DateSqlite dataSQiLte=new DateSqlite(ShineToosApplication.getContext());
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		Cursor c = base.query("ids",new String[]{"deviceSn"},"deviceSn = ?",new String[]{map.get("deviceSn").toString()},null,null,null);
		ContentValues values = new ContentValues();
		values.put("plant",map.get("plant").toString() );
		values.put("deviceAilas",map.get("deviceAilas").toString() );
		values.put("deviceType",map.get("deviceType").toString() );
		values.put("deviceSn",map.get("deviceSn").toString() );
		values.put("deviceStatus", map.get("deviceStatus").toString());
		values.put("energy",map.get("energy").toString() );
		values.put("datalogSn", map.get("datalogSn").toString());
		values.put("id",map.get("id").toString() );
		
		values.put("userId", map.get("userId").toString());
		if(c.getCount()>0){
			if(c.moveToFirst()){
				for(int i=0;i<c.getCount();i++){
					String s = c.getString(c.getColumnIndex("deviceSn"));
					//					System.out.println("deviceSn="+s);
					if(s.equals(map.get("deviceSn").toString())){
						base.execSQL("update ids set id='"+map.get("id").toString()+"' where deviceSn='"+map.get("deviceSn").toString()+"'");
						c.close();
						base.close();
						break;
					}else{
						// update ids set deviceSn='"+deviceSn+"' where id=?;
						c.moveToNext();
					}
				}
			}
		}else{
			base.insert("ids", null, values);
			c.close();
			base.close();
		}
	}


	//	map.put("deviceAilas", jsonObject.get("deviceAilas").toString());
	//	map.put("deviceType", jsonObject.get("deviceType").toString());
	//	map.put("deviceSn", jsonObject.get("deviceSn").toString());
	//	map.put("deviceStatus", jsonObject.get("deviceStatus").toString());
	//	map.put("energy", jsonObject.get("energy").toString());
	//	map.put("datalogSn", jsonObject.get("datalogSn").toString());

	public static List<Map<String, Object>> inquiryids(String plants){
		List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();
		DateSqlite dataSQiLte=new DateSqlite(ShineToosApplication.getContext());
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		Cursor c = base.rawQuery("select * from ids where plant = '"+plants+"';", null);
		if(c.getCount()>0){
			if(c.moveToFirst()){
				for(int i=0;i<c.getCount();i++){
					Map<String, Object> map=new HashMap<String, Object>();
					String plant = c.getString(c.getColumnIndex("plant"));
					String deviceAilas=c.getString(c.getColumnIndex("deviceAilas"));
					String deviceType=c.getString(c.getColumnIndex("deviceType"));
					String deviceSn=c.getString(c.getColumnIndex("deviceSn"));
					String energy=c.getString(c.getColumnIndex("energy"));
					String deviceStatus=c.getString(c.getColumnIndex("deviceStatus"));
					String datalogSn=c.getString(c.getColumnIndex("datalogSn"));
					String id=c.getString(c.getColumnIndex("id"));
					String userId=c.getString(c.getColumnIndex("userId"));
					map.put("plant", plant);
					map.put("deviceAilas", deviceAilas);
					map.put("deviceType", deviceType);
					map.put("deviceSn", deviceSn);
					map.put("deviceStatus", deviceStatus);
					map.put("energy", energy);
					map.put("datalogSn", datalogSn);
					map.put("id", id);
					map.put("userId", userId);
					list.add(map);
					c.moveToNext();
				}
			}
		}
		c.close();
		base.close();
		return list;
	}


	public static void setService(String service, int app_code){
		DateSqlite dataSQiLte=new DateSqlite(ShineToosApplication.getContext());
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("service", service);
		values.put("app_code", app_code);
		base.insert("service", null, values);
		base.close();
	}
	public static String getService(){
		String service="";
		DateSqlite dataSQiLte=new DateSqlite(ShineToosApplication.getContext());
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		Cursor c = base.query("service",null,null,null,null,null,null);
		if(c.getCount()>0){
			if(c.moveToFirst()){
				for(int i=0;i<c.getCount();i++){
					service=c.getString(c.getColumnIndex("service"));
					c.moveToNext();
				}
			}
		}
		c.close();
		base.close();
		return service;
	}
	//��ȡapp_code���º�
	public static int getApp_Code(){
		int app_code=-1;
		DateSqlite dataSQiLte=new DateSqlite(ShineToosApplication.getContext());
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		Cursor c = base.query("service",null,null,null,null,null,null);
		if(c.getCount()>0){
			if(c.moveToFirst()){
				for(int i=0;i<c.getCount();i++){
					app_code=c.getInt(c.getColumnIndex("app_code"));
					c.moveToNext();
				}
			}
		}
		c.close();
		base.close();
		return app_code;
	}
	/*查找apn*/
	public static String apn(String simOperatorName, String numeric){

		String name = "";
		SQLiteOpenHelper dataSQiLte=new DateSqlite(ShineToosApplication.getContext());
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		Cursor c = base.query("carriers",new String[]{"numeric"},"numeric = "+numeric,null,null,null,null);
		if(c.getCount()>0){
			if(c.moveToFirst()){
				for(int i=0;i<c.getCount();i++){
					name=c.getString(c.getColumnIndex("name"));
					Log.i("apnName", "apnName="+name);
					c.moveToNext();
				}
			}
		}
		c.close();
		base.close();

		return name;
	}

	/**
	 * 存储大批量数据
	 */
	public static void  setListJson(String tag , String jsonValue){
		DateSqlite dataSQiLte=new DateSqlite(ShineToosApplication.getContext());
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		base.delete(GlobalConstant.MAX_TOOLS_SQLITE,"jsonkey=?",new String[]{tag});
		ContentValues values = new ContentValues();
		values.put("jsonkey", tag);
		values.put("value",jsonValue);
		base.insert(GlobalConstant.MAX_TOOLS_SQLITE, null, values);
		base.close();
	}

	/**
	 * 获取
	 */
	public static String getListJson(String tag){
		String value = "";
		DateSqlite dataSQiLte=new DateSqlite(ShineToosApplication.getContext());
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		Cursor c = base.rawQuery("SELECT * FROM max_tools_sqlite WHERE jsonkey = ?", new String[]{tag});
		if(c.getCount()>0){
			if(c.moveToFirst()){
				for(int i=0;i<c.getCount();i++){
					value = c.getString(c.getColumnIndex("value"));
					c.moveToNext();
				}
			}
		}
		c.close();
		base.close();
        LogUtil.i("上次数据："+ value);
		return value;
	}
}













