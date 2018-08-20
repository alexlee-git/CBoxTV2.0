package tv.newtv.cboxtv.cms.mainPage.presenter;

import tv.newtv.cboxtv.cms.mainPage.model.ModuleInfoResult;
import tv.newtv.cboxtv.cms.mainPage.model.NavInfoResult;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.mainPage.presenter
 * 创建事件:         14:25
 * 创建人:           weihaichao
 * 创建日期:          2018/4/12
 */
public interface IContentPagePresenter {
    void requestContentData(String uuid);
    void onFailed(String desc);
    void inflateContentView(ModuleInfoResult navInfoResult, String dataFrom);
}
