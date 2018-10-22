package com.newtv.libs.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 项目名称:         DanceTv_Android
 * 包名:            com.newtv.dancetv.db
 * 创建事件:         12:26
 * 创建人:           weihaichao
 * 创建日期:          2018/2/24
 */

class DataHelper extends SQLiteOpenHelper {

    DataHelper(Context context) {
        this(context, DBConfig.DB_NAME, null, DBConfig.DB_VERSION);
    }

    private DataHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DataHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int
            version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBConfig.CREATE_COLLECT_TABLE_SQL);
        db.execSQL(DBConfig.CREATE_ATTENTION_TABLE_NAME);
        db.execSQL(DBConfig.CREATE_HISTORY_TABLE_SQL);
        db.execSQL(DBConfig.CREATE_SUBSCRIBE_TABLE_SQL);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        String str = "alter table " + DBConfig.COLLECT_TABLE_NAME+" add "+DBConfig.UPDATE_TIME+" long default 0";
        String str2 = "alter table " + DBConfig.ATTENTION_TABLE_NAME+" add "+DBConfig.UPDATE_TIME+" long default 0";
        String str3 = "alter table " + DBConfig.SUBSCRIBE_TABLE_NAME+" add "+DBConfig.UPDATE_TIME+" long default 0";
        String str4 = "alter table " + DBConfig.HISTORY_TABLE_NAME+" add "+DBConfig.UPDATE_TIME+" long default 0";
        //数据库升级
        if(newVersion==2&&oldVersion==1){

            sqLiteDatabase.execSQL(str);
            sqLiteDatabase.execSQL(str2);
            sqLiteDatabase.execSQL(str3);
            sqLiteDatabase.execSQL(str4);
        }

    }
}
