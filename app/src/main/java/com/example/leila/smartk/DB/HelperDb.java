package com.example.leila.smartk.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库文件
 * Created by Leila on 2017/8/26.
 */

public class HelperDb extends SQLiteOpenHelper {
    //构造方法
    public HelperDb(Context context) {
        super(context, "db_smark_messge.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table smark_messge (id integer  primary key AUTOINCREMENT," +
                "user text," +
                "title text," +
                "description text," +
                "time text" +
                ")";
        String sqldb = "create table smark_EZUI (id integer  primary key AUTOINCREMENT," +
                "accessToken text," +
                "expireTime text," +
                "secret text" +
                ")";
        db.execSQL(sql);
        db.execSQL(sqldb);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
