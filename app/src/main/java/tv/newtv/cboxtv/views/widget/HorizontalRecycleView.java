package tv.newtv.cboxtv.views.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 项目名称:         DanceTv_Android
 * 包名:            com.android.view
 * 创建事件:         14:33
 * 创建人:           weihaichao
 * 创建日期:          2018/7/12
 */
public class HorizontalRecycleView extends RecyclerView {

    private boolean isHorizontal;
    private boolean hasMutipleFocus = false;
    private int mShowCount = 4;
    private View mDirStartView, mDirEndView;

    public HorizontalRecycleView(Context context) {
        this(context, null);
    }

    public HorizontalRecycleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs);
    }

    @SuppressWarnings("PointlessNullCheck")
    public int getFirstVisiblePosition() {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager != null && layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        }
        return 0;
    }

    @SuppressWarnings("PointlessNullCheck")
    public int getLastVisiblePosition() {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager != null && layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        }
        return 0;
    }

    public void setShowCounts(int count) {
        mShowCount = count;
    }

    @SuppressWarnings("PointlessNullCheck")
    public boolean requestDefaultFocus(int index) {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager != null && layoutManager instanceof LinearLayoutManager) {
            View target = layoutManager.findViewByPosition(index);
            if (target != null) {
                target.requestFocus();
            } else {
                target = layoutManager.findViewByPosition(((LinearLayoutManager) layoutManager)
                        .findFirstCompletelyVisibleItemPosition());
                if(target != null){
                    target.requestFocus();
                }else{
                    requestFocus();
                }
            }
            return true;
        }
        return false;
    }

    @SuppressWarnings({"unused", "SpellCheckingInspection"})
    public void setItemHasMutipleFocus() {
        hasMutipleFocus = true;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);

        checkIndirection();
    }

    public void setDirectors(View startView, View endView) {
        mDirStartView = startView;
        mDirEndView = endView;
    }

    private boolean canScroll(int direction) {
        if (isHorizontal) {
            return canScrollHorizontally(direction);
        }
        return canScrollVertically(direction);
    }

    private void checkIndirection() {
        if (getAdapter() != null && getAdapter().getItemCount() < mShowCount) {
            mDirEndView.setVisibility(View.INVISIBLE);
            mDirStartView.setVisibility(View.INVISIBLE);
            return;
        }
        if (mDirEndView != null) {
            if (canScroll(1)) {
                //可以向下或者向右滚动
                mDirEndView.setVisibility(View.VISIBLE);
            } else {
                mDirEndView.setVisibility(View.INVISIBLE);
            }
        }

        if (mDirStartView != null) {
            if (canScroll(-1)) {
                //可以向上或者向左滚动
                mDirStartView.setVisibility(View.VISIBLE);
            } else {
                mDirStartView.setVisibility(View.INVISIBLE);
            }
        }

        if (!canScroll(1) && !canScroll(-1)) {
            //上下或者左右都不能滚动了
            if (mDirEndView != null) {
                mDirEndView.setVisibility(View.INVISIBLE);
            }
            if (mDirStartView != null) {
                mDirStartView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);


        checkIndirection();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        checkIndirection();
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (layout instanceof LinearLayoutManager) {
            isHorizontal = ((LinearLayoutManager) layout).getOrientation() ==
                    LinearLayoutManager.HORIZONTAL;
        }
    }

    @SuppressWarnings("unused")
    private void initialize(Context context, AttributeSet attrs) {

    }

    private View findNextFocus(View focusView, KeyEvent event) {
        View nextFocus = null;
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                nextFocus = FocusFinder.getInstance().findNextFocus(this, focusView, View
                        .FOCUS_LEFT);
                if (nextFocus == null && getScrollState() == SCROLL_STATE_IDLE) {
                    smoothScrollBy(-getChildAt(0).getMeasuredWidth(), 0);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                nextFocus = FocusFinder.getInstance().findNextFocus(this, focusView, View
                        .FOCUS_RIGHT);
                if (nextFocus == null && getScrollState() == SCROLL_STATE_IDLE) {
                    smoothScrollBy(getChildAt(0).getMeasuredWidth(), 0);
                }
                break;
            default:
                break;
        }
        return nextFocus;
    }

    private View findNextParentFocus(View nextFocus, int target) {
        if (nextFocus.getParent() != null) {
            ViewGroup parent = (ViewGroup) nextFocus.getParent();
            if (parent == this) {
                return null;
            }
            View targetView = parent.findViewById(target);
            if (targetView != null) {
                return targetView;
            }
            return findNextParentFocus(parent, target);
        }
        return null;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() != KeyEvent
                .KEYCODE_DPAD_LEFT && event
                .getKeyCode() != KeyEvent.KEYCODE_DPAD_RIGHT) {
            return super.dispatchKeyEvent(event);
        }
        if (event.getAction() == KeyEvent.ACTION_UP) {
            return true;
        }
        View focusView = findFocus();
        if (focusView == null || getChildCount() == 0) {
            return false;
        }

        View nextFocus = null;
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            nextFocus = findNextFocus(focusView, event);
        }
        if (nextFocus != null) {
            if (!hasMutipleFocus) {
                nextFocus.requestFocus();
            } else {
                if (nextFocus.getId() == focusView.getId()) {
                    nextFocus.requestFocus();
                } else {
                    nextFocus = findNextParentFocus(nextFocus, focusView.getId());
                    if (nextFocus != null) {
                        nextFocus.requestFocus();
                    }
                }
            }
        }
        return true;
    }
}
