package tv.newtv.cboxtv.views.detail;

import android.content.Context;

import com.newtv.cms.BuildConfig;
import com.newtv.cms.CmsServicePresenter;
import com.newtv.cms.DataObserver;
import com.newtv.cms.ICmsPresenter;
import com.newtv.cms.ICmsView;
import com.newtv.cms.api.IContent;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.SubContent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         17:49
 * 创建人:           weihaichao
 * 创建日期:          2018/10/8
 */
class EpisodePageConstract {
    interface View extends ICmsView {
        void onSubContentResult(List<SubContent> contents);
    }

    interface Presenter extends ICmsPresenter {
        void getSubContent(String uuid);
    }

    static class EpisodePagePresenter extends CmsServicePresenter<View> implements Presenter {

        EpisodePagePresenter(@NotNull Context context, View view) {
            super(context, view);
        }

        @Override
        public void getSubContent(String uuid) {
            IContent content = getService(SERVICE_CONTENT);
            if (content != null) {
                content.getSubContent(BuildConfig.APP_KEY, BuildConfig.CHANNEL_ID, uuid, new
                        DataObserver<ModelResult<List<SubContent>>>() {

                            @Override
                            public void onResult(ModelResult<List<SubContent>> result) {
                                if (result != null) {
                                    if(result.isOk()) {
                                        getView().onSubContentResult(result.getData());
                                    }else{
                                        getView().onError(getContext(),result.getErrorMessage());
                                    }
                                }else{
                                    getView().onError(getContext(),"Error");
                                }
                            }

                            @Override
                            public void onError(@Nullable String desc) {
                                getView().onError(getContext(),desc);
                            }
                        });
            }
        }
    }
}
