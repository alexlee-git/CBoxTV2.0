package tv.newtv.cboxtv.views.detail;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.newtv.libs.ad.ADHelper;
import com.newtv.libs.ad.AdEventContent;
import com.newtv.libs.util.GsonUtil;
import com.newtv.libs.util.ScaleUtils;

import tv.newtv.cboxtv.cms.details.presenter.adpresenter.ADPresenter;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.IAdConstract;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.views.custom.RecycleImageView;

public abstract class BaseAdView extends RecycleImageView implements View.OnFocusChangeListener,View.OnClickListener, IAdConstract.IADConstractView {

    protected ADPresenter mADPresenter;
    protected ADHelper.AD.ADItem result;

    public BaseAdView(Context context) {
        this(context,null);
    }

    public BaseAdView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BaseAdView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mADPresenter = new ADPresenter(this);
        getAD(mADPresenter);
    }

    protected abstract void getAD(ADPresenter mADPresenter);

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
        if(result != null && !TextUtils.isEmpty(result.eventContent)){
            AdEventContent adEventContent = GsonUtil.fromjson(result.eventContent, AdEventContent.class);
            JumpUtil.activityJump(getContext(), adEventContent.actionType, adEventContent.contentType,
                    adEventContent.contentUUID, adEventContent.actionURI);
        }
    }

    public void showAd(ADHelper.AD.ADItem result) {
        if(!TextUtils.isEmpty(result.AdUrl)){
            hasCorner(true).load(result.AdUrl);
            setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(result.eventContent)){
                this.result = result;
                setFocusable(true);
                setFocusableInTouchMode(true);
                setOnFocusChangeListener(this);
                setOnClickListener(this);
            }
        }
    }
}
