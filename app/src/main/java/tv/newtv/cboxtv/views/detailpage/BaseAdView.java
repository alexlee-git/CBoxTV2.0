package tv.newtv.cboxtv.views.detailpage;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import tv.newtv.cboxtv.cms.details.presenter.adpresenter.ADPresenter;
import tv.newtv.cboxtv.utils.ScaleUtils;
import tv.newtv.cboxtv.views.RecycleImageView;

public abstract class BaseAdView extends RecycleImageView implements View.OnFocusChangeListener{

    protected ADPresenter mADPresenter;

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
        getAD();
    }

    protected abstract void getAD();

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            ScaleUtils.getInstance().onItemGetFocus(this);
        } else {
            ScaleUtils.getInstance().onItemLoseFocus(this);
        }
    }
}
