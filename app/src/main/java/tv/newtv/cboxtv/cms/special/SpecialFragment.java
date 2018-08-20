package tv.newtv.cboxtv.cms.special;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import tv.icntv.adsdk.AdSDK;
import tv.newtv.cboxtv.BuildConfig;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.ad.JsonParse;
import tv.newtv.cboxtv.cms.ad.model.AdInfo;
import tv.newtv.cboxtv.cms.ad.model.AdInfos;
import tv.newtv.cboxtv.cms.ad.model.MaterialInfo;
import tv.newtv.cboxtv.cms.details.view.ADSdkCallback;
import tv.newtv.cboxtv.cms.mainPage.model.ModuleInfoResult;
import tv.newtv.cboxtv.cms.special.fragment.BaseSpecialContentFragment;
import tv.newtv.cboxtv.cms.util.ADsdkUtils;
import tv.newtv.cboxtv.cms.util.LogUploadUtils;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;


/**
 * Created by lin on 2018/3/7.
 */

public class SpecialFragment extends Fragment implements SpecialContract.View, Target {
    private SpecialContract.Presenter mPresenter;

    private RelativeLayout mRootView;

    private TextView mEmptyView;
    private TextView mLoadingView;

    private BaseSpecialContentFragment currentFragment;

    private String mPageUUid;
    private String templateZT;//专题模板id

    ///////////////

    @Override
    public void onDestroy() {
        super.onDestroy();

        currentFragment = null;
        mLoadingView = null;
        mEmptyView = null;
        mRootView = null;

        mPresenter = null;



        StringBuilder dataBuff = new StringBuilder();
        dataBuff.append("1,")
                .append(mPageUUid)
                .append(",")
                .append(templateZT)//专题模板id
                .trimToSize();
        LogUploadUtils.uploadLog(Constant.LOG_NODE_SPECIAL, dataBuff.toString());//离开专题
    }

    public static SpecialFragment newInstance() {
        return new SpecialFragment();
    }

    public void setPageUUid(String pageUUid) {
        this.mPageUUid = pageUUid;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (currentFragment != null) {
            if (currentFragment.dispatchKeyEvent(event)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {

        if (mRootView == null) {
            mRootView = (RelativeLayout) inflater.inflate(R.layout.fragment_special, null);
            mEmptyView = (TextView) mRootView.findViewById(R.id.id_empty_view);
            mLoadingView = (TextView) mRootView.findViewById(R.id.loading);
        }
        return mRootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (currentFragment == null) {
            mLoadingView.setVisibility(View.VISIBLE);
            if (BuildConfig.DEBUG) {
                mPresenter.start(Constant.APP_KEY, Constant.CHANNEL_ID, mPageUUid);
            } else {
                mPresenter.start(Constant.APP_KEY, Constant.CHANNEL_ID, mPageUUid);
            }
        }
    }

    @Override
    public void setPresenter(SpecialContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public void showPageContent(ModuleInfoResult moduleInfoResult) {
        mLoadingView.setVisibility(View.GONE);

        if (moduleInfoResult == null) {
            mEmptyView.setVisibility(View.VISIBLE);
            return;
        } else {
            mEmptyView.setVisibility(View.GONE);
        }

        templateZT = moduleInfoResult.getTemplateZT();
        StringBuilder dataBuff = new StringBuilder();
        dataBuff.append("0,")
                .append(mPageUUid)//专题id
                .append(",")
                .append(templateZT)//专题模板id
                .trimToSize();
        LogUploadUtils.uploadLog(Constant.LOG_NODE_SPECIAL, dataBuff.toString());//进入专题日志上报

        initBackground(moduleInfoResult);
        currentFragment = SpecialLayoutManager.get().GenerateFragment(
                R.id.id_content_fragment_root, getArguments(), getChildFragmentManager(),
                moduleInfoResult
        );
    }

    private void initBackground(final ModuleInfoResult moduleInfoResult) {
        // 测试数据
        // moduleInfoResult.setIsAd(0);
        // moduleInfoResult.setPageBackground
        // ("http://111.32.138.56/img/cnlive/180228172554262_278.png");

        if (moduleInfoResult.getIsAd() == ModuleInfoResult.IS_AD_PAGE) {


            ADsdkUtils.getAD(Constant.AD_TOPIC, mPageUUid, -1, new ADSdkCallback() {
                @Override
                public void showAd(String type, String url) {
                    super.showAd(type, url);
                    if (TextUtils.isEmpty(url)){
                        showPosterByCMS(moduleInfoResult);
                    }else{
                        Picasso.with(getActivity()).load(url).into(SpecialFragment.this);
                    }
                }
            });

//            final StringBuffer sb = new StringBuffer();
//            Observable.create(new ObservableOnSubscribe<List<AdInfos>>() {
//                @Override
//                public void subscribe(ObservableEmitter<List<AdInfos>> e) throws Exception {
//                    AdSDK.getInstance().getAD(Constant.AD_TOPIC, null, null, mPageUUid, null,
//                            null, sb);
//                    e.onNext(JsonParse.parseAdInfo(sb.toString()));
//                }
//            }).subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Observer<List<AdInfos>>() {
//                        @Override
//                        public void onSubscribe(Disposable d) {
//
//                        }
//
//                        @Override
//                        public void onNext(List<AdInfos> value) {
//
//                            if (value == null || value.size() == 0 || value.get(0) == null ||
//                                    value.get(0).m_info == null || value.get(0).m_info.size() ==
//                                    0) {
//                                showPosterByCMS(moduleInfoResult);
//                                return;
//                            }
//                            AdInfo adInfo = value.get(0).m_info.get(0);
//
//                            if (adInfo == null || adInfo.m_material == null || adInfo.m_material
//                                    .size() == 0) {
//                                showPosterByCMS(moduleInfoResult);
//                                return;
//                            }
//                            MaterialInfo materialInfo = adInfo.m_material.get(0);
//
//                            if (materialInfo != null) {
//                                String url = materialInfo.m_filePath;
//                                Log.i("mm", "url:" + url);
//                                Picasso.with(getActivity()).load(url).into(SpecialFragment.this);
//                                AdSDK.getInstance().report(adInfo.m_mid + "", adInfo.m_aid + "",
//                                        materialInfo.m_id + "", null, null, null, null);
//                            } else {
//                                showPosterByCMS(moduleInfoResult);
//
//                            }
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            showPosterByCMS(moduleInfoResult);
//                        }
//
//                        @Override
//                        public void onComplete() {
//
//                        }
//                    });
            // TODO 加载广告背景图
        } else {
            showPosterByCMS(moduleInfoResult);
        }
    }

    //默认加载cms取得的图片
    private void showPosterByCMS(ModuleInfoResult moduleInfoResult) {

        if (!TextUtils.isEmpty(moduleInfoResult.getPageBackground())) {
            Picasso.with(getActivity()).load(moduleInfoResult.getPageBackground()).into(this);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }


    ///////////////target method
    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        if (isAdded()) {
            mRootView.setBackground(new BitmapDrawable(getResources(), bitmap));
        }
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        if (isAdded()) {
            mRootView.setBackgroundResource(R.drawable.home_page_bg);
        }
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }


}
