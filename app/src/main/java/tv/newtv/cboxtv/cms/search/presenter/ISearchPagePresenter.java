package tv.newtv.cboxtv.cms.search.presenter;


import tv.newtv.cboxtv.cms.search.bean.SearchHotInfo;

/**
 * 项目名称： NewTVLauncher
 * 类描述：
 * 创建人：wqs
 * 创建时间： 2018/3/6 0006 14:38
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public interface ISearchPagePresenter {

    void requestPageRecommendData(String appKey, String channelId);

    void inflatePageRecommendData(SearchHotInfo searchHotInfo);
}
