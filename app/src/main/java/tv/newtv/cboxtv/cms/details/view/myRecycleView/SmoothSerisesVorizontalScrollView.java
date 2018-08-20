package tv.newtv.cboxtv.cms.details.view.myRecycleView;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

/**
 * 当你使用滚动窗口焦点错乱的时候，就可以使用这个控件.
 * <p/>
 * 使用方法和滚动窗口是一样的，具体查看DEMO吧.
 * <p/>
 * 如果想改变滚动的系数，R.dimen.fading_edge
 * <p/>
 *
 * @author hailongqiu
 */
public class SmoothSerisesVorizontalScrollView extends RelativeLayout {
    private static String TAG = "SmoothVorizontalScrollView";

    private Scroller mScroller;

    public SmoothSerisesVorizontalScrollView(Context context) {
        this(context, null);
    }

    public SmoothSerisesVorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmoothSerisesVorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScroller = new Scroller(context);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            invalidate();
        }
    }

    public void smoothScrollTo(int dx, int dy, int duration) {
        int startX = getScrollX();
        int starty = getScrollY();
        mScroller.startScroll(startX, starty, dx, dy, duration);
        invalidate();
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return super.onInterceptHoverEvent(event);
    }

    public void setScrollTop() {
        scrollBy(0,-getScrollY());
    }

}

