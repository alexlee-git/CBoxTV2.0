package tv.newtv.cboxtv.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;

import com.newtv.libs.util.LogUtils;

import tv.newtv.cboxtv.R;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.views
 * 创建事件:         14:08
 * 创建人:           weihaichao
 * 创建日期:          2018/11/12
 */
public class GridRecycleView extends RecyclerView {

    private static final String TAG = "GridRecycleView";
    int spanCount = 1;
    private GridLayoutManager mLayoutManager;
    private KeyEvent mKeyEvent;
    private boolean mInvokeScroll;
    private int mTop;
    private int mBottom;

    public GridRecycleView(Context context) {
        this(context, null);
    }

    public GridRecycleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridRecycleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs, defStyle);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyle) {
        TypedArray mTypeArray = context.obtainStyledAttributes(attrs, R.styleable
                .GridRecycleView);
        if (mTypeArray != null) {
            spanCount = mTypeArray.getInt(R.styleable.GridRecycleView_spanCount, 1);
            mTop = mTypeArray.getDimensionPixelOffset(R.styleable
                    .GridRecycleView_decoration_top, 0);
            mBottom = mTypeArray.getDimensionPixelOffset(R.styleable
                    .GridRecycleView_decoration_bottom, 0);
            mTypeArray.recycle();
        }

        setPadding(getPaddingLeft(), getPaddingTop() + mTop * 2, getPaddingRight(), getPaddingBottom
                () + mBottom);

        mLayoutManager = new GridLayoutManager(context, spanCount);
        setLayoutManager(mLayoutManager);
        addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView
                    .State state) {
                outRect.top = 0;
                outRect.left = 0;
                outRect.right = 0;
                outRect.bottom = mBottom;
            }
        });
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);

        if (mInvokeScroll && mKeyEvent != null) {
            dispatchKeyEvent(mKeyEvent);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event == null) return true;
        View focusView = findFocus();
        if (focusView == null) {
            return super.dispatchKeyEvent(event);
        }
        View nextFocus = null;

        LogUtils.e(TAG, event.toString());
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    nextFocus = FocusFinder.getInstance().findNextFocus(this, focusView, View
                            .FOCUS_LEFT);
                    if (nextFocus == null) {
                        return true;
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    nextFocus = FocusFinder.getInstance().findNextFocus(this, focusView, View
                            .FOCUS_RIGHT);
                    if (nextFocus == null) {
                        return true;
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    nextFocus = FocusFinder.getInstance().findNextFocus(this, focusView, View
                            .FOCUS_UP);
                    LogUtils.e(TAG, "find focus dir=up view=" + nextFocus);
                    if (nextFocus == null) {
                        if (mInvokeScroll) return true;
                        if (canScrollVertically(1) && getScrollState() == SCROLL_STATE_IDLE) {
                            mInvokeScroll = true;
                            mKeyEvent = event;
                            //2018.12.26 wqs 此view有可能为空，需要判空
                            View firstView = mLayoutManager.findViewByPosition(mLayoutManager
                                    .findFirstVisibleItemPosition());
                            if (firstView == null) {
                                return true;
                            }
                            int height = firstView.getMeasuredHeight();
                            smoothScrollBy(0, -(height + mTop * 2 + mBottom * 2));
                            return true;
                        } else {
                            return true;
                        }
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    nextFocus = FocusFinder.getInstance().findNextFocus(this, focusView, View
                            .FOCUS_DOWN);
                    LogUtils.e(TAG, "find focus dir=down view=" + nextFocus);
                    if (nextFocus == null) {
                        if (canScrollVertically(-1) && getScrollState() == SCROLL_STATE_IDLE) {
                            if (mInvokeScroll) return true;
                            mInvokeScroll = true;
                            mKeyEvent = event;
                            //2018.12.26 wqs 此view有可能为空，需要判空
                            View firstView = mLayoutManager.findViewByPosition(mLayoutManager
                                    .findFirstVisibleItemPosition());
                            if (firstView == null) {
                                return true;
                            }
                            smoothScrollBy(0, firstView.getMeasuredHeight() + mTop +
                                    mBottom);
                            return true;
                        } else {
                            return true;
                        }
                    }
                }
                break;
        }

        if (nextFocus != null) {
            nextFocus.requestFocus();
            mInvokeScroll = false;
            mKeyEvent = null;
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
