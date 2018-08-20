package tv.newtv.cboxtv.views.detailpage;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

import tv.newtv.cboxtv.cms.util.LogUtils;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         17:01
 * 创建人:           weihaichao
 * 创建日期:          2018/5/3
 */
public class PagerScroller extends Scroller {
    private int mDuration;

    public PagerScroller(Context context) {
        super(context);
    }

    public void attachToViewPager(ViewPager viewPager, int speed) {
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            PagerScroller viewPagerScroller = new PagerScroller(viewPager.getContext(), new
                    OvershootInterpolator(0.6F));
            field.set(viewPager, viewPagerScroller);
            viewPagerScroller.setDuration(speed);
        } catch (NoSuchFieldException e) {
            LogUtils.e(e.toString());
        } catch (IllegalAccessException e) {
            LogUtils.e(e.toString());
        }
    }

    public PagerScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public void setDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, this.mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, this.mDuration);
    }
}
