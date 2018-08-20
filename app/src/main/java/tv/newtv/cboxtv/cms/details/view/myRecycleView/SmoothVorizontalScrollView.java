package tv.newtv.cboxtv.cms.details.view.myRecycleView;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

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
public class SmoothVorizontalScrollView extends ScrollView {
    private static String TAG = "SmoothVorizontalScrollView";
    private int downX;
    private int downY;
    private int mTouchSlop;
    private int mFadingEdge;

    public SmoothVorizontalScrollView(Context context) {
        super(context, null, 0);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public SmoothVorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public SmoothVorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public static boolean isVisibleLocal(View target) {
        Rect rect = new Rect();
        target.getLocalVisibleRect(rect);
        return rect.top == 0;
    }

    public void setFadingEdge(int fadingEdge) {
        this.mFadingEdge = fadingEdge;
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return super.onInterceptHoverEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            return super.dispatchKeyEvent(event);
        } else {
            View focusView = this.findFocus();
            if (focusView == null) {
                return super.dispatchKeyEvent(event);
            }


            int dy;
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                View downView = FocusFinder.getInstance().findNextFocus(this, focusView, View
                        .FOCUS_DOWN);
                if (downView != null) {
                    dy = (int) (downView.getHeight() * 2.5);
                    Log.i(TAG, "rightView is null:" + downView.toString());
                    if (!isVisibleLocal(downView)) {
                        smoothScrollBy(0, dy);
                    } else {
                        View nextView = FocusFinder.getInstance().findNextFocus(this, downView,
                                View.FOCUS_DOWN);
                        if (nextView == null) {
                            smoothScrollBy(0, dy);
                        }
                    }
                }
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                View upView = FocusFinder.getInstance().findNextFocus(this, focusView, View
                        .FOCUS_UP);
                Log.i(TAG, "rightView is null:" + (upView == null));
                if (upView != null) {
                    if (!isVisibleLocal(upView)) {
                        dy = (int) (upView.getHeight() * 2.5);
                        smoothScrollBy(0, -dy);
                    }
                } else {

                }
            }

        }

        return super.dispatchKeyEvent(event);
    }
    //    @Override
//    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
//        if (getChildCount() == 0)
//            return 0;
//        int height = getHeight();
//        int screenTop = getScrollY();
//        int screenBottom = screenTop + height;
//        int fadingEdge = mFadingEdge > 0 ? mFadingEdge : 0;
//        if (rect.top > 0) {
//            screenTop += fadingEdge;
//        }
//        if (rect.bottom < getChildAt(0).getHeight()) {
//            screenBottom -= fadingEdge;
//        }
//        //
//        int scrollYDelta = 0;
//        if (rect.bottom > screenBottom && rect.top > screenTop) {
//            if (rect.height() > height) {
//                scrollYDelta += (rect.top - screenTop);
//            } else {
//                scrollYDelta += (rect.bottom - screenBottom);
//            }
//            int bottom = getChildAt(0).getBottom();
//            int distanceToBottom = bottom - screenBottom;
//            scrollYDelta = Math.min(scrollYDelta, distanceToBottom);
//        } else if (rect.top < screenTop && rect.bottom < screenBottom) {
//            if (rect.height() > height) {
//                scrollYDelta -= (screenBottom - rect.bottom);
//            } else {
//                scrollYDelta -= (screenTop - rect.top);
//            }
//            scrollYDelta = Math.max(scrollYDelta, -getScrollY());
//        }
//        return scrollYDelta;
//    }
//

    /**
     * 滑动事件
     */
    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 40);
    }

    public void setScrollTop() {
        scrollTo(0, 0);
    }

}

