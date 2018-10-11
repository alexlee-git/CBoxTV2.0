package tv.newtv.cboxtv.views.custom;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv
 * 创建事件:         09:59
 * 创建人:           weihaichao
 * 创建日期:          2018/4/2
 */

public class FocusRelativeLayout extends RelativeLayout {
    public FocusRelativeLayout(Context context) {
        super(context);
    }

    public FocusRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if(gainFocus){
            bringToFront();
            animate().scaleX(1.3f).scaleY(1.3f).start();
        }else{
            animate().scaleX(1.0f).scaleY(1.0f).start();
        }
    }
}
