package tv.newtv.cboxtv.views.detail;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.newtv.libs.Constant;

import tv.newtv.cboxtv.cms.details.presenter.adpresenter.ADPresenter;

public class SmallWindowView extends BaseAdView implements IEpisode {
    private static final String TAG = "SmallWindowView";

    public SmallWindowView(Context context) {
        this(context,null);
    }

    public SmallWindowView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SmallWindowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setVisibility(View.INVISIBLE);
    }

    @Override
    protected void getAD(ADPresenter mADPresenter) {
//        mADPresenter.getAD(Constant.AD_DESK,Constant.AD_DETAILPAGE_RIGHTPOS,"");
    }

    @Override
    public String getContentUUID() {
        return null;
    }

    @Override
    public boolean interruptKeyEvent(KeyEvent event) {
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
        if(mADPresenter != null) {
            mADPresenter.destroy();
            mADPresenter = null;
        }
    }

}
