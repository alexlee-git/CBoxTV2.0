package tv.newtv.cboxtv.views.detail;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.newtv.cms.contract.AdContract;
import com.newtv.libs.ad.ADHelper;
import com.newtv.libs.ad.AdEventContent;
import com.newtv.libs.bean.AdInfo;
import com.newtv.libs.util.GsonUtil;
import com.newtv.libs.util.ScaleUtils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.views.custom.RecycleImageView;

public abstract class BaseAdView extends RecycleImageView implements View.OnFocusChangeListener,
        View.OnClickListener, AdContract.View {

    protected AdContract.Presenter mADPresenter;
    private ADHelper.AD.ADItem mAdItem;

    public BaseAdView(Context context) {
        this(context, null);
    }

    public BaseAdView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseAdView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mADPresenter = new AdContract.AdPresenter(getContext(), this);
        getAD(mADPresenter);
    }

    protected abstract void getAD(AdContract.Presenter mADPresenter);

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            ScaleUtils.getInstance().onItemGetFocus(this);
        } else {
            ScaleUtils.getInstance().onItemLoseFocus(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (mAdItem != null && !TextUtils.isEmpty(mAdItem.eventContent)) {
            AdEventContent adEventContent = GsonUtil.fromjson(mAdItem.eventContent, AdEventContent
                    .class);
            JumpUtil.activityJump(getContext(), adEventContent.actionType, adEventContent
                            .contentType,
                    adEventContent.contentUUID, adEventContent.actionURI);
        }
    }

    @Override
    public void showAd(@org.jetbrains.annotations.Nullable String type, @org.jetbrains
            .annotations.Nullable String url, @org.jetbrains.annotations.Nullable HashMap<?, ?>
                               hashMap) {
        ADHelper.AD.ADItem result = mADPresenter.getAdItem();
        if (result != null && !TextUtils.isEmpty(result.AdUrl)) {
            hasCorner(true).load(result.AdUrl);
            setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(result.eventContent)) {
                mAdItem = result;
                setFocusable(true);
                setFocusableInTouchMode(true);
                setOnFocusChangeListener(this);
                setOnClickListener(this);
            }
        }

    }

    @Override
    public void updateTime(int total, int left) {

    }

    @Override
    public void complete() {

    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @org.jetbrains.annotations.Nullable String desc) {

    }
}
