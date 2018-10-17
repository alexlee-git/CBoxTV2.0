package com.newtv.cms.contract;

import android.content.Context;

import com.newtv.cms.CmsServicePresenter;
import com.newtv.cms.DataObserver;
import com.newtv.cms.ICmsPresenter;
import com.newtv.cms.ICmsView;
import com.newtv.cms.api.IPage;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.Page;
import com.newtv.libs.Libs;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.cms.mainPage.view
 * 创建事件:         15:50
 * 创建人:           weihaichao
 * 创建日期:          2018/9/27
 */
public class PageContract {
    public interface View extends ICmsView {
        void onPageResult(List<Page> page);
    }

    public interface Presenter extends ICmsPresenter {
        void getPageContent(String contentId);
    }

    public static class ContentPresenter extends CmsServicePresenter<View> implements Presenter {

        public ContentPresenter(Context context, View view) {
            super(context, view);
        }

        @Override
        public void getPageContent(String contentId) {
            IPage page = getService(SERVICE_PAGE);
            if (page != null) {
                page.getPage(
                        Libs.get().getAppKey(),
                        Libs.get().getChannelId(),
                        contentId, new
                        DataObserver<ModelResult<List<Page>>>() {
                            @Override
                            public void onResult(ModelResult<List<Page>> result) {
                                if (result.isOk()) {
                                    getView().onPageResult(result.getData());
                                } else {
                                    onError(result.getErrorMessage());
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
