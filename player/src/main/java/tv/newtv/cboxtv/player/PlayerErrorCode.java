package tv.newtv.cboxtv.player;

import android.content.Context;

import tv.newtv.player.R;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player
 * 创建事件:         17:46
 * 创建人:           weihaichao
 * 创建日期:          2018/11/28
 */
public final class PlayerErrorCode {

    public static final String USER_NOT_BUY = "60006";                      //用户未购买
    public static final String USER_NOT_LOGIN = "60017";                    //用户未登录
    public static final String USER_TOKEN_IS_EXPIRED = "60019";             //登录TOKEN过期
    public static final String LIVE_INFO_EMPTY = "80020";                   //直播信息为空
    public static final String PROGRAM_SERIES_EMPTY = "80021";              //节目集子节目为空
    public static final String PROGRAM_CDN_EMPTY = "80022";                 //CND节点为空
    public static final String PROGRAM_PLAY_URL_EMPTY = "80023";            //播放地址为空
    public static final String INTERNET_ERROR = "80024";                    //网络错误
    public static final String PERMISSION_CHECK_RESULT_EMPTY = "80025";     //播控鉴权结果为空


    public static String getErrorDesc(Context context, String code) {
        switch (code) {
            case USER_NOT_BUY:
                return "用户尚未购买";
            case USER_NOT_LOGIN:
                return "用户尚未登录";
            case USER_TOKEN_IS_EXPIRED:
                return "用户登录状态过期";
            case INTERNET_ERROR:
                return "网络不好，请重试";
            case PROGRAM_CDN_EMPTY:
            case PERMISSION_CHECK_RESULT_EMPTY:
                return context.getResources().getString(R.string.check_error);
            default:
                return "";
        }
    }
}
