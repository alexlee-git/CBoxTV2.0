package tv.newtv.cboxtv.views.detail;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.BuildConfig;
import tv.newtv.cboxtv.cms.net.NetClient;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         11:16
 * 创建人:           weihaichao
 * 创建日期:          2018/5/4
 */
public class EpisodeHelper {

    public static final int TYPE_COLUMN_DETAIL = 1;         //栏目详情页
    // id:c57bd7ab18674de49ca676ec4b87bb95
    public static final int TYPE_VARIETY_SHOW = 2;          //节目集综艺详情页  id:22781
    public static final int TYPE_PROGRAM_LIST_DETAIL = 3;   //节目合集详情页
    public static final int TYPE_PERSONS_DETAIL = 4;        //人物详情页
    public static final int TYPE_PROGRAME_SERIES = 5;       //节目集剧集详情页

    public static final int TYPE_PROGRAME_XG = 6;       //相关节目
    public static final int TYPE_PROGRAME_STAR = 7;       //名人堂
    public static final int TYPE_PROGRAME_SAMETYPE = 8;       //通分类\
    public static final int TYPE_SEARCH = 9; //节目集的相关推荐
    public static final int TYPE_PERSON_DETAIL_RELATION = 10; // 人物详情页　TA 相关的名人

    public static String getTitleByType(int type) {
        switch (type) {
            case TYPE_PROGRAME_STAR:
                return "名人堂";
            case TYPE_SEARCH:
            case TYPE_PROGRAME_XG:
            case TYPE_PROGRAME_SAMETYPE:
                return "相关推荐";
            case TYPE_PERSON_DETAIL_RELATION:
                return "TA 相关的名人";
            default:
                return "内容列表";
        }
    }

    public static Observable<ResponseBody> GetInfo(Object... params) {
        return NetClient.INSTANCE.getDetailsPageApi().getInfo(BuildConfig.APP_KEY,
                BuildConfig.CHANNEL_ID, (String) params[0], (String)
                        params[1], (String) params[2]);
    }


    static Observable<ResponseBody> GetInterface(int type, Object... params) {
        switch (type) {
            case TYPE_PERSON_DETAIL_RELATION:
                return NetClient.INSTANCE.getDetailsPageApi().getCharacterlist(BuildConfig.APP_KEY,
                        BuildConfig.CHANNEL_ID, (String) params[0], (String)
                                params[1], (String) params[2]);
            case TYPE_COLUMN_DETAIL:
                return NetClient.INSTANCE.getDetailsPageApi().getHistoryColmn(BuildConfig
                        .APP_KEY, BuildConfig.CHANNEL_ID, (String) params[0], (String)
                        params[1], (String) params[2]);
            case TYPE_PERSONS_DETAIL:
                return NetClient.INSTANCE.getDetailsPageApi().getProgramList(BuildConfig.APP_KEY,
                        BuildConfig.CHANNEL_ID, (String) params[0], (String)
                                params[1], (String) params[2]);
            case TYPE_PROGRAM_LIST_DETAIL:
            case TYPE_VARIETY_SHOW:
                return NetClient.INSTANCE.getDetailsPageApi().getInfo(BuildConfig.APP_KEY, BuildConfig
                                .CHANNEL_ID,
                        (String) params[0], (String)
                                params[1], (String) params[2]);
            case TYPE_PROGRAME_SERIES:
                return null;
            case TYPE_PROGRAME_XG:
                return NetClient.INSTANCE.getDetailsPageApi().getCurrentColmn(BuildConfig.APP_KEY,
                        BuildConfig.CHANNEL_ID, (String) params[0], (String)
                                params[1], (String) params[2]);
            case TYPE_PROGRAME_STAR:
                return NetClient.INSTANCE.getDetailsPageApi().getCharacterlist(BuildConfig.APP_KEY,
                        BuildConfig.CHANNEL_ID, (String) params[0], (String)
                                params[1], (String) params[2]);
            case TYPE_PROGRAME_SAMETYPE:
                return NetClient.INSTANCE.getDetailsPageApi().getChannelColmn(BuildConfig.APP_KEY,
                        BuildConfig.CHANNEL_ID, (String) params[0]);
            case TYPE_SEARCH:
                return NetClient.INSTANCE.getListPageApi()
                        .getScreenResult((String) params[0], BuildConfig.APP_KEY, BuildConfig
                                        .CHANNEL_ID, "PS", "",
                                "", "", 0 + "", 6 + "");
        }
        return null;
    }


}
