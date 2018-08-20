package tv.newtv.cboxtv.cms.mainPage.presenter;

import tv.newtv.cboxtv.cms.mainPage.model.NavInfoResult;

/**
 * Created by lixin on 2018/1/16.
 */

public interface IMainPagePresenter {

    void requestNavData();
    void onFailed(String desc);
    void inflateNavigationBar(NavInfoResult navInfoResult, String dataFrom);
}
