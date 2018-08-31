package tv.newtv.cboxtv.views.detailpage;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.cms.ad.model.AdEventContent;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.ADPresenter;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.IAdConstract;
import tv.newtv.cboxtv.cms.util.GsonUtil;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.utils.ADHelper;

public class SmallWindowView extends BaseAdView implements IAdConstract.IADConstractView,IEpisode, View.OnClickListener {
    private static final String TAG = "SmallWindowView";
    private ADHelper.AD.ADItem result;

    public SmallWindowView(Context context) {
        this(context,null);
    }

    public SmallWindowView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SmallWindowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void getAD() {
        mADPresenter = new ADPresenter(this);
        mADPresenter.getAD(Constant.AD_DESK,Constant.AD_DETAILPAGE_RIGHTPOS,"");
    }

    @Override
    public void showAd(ADHelper.AD.ADItem result) {
        Log.i(TAG, "showAd: "+result);
        if(!TextUtils.isEmpty(result.AdUrl)){
            hasCorner(true).load(result.AdUrl);
            if(!TextUtils.isEmpty(result.eventContent)){
                this.result = result;
                setFocusable(true);
                setFocusableInTouchMode(true);
                setOnFocusChangeListener(this);
                setOnClickListener(this);
            }
        }
    }


    @Override
    public String getContentUUID() {
        return null;
    }

    @Override
    public boolean interuptKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT || event.getKeyCode() == KeyEvent
                    .KEYCODE_DPAD_RIGHT) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
        mADPresenter.destroy();
    }

    @Override
    public void onClick(View v) {
        AdEventContent adEventContent = GsonUtil.fromjson(result.eventContent, AdEventContent.class);
        JumpUtil.activityJump(getContext(), adEventContent.actionType, adEventContent.contentType,
                adEventContent.contentUUID, adEventContent.actionURI);
    }
}
