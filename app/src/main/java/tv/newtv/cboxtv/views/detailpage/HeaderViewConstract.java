package tv.newtv.cboxtv.views.detailpage;

import android.content.Context;

import com.newtv.cms.BuildConfig;
import com.newtv.cms.CmsServicePresenter;
import com.newtv.cms.DataObserver;
import com.newtv.cms.ICmsPresenter;
import com.newtv.cms.ICmsView;
import com.newtv.cms.api.IContent;
import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.ModelResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         16:55
 * 创建人:           weihaichao
 * 创建日期:          2018/10/8
 */
@SuppressWarnings("SpellCheckingInspection")
class HeaderViewConstract {
    interface View extends ICmsView<Presenter> {
        void onInfoResult(Content content);
    }

    interface Presenter extends ICmsPresenter {
        void requestInfo(String uuid);
    }

    static class HeaderViewPresenter extends CmsServicePresenter<View> implements Presenter {

        HeaderViewPresenter(@NotNull Context context, View view) {
            super(context, view);
            view.setPresenter(this);
        }

        @Override
        public void requestInfo(String uuid) {
            IContent content = getService(SERVICE_CONTENT);
            if (content != null) {
                content.getContentInfo(BuildConfig.APP_KEY, BuildConfig.CHANNEL_ID, uuid, new
                        DataObserver<ModelResult<Content>>() {
                            @Override
                            public void onResult(ModelResult<Content> result) {
                                if (result != null && result.isOk()) {
                                    getView().onInfoResult(result.getData());
                                } else {
                                    getView().onError(getContext(), "Error");
                                }
                            }

                            @Override
                            public void onError(@Nullable String desc) {
                                getView().onError(getContext(), desc);
                            }
                        });
            }
        }
    }


}
