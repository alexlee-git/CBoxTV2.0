package tv.newtv.cboxtv.cms.search.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.newtv.cms.bean.SubContent;
import com.newtv.cms.contract.SearchContract;
import com.newtv.libs.util.LogUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

import tv.newtv.cboxtv.JumpScreen;
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.cms.search.adapter.SearchResultAdapter;
import tv.newtv.cboxtv.cms.search.custom.SearchRecyclerView;
import tv.newtv.cboxtv.cms.search.listener.SearchResultDataInfo;

/**
 * 类描述：
 * 创建人：wqs
 * 创建时间： 2018/3/29 0029 11:43
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public abstract class SearchBaseFragment extends Fragment implements SearchContract.LoadingView,
        SearchResultAdapter.SearchHolderAction {

    public View mLabelFocusView;//line
    public View mLabelView;
    public SearchRecyclerView mSearchRecyclerView;
    private int currentPos = -1; // 当前加载到第几页的索引（第一个索引值）
    private int totalSize = -1;
    private TextView titleText;
    private View contentView;
    private HashMap<String, SearchResult> cacheDatas;
    private String currentkey;
    private int mIndex = 0;
    private SearchResultDataInfo mSearchResultDataInfo;
    private Long requestId = 0L;
    private SearchContract.Presenter mSearchPresenter;
    private boolean mIsLoading = false;
    private View mLoadingLayout;
    private View mLoadingImg;

    public SearchBaseFragment() {
        mSearchPresenter = new SearchContract.SearchPresenter(LauncherApplication.AppContext, this);
    }


    public abstract String getType();

    public abstract String getKeyType();

    public abstract String getPageNum();

    public abstract String getPageSize();

    protected abstract void creasePage();

    public abstract View findDefaultFocus();

    protected abstract void onResult(long requestID, @Nullable ArrayList<SubContent> result,
                                     @Nullable Integer total);

    protected abstract void inputKeyChange();

    @Override
    public void onItemClick(int position, SubContent subContent) {
//        JumpUtil.detailsJumpActivity(getContext(), subContent.getContentType(), subContent
//                .getContentID());
//        ToastUtil.showToast(getContext(),"当前是第 ：" + position +"项");//测试使用
        JumpScreen.jumpDetailActivity(getContext(), subContent.getContentID(), subContent
                .getContentType());

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            titleText.setTextColor(Color.parseColor("#13d6f9"));
        } else {
            titleText.setTextColor(Color.parseColor("#ededed"));
        }
    }

    @Override
    public void onFocusToTop() {
        mLabelView.requestFocus();
    }

    protected void setRecycleView(final RecyclerView recycleView, final
    StaggeredGridLayoutManager layoutManager) {
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

    protected abstract View createView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (contentView == null) {
            contentView = createView(inflater, container, savedInstanceState);
        }

        if (contentView.getParent() != null) {
            ViewGroup viewGroup = (ViewGroup) contentView.getParent();
            viewGroup.removeView(contentView);
        }

        return contentView;
    }

    public void setKey(String key) {
//        key = key.trim();
        if (!TextUtils.isEmpty(currentkey) && cacheDatas != null) {

            if (key.length() < currentkey.length() && !currentkey.endsWith(" ") && "".equals(currentkey.replaceAll(" ",""))) {
                if (cacheDatas.containsKey(key)) {
                    SearchResult current = cacheDatas.remove(key);
                    notifyToDataInfoResult(current.contents == null || current.contents.size() <=
                            0);
                    onResult(requestId, current.contents, current.total);
                    currentkey = key;
                    return;
                }
            } else {
                if (Integer.parseInt(getPageNum()) > 1) {
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

    public void setLoadingLayout(View loadingLayout, View loadingImg) {
        mLoadingLayout = loadingLayout;
        mLoadingImg = loadingImg;
    }

    public void setLabelFocusView(View view) {
        mLabelFocusView = view;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public SearchRecyclerView getSearchRecyclerView() {
        return mSearchRecyclerView;
    }

    public void setSearchRecyclerView(SearchRecyclerView searchRecyclerView) {
        mSearchRecyclerView = searchRecyclerView;
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

    private void requestData(String key) {
        if (TextUtils.isEmpty(key) || "".equals(key.replace(" ",""))) {
            if (cacheDatas != null) {
                cacheDatas.clear();
            }
            currentkey = "";
            currentPos = -1;
            mIsLoading = false;
            if (mSearchPresenter != null){
                mSearchPresenter.stop();
            }
            inputKeyChange();
            notifyToDataInfoResult(true);
            return;
        }

        if (!TextUtils.equals(currentkey, key)) {
            currentPos = -1;
            inputKeyChange();
        }

//        if (!TextUtils.isEmpty(currentkey) &&
//                key.length()<currentkey.length() &&
//                " ".equals(currentkey.substring(key.length(),currentkey.length())) &&
//                !"".equals(currentkey.replace(" ",""))
//                ){
//            return;
//        }
//        if (!TextUtils.isEmpty(currentkey) &&
//                key.length()>currentkey.length() &&
//                " ".equals(key.substring(currentkey.length(),key.length())) &&
//                !"".equals(currentkey.replace(" ",""))
//                ){
//            return;
//        }


//        else {
//            return;
//        }

        SearchContract.SearchCondition conditionTV = SearchContract.SearchCondition
                .Companion
                .Builder()
                .setContentType(getType())
                .setKeyword(key.trim())
                .setKeywordType(getKeyType())
                .setPage(getPageNum())//页号
                .setRows(getPageSize());//每页条数
        if (requestId != 0L) {
            mSearchPresenter.cancel(requestId);
        }
        currentkey = key;
        mIsLoading = true;
        notifyToDataInfoResult(true);
        requestId = mSearchPresenter.search(conditionTV);
    }

    public void requestNextPageData() {

        SearchContract.SearchCondition conditionTV = SearchContract.SearchCondition
                .Companion
                .Builder()
                .setContentType(getType())
                .setKeyword(currentkey.trim())
                .setKeywordType(getKeyType())
                .setPage(String.valueOf(getPageNum()))//页号
                .setRows(getPageSize());//每页条数
        mIsLoading = true;
        requestId = mSearchPresenter.search(conditionTV);
    }

    public boolean isLoading() {
        return mIsLoading;
    }

    @Override
    public void onLoading() {
        startLoadingAni();
    }

    @Override
    public void loadingFinish() {
        stopLoadingAni();
    }

    private void startLoadingAni() {
        AnimationDrawable mAni = (AnimationDrawable) mLoadingImg.getBackground();
        mLoadingLayout.setVisibility(View.VISIBLE);
        mAni.start();
    }

    private void stopLoadingAni() {
        AnimationDrawable mAni = (AnimationDrawable) mLoadingImg.getBackground();
        mLoadingLayout.setVisibility(View.GONE);
        mAni.stop();
    }

    @Override
    public void searchResult(long reqId, @Nullable ArrayList<SubContent> result, @Nullable
            Integer total) {
        totalSize = total;

        if (requestId != reqId) {
            return;
        }
        if (cacheDatas == null) {
            cacheDatas = new HashMap<>();
        }

        cacheDatas.put(currentkey, new SearchResult(result, total));
        mIsLoading = false;
        if ("1".equals(getPageNum())) {
            notifyToDataInfoResult(result == null || result.size() <= 0);
        }
        onResult(reqId, result, total);
    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @Nullable String desc) {
        LogUtils.e("BaseFragment", "onError:" + desc);
        notifyToDataInfoResult(true);
    }

    private static class SearchResult {
        private ArrayList<SubContent> contents;
        private Integer total;

        private SearchResult(ArrayList<SubContent> value, Integer size) {
            contents = value;
            total = size;
        }
    }

    public void onDestroyPresenter(){
        if (mSearchPresenter != null){
            mSearchPresenter.destroy();
            mSearchPresenter = null;
        }
    }

}
