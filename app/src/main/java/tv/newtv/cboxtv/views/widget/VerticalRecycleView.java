package tv.newtv.cboxtv.views.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.newtv.libs.IDefaultFocus;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.views.widget
 * 创建事件:         15:13
 * 创建人:           weihaichao
 * 创建日期:          2018/11/8
 */
public class VerticalRecycleView extends RecyclerView implements IDefaultFocus {

    public VerticalRecycleView(Context context) {
        this(context, null);
    }

    public VerticalRecycleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalRecycleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        setClipChildren(false);
        setClipToPadding(false);
        setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
    }

    private View findNextFocus(View focusView, KeyEvent event) {
        View nextFocus = null;
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
                nextFocus = FocusFinder.getInstance().findNextFocus(this, focusView, View
                        .FOCUS_UP);
                if (nextFocus == null && getScrollState() == SCROLL_STATE_IDLE) {
                    smoothScrollBy(0, -getChildAt(0).getMeasuredHeight());
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                nextFocus = FocusFinder.getInstance().findNextFocus(this, focusView, View
                        .FOCUS_DOWN);
                if (nextFocus == null && getScrollState() == SCROLL_STATE_IDLE) {
                    smoothScrollBy(0, getChildAt(0).getMeasuredHeight());
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
                .KEYCODE_DPAD_UP && event
                .getKeyCode() != KeyEvent.KEYCODE_DPAD_DOWN) {
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
            nextFocus.requestFocus();
        }
        return true;
    }

    @Override
    public View getDefaultFocusView() {
        return null;
    }
}