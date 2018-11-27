package tv.newtv.cboxtv.player.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player.view
 * 创建事件:         11:02
 * 创建人:           weihaichao
 * 创建日期:          2018/11/26
 */
public class NewTvLauncherPlayerTip extends RelativeLayout {

    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_RIGHT = 1;
    public static final int ALIGN_TOP = 2;
    public static final int ALIGN_BOTTOM = 3;
    public static final int ALIGN_LEFT_TOP = 4;
    public static final int ALIGN_LEFT_BOTTOM = 5;
    public static final int ALIGN_RIGHT_TOP = 6;
    public static final int ALIGN_RIGHT_BOTTOM = 7;
    public static final int ALIGN_CENTER = 8;
    public static final int ALIGN_FULLSCREEN = 9;

    private View mTipView;

    public NewTvLauncherPlayerTip(Context context) {
        this(context, null);
    }

    public NewTvLauncherPlayerTip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewTvLauncherPlayerTip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs, defStyle);
    }

    public void show(int align, View view) {
        mTipView = view;

        LayoutParams layoutParams;
        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .MATCH_PARENT);
        switch (align) {
            case ALIGN_LEFT:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    layoutParams.addRule(ALIGN_PARENT_START, TRUE);
                }
                layoutParams.addRule(ALIGN_PARENT_LEFT, TRUE);
                layoutParams.addRule(CENTER_VERTICAL, TRUE);
                break;
            case ALIGN_RIGHT:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    layoutParams.addRule(ALIGN_PARENT_END, TRUE);
                }
                layoutParams.addRule(ALIGN_PARENT_RIGHT, TRUE);
                layoutParams.addRule(CENTER_VERTICAL, TRUE);
                break;
            case ALIGN_TOP:
                layoutParams.addRule(ALIGN_PARENT_TOP, TRUE);
                layoutParams.addRule(CENTER_HORIZONTAL, TRUE);
                break;
            case ALIGN_BOTTOM:
                layoutParams.addRule(ALIGN_PARENT_BOTTOM, TRUE);
                layoutParams.addRule(CENTER_HORIZONTAL, TRUE);
                break;
            case ALIGN_LEFT_TOP:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    layoutParams.addRule(ALIGN_PARENT_START, TRUE);
                }
                layoutParams.addRule(ALIGN_PARENT_LEFT, TRUE);
                layoutParams.addRule(ALIGN_PARENT_TOP, TRUE);
                break;
            case ALIGN_LEFT_BOTTOM:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    layoutParams.addRule(ALIGN_PARENT_START, TRUE);
                }
                layoutParams.addRule(ALIGN_PARENT_LEFT, TRUE);
                layoutParams.addRule(ALIGN_PARENT_BOTTOM, TRUE);
                break;
            case ALIGN_RIGHT_TOP:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    layoutParams.addRule(ALIGN_PARENT_END, TRUE);
                }
                layoutParams.addRule(ALIGN_PARENT_RIGHT, TRUE);
                layoutParams.addRule(ALIGN_PARENT_TOP, TRUE);
                break;
            case ALIGN_RIGHT_BOTTOM:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    layoutParams.addRule(ALIGN_PARENT_END, TRUE);
                }
                layoutParams.addRule(ALIGN_PARENT_RIGHT, TRUE);
                layoutParams.addRule(ALIGN_PARENT_BOTTOM, TRUE);
                break;
            case ALIGN_CENTER:
            default:
                layoutParams.addRule(ALIGN_CENTER, TRUE);
                break;
        }
        mTipView.setLayoutParams(layoutParams);
        mTipView.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                setVisibility(INVISIBLE);
                NewTVLauncherPlayerViewManager.getInstance().setShowingView(NewTVLauncherPlayerView.SHOWING_NO_VIEW);
            }
        });
        addView(mTipView, layoutParams);
        setVisibility(VISIBLE);
    }

    private void removeTipView() {
        if (mTipView != null && mTipView.getParent() != null) {
            ViewGroup parent = (ViewGroup) mTipView.getParent();
            parent.removeView(mTipView);

            mTipView = null;
        }
    }

    public void dismiss() {
        removeTipView();
    }

    private void initialize(Context context, AttributeSet attrs, int defStyle) {

    }
}
