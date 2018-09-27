package tv.newtv;

import com.newtv.cms.BuildConfig;
import com.newtv.cms.CmsServicePresenter;
import com.newtv.cms.DataObserver;
import com.newtv.cms.ICmsPresenter;
import com.newtv.cms.ICmsView;
import com.newtv.cms.api.IContent;
import com.newtv.cms.api.INav;
import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.Nav;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv
 * 创建事件:         14:14
 * 创建人:           weihaichao
 * 创建日期:          2018/9/27
 */
class NavContract {
    /**
     * View接口
     */
    interface View extends ICmsView<Presenter> {
        void onNavResult(List<Nav> result);
        void onContentResult(Content content);
    }

    /**
     * Presenter接口
     */
    interface Presenter extends ICmsPresenter {
        void getNav();
        void getContent(String contentId);
    }

    static class NavPresenter extends CmsServicePresenter<View> implements Presenter {

        NavPresenter(View view) {
            super(view);
            view.setPresenter(this);
        }

        @Override
        public void getNav() {
            INav nav = getService(SERVICE_NAV);
            if (nav != null) nav.getNav(BuildConfig.APP_KEY, BuildConfig.CHANNEL_ID,
                    new DataObserver<ModelResult<List<Nav>>>() {
                        @Override
                        public void onResult(ModelResult<List<Nav>> result) {
                            if (result.isOk()) getView().onNavResult(result.getData());
                            else onError(result.getErrorMesssage());
                        }

                        @Override
                        public void onError(@Nullable String desc) {
                            getView().onError(desc);
                        }
                    });
        }

        @Override
        public void getContent(String contentId) {
            IContent content = getService(SERVICE_CONTENT);
            if (content != null)
                content.getInfo(BuildConfig.APP_KEY, BuildConfig.CHANNEL_ID, contentId,
                        new DataObserver<ModelResult<Content>>() {
                            @Override
                            public void onResult(ModelResult<Content> result) {
                                if (result.isOk()) getView().onContentResult(result.getData());
                                else onError(result.getErrorMesssage());
                            }

                            @Override
                            public void onError(@Nullable String desc) {
                                getView().onError(desc);
                            }
                        });
        }
    }
}
