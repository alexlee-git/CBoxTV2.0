package tv.newtv.cboxtv.cms.mainPage.presenter;

import android.content.Context;

import tv.newtv.cboxtv.cms.mainPage.model.ContentPageModelImpl;
import tv.newtv.cboxtv.cms.mainPage.model.IContentPageModel;
import tv.newtv.cboxtv.cms.mainPage.model.ModuleInfoResult;
import tv.newtv.cboxtv.cms.mainPage.view.IContentPageView;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.mainPage.presenter
 * 创建事件:         14:25
 * 创建人:           weihaichao
 * 创建日期:          2018/4/12
 */
public class ContentpagePresenter implements IContentPagePresenter {
    private IContentPageView pageView;
    private IContentPageModel pageModel;

    private ModuleInfoResult moduleInfoResult;

    public ContentpagePresenter(Context context, IContentPageView view) {
        pageModel = new ContentPageModelImpl(context, this);
        pageView = view;
    }

    public void destroy() {
        pageView = null;
        if (pageModel != null) {
            pageModel.destroy();
            pageModel = null;
        }
        moduleInfoResult = null;
    }

    @Override
    public void requestContentData(String uuid) {
        pageModel.requestContentData(uuid);
    }

    @Override
    public void onFailed(String desc) {
        pageView.onFailed(desc);
    }

    @Override
    public void inflateContentView(ModuleInfoResult navInfoResult, String dataFrom) {
        moduleInfoResult = navInfoResult;
        pageView.inflateContentPage(navInfoResult, dataFrom);
    }

    public ModuleInfoResult getModuleInfoResult() {
        return this.moduleInfoResult;
    }
}
