package tv.newtv.cboxtv.cms.search.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.newtv.cms.bean.SubContent;
import com.newtv.libs.util.LogUtils;

import java.util.ArrayList;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.search.adapter.SearchResultAdapter;
import tv.newtv.cboxtv.cms.search.custom.SearchRecyclerView;

//import tv.newtv.cboxtv.cms.net.ApiUtil;

/**
 * 类描述：搜索结果人物页
 * 创建人：wqs
 * 创建时间： 2018/3/29 0029 11:40
 * 修改人：wqs
 * 修改时间：2018/4/30 0029 18:40
 * 修改备注：新增人物页
 */
public class PersonFragment extends BaseFragment{

    private SearchRecyclerView mRecyclerView;
    private TextView mEmptyView;
    private TextView mResultTotalView;
    private ArrayList<SubContent> mDatas;
    private SearchResultAdapter mAdapter;
    private StaggeredGridLayoutManager mLayoutManager;

    public boolean mDataStatus = false;//获取数据的状态

    public int mPageNum = 1;
    private String mPageSize = "48";
    private String mKeyType = "name";
    private String mType = "FG";

    @Override
    public View createView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.newtv_search_result_fragment_person, null, false);
        mRecyclerView =  view.findViewById(R.id.id_search_result_person_recyclerView);
        mResultTotalView = view.findViewById(R.id.id_fragment_person_result_total);
        mEmptyView = view.findViewById(R.id.id_search_result_person_empty);

        mLayoutManager = new StaggeredGridLayoutManager(6, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        setRecycleView(mRecyclerView,mLayoutManager);
        setSearchRecyclerView(mRecyclerView);
        return view;
    }

    public void setData(ArrayList<SubContent> result,Integer total) {

        if (result != null && result.size() > 0) {
            LogUtils.e("loaddata","fg result : " + result.size());
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
                mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), mDatas.size() - 1);
            }
        } else {
            if(mDatas == null || mDatas.size() == 0) {
                mDataStatus = false;
                mEmptyView.setVisibility(View.VISIBLE);
                mResultTotalView.setVisibility(View.INVISIBLE);
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
        return String.valueOf(mPageNum);
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
        setData(result,total);
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
    }

    public View findDefaultFocus() {
        if(mAdapter != null){
            return mAdapter.getlastFocusView();
        }
        return null;
    }

}
