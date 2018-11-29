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
    public static final int DB_VERSION = 4;  //数据库表添加一列，升级数据库

    /* database table names */
    public static final String COLLECT_TABLE_NAME   = "user_collect_info";//我的收藏
    public static final String ATTENTION_TABLE_NAME = "user_attention_info";//我的关注
    public static final String SUBSCRIBE_TABLE_NAME = "user_subscribe_info";//我的订阅
    public static final String HISTORY_TABLE_NAME   = "user_history_info";//历史记录

    public static final String REMOTE_COLLECT_TABLE_NAME   = "remote_user_collect_info";//我的收藏
    public static final String REMOTE_ATTENTION_TABLE_NAME = "remote_user_attention_info";//我的关注
    public static final String REMOTE_SUBSCRIBE_TABLE_NAME = "remote_user_subscribe_info";//我的订阅
    public static final String REMOTE_HISTORY_TABLE_NAME   = "remote_user_history_info";//历史记录

    public static final String LB_COLLECT_TABLE_NAME = "lb_user_colllect_info";//轮播台收藏


    /* VideoDetail db fields */

    public static final String CONTENTUUID   = "_contentuuid"; //内容id
    public static final String CONTENTTYPE   = "_contenttype"; //内容类型
    public static final String ACTIONTYPE    = "_actiontype"; //跳转类型
    public static final String IMAGEURL      = "_imageurl"; // 内容图片
    public static final String TITLE_NAME    = "_title_name"; //内容标题
    public static final String SUPERSCRIPT   = "superscript"; // 右上角角标id
    public static final String PLAY_PROGRESS = "_play_progress"; // 单个内容的播放进度
    public static final String PLAYINDEX     = "_play_index"; // 播放的是第几集
    public static final String CONTENT_GRADE = "grade"; // 评分
    public static final String UPDATE_TIME   = "_update_time";//更新时间
    public static final String PLAYPOSITION  = "_play_position";// 断点时间
    public static final String USERID        = "_user_id"; // 用户id
    public static final String TOTAL_CNT     = "_total_count"; // 内容总集数
    public static final String PLAYID        = "_play_id";//子节目id
    public static final String VIDEO_TYPE    = "_video_type"; // 内容的一集分类
    public static final String UPDATE_SUPERSCRIPT = "_update_superscript"; // 是否展示更新角标
    public static final String CONTENT_DURATION = "_content_duration"; // 内容时长
    public static final String EPISODE_NUM   = "_episode_num";
    public static final String PROGRAM_CHILD_NAME = "_program_child_name";
    public static final String CONTENT_ID = "_contentid";

    public static final String ORDER_BY_TIME = UPDATE_TIME + " desc";//排序条件
    public static final String IS_FINISH = "is_finish";//是否结束
    public static final String REAL_EXCLUSIVE = "real_exclusive";//运营标识
    public static final String ISSUE_DATE = "issue_date";
    public static final String LAST_PUBLISH_DATE = "last_publish_date";
    public static final String SUB_TITLE = "sub_title";//子标题
    public static final String V_IMAGE = "v_image";
    public static final String H_IMAGE = "h_image";
    public static final String VIP_FLAG = "vip_flag";//付费标识

    static final String CREATE_LB_COLLECT_TABLE_SQL =
            "create table "+ LB_COLLECT_TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CONTENTUUID + " varchar2(1000) UNIQUE ON CONFLICT REPLACE," +
                    CONTENT_ID + " varchar2(1000)," +
                    TITLE_NAME + " varchar2(1000)," +
                    IS_FINISH + " varchar2(1000)," +
                    REAL_EXCLUSIVE + " varchar2(1000)," +
                    ISSUE_DATE + " varchar2(1000)," +
                    LAST_PUBLISH_DATE + " varchar2(1000)," +
                    SUB_TITLE + " varchar2(1000)," +
                    UPDATE_TIME + " long," +
                    USERID + " varchar2(1000)," +
                    V_IMAGE + " varchar2(1000)," +
                    H_IMAGE + " varchar2(1000)," +
                    VIP_FLAG + " varchar2(1000)," +
                    CONTENTTYPE + " varchar2(1000)" +
                    ")";

    static final String CREATE_REMOTE_COLLECT_TABLE_SQL =
            "create table " + REMOTE_COLLECT_TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CONTENTUUID + " varchar2(1000) UNIQUE ON CONFLICT REPLACE," +
                    CONTENTTYPE + " varchar2(1000)," +
                    ACTIONTYPE + " varchar2(1000)," +
                    IMAGEURL + " varchar2(1000)," +
                    TITLE_NAME + " varchar2(1000)," +
                    SUPERSCRIPT + " varchar2(1000)," +
                    PLAYINDEX + " varchar2(1000)," +
                    CONTENT_GRADE + " varchar2(1000)," +
                    UPDATE_TIME + " long," +
                    USERID + " varchar2(1000)," +
                    VIDEO_TYPE + " varchar2(1000)," +
                    EPISODE_NUM + " varchar2(1000)," +
                    UPDATE_SUPERSCRIPT + " varchar2(1000)" +
                    ")";

    static final String CREATE_COLLECT_TABLE_SQL =
            "create table " + COLLECT_TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CONTENTUUID + " varchar2(1000) UNIQUE ON CONFLICT REPLACE," +
                    CONTENTTYPE + " varchar2(1000)," +
                    ACTIONTYPE + " varchar2(1000)," +
                    IMAGEURL + " varchar2(1000)," +
                    TITLE_NAME + " varchar2(1000)," +
                    SUPERSCRIPT + " varchar2(1000)," +
                    PLAYINDEX + " varchar2(1000)," +
                    CONTENT_GRADE + " varchar2(1000)," +
                    UPDATE_TIME + " long," +
                    USERID + " varchar2(1000)," +
                    VIDEO_TYPE + " varchar2(1000)," +
                    EPISODE_NUM + " varchar2(1000)," +
                    UPDATE_SUPERSCRIPT + " varchar2(1000)" +
                    ")";

    static final String CREATE_REMOTE_ATTENTION_TABLE_SQL =
            "create table " + REMOTE_ATTENTION_TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CONTENTUUID + " varchar2(1000) UNIQUE ON CONFLICT REPLACE," +
                    CONTENTTYPE + " varchar2(1000)," +
                    IMAGEURL + " varchar2(1000)," +
                    TITLE_NAME + " varchar2(1000)," +
                    ACTIONTYPE + " varchar2(1000)," +
                    USERID + " varchar2(1000)," +
                    UPDATE_TIME + " long" +
                    ")";

    static final String CREATE_ATTENTION_TABLE_SQL =
            "create table " + ATTENTION_TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CONTENTUUID + " varchar2(1000) UNIQUE ON CONFLICT REPLACE," +
                    CONTENTTYPE + " varchar2(1000)," +
                    IMAGEURL + " varchar2(1000)," +
                    TITLE_NAME + " varchar2(1000)," +
                    ACTIONTYPE + " varchar2(1000)," +
                    USERID + " varchar2(1000)," +
                    UPDATE_TIME + " long" +
                    ")";

    static final String CREATE_REMOTE_SUBSCRIBE_TABLE_SQL =
            "create table " + REMOTE_SUBSCRIBE_TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CONTENTUUID + " varchar2(1000) UNIQUE ON CONFLICT REPLACE," +
                    CONTENTTYPE + " varchar2(1000)," +
                    IMAGEURL + " varchar2(1000)," +
                    TITLE_NAME + " varchar2(1000)," +
                    SUPERSCRIPT + " varchar2(1000)," +
                    ACTIONTYPE + " varchar2(1000)," +
                    CONTENT_GRADE + " varchar2(1000)," +
                    USERID + " varchar2(1000)," +
                    UPDATE_TIME + " long," +
                    EPISODE_NUM + " varchar2(1000)," +
                    UPDATE_SUPERSCRIPT + " varchar2(1000)," +
                    VIDEO_TYPE + " varchar2(1000)," +
                    PLAYINDEX + " varchar2(1000)" +
                    ")";

    static final String CREATE_SUBSCRIBE_TABLE_SQL =
            "create table " + SUBSCRIBE_TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CONTENTUUID + " varchar2(1000) UNIQUE ON CONFLICT REPLACE," +
                    CONTENTTYPE + " varchar2(1000)," +
                    IMAGEURL + " varchar2(1000)," +
                    TITLE_NAME + " varchar2(1000)," +
                    SUPERSCRIPT + " varchar2(1000)," +
                    ACTIONTYPE + " varchar2(1000)," +
                    CONTENT_GRADE + " varchar2(1000)," +
                    USERID + " varchar2(1000)," +
                    UPDATE_TIME + " long," +
                    EPISODE_NUM + " varchar2(1000)," +
                    UPDATE_SUPERSCRIPT + " varchar2(1000)," +
                    VIDEO_TYPE + " varchar2(1000)," +
                    PLAYINDEX + " varchar2(1000)" +
                    ")";

    static final String CREATE_REMOTE_HISTORY_TABLE_SQL =
            "create table " + REMOTE_HISTORY_TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CONTENTUUID + " varchar2(1000) UNIQUE ON CONFLICT REPLACE," +
                    CONTENTTYPE + " varchar2(1000)," +
                    ACTIONTYPE + " varchar2(1000)," +
                    IMAGEURL + " varchar2(1000)," +
                    TITLE_NAME + " varchar2(1000)," +
                    SUPERSCRIPT + " varchar2(1000)," +
                    PLAY_PROGRESS + " varchar2(1000)," +
                    PLAYINDEX + " varchar2(1000)," +
                    CONTENT_GRADE + " varchar2(1000)," +
                    UPDATE_TIME + " long," +
                    PLAYPOSITION + " varchar2(1000)," +
                    USERID + " varchar2(1000)," +
                    VIDEO_TYPE + " varchar2(1000)," +
                    CONTENT_DURATION + " varchar2(1000)," +
                    PLAYID + " varchar2(1000)," +
                    EPISODE_NUM + " varchar2(1000)," +
                    UPDATE_SUPERSCRIPT + " varchar2(1000)" +
                    ")";

    static final String CREATE_HISTORY_TABLE_SQL =
            "create table " + HISTORY_TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CONTENTUUID + " varchar2(1000) UNIQUE ON CONFLICT REPLACE," +
                    CONTENTTYPE + " varchar2(1000)," +
                    ACTIONTYPE + " varchar2(1000)," +
                    IMAGEURL + " varchar2(1000)," +
                    TITLE_NAME + " varchar2(1000)," +
                    SUPERSCRIPT + " varchar2(1000)," +
                    PLAY_PROGRESS + " varchar2(1000)," +
                    PLAYINDEX + " varchar2(1000)," +
                    CONTENT_GRADE + " varchar2(1000)," +
                    UPDATE_TIME + " long," +
                    PLAYPOSITION + " varchar2(1000)," +
                    USERID + " varchar2(1000)," +
                    VIDEO_TYPE + " varchar2(1000)," +
                    CONTENT_DURATION + " varchar2(1000)," +
                    PLAYID + " varchar2(1000)," +
                    EPISODE_NUM + " varchar2(1000)," +
                    UPDATE_SUPERSCRIPT + " varchar2(1000)" +
                    ")";
}
