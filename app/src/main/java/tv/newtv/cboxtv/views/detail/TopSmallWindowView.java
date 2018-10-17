package tv.newtv.cboxtv.views.detail;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.newtv.cms.contract.AdContract;
import com.newtv.libs.Constant;

import tv.newtv.cboxtv.player.PlayerConfig;


public class TopSmallWindowView extends BaseAdView{

    public TopSmallWindowView(Context context) {
        this(context,null);
    }

    public TopSmallWindowView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TopSmallWindowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void getAD(AdContract.Presenter mADPresenter) {
        mADPresenter.getAdByChannel(Constant.AD_DESK,Constant.AD_DETAILPAGE_TOPPOS,"",PlayerConfig
                .getInstance().getFirstChannelId(),PlayerConfig.getInstance().getSecondChannelId
                (),PlayerConfig.getInstance().getTopicId(),null);
    }

}
