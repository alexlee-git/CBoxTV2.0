package tv.newtv.cboxtv.cms.search.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.newtv.cms.bean.SubContent;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.SharePreferenceUtils;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.search.adapter.SearchResultAdapter;
import tv.newtv.cboxtv.cms.search.custom.SearchRecyclerView;

/**
 * Created by linzy on 2018/11/12.
 *
 * des : 搜索结果：单片
 */

public class SingleProgramFragment  extends BaseFragment {

    private View view;
    private SearchRecyclerView mRecyclerView;
    private TextView mEmptyView;
    private TextView mResultTotalView;
    private SearchResultAdapter mAdapter;
    private StaggeredGridLayoutManager mLayoutManager;

    private ArrayList<SubContent> mDatas;
    public ArrayList<SubContent> mResult;

    public boolean mDataStatus = false;//获取数据的状态

    public int mPageNum = 1;
    private String mPageSize = "48";
    private String mKeyType = "name";
    private String mType = "PG";

    @Override
    public View createView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.newtv_search_result_fragment_person, null, false);
        mRecyclerView = view.findViewById(R.id.id_search_result_person_recyclerView);

        mResultTotalView = view.findViewById(R.id.id_fragment_person_result_total);
        mEmptyView = view.findViewById(R.id.id_search_result_person_empty);

        mLayoutManager = new StaggeredGridLayoutManager(6, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        setRecycleView(mRecyclerView,mLayoutManager);
        setSearchRecyclerView(mRecyclerView);
        return view;
    }

    public void setData(ArrayList<SubContent> result, Integer total) {

        if (result != null && result.size() > 0) {

            mResultTotalView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
            mDataStatus = true;
            mResultTotalView.setText(total + "个结果");

            if (mDatas == null) {
                mDatas = new ArrayList<>();
            }
            mDatas.addAll(result);
            mAdapter = (SearchResultAdapter) mRecyclerView.getAdapter();
            if (mAdapter == null) {
                mAdapter = new SearchResultAdapter(getContext(), mDatas, mLabelView, mRecyclerView);
                mAdapter.setSearchHolderAction(this);
                mRecyclerView.setAdapter(mAdapter);
            }else {
                mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), mDatas.size());
            }
        } else {
            if(mDatas == null || mDatas.size() == 0) {
                mDataStatus = false;
                if (mEmptyView != null){
                    mEmptyView.setVisibility(View.VISIBLE);
                }
                if (mResultTotalView != null){
                    mResultTotalView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public String getType() {
        return mType;
    }

    @Override
    public String getKeyType() {
        return mKeyType;
    }

    @Override
    public String getPageNum() {
        return Integer.toString(mPageNum);
    }

    @Override
    public String getPageSize() {
        return mPageSize;
    }

    @Override
    protected void creasePage() {
        mPageNum++;
    }

    @Override
    protected void onResult(long requestID, @Nullable ArrayList<SubContent> result, @Nullable Integer total) {
        if ("1".equals(getPageNum())) {
            if (result == null || result.size()<=0){
                view.setVisibility(View.GONE);
                mResult = null;
            }else {
                mResult = result;
                view.setVisibility(View.VISIBLE);
            }
        }
        setData(result, total);
    }

    @Override
    protected void inputKeyChange() {
        if (mDatas != null && mDatas.size()>0){
            mDatas.clear();
        }
        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        mPageNum = 1;
        mResult = null;
    }

    @Override
    public View findDefaultFocus() {
        if(mAdapter != null){
            return mAdapter.getlastFocusView();
        }
        return null;
    }
}
