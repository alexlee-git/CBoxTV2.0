package tv.newtv.cboxtv.views.detail;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.newtv.libs.util.ScreenUtils;

import tv.newtv.cboxtv.R;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         12:37
 * 创建人:           weihaichao
 * 创建日期:          2018/5/5
 */
public class SmoothScrollView extends RelativeLayout {

    private Scroller mScroller;
    private int topSpace = 0;
    private static final int SCROLL_DURATION = 450;

    public boolean isScrollMode(){
        return  mScroller.computeScrollOffset();
    }

    public SmoothScrollView(Context context) {
        this(context, null);
    }

    public SmoothScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmoothScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initalzie(context, attrs, defStyleAttr);
    }

    public void destroy() {
        mScroller = null;
        removeAllViews();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isInEditMode()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);

        int height = 0;
        int childCount = getChildCount();
        for (int index = 0; index < childCount; index++) {
            View child = getChildAt(index);
//            if(child.getVisibility() == View.GONE) continue;
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            height += child.getMeasuredHeight();
        }
        if (height < ScreenUtils.getScreenH()) {
            height = ScreenUtils.getScreenH();
        }

        setMeasuredDimension(sizeWidth, height);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        GainFocus(child);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (isInEditMode()) {
            super.onLayout(changed, l, t, r, b);
            return;
        }
        int childCount = getChildCount();
        for (int index = 0; index < childCount; index++) {
            View child = getChildAt(index);
            int width = child.getMeasuredWidth();
            int left = (getMeasuredWidth() - width) / 2;
            child.layout(left, t, left + child.getMeasuredWidth(), t + child
                    .getMeasuredHeight());
            t += child.getMeasuredHeight();
        }
    }

    private View getRootView(View focusView) {
        if (focusView == null) return null;
        if (focusView.getParent() instanceof SmoothScrollView) {
            return focusView;
        }
        return getRootView((View) focusView.getParent());
    }

    public boolean isComputeScroll() {
        if (mScroller != null) {
            return mScroller.computeScrollOffset();
        }
        return true;
    }


    private View findLastVisibleView() {
        int count = getChildCount();
        View lastView = null;
        for (int index = 0; index < count; index++) {
            View target = getChildAt(index);
            if (target.getVisibility() == View.VISIBLE) {
                lastView = target;
            }
        }
        return lastView;
    }

    void GainFocus(View focusView) {
        if (focusView == null) return;
        View parentView = getRootView(focusView);
        if (parentView == null) {
            return;
        }


        LayoutParams layoutParams = (LayoutParams) parentView.getLayoutParams();
        if (parentView.getTop() - layoutParams.topMargin == 0) {
            if (getScrollY() != 0) {
                smoothScrollTo(0, getScrollY() * -1, SCROLL_DURATION);
            }
            return;
        }

        Rect visible = new Rect();
        getGlobalVisibleRect(visible);

        int dis = getHeight() - getScrollY() - visible.height();
        int pos = parentView.getTop() - topSpace - getScrollY();

        if (pos < dis) {
            dis = pos;
        }

        smoothScrollTo(0, dis, SCROLL_DURATION);
    }

    private void initalzie(Context context, AttributeSet attrs, int defStyleAttr) {

        setClipChildren(false);
        setClipToPadding(false);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SmoothScrollView);
        if (typedArray != null) {
//            topSpace = typedArray.getDimensionPixelOffset(R.styleable.SmoothScrollView_topSpace,
//                    0);

            typedArray.recycle();
        }

        mScroller = new Scroller(getContext());
    }

    public void scrollToTop() {
        smoothScrollTo(0, getScrollY() * -1, SCROLL_DURATION);
    }

    public void smoothScrollTo(int dx, int dy, int duration) {
        int startX = getScrollX();
        int starty = getScrollY();
        mScroller.startScroll(startX, starty, dx, dy, duration);
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }
}
