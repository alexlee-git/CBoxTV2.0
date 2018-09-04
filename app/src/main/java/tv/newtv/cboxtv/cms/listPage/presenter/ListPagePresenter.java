package tv.newtv.cboxtv.cms.listPage.presenter;


import android.content.Context;

import tv.newtv.cboxtv.cms.listPage.model.IListPageModel;
import tv.newtv.cboxtv.cms.listPage.model.ListPageModel;
import tv.newtv.cboxtv.cms.listPage.model.NavListPageInfoResult;
import tv.newtv.cboxtv.cms.listPage.view.ListPageView;

/**
 * Created by lixin on 2018/1/15.
 */

public class ListPagePresenter implements IListPagePresenter {

    private ListPageView mListPageView;
    private IListPageModel mListPageModel;

    public ListPagePresenter(ListPageView mListPageView, Context context) {
        this.mListPageView = mListPageView;
        this.mListPageModel = new ListPageModel(this,context);
    }

    @Override
    public void inflateListPageNav(NavListPageInfoResult value,String from) {
        if (mListPageView!=null){
            mListPageView.inflateListPage(value,from);
        }
    }

    @Override
    public void onFailed(String desc) {
        if(mListPageView != null){
            mListPageView.onFailed(desc);
        }
    }

    @Override
    public void requestListPageNav(String url) {
        if (mListPageModel!=null){
            mListPageModel.requestPageListNav(url);
        }
    }

    @Override
    public void destroy() {
        mListPageView = null;
        if(mListPageModel != null){
            mListPageModel.destroy();
            mListPageModel = null;
        }
    }
}
