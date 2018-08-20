package tv.newtv.cboxtv.cms.listPage.presenter;

import tv.newtv.cboxtv.cms.listPage.model.NavListPageInfoResult;

/**
 * Created by lixin on 2018/1/15.
 */

public interface IListPagePresenter {

    void inflateListPageNav(NavListPageInfoResult value,String from);
    void onFailed(String desc);
    void requestListPageNav(String uuid);

}
