package tv.newtv.cboxtv.cms.mainPage.presenter;

import android.content.Context;

import tv.newtv.cboxtv.cms.mainPage.model.IMainPageModel;
import tv.newtv.cboxtv.cms.mainPage.model.MainPageModel;
import tv.newtv.cboxtv.cms.mainPage.model.NavInfoResult;
import tv.newtv.cboxtv.cms.mainPage.view.IMainPageView;

/**
 * Created by lixin on 2018/1/16.
 */

public class MainPagePresenter implements IMainPagePresenter {

    private IMainPageModel mModel;
    private IMainPageView mMainPageView;
    private Context mContext;

    public MainPagePresenter(IMainPageView mainPageView, Context context) {
        mModel = new MainPageModel(this, context);
        mMainPageView = mainPageView;
        mContext = context;
    }

    @Override
    public void requestNavData() {
        if (mModel != null) {
            mModel.requestNavBarData();
        }
    }

    @Override
    public void onFailed(String desc) {
        if(mMainPageView != null){
            mMainPageView.onFailed(desc);
        }
    }

    @Override
    public void inflateNavigationBar(NavInfoResult navInfoResult, String dataFrom) {
        if (mMainPageView != null) {
            mMainPageView.inflateNavigationBar(navInfoResult, dataFrom);
        }
    }
}
