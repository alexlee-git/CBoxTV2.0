package tv.newtv.cboxtv.cms.mainPage;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;

import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.viewholder.UniversalAdapter;
import tv.newtv.cboxtv.cms.util.ModuleLayoutManager;
import tv.newtv.cboxtv.views.RecycleSpaceDecoration;

/**
 * Created by lixin on 2018/2/8.
 */

public class AiyaRecyclerView extends RecyclerView {

    public static final int ALIGN_START = 0;    //首对齐
    public static final int ALIGN_CENTER = 1;   //居中对齐
    public static final int ALIGN_END = 3;      //尾对齐
    public static final int ALIGN_AUTO = 4;      //尾对齐
    public static final int ALIGN_AUTO_TWO = 5;
    private static final String TAG = AiyaRecyclerView.class.getSimpleName();
    private DispatchKeyHandle mDispatchKeyHandle;
    private int mAlign = ALIGN_START;

    private boolean isLinear = false;
    private boolean isHorizontal = false;


    private View currentFocus;

    private View mStartIndicator;
    private View mEndIndicator;

    private boolean isScroll = false;
    private boolean AutoScroll = true;

    private int currentDir = View.FOCUS_RIGHT;

    private boolean canReverseMove = true;

    public AiyaRecyclerView(Context context) {
        super(context);
        setFocusableInTouchMode(true);
        setFocusable(false);
    }

    public AiyaRecyclerView(Context context, Boolean autoScroll) {
        super(context);
        AutoScroll = autoScroll;
        setFocusableInTouchMode(true);
        setFocusable(false);
    }

    public AiyaRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setFocusableInTouchMode(true);
        setFocusable(false);
    }

    public void setCanReversMove(boolean value) {
        canReverseMove = value;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof UniversalAdapter) {
            int bottomMargin = getContext().getResources().getDimensionPixelSize(R.dimen
                    .height_50px);
            ((UniversalAdapter) adapter).setLastItemBottomMargin(bottomMargin);
        }
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
    }

    /**
     * 自定义处理按键事件
     *
     * @param handle
     */
    public void setDispatchKeyHandle(DispatchKeyHandle handle) {
        this.mDispatchKeyHandle = handle;
    }

    /**
     * 设置首尾箭头指示
     *
     * @param startView
     * @param endView
     */
    public void setDirIndicator(View startView, View endView) {
        mStartIndicator = startView;
        mEndIndicator = endView;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (layout instanceof LinearLayoutManager) {
            isLinear = true;
            isHorizontal = (((LinearLayoutManager) layout).getOrientation() == LinearLayoutManager
                    .HORIZONTAL);
        }
    }

    /**
     * 设置列表选中项对齐方式
     *
     * @param align
     */
    public void setAlign(int align) {
        mAlign = align;
    }

    public void setSpace(int vertical, int horizontal) {
        this.addItemDecoration(new RecycleSpaceDecoration(vertical, horizontal));
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            isScroll = false;
        } else {
            isScroll = true;
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
    }

    @Override
    public void onScrolled(int dx, int dy) {
        if (dx == 0 && dy == 0) return;
        super.onScrolled(dx, dy);
        if (isLinear) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
            int first = layoutManager.findFirstVisibleItemPosition();
            int last = layoutManager.findLastVisibleItemPosition();
            if (first == 0 && isVisible(getChildAt(0))) {
                if (mStartIndicator != null) mStartIndicator.setVisibility(View.VISIBLE);
            } else if (last == getAdapter().getItemCount() - 1) {
                if (mEndIndicator != null) mEndIndicator.setVisibility(View.INVISIBLE);
            } else {
                if (mStartIndicator != null) mStartIndicator.setVisibility(View.VISIBLE);
                if (mEndIndicator != null) mEndIndicator.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean isVisible(View view) {
        Rect rect = new Rect();
        view.getLocalVisibleRect(rect);
        return rect.top == 0;
    }

    public View getDefaultFocusView() {
        if (currentFocus == null) {
            return getChildAt(0);
        }
        return currentFocus;
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        currentFocus = child;
        if (AutoScroll) {
            setFocusView(child);
        }
    }

    public View getRootView(View view) {
        if (view.getParent() == null) return null;
        if (view.getParent() instanceof AiyaRecyclerView) {
            return view;
        }
        return getRootView((View) view.getParent());
    }

    public boolean isSlideToRight() {
        return computeHorizontalScrollExtent() + computeHorizontalScrollOffset()
                >= computeHorizontalScrollRange();
    }

    public void setFocusView(View view) {
        if(view.getHeight() == 0 || view.getWidth() == 0) return;
        View rootView = getRootView(view);
        if (rootView == null) return;
        if (isLinear) {
            if (isHorizontal) {
                if (currentDir == FOCUS_LEFT) {
                    if (computeHorizontalScrollOffset() == 0) {
                        return;
                    }
                }
                if (currentDir == FOCUS_RIGHT) {

                }
                if (mAlign == ALIGN_CENTER) {
                    int xSpace = rootView.getLeft() - getScrollX() - (getWidth() - rootView
                            .getWidth()) / 2;
                    smoothScrollBy(xSpace, 0);
                } else if (mAlign == ALIGN_START) {
                    smoothScrollBy(rootView.getLeft() - getScrollX(), 0);
                } else if (mAlign == ALIGN_END) {
                    smoothScrollBy(rootView.getRight() - (getScrollX() + getWidth()), 0);
                }else if (mAlign==ALIGN_AUTO_TWO){
                    if (currentDir == View.FOCUS_RIGHT) {
                        if (rootView.getLeft()-getScrollX()>rootView.getWidth()){
                            smoothScrollBy(rootView.getLeft() - getScrollX() - rootView.getWidth(), 0);
                        }
                    } else if (currentDir == View.FOCUS_LEFT) {
                        if (rootView.getLeft()-getScrollX()<=0){
                            smoothScrollBy(rootView.getWidth()*-1,
                                    0);
                        }

                    }
                } else {
                    if (currentDir == View.FOCUS_RIGHT) {
                        smoothScrollBy(rootView.getLeft() - getScrollX() - rootView.getWidth(), 0);
                    } else if (currentDir == View.FOCUS_LEFT) {
                        smoothScrollBy(rootView.getLeft() - getScrollX
                                        () - (getWidth() - rootView.getWidth() * 2),
                                0);
                    }
                }
            } else {
                if (mAlign == ALIGN_CENTER) {
                    int space = rootView.getTop() - (getHeight() - rootView.getHeight()) / 2 ;
                    Log.d(TAG,String.format("move height=%d parentHeight=%d top=%d space=%d",
                            rootView.getHeight(),getHeight(),rootView.getTop(),space));
                    smoothScrollBy(0, space);
                } else if (mAlign == ALIGN_START) {
                    smoothScrollBy(0, rootView.getTop() - getScrollY());
                } else if (mAlign == ALIGN_END) {

                } else {
                    int locationY = rootView.getTop() - getScrollY();
                    if (currentDir == View.FOCUS_UP) {
                        smoothScrollBy(0,
                                locationY + rootView.getMeasuredHeight()
                        );
                    } else if (currentDir == View.FOCUS_DOWN) {
                        smoothScrollBy(
                                0,
                                locationY + getMeasuredHeight() - rootView.getMeasuredHeight() * 2
                        );
                    }
                }
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (isScroll) return true;

        View focusView = this.findFocus();

        if (mDispatchKeyHandle != null) {
            if (mDispatchKeyHandle.HandleDispatchKeyEvent(event, focusView, super.dispatchKeyEvent
                    (event))) {
                return true;
            }
        }

        boolean result = super.dispatchKeyEvent(event);

        if (focusView == null) {
            return result;
        } else {
            int dy = 0;
            int dx = 0;
            if (getChildCount() > 0) {
                View firstView = this.getChildAt(0);
                dy = firstView.getHeight();
                dx = firstView.getWidth();
            }
            if (event.getAction() == KeyEvent.ACTION_UP) {
                // 放行back键
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    return super.dispatchKeyEvent(event);
                }
                return true;
            } else {
                String cellCode = (String) focusView.getTag();
                View root = getRootView(focusView);
                Log.e(Constant.TAG, "view tag : " + cellCode + ", height : " + focusView
                        .getHeight());
                int index = getLayoutManager().getPosition(root);
                RecycleSpaceDecoration recycleSpaceDecoration = (RecycleSpaceDecoration)
                        getItemDecorationAt(index);
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        if (!isHorizontal && canReverseMove) return super.dispatchKeyEvent(event);
                        if (ModuleLayoutManager.getInstance().isNeedInterceptRightKeyEvent
                                (cellCode)) {
                            return true;
                        }
                        currentDir = View.FOCUS_RIGHT;
                        View rightView = FocusFinder.getInstance().findNextFocus(this, focusView,
                                View.FOCUS_RIGHT);
                        Log.i(TAG, "rightView is null:" + (rightView == null));
                        if (rightView != null) {
                            rightView.requestFocus();
                            return true;
                        } else {
                            this.smoothScrollBy(focusView.getWidth() +
                                    (recycleSpaceDecoration != null ? recycleSpaceDecoration
                                            .getLRSpace() : 0), 0);
                            return true;
                        }

                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        if (!isHorizontal && canReverseMove) return super.dispatchKeyEvent(event);
                        View leftView = FocusFinder.getInstance().findNextFocus(this, focusView,
                                View.FOCUS_LEFT);
                        currentDir = View.FOCUS_LEFT;
                        Log.i(TAG, "leftView is null:" + (leftView == null));
                        if (leftView != null) {
                            leftView.requestFocus();
                            return true;
                        } else {
                            this.smoothScrollBy(-(focusView.getWidth() +
                                    (recycleSpaceDecoration != null ? recycleSpaceDecoration
                                            .getLRSpace() : 0)), 0);
                            return true;
                        }
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        if (isHorizontal && canReverseMove) return super.dispatchKeyEvent(event);
                        View downView = FocusFinder.getInstance().findNextFocus(this, focusView,
                                View.FOCUS_DOWN);
                        currentDir = View.FOCUS_DOWN;
                        Log.i(TAG, " downView is null:" + (downView == null));
                        if (downView != null) {
                            downView.requestFocus();
                            return true;
                        } else {
                            this.smoothScrollBy(0, focusView.getHeight() +
                                    (recycleSpaceDecoration != null ? recycleSpaceDecoration
                                            .getTBSpace() : 0));
                            return true;
                        }
                    case KeyEvent.KEYCODE_DPAD_UP:
                        if (isHorizontal && canReverseMove) return super.dispatchKeyEvent(event);
                        View upView = FocusFinder.getInstance().findNextFocus(this, focusView,
                                View.FOCUS_UP);
                        currentDir = View.FOCUS_UP;
                        Log.i(TAG, "upView is null:" + (upView == null));
                        if (event.getAction() == KeyEvent.ACTION_UP) {
                            View view = getChildAt(0);
                            if (view != null && view.isFocused()) {
                                Log.i(TAG, "upView is null : " + "action up");
                            }
                            return super.dispatchKeyEvent(event);
                        } else {
                            if (upView != null) {
                                upView.requestFocus();
                                return true;
                            } else {
                                int height = focusView.getHeight();
                                this.smoothScrollBy(0, -(focusView.getHeight() +
                                        (recycleSpaceDecoration != null ? recycleSpaceDecoration
                                                .getTBSpace() : 0)));

                                View view = getChildAt(0);
                                if (view == null) {
                                    Log.e(TAG, "view is null");
                                } else {
                                    Log.e(TAG, "view is not null");
                                    return false;
                                }
                                return false;
                            }

                        }
                    case KeyEvent.KEYCODE_BACK:
                        return super.dispatchKeyEvent(event);
                }

            }

        }
        return result;
    }

    public interface DispatchKeyHandle {
        boolean HandleDispatchKeyEvent(KeyEvent event, View focusView, boolean defaultResult);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
    }
}
