package com.newtv.cms.contract;

import android.content.Context;

import com.newtv.cms.CmsServicePresenter;
import com.newtv.cms.DataObserver;
import com.newtv.cms.ICmsPresenter;
import com.newtv.cms.ICmsView;
import com.newtv.cms.api.ITvProgram;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.SubContent;
import com.newtv.libs.Libs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.contract
 * 创建事件:         14:11
 * 创建人:           weihaichao
 * 创建日期:          2018/10/16
 */
public class SuggestContract {

    public interface View extends ICmsView {
        void columnSuggestResult(List<SubContent> result);
        void columnFiguresResult(List<SubContent> result);
    }

    public interface Presenter extends ICmsPresenter {
        /**
         * 栏目相关主持人
         */
        void getColumnFigures(String contentUUID);

        /**
         * 栏目相关推荐
         */
        void getColumnSuggest(String contentUUID);


    }

    public static class SuggestPresenter extends CmsServicePresenter<View> implements Presenter {

        public SuggestPresenter(@NotNull Context context, @NotNull View view) {
            super(context, view);
        }

        @Override
        public void getColumnFigures(String contentUUID) {
            ITvProgram tvProgram = getService(SERVICE_TV_PROGRAM);
            if (tvProgram != null) {
                tvProgram.getTvFigureList(
                        Libs.get().getAppKey(),
                        Libs.get().getChannelId(),
                        contentUUID, new DataObserver<ModelResult<List<SubContent>>>() {
                            @Override
                            public void onResult(ModelResult<List<SubContent>> result) {
                                if (result.isOk()) {
                                    getView().columnFiguresResult(result.getData());
                                } else {
                                    getView().onError(getContext(), result.getErrorMessage());
                                }
                            }

                            @Override
                            public void onError(@Nullable String desc) {
                                getView().onError(getContext(), desc);
                            }
                        });
            }
        }

        @Override
        public void getColumnSuggest(String contentUUID) {
            ITvProgram tvProgram = getService(SERVICE_TV_PROGRAM);
            if (tvProgram != null) {
                tvProgram.getTvFigureTvList(
                        Libs.get().getAppKey(),
                        Libs.get().getChannelId(),
                        contentUUID, new DataObserver<ModelResult<List<SubContent>>>() {
                            @Override
                            public void onResult(ModelResult<List<SubContent>> result) {
                                if (result.isOk()) {
                                    getView().columnSuggestResult(result.getData());
                                } else {
                                    getView().onError(getContext(), result.getErrorMessage());
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
