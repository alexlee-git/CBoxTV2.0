package tv.newtv.cboxtv.views.detail;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.newtv.libs.Constant;

import tv.newtv.cboxtv.cms.details.presenter.adpresenter.ADPresenter;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.IAdConstract;


public class TopSmallWindowView extends BaseAdView implements IAdConstract.IADConstractView{

    public TopSmallWindowView(Context context) {
        this(context,null);
    }

    public TopSmallWindowView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        destroy();
    }

    public TopSmallWindowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void getAD(ADPresenter mADPresenter) {
        mADPresenter.getAD(Constant.AD_DESK,Constant.AD_DETAILPAGE_TOPPOS,"");
    }

}
