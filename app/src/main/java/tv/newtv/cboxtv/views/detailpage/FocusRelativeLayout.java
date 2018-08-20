package tv.newtv.cboxtv.views.detailpage;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.utils.ScaleUtils;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         15:14
 * 创建人:           weihaichao
 * 创建日期:          2018/7/27
 */
public class FocusRelativeLayout extends RelativeLayout {

    private View mResizeView;

    public FocusRelativeLayout(Context context) {
        this(context, null);
    }

    public FocusRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public void setResizeView(View view) {
        mResizeView = view;
    }

    private void initialize() {
        setClipChildren(false);
        setClipToPadding(false);
        if (isInEditMode()) return;
        setBackgroundResource(R.drawable.focus_background_selector);
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (params != null && params instanceof MarginLayoutParams) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                ((MarginLayoutParams) params).setMarginStart(-getPaddingStart());
                ((MarginLayoutParams) params).setMarginEnd(-getPaddingEnd());
            }
            ((MarginLayoutParams) params).leftMargin = -getPaddingLeft();
            ((MarginLayoutParams) params).rightMargin = -getPaddingRight();
            ((MarginLayoutParams) params).topMargin = -getPaddingTop();
            ((MarginLayoutParams) params).bottomMargin = -getPaddingBottom();
        }
        super.setLayoutParams(params);

    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect
            previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

        if (gainFocus) {
            ScaleUtils.getInstance().onItemGetFocus(mResizeView != null ? mResizeView : this);
        } else {
            ScaleUtils.getInstance().onItemLoseFocus(mResizeView != null ? mResizeView : this);
        }
    }
}
