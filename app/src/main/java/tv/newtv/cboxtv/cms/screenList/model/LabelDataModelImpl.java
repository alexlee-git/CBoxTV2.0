package tv.newtv.cboxtv.cms.screenList.model;


import android.content.Context;

import com.newtv.cms.bean.SubContent;
import com.newtv.cms.contract.SearchContract;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;


/**
 * Created by 冯凯 on 2018/9/30.
 */

public class LabelDataModelImpl implements LabelDataModel, SearchContract.View {
    private SearchContract.Presenter mSearchPresenter;
    private Long currentSearchID = 0L;
    private DataCompleteListener dataCompleteListener;

    public LabelDataModelImpl(Context context) {
        mSearchPresenter = new SearchContract.SearchPresenter(context, this);
    }

    @Override
    public void requestLabelData(Map<String, Object> map, final DataCompleteListener listener) {
        dataCompleteListener = listener;

        if (currentSearchID != 0) {
            mSearchPresenter.cancel(currentSearchID);
        }
        SearchContract.SearchCondition condition = SearchContract.SearchCondition.Builder();
        Object years = map.get("years");
        Object categoryId = map.get("categoryId");
        Object classTypes = map.get("classTypes");
        Object rows = map.get("rows");
        Object areas = map.get("areas");
        Object page = map.get("page");
        if (categoryId != null) {
            condition.setCategoryId(categoryId.toString());
        }
        if (classTypes != null) {
            condition.setVideoClass(classTypes.toString());
        }
        if (areas != null) {
            condition.setArea(areas.toString());
        }
        if (years != null) {
            condition.setYear(years.toString());
        }
        condition.setRows(rows.toString());
        condition.setPage(page.toString());
        currentSearchID = mSearchPresenter.search(condition);
    }


    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @Nullable String desc) {

    }

    @Override
    public void searchResult(long requestID, @Nullable ArrayList<SubContent> result, @Nullable Integer total) {
        if (currentSearchID == requestID) {
            if (dataCompleteListener!=null&&result!=null){
                dataCompleteListener.sendLabelData(result, total);
            }
            currentSearchID = 0L;
        }
    }


}
