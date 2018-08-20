package tv.newtv.cboxtv.cms.mainPage.view;

import tv.newtv.cboxtv.cms.mainPage.model.ModuleInfoResult;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.mainPage.view
 * 创建事件:         14:28
 * 创建人:           weihaichao
 * 创建日期:          2018/4/12
 */
public interface IContentPageView {
    void inflateContentPage(ModuleInfoResult navInfoResult, String dataFrom);
    void onFailed(String desc);
}
