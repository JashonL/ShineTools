package com.growatt.shinetools.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.growatt.shinetools.utils.Log;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "DataBaseHelper";

    public static final String DB_NAME = "ShineTools.db";

    public static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "user";


    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.e(TAG,"创建数据库");
    }

    public DataBaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DataBaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }


    //数据库第一次创建的时调用
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建一个表
        String sql = "create table "
                + TABLE_NAME
                + " (_id integer primary key autoincrement,"
                + " username text, password text)";
        sqLiteDatabase.execSQL(sql);
        Log.e(TAG,"创建一个用户的数据表");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
