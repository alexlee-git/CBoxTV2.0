package com.newtv.libs.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

        db.execSQL(DBConfig.CREATE_LB_COLLECT_TABLE_SQL);
        db.execSQL(DBConfig.CREATE_REMOTE_LB_COLLECT_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d("db", "onUpgrade oldversion : " + oldVersion + ", newVersion : " + newVersion);

        String str = "alter table " + DBConfig.COLLECT_TABLE_NAME + " add " + DBConfig.UPDATE_TIME + " long default 0";
        String str2 = "alter table " + DBConfig.ATTENTION_TABLE_NAME + " add " + DBConfig.UPDATE_TIME + " long default 0";
        String str3 = "alter table " + DBConfig.SUBSCRIBE_TABLE_NAME + " add " + DBConfig.UPDATE_TIME + " long default 0";
        String str4 = "alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.UPDATE_TIME + " long default 0";

        /**
         * 数据为2时的兼容性问题
         * 数据库为2时的数据库，四个本地表都增加了一个updateTime的字段,用来储存记录的生成时间
         * 所以要做的事就是给四个表都增加一个字段
         */
        if (newVersion == 2 && oldVersion == 1) {
            sqLiteDatabase.execSQL(str);
            sqLiteDatabase.execSQL(str2);
            sqLiteDatabase.execSQL(str3);
            sqLiteDatabase.execSQL(str4);
        }

        /**
         * 当数据库版本3遇上数据库版本2时
         * 数据库版本为3时，在四类数据的本地表的基础上又增加了远程表,用来储存用户登录后产生的用户行为数据
         * 1.创建四个远程表
         * 2.给四个本地表添加字段
         */
        if (newVersion == 3 && oldVersion == 2) {
            String[] params = new String[]{DBConfig.SUPERSCRIPT, DBConfig.PLAYINDEX,
                    DBConfig.CONTENT_GRADE, DBConfig.PLAYPOSITION,
                    DBConfig.VIDEO_TYPE, DBConfig.UPDATE_SUPERSCRIPT,
                    DBConfig.PLAYID, DBConfig.EPISODE_NUM, DBConfig.TOTAL_CNT, DBConfig.RECENT_MSG};

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
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.TOTAL_CNT + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.RECENT_MSG + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.CONTENT_DURATION + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.PLAY_PROGRESS + " varchar2(1000)");

            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_HISTORY_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_COLLECT_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_ATTENTION_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_SUBSCRIBE_TABLE_SQL);
        }

        if (newVersion == 3 && oldVersion == 1) {
            String[] params = new String[]{DBConfig.UPDATE_TIME, DBConfig.SUPERSCRIPT,
                    DBConfig.PLAYINDEX, DBConfig.CONTENT_GRADE, DBConfig.PLAYPOSITION,
                    DBConfig.VIDEO_TYPE, DBConfig.UPDATE_SUPERSCRIPT, DBConfig.PLAYID,
                    DBConfig.EPISODE_NUM, DBConfig.TOTAL_CNT, DBConfig.RECENT_MSG};

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
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.TOTAL_CNT + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.RECENT_MSG + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.CONTENT_DURATION + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.PLAY_PROGRESS + " varchar2(1000)");

            sqLiteDatabase.execSQL("alter table " + DBConfig.ATTENTION_TABLE_NAME + " add " + DBConfig.UPDATE_TIME + " long default 0");

            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_HISTORY_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_COLLECT_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_ATTENTION_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_SUBSCRIBE_TABLE_SQL);
        }


        /**
         *数据库版本4遇上版本3
         * 1.创建轮播表
         * 2.给历史和收藏添加content_id字段(包括本地表和历史表)
         */
        if (newVersion == 4 && oldVersion == 3) {
            sqLiteDatabase.execSQL(DBConfig.CREATE_LB_COLLECT_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_LB_COLLECT_TABLE_SQL);
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.CONTENT_ID + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.COLLECT_TABLE_NAME + " add " + DBConfig.CONTENT_ID + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.REMOTE_HISTORY_TABLE_NAME + " add " + DBConfig.CONTENT_ID + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.REMOTE_COLLECT_TABLE_NAME + " add " + DBConfig.CONTENT_ID + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.SUBSCRIBE_TABLE_NAME + " add " + DBConfig.CONTENT_ID + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.ATTENTION_TABLE_NAME + " add " + DBConfig.CONTENT_ID + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.REMOTE_SUBSCRIBE_TABLE_NAME + " add " + DBConfig.CONTENT_ID + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.REMOTE_ATTENTION_TABLE_NAME + " add " + DBConfig.CONTENT_ID + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.CONTENT_ID + " varchar2(1000)");

            sqLiteDatabase.execSQL("alter table " + DBConfig.COLLECT_TABLE_NAME + " add " + DBConfig.TOTAL_CNT + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.REMOTE_HISTORY_TABLE_NAME + " add " + DBConfig.TOTAL_CNT + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.REMOTE_COLLECT_TABLE_NAME + " add " + DBConfig.TOTAL_CNT + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.SUBSCRIBE_TABLE_NAME + " add " + DBConfig.TOTAL_CNT + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.ATTENTION_TABLE_NAME + " add " + DBConfig.TOTAL_CNT + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.REMOTE_SUBSCRIBE_TABLE_NAME + " add " + DBConfig.TOTAL_CNT + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.REMOTE_ATTENTION_TABLE_NAME + " add " + DBConfig.TOTAL_CNT + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.COLLECT_TABLE_NAME + " add " + DBConfig.TOTAL_CNT + " varchar2(1000)");


            sqLiteDatabase.execSQL("alter table " + DBConfig.REMOTE_HISTORY_TABLE_NAME + " add " + DBConfig.RECENT_MSG + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.REMOTE_COLLECT_TABLE_NAME + " add " + DBConfig.RECENT_MSG + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.SUBSCRIBE_TABLE_NAME + " add " + DBConfig.RECENT_MSG + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.ATTENTION_TABLE_NAME + " add " + DBConfig.RECENT_MSG + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.REMOTE_SUBSCRIBE_TABLE_NAME + " add " + DBConfig.RECENT_MSG + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.REMOTE_ATTENTION_TABLE_NAME + " add " + DBConfig.RECENT_MSG + " varchar2(1000)");
        }

        /**
         *数据库版本4遇上版本2
         * 1.创建轮播表
         * 2.创建四类(历史，收藏，关注和订阅)数据的远程表
         * 3.给四类数据的本地表添加需要增加的字段
         */
        if (newVersion == 4 && oldVersion == 2) {
            sqLiteDatabase.execSQL(DBConfig.CREATE_LB_COLLECT_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_HISTORY_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_COLLECT_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_ATTENTION_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_SUBSCRIBE_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_LB_COLLECT_TABLE_SQL);
            String[] params = new String[]{DBConfig.SUPERSCRIPT, DBConfig.PLAYINDEX,
                    DBConfig.CONTENT_GRADE, DBConfig.PLAYPOSITION,
                    DBConfig.VIDEO_TYPE, DBConfig.UPDATE_SUPERSCRIPT,
                    DBConfig.PLAYID, DBConfig.EPISODE_NUM, DBConfig.TOTAL_CNT, DBConfig.RECENT_MSG};

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
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.TOTAL_CNT + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.RECENT_MSG + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.CONTENT_DURATION + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.PLAY_PROGRESS + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.CONTENT_ID + " varchar2(1000)");

            sqLiteDatabase.execSQL("alter table " + DBConfig.COLLECT_TABLE_NAME + " add " + DBConfig.CONTENT_ID + " varchar2(1000)");
        }

        /**
         *
         */
        if (newVersion == 4 && oldVersion == 1) {
            sqLiteDatabase.execSQL(DBConfig.CREATE_LB_COLLECT_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_HISTORY_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_COLLECT_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_ATTENTION_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_SUBSCRIBE_TABLE_SQL);
            sqLiteDatabase.execSQL(DBConfig.CREATE_REMOTE_LB_COLLECT_TABLE_SQL);
            String[] params = new String[]{DBConfig.UPDATE_TIME, DBConfig.SUPERSCRIPT,
                    DBConfig.PLAYINDEX, DBConfig.CONTENT_GRADE, DBConfig.PLAYPOSITION,
                    DBConfig.VIDEO_TYPE, DBConfig.UPDATE_SUPERSCRIPT, DBConfig.PLAYID,
                    DBConfig.EPISODE_NUM, DBConfig.TOTAL_CNT, DBConfig.RECENT_MSG};

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
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.TOTAL_CNT + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.RECENT_MSG + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.CONTENT_DURATION + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.PLAY_PROGRESS + " varchar2(1000)");
            sqLiteDatabase.execSQL("alter table " + DBConfig.HISTORY_TABLE_NAME + " add " + DBConfig.CONTENT_ID + " varchar2(1000)");

            sqLiteDatabase.execSQL("alter table " + DBConfig.COLLECT_TABLE_NAME + " add " + DBConfig.CONTENT_ID + " varchar2(1000)");
        }
        switch (oldVersion) {
            case 1:
            case 2:
            case 3:
                sqLiteDatabase.execSQL("alter table " + DBConfig.LB_COLLECT_TABLE_NAME + " add " + DBConfig.ALTERNATE_NUMBER + " varchar2(1000)");
                sqLiteDatabase.execSQL(String.format("alter table %s add %s varchar2(1000)", DBConfig.CREATE_HISTORY_TABLE_SQL, DBConfig.ALTERNATE_NUMBER));
                sqLiteDatabase.execSQL(String.format("alter table %s add %s varchar2(1000)", DBConfig.CREATE_REMOTE_HISTORY_TABLE_SQL, DBConfig.ALTERNATE_NUMBER));
        }
    }
}
