package tv.newtv.cboxtv.views;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views
 * 创建事件:         10:23
 * 创建人:           weihaichao
 * 创建日期:          2018/4/28
 */
public class VideoFrameLayout extends FrameLayout {

    private static final String TAG = VideoFrameLayout.class.getSimpleName();

    private TextView LeftTime;
    private int mTextSize = 0;

    public VideoFrameLayout(@NonNull Context context) {
        super(context);
    }

    public VideoFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int
            defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        LeftTime = child.findViewWithTag("text");
        if (LeftTime != null) {
            Log.e(VideoFrameLayout.class.getSimpleName(), "获取到了TextView");
            LayoutParams layoutParams = (LayoutParams) params;
            layoutParams.rightMargin = getMeasuredWidth() / 50;
            LeftTime.setLayoutParams(layoutParams);
            if (mTextSize != 0) {
                LeftTime.setTextSize(mTextSize);
            }
            LeftTime.postInvalidate();
            super.addView(child, layoutParams);
            return;
        }
        super.addView(child, params);
    }
    
    public void updateTimeTextView(int size) {
        mTextSize = size;
        if (LeftTime != null) {
            LeftTime.setTextSize(size);
        }
    }

}
