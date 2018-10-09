package tv.newtv.cboxtv.cms.search.presenter;

import android.content.Context;

import tv.newtv.cboxtv.cms.search.bean.SearchHotInfo;
import tv.newtv.cboxtv.cms.search.model.ISearchPageModel;
import tv.newtv.cboxtv.cms.search.model.SearchPageModel;
import tv.newtv.cboxtv.cms.search.view.ISearchPageView;


/**
 * 类描述：
 * 创建人：wqs
 * 创建时间： 2018/3/6 0006 14:39
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class SearchPagePresenter implements ISearchPagePresenter {
    private ISearchPageModel mSearchModel;
    private ISearchPageView mSearchPageView;

    public SearchPagePresenter(Context context, ISearchPageView searchPageView) {
        mSearchModel = new SearchPageModel(this, context);
        this.mSearchPageView = searchPageView;
    }

    @Override
    public void requestPageRecommendData(String appKey, String channelId) {
        if (mSearchModel != null) {
            mSearchModel.requestPageRecommendData(appKey, channelId);
        }
    }

    @Override
    public void inflatePageRecommendData(SearchHotInfo searchHotInfo) {
        if (mSearchPageView != null) {
            mSearchPageView.inflatePageRecommendData(searchHotInfo);
        }
    }


}
