package tv.newtv.cboxtv;

import android.animation.AnimatorSet;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;

import com.newtv.cms.contract.AdContract;
import com.newtv.libs.bean.AdBean;
import com.newtv.libs.util.GsonUtil;
import com.newtv.libs.util.LogUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import tv.icntv.adsdk.AdSDK;
import tv.newtv.cboxtv.cms.search.bean.SearchHotInfo;
import tv.newtv.cboxtv.cms.search.bean.SearchResultInfos;
import tv.newtv.cboxtv.cms.search.presenter.SearchPagePresenter;
import tv.newtv.cboxtv.cms.search.view.ISearchPageView;

import org.jetbrains.annotations.Nullable;

import tv.newtv.cboxtv.views.SpacesItemDecoration;
import tv.newtv.cboxtv.views.custom.RecycleImageView;


public class WarningExitActivity extends BaseActivity implements View.OnClickListener, ISearchPageView ,View.OnFocusChangeListener{

    private RecycleImageView exit_image;
    private SearchResultInfos.ResultListBean mResultListBeanInfo;
    private List<SearchResultInfos.ResultListBean> mResultListBeanList;
    private RecyclerView mRecyclerView;
    private ExitPromptLikeAdapter mAdapter;
    private SearchPagePresenter mSearchPagePresenter;
    private AnimatorSet mScaleAnimator;
    private Interpolator mSpringInterpolator;
    private AdContract.Presenter mAdPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrontStage = true;

        setContentView(R.layout.activity_warning_exit);
        mSpringInterpolator = new OvershootInterpolator(2.2f);

        final Button okButton = (Button) findViewById(R.id.okButton);
        final Button cancelButton = (Button) findViewById(R.id.cancelButton);

        exit_image = findViewById(R.id.exit_image);
        mSearchPagePresenter = new SearchPagePresenter(this, this);

        cancelButton.requestFocus();

        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        okButton.setOnFocusChangeListener(this);
        cancelButton.setOnFocusChangeListener(this);

        mAdPresenter = new AdContract.AdPresenter(getApplicationContext(),null);

        getAD();//获取广告
        initView();
    }


    private void initView(){


        mRecyclerView = findViewById(R.id.guess_like_recyclerview);
        mAdapter = new ExitPromptLikeAdapter(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) );
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.width_10px)));
        mRecyclerView.setAdapter(mAdapter);
//        mSearchPagePresenter.requestPageRecommendData(Constant.APP_KEY, Constant.CHANNEL_ID);//获取服务器数据

    }

    //加载热搜数据，进行分类填充
    @Override
    public void inflatePageRecommendData(SearchHotInfo searchHotInfo) {
        try {
            if (searchHotInfo==null){
                return;
            }
            mResultListBeanList = new ArrayList<>();
            List<SearchHotInfo.DataBean.ProgramsBean> moduleItemList = new ArrayList<>();
            SearchHotInfo.DataBean.ProgramsBean programInfo;
            if (searchHotInfo.getData() != null && searchHotInfo.getData().size() > 0) {
                for (int i = 0; i < searchHotInfo.getData().size(); i++) {
                    if (searchHotInfo.getData().get(i).getBlockType().equals("recommendOnCell")) {
                        if (searchHotInfo.getData().get(i).getPrograms() != null && searchHotInfo.getData().get(i).getPrograms().size() > 0) {
                            for (int j = 0; j < searchHotInfo.getData().get(i).getPrograms().size(); j++) {
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
                        if (moduleItemList.get(j).getActionUri()!=null){
                            mResultListBeanInfo.setActionUri(moduleItemList.get(j).getActionUri().toString());
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

        if (hasFocus){
            ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation
                    .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            sa.setFillAfter(true);
            sa.setDuration(400);
            sa.setInterpolator(mSpringInterpolator);
            v.startAnimation(sa);
        }else{
            ScaleAnimation sa = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f, Animation
                    .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            sa.setFillAfter(true);
            sa.setDuration(400);
            sa.setInterpolator(mSpringInterpolator);
            v.startAnimation(sa);
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
                if (!TextUtils.isEmpty(url)){
                    exit_image.setVisibility(View.VISIBLE);
                    Picasso.get().load(url).into(exit_image);
                }
            }
        });

        final StringBuffer sb = new StringBuffer();
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(AdSDK.getInstance().getAD("quit", null, null, null, null, null, sb));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer result) throws Exception {
                        if (result == 0) {
                            LogUtils.i("mm", "getAD:" + sb.toString());
                            AdBean bean = GsonUtil.fromjson(sb.toString(), AdBean.class);

                            if (bean==null||bean.adspaces==null||bean.adspaces.quit==null||bean.adspaces.quit.size()<1){
                                return;
                            }

                            final AdBean.AdspacesItem item = bean.adspaces.quit.get(0);


                            if (bean.adspaces.quit.get(0)==null||bean.adspaces.quit.get(0).materials==null){
                                return;
                            }
                             final AdBean.Material material =bean.adspaces.quit.get(0).materials.get(0);

                            String url = material.filePath;
                            LogUtils.i("mm", "url:" + url);
                            exit_image.setVisibility(View.VISIBLE);
                            Picasso.get().load(url).into(exit_image, new Callback() {
                                @Override
                                public void onSuccess() {
                                    if (material!=null&&item!=null) {
                                        AdSDK.getInstance().report((item.mid + ""), item.aid + "", material.id + "", "",
                                                null, material.playTime + "", null);
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
}
