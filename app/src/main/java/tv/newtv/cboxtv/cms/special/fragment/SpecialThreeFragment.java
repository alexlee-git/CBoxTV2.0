package tv.newtv.cboxtv.cms.special.fragment;

import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.Page;
import com.newtv.libs.Constant;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.AiyaRecyclerView;
import tv.newtv.cboxtv.cms.mainPage.viewholder.UniversalAdapter;
import tv.newtv.cboxtv.cms.special.viewholder.SpecialUniversalAdapter;
import tv.newtv.cboxtv.cms.util.ModuleLayoutManager;

public class SpecialThreeFragment extends BaseSpecialContentFragment {

    private TextView mPageTitle;
    private TextView mPageSubTitle;
    private Button mPageFavoriteButton;
    private AiyaRecyclerView mRecyclerView;
    private List<Page> mDatas;
    private UniversalAdapter mAdapter;
    private ModelResult<ArrayList<Page>> mModuleInfoResult;


    @Override
    protected int getLayoutId() {
        return R.layout.special_three_layout;
    }

    @Override
    protected void onItemContentResult(String uuid, Content content, int playIndex) {

    }

    @Override
    protected void setUpUI(View view) {
        mPageTitle = (TextView) view.findViewById(R.id.page_title);
        mPageSubTitle = (TextView) view.findViewById(R.id.page_desc);
        mPageFavoriteButton = (Button) view.findViewById(R.id.page_favorite);
        mRecyclerView = (AiyaRecyclerView) view.findViewById(R.id.id_content_fragment_root);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstItem = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                        .findFirstCompletelyVisibleItemPosition();
                int lastItem = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                        .findLastCompletelyVisibleItemPosition();
                // Log.e(Constant.TAG, "dy : " + dy + ", firstItem : " + firstItem + ",lastItem:" + lastItem);
            }
        });

        if (mModuleInfoResult != null) {
            updateUI();
        }
    }

    @Override
    public void setModuleInfo(ModelResult<ArrayList<Page>> infoResult) {
        mModuleInfoResult = infoResult;
        if (getView() != null) {
            updateUI();
        }
    }

    private void updateUI() {

        //TODO 判断是不是已经收藏的

        setTitleText(mModuleInfoResult);
        UniversalAdapter adapter = (UniversalAdapter) mRecyclerView.getAdapter();
        mDatas = mModuleInfoResult.getData();
        ModuleLayoutManager.getInstance().filterLayoutDatas(mDatas);
        if (adapter == null) {
            adapter = new SpecialUniversalAdapter(getContext(), mDatas);
            Log.e(Constant.TAG, "DefaultSpecial_mDatas : " + mDatas);
            adapter.setHasStableIds(true);
            mAdapter = adapter;
            mRecyclerView.setAdapter(mAdapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        adapter.showFirstLineTitle(true);
    }

    private void setTitleText(ModelResult<ArrayList<Page>> moduleInfoResult) {
        //display page title
        if (!TextUtils.isEmpty(moduleInfoResult.getPageTitle())) {
            mPageTitle.setVisibility(View.VISIBLE);
            mPageTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            mPageTitle.getPaint().setFakeBoldText(true);
            mPageTitle.setText(moduleInfoResult.getSubTitle());
        } else {
            mPageTitle.setVisibility(View.GONE);
        }

        // display sub title
        if (!TextUtils.isEmpty(moduleInfoResult.getSubTitle())) {
            mPageSubTitle.setVisibility(View.VISIBLE);
            mPageSubTitle.setText(moduleInfoResult.getDescription());
        } else {
            mPageSubTitle.setVisibility(View.GONE);
        }
    }
}
