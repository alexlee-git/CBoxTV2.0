package tv.newtv.cboxtv.views.detail;

import com.newtv.cms.BuildConfig;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
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

}
