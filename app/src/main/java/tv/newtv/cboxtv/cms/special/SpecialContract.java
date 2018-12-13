package tv.newtv.cboxtv.cms.special;

import android.content.Context;
import android.text.TextUtils;

import com.newtv.cms.CmsServicePresenter;
import com.newtv.cms.ICmsPresenter;
import com.newtv.cms.ICmsView;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.Page;
import com.newtv.cms.contract.PageContract;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Created by lin on 2018/3/7.
 */
class SpecialContract {
    interface View extends ICmsView {
        boolean isActive();
    }

    interface ModelResultView extends View {
        void showPageContent(ModelResult<ArrayList<Page>> modelResult);
    }

    interface PageResultView extends View {
        void showPageContent(ArrayList<Page> modelResult);
    }

    interface Presenter extends ICmsPresenter {
        void start(String appkey, String channelId, String pageUUID);
    }

    static class SpecialPresenter extends CmsServicePresenter<View> implements Presenter,
            PageContract.ModelView {

        private PageContract.Presenter mPresenter;

        public SpecialPresenter(@NotNull Context context, View view) {
            super(context, view);
            mPresenter = new PageContract.ContentPresenter(context, this);
        }

        @Override
        public void destroy() {
            super.destroy();
            if (mPresenter != null) {
                mPresenter.destroy();
                mPresenter = null;
            }
        }

        @Override
        public void start(String appkey, String channelId, String pageUUID) {
            if(TextUtils.isEmpty(pageUUID)){
                getView().onError(getContext(), "" , "页面ID不能为空");
                return;
            }
            getPageData(appkey, channelId, pageUUID);
        }

        public void getPageData(String appkey, String channelId, String pageUUID) {
            mPresenter.getPageContent(pageUUID);
        }

        @Override
        public void onPageResult(@Nullable ModelResult<ArrayList<Page>> page) {
            View view = getView();
            if (view != null && page != null) {
                if (view instanceof SpecialContract.ModelResultView) {
                    ((SpecialContract.ModelResultView) view).showPageContent(page);
                } else if (view instanceof SpecialContract.PageResultView) {
                    ((SpecialContract.PageResultView) view).showPageContent(page.getData());
                }
            }
        }

        @Override
        public void tip(@NotNull Context context, @NotNull String message) {

        }

        @Override
        public void onError(@NotNull Context context, @NotNull String code, @Nullable String desc) {

        }

        @Override
        public void startLoading() {

        }

        @Override
        public void loadingComplete() {

        }
    }

}
