package tv.newtv.cboxtv.cms.special.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.AiyaRecyclerView;
import tv.newtv.cboxtv.cms.mainPage.model.ModuleInfoResult;
import tv.newtv.cboxtv.cms.mainPage.model.ModuleItem;
import tv.newtv.cboxtv.cms.mainPage.viewholder.UniversalAdapter;
import tv.newtv.cboxtv.cms.special.viewholder.SpecialUniversalAdapter;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.special.fragment
 * 创建事件:         14:50
 * 创建人:           weihaichao
 * 创建日期:          2018/4/25
 */
public class DefaultSpecial extends BaseSpecialContentFragment {

    private TextView mPageTitle;
    private TextView mPageSubTitle;
    private Button mPageFavoriteButton;
    private AiyaRecyclerView mRecyclerView;
    private List<ModuleItem> mDatas;
    private UniversalAdapter mAdapter;
    private ModuleInfoResult mModuleInfoResult;


    @Override
    protected int getLayoutId() {
        return R.layout.special_default_layout;
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

        if(mModuleInfoResult != null){
            updateUI();
        }
    }

    @Override
    public void setModuleInfo(ModuleInfoResult infoResult) {
        mModuleInfoResult = infoResult;
        if(getView() != null) {
            updateUI();
        }
    }

    private void updateUI(){
        if (mModuleInfoResult.getIsCollection() != 0) {
            mPageFavoriteButton.setVisibility(View.VISIBLE);
        } else {
            mPageFavoriteButton.setVisibility(View.GONE);
        }

        setTitleText(mModuleInfoResult);
        UniversalAdapter adapter = (UniversalAdapter) mRecyclerView.getAdapter();
        if (adapter == null) {
            mDatas = mModuleInfoResult.getDatas();
            adapter = new SpecialUniversalAdapter(getContext(), mDatas);
             Log.e(Constant.TAG, "DefaultSpecial_mDatas : "+mDatas);

            adapter.setHasStableIds(true);
            mAdapter = adapter;
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mDatas.clear();
            mDatas.addAll(mModuleInfoResult.getDatas());
            adapter.notifyDataSetChanged();
        }
		 adapter.showFirstLineTitle(true);
    }

    private void setTitleText(ModuleInfoResult moduleInfoResult) {
        //display page title
        if (!TextUtils.isEmpty(moduleInfoResult.getPageTitle())) {
            mPageTitle.setVisibility(View.VISIBLE);
            mPageTitle.setText(moduleInfoResult.getSubTitle());
//            mPageTitle.setText(moduleInfoResult.getPageTitle());
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
