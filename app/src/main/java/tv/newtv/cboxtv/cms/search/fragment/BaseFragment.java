package tv.newtv.cboxtv.cms.search.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.newtv.cms.bean.SubContent;
import com.newtv.cms.contract.SearchContract;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.ToastUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.cms.search.adapter.SearchResultAdapter;
import tv.newtv.cboxtv.cms.search.custom.SearchRecyclerView;
import tv.newtv.cboxtv.cms.search.listener.OnGetSearchResultFocus;
import tv.newtv.cboxtv.cms.search.listener.SearchResultDataInfo;

/**
 * 类描述：
 * 创建人：wqs
 * 创建时间： 2018/3/29 0029 11:43
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public abstract class BaseFragment extends Fragment implements SearchContract.LoadingView,SearchResultAdapter.SearchHolderAction {

    private int currentPos = -1;
    private int totalSize = -1;

    private TextView titleText;

    public abstract String getType();

    public abstract String getKeyType();

    public abstract String getPageNum();

    public abstract String getPageSize();

    protected abstract void creasePage();

    public abstract View findDefaultFocus();

    protected abstract void onResult(long requestID, @Nullable ArrayList<SubContent> result, @Nullable Integer total);

    protected abstract void inputKeyChange();

    @Override
    public void onItemClick(int position){

        ToastUtil.showToast(getContext(),"点击第 " + position + " 项");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            titleText.setTextColor(Color.parseColor("#13d6f9"));
        }else{
            titleText.setTextColor(Color.parseColor("#ededed"));
        }
    }

    @Override
    public void onFocusToTop(){
        mLabelView.requestFocus();
    }

    static class SearchResult {
        private ArrayList<SubContent> contents;
        private Integer total;

        private SearchResult(ArrayList<SubContent> value, Integer size) {
            contents = value;
            total = size;
        }
    }

    protected void setRecycleView(final RecyclerView recycleView, final StaggeredGridLayoutManager layoutManager) {
        recycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int[] position;
                if (recycleView.getScrollY() == 0) {
                    position = layoutManager.findFirstVisibleItemPositions(new int[6]);
                } else {
                    position = layoutManager.findLastVisibleItemPositions(new int[6]);
                }
                if (position[0] % 48 == 0 && position[0] != currentPos && position[0] < totalSize) {
                    if (position[0] < currentPos) {
                        return;
                    }
                    currentPos = position[0];
                    creasePage();
                    LogUtils.d("scroll", "requestPage =" + getPageNum());
                    requestNextPageData();
                }

            }
        });
    }

    private View contentView;

    protected abstract View createView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (contentView == null) {
            contentView = createView(inflater, container, savedInstanceState);
        }

        if (contentView.getParent() != null) {
            ViewGroup viewGroup = (ViewGroup) contentView.getParent();
            viewGroup.removeView(contentView);
        }

        return contentView;
    }

    private HashMap<String, SearchResult> cacheDatas;
    public View mLabelFocusView;//line
    public View mLabelView;
    public LinearLayout mLoadingLayout;
    private String currentkey;
    private int mIndex = 0;
    private SearchResultDataInfo mSearchResultDataInfo;
    public SearchRecyclerView mSearchRecyclerView;

    public void setKey(String key) {
        if (!TextUtils.isEmpty(currentkey)) {
            if (key.length() < currentkey.length()) {
                if(cacheDatas.containsKey(key)) {
                    SearchResult current = cacheDatas.remove(key);
                    notifyToDataInfoResult(current.contents == null || current.contents.size() <= 0);
                    onResult(requestId, current.contents, current.total);
                    return;
                }
            } else {
                if(Integer.parseInt(getPageNum()) > 1) {
                    cacheDatas.remove(currentkey);
                }
            }
        }

        requestData(key);
    }

    public void setLabelView(View view) {
        mLabelView = view;
        titleText = mLabelView.findViewWithTag("title_text");
    }

    public void setLoadingLayout(LinearLayout loadingLayout){
        mLoadingLayout = loadingLayout;
    }

    public void setLabelFocusView(View view) {
        mLabelFocusView = view;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setSearchRecyclerView(SearchRecyclerView searchRecyclerView){
        mSearchRecyclerView = searchRecyclerView;
    }

    public SearchRecyclerView getSearchRecyclerView(){
        return mSearchRecyclerView;
    }

    public void attachDataInfoResult(SearchResultDataInfo resultDataInfo) {
        mSearchResultDataInfo = resultDataInfo;
    }

    protected void notifyToDataInfoResult(boolean isGone) {
        if (mLabelView != null) {
            mLabelView.setVisibility(isGone ? View.GONE : View.VISIBLE);
        }
        mSearchResultDataInfo.updateFragmentList(this, isGone);
    }

    private Long requestId = 0L;
    private SearchContract.Presenter mSearchPresenter;

    public BaseFragment() {
        mSearchPresenter = new SearchContract.SearchPresenter(LauncherApplication.AppContext, this);
    }

    private void requestData(String key) {
        key = key.trim();
        if (!TextUtils.equals(currentkey, key)) {
            currentPos = -1;

            inputKeyChange();
        }
        SearchContract.SearchCondition conditionTV = SearchContract.SearchCondition
                .Companion
                .Builder()
                .setContentType(getType())
                .setKeyword(key)
                .setKeywordType(getKeyType())
                .setPage(getPageNum())//页号
                .setRows(getPageSize());//每页条数
        if (requestId != 0L) {
            mSearchPresenter.cancel(requestId);
        }
        currentkey = key;
        requestId = mSearchPresenter.search(conditionTV);
    }

    public void requestNextPageData() {

        SearchContract.SearchCondition conditionTV = SearchContract.SearchCondition
                .Companion
                .Builder()
                .setContentType(getType())
                .setKeyword(currentkey)
                .setKeywordType(getKeyType())
                .setPage(String.valueOf(getPageNum()))//页号
                .setRows(getPageSize());//每页条数

        requestId = mSearchPresenter.search(conditionTV);
    }

    @Override
    public void onLoading() {
        mLoadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void loadingFinish() {
        mLoadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void searchResult(long reqId, @Nullable ArrayList<SubContent> result, @Nullable Integer total) {
        totalSize = total;

        if (requestId != reqId) {
            return;
        }
        if (cacheDatas == null) {
            cacheDatas = new HashMap<>();
        }

        cacheDatas.put(currentkey, new SearchResult(result, total));
        if (getPageNum().equals("1")) {
            notifyToDataInfoResult(result == null || result.size() <= 0);
        }
        onResult(reqId, result, total);
    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @Nullable String desc) {

    }
}
