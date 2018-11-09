package com.newtv.libs.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.OptionalLong;

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
        Log.d("db", "onCreate");
        // 建立本地数据库,用于存储设备在用户登录之前产生的数据
        db.execSQL(DBConfig.CREATE_COLLECT_TABLE_SQL);
        db.execSQL(DBConfig.CREATE_ATTENTION_TABLE_SQL);
        db.execSQL(DBConfig.CREATE_HISTORY_TABLE_SQL);
        db.execSQL(DBConfig.CREATE_SUBSCRIBE_TABLE_SQL);

        // 建立用于存储远端(即服务端)的数据, 用于存储用户登录后的数据
        db.execSQL(DBConfig.CREATE_REMOTE_COLLECT_TABLE_SQL);
        db.execSQL(DBConfig.CREATE_REMOTE_ATTENTION_TABLE_SQL);
        db.execSQL(DBConfig.CREATE_REMOTE_HISTORY_TABLE_SQL);
        db.execSQL(DBConfig.CREATE_REMOTE_SUBSCRIBE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d("db", "onUpgrade oldversion : " + oldVersion + ", newVersion : " + newVersion);

        String str = "alter table " + DBConfig.COLLECT_TABLE_NAME+" add "+DBConfig.UPDATE_TIME+" long default 0";
        String str2 = "alter table " + DBConfig.ATTENTION_TABLE_NAME+" add "+DBConfig.UPDATE_TIME+" long default 0";
        String str3 = "alter table " + DBConfig.SUBSCRIBE_TABLE_NAME+" add "+DBConfig.UPDATE_TIME+" long default 0";
        String str4 = "alter table " + DBConfig.HISTORY_TABLE_NAME+" add "+DBConfig.UPDATE_TIME+" long default 0";

        //数据库升级
        if (newVersion == 2 && oldVersion == 1){
            sqLiteDatabase.execSQL(str);
            sqLiteDatabase.execSQL(str2);
            sqLiteDatabase.execSQL(str3);
            sqLiteDatabase.execSQL(str4);
        }

        if (newVersion == 3 && oldVersion == 2) {
            String[] params = new String[]{DBConfig.SUPERSCRIPT, DBConfig.PLAYINDEX,
                    DBConfig.CONTENT_GRADE, DBConfig.PLAYPOSITION,
                    DBConfig.VIDEO_TYPE, DBConfig.UPDATE_SUPERSCRIPT,
                    DBConfig.PLAYID, DBConfig.EPISODE_NUM};

            String[] tables = new String[]{DBConfig.COLLECT_TABLE_NAME, DBConfig.SUBSCRIBE_TABLE_NAME};
            for (String table : tables) {
                for (String param : params) {
                    sqLiteDatabase.execSQL("alter table " + table + " add " + param + " varchar2(1000)");
                }
            }

            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.SUPERSCRIPT + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.CONTENT_GRADE + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.VIDEO_TYPE + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.UPDATE_SUPERSCRIPT + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.EPISODE_NUM + " varchar2(1000)");


            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_HISTORY_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_COLLECT_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_ATTENTION_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_SUBSCRIBE_TABLE_SQL);
        }

        if (newVersion == 3 && oldVersion == 1) {
            String[] params = new String[]{DBConfig.UPDATE_TIME, DBConfig.SUPERSCRIPT,
                    DBConfig.PLAYINDEX, DBConfig.CONTENT_GRADE, DBConfig.PLAYPOSITION,
                    DBConfig.VIDEO_TYPE, DBConfig.UPDATE_SUPERSCRIPT, DBConfig.PLAYID,
                    DBConfig.EPISODE_NUM};

            String[] tables = new String[]{DBConfig.COLLECT_TABLE_NAME, DBConfig.SUBSCRIBE_TABLE_NAME};
            for (String table : tables) {
                for (String param : params) {
                    sqLiteDatabase.execSQL("alter table " + table + " add " + param + " varchar2(1000)");
                }
            }

            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.UPDATE_TIME + " long default 0");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.SUPERSCRIPT + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.CONTENT_GRADE + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.VIDEO_TYPE + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.UPDATE_SUPERSCRIPT + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.EPISODE_NUM + " varchar2(1000)");

            sqLiteDatabase.execSQL("alter table " + DBConfig.ATTENTION_TABLE_NAME + " add " + DBConfig.UPDATE_TIME + " long default 0");

            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_HISTORY_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_COLLECT_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_ATTENTION_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_SUBSCRIBE_TABLE_SQL);
        }
    }
}
