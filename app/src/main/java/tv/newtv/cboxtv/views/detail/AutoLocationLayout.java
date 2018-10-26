package tv.newtv.cboxtv.views.detail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Project: CBoxTV2.0
 * Package: tv.newtv.cboxtv.views.detail
 * Time：2018/10/24 17:51
 * Author：renpengfei
 */
public class AutoLocationLayout extends FrameLayout {
    private int mRowCount = 1;
    private int mColumnCount = 1;

    public AutoLocationLayout(@NonNull Context context) {
        this(context, null);
    }

    public AutoLocationLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoLocationLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    public void setRowCount(int count) {
        mRowCount = count;
    }

    public void setColumnCount(int count) {
        mColumnCount = count;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int count = getChildCount();
        int currentColumn = 0;
        for (int index = 0; index < count; index++) {
            View target = getChildAt(index);
            if (index % mRowCount == 0) {
                currentColumn++;
            }
            int current = index % mRowCount;
            target.layout(target.getMeasuredWidth() * current, target.getMeasuredHeight() * currentColumn, target.getMeasuredWidth(), target.getMeasuredHeight());
        }
        requestLayout();
        invalidate();

    }

    private void initialize(Context context) {

    }
}
