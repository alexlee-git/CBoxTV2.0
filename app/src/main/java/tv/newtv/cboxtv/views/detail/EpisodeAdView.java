package tv.newtv.cboxtv.views.detail;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.newtv.libs.Constant;
import com.newtv.libs.ad.ADHelper;
import com.newtv.libs.ad.AdEventContent;
import com.newtv.libs.util.GsonUtil;
import com.newtv.libs.util.ScaleUtils;
import com.squareup.picasso.Callback;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.ADPresenter;
import tv.newtv.cboxtv.views.custom.RecycleImageView;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.IAdConstract;
import tv.newtv.cboxtv.cms.util.JumpUtil;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         18:23
 * 创建人:           weihaichao
 * 创建日期:          2018/5/8
 */
public class EpisodeAdView extends RecycleImageView implements IEpisode, View.OnFocusChangeListener,
        View.OnClickListener, IAdConstract.IADConstractView {

    private ADPresenter mADPresenter;
    private int measuredWidth, measuredHeight;
    private boolean isSuccess = false;
    private ADHelper.AD.ADItem adItem;


    public EpisodeAdView(Context context) {
        this(context, null);
    }

    public EpisodeAdView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EpisodeAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnFocusChangeListener(this);
        setOnClickListener(this);
        setVisibility(GONE);

        measuredHeight = (int) getResources().getDimension(R.dimen.height_386px);
        measuredWidth = (int) getResources().getDimension(R.dimen.width_1746px);

    }

    @Override
    public void destroy() {
        if (mADPresenter != null) {
            mADPresenter.destroy();
            mADPresenter = null;
        }
        adItem = null;
    }

    @Override
    public String getContentUUID() {
        return null;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mADPresenter != null) {
            mADPresenter.destroy();
            mADPresenter = null;
        }
    }

    @Override
    public boolean interruptKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT || event.getKeyCode() == KeyEvent
                    .KEYCODE_DPAD_RIGHT) {
                return true;
            }

            if (isSuccess && !hasFocus()) {
                requestFocus();
                return true;
            }
        }
        return false;
    }

    public void requestAD() {
        if(mADPresenter == null) {
            mADPresenter = new ADPresenter(this);
        }
        mADPresenter.getAD(Constant.AD_DESK, Constant
                .AD_DETAILPAGE_BANNER, Constant
                .AD_DETAILPAGE_BANNER);//获取广告
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isSuccess) {
            int withMeasure = MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY);
            int heighMeasure = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY);
            super.onMeasure(withMeasure, heighMeasure);
            setVisibility(VISIBLE);
        } else {
            super.onMeasure(0, 0);
        }
    }

    private void remove() {
        if (getParent() != null) {
            ((ViewGroup) getParent()).removeView(this);
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (b) {
            ScaleUtils.getInstance().onItemGetFocus(this);
        } else {
            ScaleUtils.getInstance().onItemLoseFocus(this);
        }
    }

    @Override
    public void onClick(View v) {
        if(adItem != null && !TextUtils.isEmpty(adItem.eventContent)){
            AdEventContent adEventContent = GsonUtil.fromjson(adItem.eventContent, AdEventContent.class);
            JumpUtil.activityJump(getContext(), adEventContent.actionType, adEventContent.contentType,
                    adEventContent.contentUUID, adEventContent.actionURI,adEventContent.defaultUUID);
        }
    }

    @Override
    public void showAd(ADHelper.AD.ADItem result) {
        adItem = result;
        if (!TextUtils.isEmpty(result.AdUrl)) {
            getParent().requestLayout();
            setImageResource(R.drawable.focus_1680_320);
            useResize(false)
                    .placeHolder(R.drawable.focus_1680_320)
                    .errorHolder(R.drawable.focus_1680_320)
                    .hasCorner(true)
                    .withCallback(new Callback() {
                        @Override
                        public void onSuccess() {
                            isSuccess = true;
                            postInvalidate();
                        }

                        @Override
                        public void onError(Exception e) {
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    remove();
                                }
                            });
                        }
                    })
                    .load(result.AdUrl);
        } else {
            remove();
        }
    }
}
