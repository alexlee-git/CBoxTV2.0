package com.newtv.libs;

import android.os.Environment;
import android.text.TextUtils;

//import com.newtv.libs.util.DeviceUtil;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.SPrefUtils;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * Created by lixin on 2018/1/11.
 */

public class Constant {

    //是否启用轮播
    public static final boolean canUseAlternate  = true;


    public static long TIP_LIVE_DURATION = 3600 * 2;
    public static long TIP_VOD_DURATION = 3600 * 4;

    public static final boolean isLocalData = false;
    public static final String AdCache = Environment.getExternalStorageDirectory()
            .getAbsolutePath() +
            "/ad_cache";
    public static final String TAG = "CBoxTV";

    //广告位类型
    public static final String AD_TOPIC = "topic";//专题广告
    public static final String AD_DESK = "desk";//桌面广告
    //详情页通栏广告
    public static final String AD_DETAILPAGE_BANNER = "cbox_detailpage_banner";
    //轮播台广告
    public static final String AD_CAROUSEL_CHANGE = "carousel_change";
    //详情页背景广告
    public static final String AD_DETAILPAGE_BACKGROUND = "cbox_detailpage_background";
    //详情页内容列表广告
    public static final String AD_DETAILPAGE_CONTENTLIST = "cbox_detaipage_contentlist";
    //详情页小窗口广告
    public static final String AD_DETAILPAGE_RIGHTPOS = "cbox_detailpage_rightpos";
    //悬浮窗广告
    public static final String AD_GLOBAL_POPUP = "cbox_global_popup";
    //详情页顶部小窗口广告
    public static final String AD_DETAILPAGE_TOPPOS = "cbox_detailpage_toppos";
    //边看变买广告
    public static final String AD_BUY_GOODS = "buygoods";
    //广告内容类型
    public static final String AD_IMAGE_TYPE = "image";//图片广告
    public static final String AD_VIDEO_TYPE = "video";//视频广告
    public static final String AD_TEXT_TYPE = "text";//文字广告

    public static final String APPSECRET = "5047cbcc9e193d66147084f68ecd3952";//开发暂时用
    //public static final String VERSION_UP = "http://stage-bzo.cloud.ottcn.com/"; //版本升级
    public static final int BUFFER_SIZE_4 = 4;
    public static final int BUFFER_SIZE_8 = 8;
    public static final int BUFFER_SIZE_16 = 16;
    public static final int BUFFER_SIZE_32 = 32;
    public static final int BUFFER_SIZE_64 = 64;
    public static final int BUFFER_SIZE_128 = 128;
    public static final int BUFFER_SIZE_256 = 256;
    public static final int BUFFER_SIZE_512 = 512;
    public static final int BUFFER_SIZE_1K = 1024;
    public static final int BUFFER_SIZE_2K = 2048;
    public static final String CONTENTTYPE_PG = "PG";   //单节目
    public static final String CONTENTTYPE_PAGE = "Page";   //页面
    public static final String CONTENTTYPE_PS = "PS";   //节目集
    public static final String CONTENTTYPE_CP = "CP";   //子节目
    public static final String CONTENTTYPE_CS = "CS";   //节目集合集
    public static final String CONTENTTYPE_CG = "CG";   //节目合集
    public static final String CONTENTTYPE_SA = "SA";   //专题
    public static final String CONTENTTYPE_LV = "LV";   //直播
    public static final String CONTENTTYPE_LB = "LB";   //轮播
    public static final String CONTENTTYPE_LK = "VL";   //连接
    // 央视影音新加入
    public static final String CONTENTTYPE_CR = "CR";   //人物
    public static final String CONTENTTYPE_FG = "FG";   //人物
    public static final String CONTENTTYPE_CH = "CH";   //Channel
    public static final String CONTENTTYPE_CL = "CL";   //Column
    public static final String CONTENTTYPE_TV = "TV";   //Column
    public static final String CONTENTYPE_LISTPAGE = "listPage"; //栏目的导航页
    public static final String VIDEOTYPE_FILM = "电影";
    public static final String VIDEOTYPE_TV = "电视剧";
    public static final String VIDEOTYPE_VARIETY = "综艺";
    public static final String VIPFLAG_0 = "0";  //免费
    public static final String VIPFLAG_1 = "1";  //vip免费
    public static final String VIPFLAG_2 = "2";  //单点付费
    public static final String OPEN_LIVE = "OPEN_LIVE"; //打开直播
    public static final String OPEN_PLAYLIST = "OPEN_PLAYLIST";  //打开连播
    public static final String OPEN_PAGE = "OPEN_PAGE";  //打开页面
    public static final String OPEN_FILTER = "OPEN_FILTER";   //打开列表页
    public static final String OPEN_APK = "OPEN_APK";  //打开APK
    public static final String DOWNLOAD_APK = "DOWNLOAD_APK";   //下载APK
    public static final String OPEN_DETAILS = "OPEN_DETAILS";   //打开详情页
    public static final String OPEN_LINK = "OPEN_LINK";  //打开链接
    public static final String OPEN_SEARCH = "OPEN_SEARCH";   //执行搜索
    public static final String OPEN_VIDEO = "OPEN_VIDEO";  //打开视频
    public static final String OPEN_USERCENTER = "OPEN_USERCENTER";  //打开个人中心
    public static final String OPEN_VIPCENTER = "OPEN_VIPCENTER"; //会员中心
    public static final String OPEN_SPECIAL = "OPEN_SPECIAL"; // 打开专题
    public static final String OPEN_APP_LIST = "OPEN_APP_LIST";
    public static final String PAGE_UUID = "page_uuid";
    public static final String ACTION_URI = "action_uri";
    public static final String ACTION_FROM = "action_from";
    public static final String CONTENT_TYPE = "content_type";
    public static final String SERIES_UUID = "series_uuid";
    public static final String ACTION_TYPE = "action_type";
    public static final String ACTION_AD_ENTRY = "action_ad_entry";//判断是不是从广告点击进入
    public static final String CONTENT_UUID = "content_uuid";
    public static final String CONTENT_CHILD_UUID = "content_child_uuid";
    public static final String DEFAULT_UUID = "default_uuid";
    public static final String FOCUSPARAM = "focusParam";
    public static final String NAV_ID = "nav_id";//导航id
    public static final String FIRST_CHANNEL_ID = "FIRST_CHANNEL_ID";   //用于视频获取广告 一级频道
    public static final String SECOND_CHANNEL_ID = "SECOND_CHANNEL_ID"; //用于视频获取广告 二级频道
    public static final String OPEN_CHANNEL = "OPEN_CHANNEL";
    public static final String NAV_SEARCH = "搜索";
    public static final String NAV_UC = "我的";
    public static final String EXTERNAL_OPEN_PANEL = "panel";
    public static final String EXTERNAL_OPEN_NEWS = "news";
    public static final String EXTERNAL_OPEN_URI = "uri"; //for smallWindowAD
    public static final String EXTERNAL_OPEN_PAGE = "page";
    public static final String EXTERNAL_OPEN_LISTPAGE = "list_page";
    public static final String EXTERNAL_PLAYER = "player";
    //跳转参数定义
    public static final String EXTERNAL_PARAM_CONTENT_UUID = "id";//ContentUUID
    public static final String EXTERNAL_PARAM_ACTION_URI = "uri";//
    public static final String EXTERNAL_PARAM_FOCUS_UUID = "fid";//默认焦点UUID
    public static final String EXTERNAL_PARAM_SERIES_SUB_UUID = "sid";//节目集ID
    // 定义log
    public static final int LOG_NODE_HOME_PAGE = 0;           // 首页日志
    public static final int LOG_NODE_ADVERT = 6;           // 开屏广告日志
    public static final int LOG_COLUMN_INTO = 1;          // 进入栏目列表
    public static final int LOG_NODE_NAVIGATION_SELECT = 66;    // 导航日志
    public static final int LOG_NODE_SEARCH = 2;              // 搜索页日志
    public static final int LOG_NODE_DETAIL = 13;               // 详情页日志
    public static final int LOG_NODE_DETAIL_SUGGESt = 16;               // 详情推荐
    public static final int LOG_NODE_SPECIAL = 17;               // 专题
    public static final int LOG_NODE_HISTORY = 15;               // 历史记录
    public static final int LOG_NODE_RECOMMEND = 18;               // 推荐位
    public static final int LOG_NODE_COLLECT = 5;               // 收藏
    public static final int LOG_NODE_SUBSCRIP = 21;               // 订阅
    public static final int LOG_NODE_ATTENTION = 22;               // 关注
    public static final int LOG_NODE_LIKE = 23;               // 点赞 送花
    public static final int LOG_NODE_USER_CENTER = 8;               //用户中心
    public static final int LOG_NODE_PAY = 7;                      //支付
    public static final int LOG_NODE_ONE__DETAIL = 3;               // 某个影片的详情页
    public static final int LOG_NODE_PAGE = 19;                 // 页面日志
    public static final int LOG_NODE_RECOMMEND_POS = 501;        // 页面数据的推荐位日志
    public static final int LOG_NODE_SPECIAL_PAGE = 17;         // 专题页日志
    public static final int LOG_NODE_SCREEN = 700;               // 筛选页日志
    public static final int LOG_NODE_APP_VERSION = 87;          //版本信息
    public static final int LOG_NODE_SWITCH = 88;          //日志开关
    public static final int LOG_NODE_JUMP = 68;            //外部推荐位跳转
    public static final int LOG_NODE_DEVICE_INFO = 86;          // 终端设备信息
    public static final int LOG_NODE_AUTH_INFO = 10;          // 认证
    public static final int LOG_BUY_GOODS = 47;              //边看边买日志
    public static final int FLOATING_LAYER = 4;
    public static final String BACK_FIRST_NAV = "back_first_nav"; // 返回一级导航
    public static final String BG_EVENT = "bg_event";
    public static final String UPDATE_UC_DATA = "update_uc_data";
    public static final String UPDATE_VIDEO_PLAY_INFO = "update_video_play_info";
    public static final String IS_VIDEO_END = "isVideoEnd";
    public static final String IS_HAVE_AD = "is_have_ad_event";
    //更新是否成功
    public static final String UP_VERSION_IS_SUCCESS = "up_version_is_success";
    public static final String ADCACHE = Environment.getExternalStorageDirectory()
            .getAbsolutePath() +
            "/ad_cache";
    public static final String INIT_SDK = "init_sdk";//sdk初始化
    public static final String INIT_ADSDK = "init_sdk";//adsdk初始化
    public static final String INIT_LOGSDK = "init_logsdk";//logsdk初始化

    //用户中心
    public static final String RESPONSE_TYPE = "device_code";

    public static final String GRANT_TYPE_SMS = "sms_code";
    public static final String GRANT_TYPE_REFRESH = "refresh_code";
    public static final String CLIENT_ID = Libs.get().getClientId();
    public static final String USER_ID = "USER_ID";
    public static final String UC_HISTORY = "历史记录";
    //支付
    public static final String BUY_VIPANDONLY = "1";
    public static final String BUY_VIP = "3";
    public static final String BUY_ONLY = "4";

    //检查是否是定向升级
    public static final String UC_FOLLOW = "关注";
    public static final String UC_SUBSCRIBE = "订阅";
    public static final String UC_COLLECTION = "收藏";
    private static final Map<String, String> mServerAddressMap = new HashMap<>();
    
    /**
     * log服务器地址
     */
    //public static final String LOG_ADDR = "log.cloud.ottcn.com:14630";
    public static final String LOG_ADDR = !TextUtils.isEmpty(getBaseUrl(HeadersInterceptor.LOG))
            ? getBaseUrl(HeadersInterceptor.LOG) : "log.cloud.ottcn.com:14630";

    public static final String BASE_URL_CDN = !TextUtils.isEmpty(getBaseUrl(HeadersInterceptor
            .CDN)) ? getBaseUrl(HeadersInterceptor.CDN) : "https://cdndispatchnewtv.ottcn.com";

    public static final String DYNAMIC_KEY = !TextUtils.isEmpty(getBaseUrl(HeadersInterceptor
            .DYNAMIC_KEY)) ? getBaseUrl(HeadersInterceptor.DYNAMIC_KEY) : "https://k.cloud.ottcn" +
            ".com"; //动态防盗链


    private static final String CMS_ONLINE = Libs.get().isDebug()
            ? "http://111.32.132.156/"
            : "http://api31.cloud.ottcn.com/";

    public static final String BASE_URL_CMS = !TextUtils.isEmpty(getBaseUrl(HeadersInterceptor.CMS))
            ? getBaseUrl(HeadersInterceptor.CMS)
            : CMS_ONLINE;


    private static final String CMS_NEW_ONLINE = Libs.get().isDebug()
            ? "http://testcms31.ottcn.com:30013/"
            : "http://api31.cloud.ottcn.com/";


    public static final String BASE_URL_NEW_CMS = !TextUtils.isEmpty(getBaseUrl(HeadersInterceptor
            .NEW_CMS)) ? getBaseUrl(HeadersInterceptor.NEW_CMS) : CMS_NEW_ONLINE;


    //public static final String BASE_URL_AD = !TextUtils.isEmpty(getBaseUrl(HeadersInterceptor
    // .AD)) ? getBaseUrl(HeadersInterceptor.AD) : "https://api.adott.ottcn.com/"; //广告正式地址
    private static final String AD_URL = Libs.get().isDebug() ?
            "http://api.adott.ottcn.org/" : "https://api.adott.ottcn.com/";

    public static final String BASE_URL_AD = !TextUtils.isEmpty(getBaseUrl(HeadersInterceptor.AD)
    ) ? getBaseUrl(HeadersInterceptor.AD) : AD_URL; //广告正式地址


    //版本升级状态
    public static boolean VERSION_UPDATE = false;
    // 激活认证接口地址

    private static final String ACTIVATE_URL = Libs.get().isDebug() ?
            "http://stage-bzo.cloud.ottcn.com/": "https://terminal.cloud.ottcn.com/";
    public static String BASE_URL_ACTIVATE = !TextUtils.isEmpty(getBaseUrl(HeadersInterceptor
            .ACTIVATE)) ? getBaseUrl(HeadersInterceptor.ACTIVATE) : ACTIVATE_URL; //激活接口

    //会员中心首页推荐位页面ID
    public static String ID_PAGE_MEMBER = getBaseUrl(HeadersInterceptor.PAGE_MEMBER);
    //热门收藏页面ID
    public static String ID_PAGE_COLLECTION = getBaseUrl(HeadersInterceptor.PAGE_COLLECTION);
    //订阅页面ID
    public static String ID_PAGE_SUBSCRIPTION = getBaseUrl(HeadersInterceptor.PAGE_SUBSCRIPTION);
    //猜你喜欢页面ID
    public static String ID_PAGE_USERCENTER = getBaseUrl(HeadersInterceptor.PAGE_USERCENTER);
    //使用帮助html地址
    public static String HTML_PATH_HELPER = getBaseUrl(HeadersInterceptor.HTML_PATH_HELPER);
    //关于我们html地址
    public static String HTML_PATH_ABOUT_US = getBaseUrl(HeadersInterceptor.HTML_PATH_ABOUT_US);
    //会员协议html地址
    public static String HTML_PATH_MEMBER_PROTOCOL = getBaseUrl(HeadersInterceptor
            .HTML_PATH_MEMBER_PROTOCOL);
    //用户协议html地址
    public static String HTML_PATH_USER_PROTOCOL = getBaseUrl(HeadersInterceptor
            .HTML_PATH_USER_PROTOCOL);
    //会员中心跳转会员片库params值
    public static String MEMBER_CENTER_PARAMS = getBaseUrl(HeadersInterceptor
            .HTML_PATH_MEMBER_PROTOCOL);
    public static boolean isInitStatus = true;
    public static String UUID_KEY = "uuid";
    public static String UUID = "";
    public static String ALREADY_SAVE = "alreadySave";
    // 定义一个全局的静态变量   用于在小屏和大屏两种模式下， 确定当前是否符合直播的条件
    // 开启直播时，将该值置为true   到达直播结束时间，关闭直播时，将该值置为false
    // 什么时候会开启直播？
    // 1. 小屏时 刚加载完播放列表之后，需要判断当前时间是否满足直播
    // 2. 小屏或大屏加载完一个点播文件，播放下一个之前，需要判断当前时间是否满足直播
    // 3. 小屏或大屏强制点播下一个文件时 将isLiving置为false
    public static boolean isLiving = false;
    // 直播测试地址
    // public static String liveUrl = "http://s003.test.vod06.icntvcdn.com/live/sscntv63.m3u8";
    // 点播测试地址
    public static String mPlayUrl = "http://n3.cloud.icntvcdn" +
            ".com/hls/1.8M/2018/04/27/b22fc66d931540d89d3b455d4cba2539_H2641500000aac128" +
            "/b22fc66d931540d89d3b455d4cba2539_H2641500000aac128.m3u8";
    public static List<String> activateUrls = new ArrayList<>();
    public static String Authorization;

    public static String getBaseUrl(String key) {
        String result = mServerAddressMap.get(key);
        if (TextUtils.isEmpty(result)) {
            parseServerAddress();
        }
        result = mServerAddressMap.get(key);
        return result;
    }

    private static void parseServerAddress() {
        parseServerAddress((String) SPrefUtils.getValue(Libs.get().getContext(),
                SPrefUtils.KEY_SERVER_ADDRESS, ""));
    }

    public static void parseServerAddress(String serverInfo) {
        if (TextUtils.isEmpty(serverInfo)) {
            return;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new ByteArrayInputStream(serverInfo
                    .getBytes())));
            NodeList list = document.getElementsByTagName("address");
            for (int i = 0; i < list.getLength(); ++i) {
                NamedNodeMap namedNodeMap = list.item(i).getAttributes();
                Node urlNode = namedNodeMap.getNamedItem("url");
                Node nameNode = namedNodeMap.getNamedItem("name");
                mServerAddressMap.put(nameNode.getNodeValue(), urlNode.getNodeValue());
            }
        } catch (ParserConfigurationException e) {
            LogUtils.e("parse server address ParserConfigurationException" + e);
        } catch (SAXException e) {
            LogUtils.e("parse server address SAXException" + e);
        } catch (IOException e) {
            LogUtils.e("parse server address IOException" + e);
        }
    }
}
