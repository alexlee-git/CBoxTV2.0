package com.newtv.cms.contract;

import android.content.Context;

import com.newtv.cms.CmsServicePresenter;
import com.newtv.cms.DataObserver;
import com.newtv.cms.ICmsPresenter;
import com.newtv.cms.ICmsView;
import com.newtv.cms.api.ISearch;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.SubContent;
import com.newtv.libs.Libs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.contract
 * 创建事件:         14:44
 * 创建人:           weihaichao
 * 创建日期:          2018/10/16
 */
public class SearchContract {
    public interface View extends ICmsView {
        void searchResult(List<SubContent> result);
    }

    public interface Presenter extends ICmsPresenter {
        /**
         * 搜索
         */
        void search(SearchCondition condition);
    }

    public static class SearchCondition {

        String categoryId = "";
        String contentType = "";
        String videoType = "";
        String videoClass = "";
        String area = "";
        String year = "";
        String keyword = "";
        String page = "0";          //第几页
        String rows = "40";         //每页条数
        String keywordType = "";

        /**
         * @return
         */
        public static SearchCondition Builder() {
            return new SearchCondition();
        }

        public SearchCondition setRows(String rows) {
            this.rows = rows;
            return this;
        }

        public SearchCondition setPage(String page) {
            this.page = page;
            return this;
        }

        public SearchCondition setKeywordType(String keywordType) {
            this.keywordType = keywordType;
            return this;
        }

        public SearchCondition setKeyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public SearchCondition setYear(String year) {
            this.year = year;
            return this;
        }

        public SearchCondition setVideoType(String videoType) {
            this.videoType = videoType;
            return this;
        }

        public SearchCondition setVideoClass(String videoClass) {
            this.videoClass = videoClass;
            return this;
        }

        public SearchCondition setContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public SearchCondition setCategoryId(String categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public SearchCondition setArea(String area) {
            this.area = area;
            return this;
        }
    }

    public static class SearchPresenter extends CmsServicePresenter<View> implements Presenter {

        public SearchPresenter(@NotNull Context context, @NotNull View view) {
            super(context, view);
        }

        @Override
        public void search(SearchCondition condition) {
            ISearch search = getService(SERVICE_SEARCH);
            if (search != null) {
                search.search(
                        Libs.get().getAppKey(),
                        Libs.get().getChannelId(), condition.categoryId,
                        condition.contentType, condition.videoType, condition.videoClass, condition
                                .area, condition.year, condition.keyword, condition.page, condition
                                .rows, condition.keywordType, new
                                DataObserver<ModelResult<List<SubContent>>>() {
                                    @Override
                                    public void onResult(ModelResult<List<SubContent>> result) {
                                        if (result.isOk()) {
                                            getView().searchResult(result.getData());
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
