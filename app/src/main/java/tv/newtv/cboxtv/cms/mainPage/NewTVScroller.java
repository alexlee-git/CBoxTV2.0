package tv.newtv.cboxtv.cms.mainPage;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Created by lixin on 2018/3/17.
 */

public class NewTVScroller extends Scroller {

    private int mDuration;

    public NewTVScroller(Context context, Interpolator interpolator, int duration) {
        super(context, interpolator);
        mDuration = duration;
    }

    public NewTVScroller(Context context) {
        this(context, null, 0);
    }

    public void setScrollDuration(int duration) {
        mDuration = duration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        this.startScroll(startX, startY, dx, dy, mDuration);
    }
}
