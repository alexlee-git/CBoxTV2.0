package tv.newtv.cboxtv.cms.details.view.myRecycleView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
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
public class NewSmoothVorizontalScrollView extends ScrollView {


    private static String TAG = "SmoothVorizontalScrollView";
    private Scroller mScroller;

    public NewSmoothVorizontalScrollView(Context context) {
        this(context, null );


    }



    public NewSmoothVorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public NewSmoothVorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScroller = new Scroller(context);
    }



    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return super.onInterceptHoverEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            View focusView = this.findFocus();
            if (focusView == null) {
                return super.dispatchKeyEvent(event);
            }

            int dy;
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                View downView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_DOWN);
                if (downView != null) {
                    dy = (int) ((getHeight()-downView.getHeight())/2);
                        smoothScrollBySlow(0, dy,700);
                        return true;
                }else {
                    int height = getHeight();
                    smoothScrollBySlow(0,height ,700);
                    return true;
                }
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                View upView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_UP);
                Log.i(TAG, "rightView is null:" + (upView == null));
                if (upView != null) {
                        dy = (int) ((getHeight()-upView.getHeight())/2);
                        smoothScrollBySlow(0, -dy,700);
                        return true;
                } else {
                    smoothScrollBySlow(0, -300,700);
                    return true;
                }
            }

        }

        return super.dispatchKeyEvent(event);
    }

    //调用此方法设置滚动的相对偏移
    public void smoothScrollBySlow(int dx, int dy,int duration) {
        Log.d(TAG, "smoothScrollBySlow(int dx, int dy,int duration)");

        //设置mScroller的滚动偏移量
        mScroller.startScroll(getScrollX(), getScrollY(), dx, dy,duration);//scrollView使用的方法（因为可以触摸拖动）
//        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, duration);  //普通view使用的方法
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    @Override
    public void computeScroll() {
        Log.d(TAG, "s-computeScroll()");
        //先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {

            //这里调用View的scrollTo()完成实际的滚动
            smoothScrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            Log.d(TAG, "s-computeScroll()-滚动未完成- mScroller.getCurrY()="+ mScroller.getCurrY());
            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
        super.computeScroll();
    }
    /**
     * 滑动事件
     */
    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 100);
    }

    public void setScrollTop(){
        scrollTo(0,0);
    }

}

