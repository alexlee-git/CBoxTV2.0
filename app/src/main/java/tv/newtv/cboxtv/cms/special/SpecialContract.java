package tv.newtv.cboxtv.cms.special;

import java.util.List;

import tv.newtv.cboxtv.cms.mainPage.model.ModuleInfoResult;
import tv.newtv.cboxtv.cms.special.base.BaseView;
import tv.newtv.cboxtv.cms.mainPage.model.ModuleItem;
import tv.newtv.cboxtv.cms.special.base.BasePresenter;

/**
 * Created by lin on 2018/3/7.
 */

public interface SpecialContract {
    interface View extends BaseView<Presenter> {
        void showPageContent(ModuleInfoResult moduleInfoResult);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {
        void start(String appkey, String channelId, String pageUUID);
        void getPageData(String appkey, String channelId, String pageUUID);
    }
}
