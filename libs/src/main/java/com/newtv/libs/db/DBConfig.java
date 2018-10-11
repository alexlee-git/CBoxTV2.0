package com.newtv.libs.db;

import com.newtv.libs.Libs;
import com.newtv.libs.util.DeviceUtil;

/**
 * 项目名称:         DanceTv_Android
 * 包名:            com.newtv.dancetv.db
 * 创建事件:         12:19
 * 创建人:           weihaichao
 * 创建日期:          2018/2/24
 */

public final class DBConfig {

    public static final String DB_TRUE = "1";
    public static final String DB_FALSE = "0";

    /* database name */
    public static final String DB_NAME;
    static {
        if (Libs.get().getFlavor().equals(DeviceUtil.XIONG_MAO)) {
            DB_NAME = "cbox_database_panda";
        } else {
            DB_NAME = "cbox_database";
        }
    }

    /* database version code */
    public static final int DB_VERSION = 2;  //数据库表添加一列，升级数据库

    /* database table names */
    public static final String COLLECT_TABLE_NAME = "user_collect_info";//我的收藏
    public static final String ATTENTION_TABLE_NAME = "user_attention_info";//我的关注
    public static final String SUBSCRIBE_TABLE_NAME = "user_subscribe_info";//我的订阅
    public static final String HISTORY_TABLE_NAME = "user_history_info";//历史记录


    /* VideoDetail db fields */

    public static final String CONTENTUUID = "_contentuuid"; //内容id
    public static final String CONTENTTYPE = "_contenttype"; //内容类型
    public static final String ACTIONTYPE = "_actiontype"; //跳转类型
    public static final String IMAGEURL = "_imageurl"; // 内容图片
    public static final String TITLE_NAME = "_title_name"; //内容标题
    public static final String USERID = "_user_id"; //内容标题
    public static final String PLAYINDEX = "_play_index";
    public static final String PLAYPOSITION = "_play_position";
    public static final String UPDATE_TIME = "_update_time";//更新时间
    public static final String UPDATE_TEST = "_update_test";//更新时间
    public static final String PLAYID = "_play_id";//更新时间

    public static final String ORDER_BY_TIME =  UPDATE_TIME+" desc";//排序条件



    static final String CREATE_COLLECT_TABLE_SQL =
            "create table " + COLLECT_TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CONTENTUUID + " varchar2(1000) UNIQUE ON CONFLICT REPLACE," +
                    CONTENTTYPE + " varchar2(1000)," +
                    IMAGEURL + " varchar2(1000)," +
                    TITLE_NAME + " varchar2(1000)," +
                    ACTIONTYPE + " varchar2(1000)," +
                    USERID + " varchar2(1000)," +
                    UPDATE_TIME + " long" +
                    ")";
    static final String CREATE_ATTENTION_TABLE_NAME =
            "create table " + ATTENTION_TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CONTENTUUID + " varchar2(1000) UNIQUE ON CONFLICT REPLACE," +
                    CONTENTTYPE + " varchar2(1000)," +
                    IMAGEURL + " varchar2(1000)," +
                    TITLE_NAME + " varchar2(1000)," +
                    ACTIONTYPE + " varchar2(1000)," +
                    USERID + " varchar2(1000)," +
                    UPDATE_TIME + " long" +
                    ")";
    static final String CREATE_SUBSCRIBE_TABLE_SQL =
            "create table " + SUBSCRIBE_TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CONTENTUUID + " varchar2(1000) UNIQUE ON CONFLICT REPLACE," +
                    CONTENTTYPE + " varchar2(1000)," +
                    IMAGEURL + " varchar2(1000)," +
                    TITLE_NAME + " varchar2(1000)," +
                    ACTIONTYPE + " varchar2(1000)," +
                    USERID + " varchar2(1000)," +
                    UPDATE_TIME + " long" +
                    ")";
    static final String CREATE_HISTORY_TABLE_SQL =
            "create table " + HISTORY_TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CONTENTUUID + " varchar2(1000) UNIQUE ON CONFLICT REPLACE," +
                    CONTENTTYPE + " varchar2(1000)," +
                    IMAGEURL + " varchar2(1000)," +
                    TITLE_NAME + " varchar2(1000)," +
                    ACTIONTYPE + " varchar2(1000)," +
                    PLAYID + " varchar2(1000)," +
                    PLAYINDEX + " varchar2(1000)," +
                    PLAYPOSITION + " varchar2(1000)," +
                    USERID + " varchar2(1000)," +
                    UPDATE_TIME + " long" +
                    ")";
}
