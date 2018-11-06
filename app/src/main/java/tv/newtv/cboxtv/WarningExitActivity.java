package tv.newtv.cboxtv;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;

import com.newtv.cms.contract.AdContract;
import com.newtv.libs.ad.ADHelper;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.ScaleUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tv.icntv.adsdk.AdSDK;
import tv.newtv.cboxtv.cms.search.bean.SearchHotInfo;
import tv.newtv.cboxtv.cms.search.bean.SearchResultInfos;
import tv.newtv.cboxtv.cms.search.presenter.SearchPagePresenter;
import tv.newtv.cboxtv.cms.search.view.ISearchPageView;
import tv.newtv.cboxtv.exit.bean.RecommendBean;
import tv.newtv.cboxtv.exit.presenter.RecommendPresenterImpl;
import tv.newtv.cboxtv.exit.view.RecommendView;
import tv.newtv.cboxtv.views.SpacesItemDecoration;
import tv.newtv.cboxtv.views.custom.RecycleImageView;


public class WarningExitActivity extends BaseActivity implements View.OnClickListener,
        ISearchPageView, View.OnFocusChangeListener,RecommendView {

    private RecycleImageView exit_image;
    private SearchResultInfos.ResultListBean mResultListBeanInfo;
    private List<SearchResultInfos.ResultListBean> mResultListBeanList;
    private RecyclerView mRecyclerView;
    private ExitPromptLikeAdapter mAdapter;
    private AdContract.Presenter mAdPresenter;
    RecommendPresenterImpl presenter;
    OvershootInterpolator mSpringInterpolator;
    SearchPagePresenter mSearchPagePresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrontStage = true;
        presenter = new RecommendPresenterImpl();
        presenter.attachView(this);
        presenter.getRecommendData();
        setContentView(R.layout.activity_warning_exit);
        mSpringInterpolator = new OvershootInterpolator(2.2f);

        final Button okButton = (Button) findViewById(R.id.okButton);
        final Button cancelButton = (Button) findViewById(R.id.cancelButton);

        exit_image = findViewById(R.id.exit_image);
        mSearchPagePresenter = new SearchPagePresenter(this, this);

        okButton.requestFocus();
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        okButton.setOnFocusChangeListener(this);
        cancelButton.setOnFocusChangeListener(this);

//        getAD();//获取广告
        initView();
    }


    private void initView() {


        mRecyclerView = findViewById(R.id.guess_like_recyclerview);
        mAdapter = new ExitPromptLikeAdapter(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager
                .HORIZONTAL, false));
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(getResources()
                .getDimensionPixelSize(R.dimen.width_10px)));
        mRecyclerView.setAdapter(mAdapter);
//        mSearchPagePresenter.requestPageRecommendData(Constant.APP_KEY, Constant.CHANNEL_ID);//获取服务器数据

    }

    //加载热搜数据，进行分类填充
    @Override
    public void inflatePageRecommendData(SearchHotInfo searchHotInfo) {
        try {
            if (searchHotInfo == null) {
                return;
            }
            mResultListBeanList = new ArrayList<>();
            List<SearchHotInfo.DataBean.ProgramsBean> moduleItemList = new ArrayList<>();
            SearchHotInfo.DataBean.ProgramsBean programInfo;
            if (searchHotInfo.getData() != null && searchHotInfo.getData().size() > 0) {
                for (int i = 0; i < searchHotInfo.getData().size(); i++) {
                    if (searchHotInfo.getData().get(i).getBlockType().equals("recommendOnCell")) {
                        if (searchHotInfo.getData().get(i).getPrograms() != null && searchHotInfo
                                .getData().get(i).getPrograms().size() > 0) {
                            for (int j = 0; j < searchHotInfo.getData().get(i).getPrograms().size
                                    (); j++) {
                                programInfo = searchHotInfo.getData().get(i).getPrograms().get(j);
                                moduleItemList.add(programInfo);
                            }
                        }
                    }
                }
                if (moduleItemList.size() > 0) {
                    for (int j = 0; j < moduleItemList.size(); j++) {
                        mResultListBeanInfo = new SearchResultInfos.ResultListBean();
                        mResultListBeanInfo.setUUID(moduleItemList.get(j).getContentUUID());
                        mResultListBeanInfo.setContentType(moduleItemList.get(j).getContentType());
                        mResultListBeanInfo.setType(moduleItemList.get(j).getActionType());
                        mResultListBeanInfo.setHpicurl(moduleItemList.get(j).getImg());
                        mResultListBeanInfo.setName(moduleItemList.get(j).getTitle());
                        if (moduleItemList.get(j).getActionUri() != null) {
                            mResultListBeanInfo.setActionUri(moduleItemList.get(j).getActionUri()
                                    .toString());
                        }

                        mResultListBeanList.add(mResultListBeanInfo);
                    }

                    mAdapter.appendToList(mResultListBeanList);
                    mAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            LogUtils.e("MM", "---inflatePageRecommendData：Exception：" + e.toString());
        }


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.okButton) {
            setResult(RESULT_OK);
        } else if (id == R.id.cancelButton) {
            setResult(RESULT_CANCELED);
        }
        finish();
    }


    //焦点变化监听
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            ScaleUtils.getInstance().onItemGetFocus(v);
        } else {
            ScaleUtils.getInstance().onItemLoseFocus(v);
        }
    }

    /**
     * 退出广告
     */
    public void getAD() {
        mAdPresenter.getAdByType("quit", null, "", null, new AdContract.Callback() {
            @Override
            public void showAd(@Nullable String type, @Nullable String url, @Nullable HashMap<?,
                    ?> hashMap) {
                if (!TextUtils.isEmpty(url)) {
                    exit_image.setVisibility(View.VISIBLE);
                    Picasso.get().load(url).into(exit_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            ADHelper.AD.ADItem item = mAdPresenter.getAdItem();
                            if(item != null) {
                                AdSDK.getInstance().report((item.mid + ""), item.aid + "", item.id + "",
                                        "", null, item.PlayTime + "", null);
                            }
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }
            }
        });
    }

    @Override
    public void showData(RecommendBean recommendBean) {

        if (recommendBean.getIsAd().equals("1")) {
            getAD();//获取广告
        } else {
            if (recommendBean.getBackground() != null) {
                Picasso.get().load(recommendBean.getBackground()).into(exit_image);
            }

        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }
}
