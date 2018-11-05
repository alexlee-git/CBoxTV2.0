package tv.newtv.cboxtv.cms.special;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newtv.cms.BuildConfig;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.Page;
import com.newtv.cms.contract.AdContract;
import com.newtv.libs.Constant;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.ToastUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.special.fragment.BaseSpecialContentFragment;


/**
 * Created by lin on 2018/3/7.
 */

public class SpecialFragment extends Fragment implements SpecialContract.ModelResultView, Target {
    private static final String TAG = SpecialFragment.class.getSimpleName();

    private SpecialContract.Presenter mPresenter;
    private AdContract.Presenter mAdPresenter;

    private RelativeLayout mRootView;

    private TextView mEmptyView;
    private TextView mLoadingView;

    private BaseSpecialContentFragment currentFragment;

    private String mPageUUid;
    private String templateZT;//专题模板id

    ///////////////

    public static SpecialFragment newInstance() {
        return new SpecialFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        currentFragment = null;
        mLoadingView = null;
        mEmptyView = null;
        mRootView = null;

        if (mAdPresenter != null) {
            mAdPresenter.destroy();
            mAdPresenter = null;
        }

        if(mPresenter != null){
            mPresenter.destroy();
            mPresenter = null;
        }


        StringBuilder dataBuff = new StringBuilder();
        dataBuff.append("1,")
                .append(mPageUUid)
                .append(",")
                .append(templateZT)//专题模板id
                .trimToSize();
        LogUploadUtils.uploadLog(Constant.LOG_NODE_SPECIAL, dataBuff.toString());//离开专题
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
        mPresenter = new SpecialContract.SpecialPresenter(LauncherApplication.AppContext,this);
        mAdPresenter = new AdContract.AdPresenter(LauncherApplication.AppContext,null);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (currentFragment == null) {
            mLoadingView.setVisibility(View.VISIBLE);
            if (BuildConfig.DEBUG) {
                mPresenter.start(BuildConfig.APP_KEY, BuildConfig.CHANNEL_ID, mPageUUid);
            } else {
                mPresenter.start(BuildConfig.APP_KEY, BuildConfig.CHANNEL_ID, mPageUUid);
            }
        }
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public void showPageContent(ModelResult<ArrayList<Page>> moduleInfoResult) {
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

    private void initBackground(final ModelResult moduleInfoResult) {
        // 测试数据
        if (ModelResult.IS_AD_TYPE.equals(moduleInfoResult.isAd())) {

            mAdPresenter.getAdByType(Constant.AD_TOPIC, mPageUUid, "", null, new AdContract
                    .Callback() {
                @Override
                public void showAd(@org.jetbrains.annotations.Nullable String type, @org
                        .jetbrains.annotations.Nullable String url, @org.jetbrains.annotations
                        .Nullable HashMap<?, ?> hashMap) {
                    if (TextUtils.isEmpty(url)) {
                        showPosterByCMS(moduleInfoResult);
                    } else {
                        Picasso.get().load(url).into(SpecialFragment.this);
                    }
                }
            });
            // TODO 加载广告背景图
        } else {
            showPosterByCMS(moduleInfoResult);
        }
    }

    //默认加载cms取得的图片
    private void showPosterByCMS(ModelResult moduleInfoResult) {

        if (!TextUtils.isEmpty(moduleInfoResult.getBackground())) {
            Picasso.get().load(moduleInfoResult.getBackground()).into(this);
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
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        LogUtils.e(TAG, e.toString());
        if (isAdded()) {
            mRootView.setBackgroundResource(R.drawable.home_page_bg);
        }
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }


    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @org.jetbrains.annotations.Nullable String desc) {
        ToastUtil.showToast(context.getApplicationContext(),desc);
        getActivity().finish();
    }
}
