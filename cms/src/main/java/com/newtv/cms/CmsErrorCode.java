package com.newtv.cms;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms
 * 创建事件:         16:19
 * 创建人:           weihaichao
 * 创建日期:          2018/12/12
 */
public class CmsErrorCode {

    public static final String APP_ERROR_KEY_CHANNEL_EMPTY = "2001";
    public static final String APP_ERROR_CONTENT_ID_EMPTY = "2002";
    public static final String APP_ERROR_EXCEPTION = "2003";
    public static final String ALTERNATE_ERROR_PLAYLIST_EMPTY = "2004";
    public static final String ALTERNATE_ERROR_NOT_FOUND_TOPLAY = "2005";


    public static final String CMS_APP_KEY_EMPTY = "1001"; //appkey不存在
    public static final String CMS_APP_KEY_EMPTY_CHANNELCODE = "1002"; //	该appkey下不存在channelCode
    public static final String CMS_NO_ONLINE_CONTENT = "1003"; //	该站点下不存在已上线的该内容
    public static final String CMS_CONTENT_LIST_EMPTY = "1004"; //	内容列表为空
    public static final String CMS_CONTENT_EMPTY = "1005"; //	内容不存在
    public static final String CMS_NO_ONLINE_SERICES = "1006"; //	子节目没有所属的上线节目集
    public static final String CMS_NO_CONTENT = "1007"; //	该站点下不存在该内容
    public static final String CMS_CONTENT_NO_SUB = "1008"; //	 该内容下无子集列表
    public static final String CMS_CONTENT_NO_SUBTABLE = "1009"; // 	 内容无子表信息
    public static final String CMS_NO_SEND_MESSAGE = "1010"; // 	 无分发信息
    public static final String CMS_CONTENT_NOT_READY = "1011"; // 	 媒体信息不完整
    public static final String CMS_CONTENT_NO = "1012"; // 	 内容下无媒体
    public static final String CMS_DATA_INVALID = "1013"; // 	数据不完整{****字段非法}
    public static final String CMS_NO_CURRENT_PAGE = "1014"; // 	 无此页面
    public static final String CMS_PAGE_NO_BLOCK = "1015"; //	 页面下无区块
    public static final String CMS_AUTOBLOCK_NO_CONTENT = "1016"; //	 自动区块下无内容
    public static final String CMS_USERBLOCK_NO_CONTENT = "1017"; //	 手工区块下无内容
    public static final String CMS_SXBLOCK_NO_CONTENT = "1018"; //	 顺序区块下无内容
    public static final String CMS_GET_TEMPLATE_FAILED = "1019"; //	 获取页面templateZT失败
    public static final String CMS_GET_LAYOUT_FAILED = "1020"; //	 获取页面布局失败
    public static final String CMS_GET_CELL_FAIELD = "1021"; //	 获取布局cell失败
    public static final String CMS_LIVE_INFO_EMPTY = "1022"; //	 配置直播但直播信息是空
}
