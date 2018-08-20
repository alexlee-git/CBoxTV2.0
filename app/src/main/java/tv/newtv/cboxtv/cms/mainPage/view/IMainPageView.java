package tv.newtv.cboxtv.cms.mainPage.view;

import tv.newtv.cboxtv.cms.mainPage.model.NavInfoResult;

/**
 * Created by lixin on 2018/1/16.
 */

public interface IMainPageView {

    void inflateNavigationBar(NavInfoResult navInfoResult, String dataFrom);
    void onFailed(String desc);
}
