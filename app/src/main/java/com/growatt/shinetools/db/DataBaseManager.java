package com.growatt.shinetools.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.growatt.shinetools.bean.User;
import com.growatt.shinetools.utils.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataBaseManager {
    public static final String TAG = "DataBaseManager";
    private final DataBaseHelper dbHelper;

    public DataBaseManager(Context context) {
        dbHelper = new DataBaseHelper(context);
    }

    /**
     * 增
     */
    public void insertUser(User user) {
        Log.e(TAG,"增加数据："+user.toString());
        String sql = "insert into " + DataBaseHelper.TABLE_NAME;

        sql += "(_id,username,password) values(?,?,?)";

        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        sqlite.execSQL(sql, new String[]{user.getId(), user.getUsername(), user.getPassword()});
        sqlite.close();
    }

    /**
     * 删
     *
     * @param id
     */
    public void delete(int id) {
        Log.e(TAG,"删除数据：id = "+id);
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + DataBaseHelper.TABLE_NAME + " where _id=?");
        sqlite.execSQL(sql, new Integer[]{id});
        sqlite.close();
    }

    /**
     * 改
     */
    public void update(User user) {
        Log.e(TAG,"修改数据："+user.toString());
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("update " + DataBaseHelper.TABLE_NAME + " set _id=?,username=?, password=? where _id=?");
        sqlite.execSQL(sql,
                new String[]{user.getId(), user.getUsername(), user.getPassword(),user.getId()});
        sqlite.close();
    }

    public List<User> query() {
        return query(" ");
    }

    /**
     * 查
     *
     * @param where
     * @return
     */
    public List<User> query(String where) {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<User> data = null;
        data = new ArrayList<User>();
        Cursor cursor = sqlite.rawQuery("select * from "
                + DataBaseHelper.TABLE_NAME + where, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            User user = new User();
            int id =cursor.getInt(0);
            String username = cursor.getString(1);
            String password = cursor.getString(2);
            user.setUsername(username);
            user.setPassword(password);
            user.setId(String.valueOf(id));
            data.add(user);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();
        Log.e(TAG,"查询数据："+ data.toString());
        return data;
    }

    /**
     * 重置
     *
     * @param datas
     */
    public void reset(List<User> datas) {
        if (datas != null) {
            SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
            // 删除全部
            sqlite.execSQL("delete from " + DataBaseHelper.TABLE_NAME);
            // 重新添加
            for (User user : datas) {
                insertUser(user);
            }
            sqlite.close();
        }
    }

    /**
     * 保存一条数据到本地(若已存在则直接覆盖)
     *
     * @param user
     */
    public void save(User user) {
        List<User> datas = query(" where _id=" + user.getId());
        if (datas != null && !datas.isEmpty()) {
            update(user);
        } else {
            insertUser(user);
        }
    }

    //
    // /**
    // * 合并一条数据到本地(通过更新时间判断仅保留最新)
    // *
    // * @param data
    // * @return 数据是否被合并了
    // */
    // public boolean merge(NotebookData data) {
    // Cursor cursor = sqlite.rawQuery(
    // "select * from " + DatabaseHelper.NOTE_TABLE_NAME
    // + " where _id=" + data.getId(), null);
    // NotebookData localData = new NotebookData();
    // // 本循环其实只执行一次
    // for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
    // localData.setId(cursor.getInt(0));
    // localData.setIid(cursor.getInt(1));
    // localData.setUnixTime(cursor.getString(2));
    // localData.setDate(cursor.getString(3));
    // localData.setContent(cursor.getString(4));
    // localData.setColor(cursor.getInt(5));
    // }
    // // 是否需要合这条数据
    // boolean isMerge = localData.getUnixTime() < data.getUnixTime();
    // if (isMerge) {
    // save(data);
    // }
    // return isMerge;
    // }

    public void destroy() {
        dbHelper.close();
    }


}
