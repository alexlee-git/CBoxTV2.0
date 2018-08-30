package tv.newtv.cboxtv.views.detailpage;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.ADPresenter;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.IAdConstract;
import tv.newtv.cboxtv.utils.ADHelper;

public class SmallWindowView extends BaseAdView implements IAdConstract.IADConstractView{
    private static final String TAG = "SmallWindowView";

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
        }else {
            setImageResource(R.drawable.about_logo);
//            setFocusable(true);
//            setFocusableInTouchMode(true);
//            setOnFocusChangeListener(this);
        }
    }

}
