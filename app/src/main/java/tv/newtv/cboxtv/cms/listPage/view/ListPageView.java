package tv.newtv.cboxtv.cms.listPage.view;

import tv.newtv.cboxtv.cms.listPage.model.NavListPageInfoResult;

/**
 * Created by caolonghe on 2018/3/6 0006.
 */

public interface ListPageView {

    void inflateListPage(NavListPageInfoResult value,String from);
    void onFailed(String desc);
}
