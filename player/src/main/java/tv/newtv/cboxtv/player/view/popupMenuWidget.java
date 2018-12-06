package tv.newtv.cboxtv.player.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.newtv.libs.IDefaultFocus;
import com.newtv.libs.util.ScreenUtils;

import tv.newtv.cboxtv.player.IFocusWidget;
import tv.newtv.cboxtv.player.KeyAction;
import tv.newtv.player.R;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player
 * 创建事件:         11:43
 * 创建人:           weihaichao
 * 创建日期:          2018/9/11
 */

public class popupMenuWidget implements IFocusWidget {

    private static final String TAG = popupMenuWidget.class.getSimpleName();
    private boolean mIsShowing = false;
    private PopupWindow popupWindow;
    private ViewGroup contentView;
    private View outerView;
    private View focusView;
    private ViewGroup parentView;
    private ViewGroup.LayoutParams layoutParams;
    private int mGravity;
    private IPopupWidget mPopupWidget;

    public interface IPopupWidget{
        KeyAction[] getRegisterKeyActions();
    }

    public popupMenuWidget(Context context, View outView, int gravity,IPopupWidget popupWidget) {
        outerView = outView;
        mGravity = gravity;
        mPopupWidget = popupWidget;
    }

    @Override
    public void release() {
        Log.e(TAG, "release()");
        onBackPressed();
        popupWindow = null;
        contentView = null;
    }

    @Override
    public boolean show(ViewGroup parent) {
        if (outerView.getParent() != null) {
            parentView = (ViewGroup) outerView.getParent();
            layoutParams = outerView.getLayoutParams();
            parentView.removeView(outerView);
        }

        if (contentView == null && parentView != null) {
            if (parentView instanceof FrameLayout) {
                contentView = new FrameLayout(parent.getContext());
            } else if (parentView instanceof RelativeLayout) {
                contentView = new RelativeLayout(parent.getContext());
            } else if (parentView instanceof LinearLayout) {
                contentView = new LinearLayout(parent.getContext());
                ((LinearLayout) contentView).setOrientation(((LinearLayout) parentView)
                        .getOrientation());
            } else {
                contentView = new RelativeLayout(parent.getContext());
            }
            contentView.setBackgroundColor(Color.parseColor("#60000000"));
            contentView.setClipToPadding(false);
            contentView.setClipChildren(false);
        }

        Log.e(TAG, "show() width=" + outerView.getMeasuredWidth() + " height=" + outerView
                .getMeasuredHeight());
        popupWindow = new PopupWindow();
        popupWindow.setContentView(contentView);
        contentView.addView(outerView, layoutParams);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        int width = 0;
        int height = 0;
        if(mGravity == Gravity.RIGHT || mGravity == Gravity.END){
            width = outerView.getWidth();
            height = outerView.getHeight();
            popupWindow.setAnimationStyle(R.style.anim_x_right_side);
        }else if (mGravity == Gravity.LEFT || mGravity == Gravity.START) {
            width = outerView.getWidth();
            height = outerView.getHeight();
            popupWindow.setAnimationStyle(R.style.anim_x_side);
        } else if (mGravity == Gravity.BOTTOM) {
            height = contentView.getContext().getResources().getDimensionPixelSize(R.dimen
                    .height_350px);
            width = ScreenUtils.getScreenW();
            popupWindow.setAnimationStyle(R.style.anim_y_side);
        } else if (mGravity == Gravity.TOP) {
            height = contentView.getContext().getResources().getDimensionPixelSize(R.dimen
                    .height_350px);
            width = ScreenUtils.getScreenW();
            popupWindow.setAnimationStyle(R.style.anim_y_top_side);
        }
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
        popupWindow.showAtLocation(parent, mGravity, 0, 0);

        mIsShowing = true;
        return true;
    }

    @Override
    public KeyAction[] getRegisterKeyCodes() {
        return mPopupWidget.getRegisterKeyActions();
    }

    @Override
    public boolean isShowing() {
        return mIsShowing;
    }

    @Override
    public boolean isToggleKey(int keycode) {
        return keycode == KeyEvent.KEYCODE_MENU;
    }

    @Override
    public boolean onBackPressed() {
        Log.e(TAG, "onBackPressed()");
        if (popupWindow != null && popupWindow.isShowing()) {
            focusView = contentView.findFocus();
            popupWindow.dismiss();
            popupWindow = null;
        }

        if (outerView.getParent() == contentView) {
            contentView.removeView(outerView);
        }

        if (contentView.getParent() != null) {
            ViewGroup parent = (ViewGroup) contentView.getParent();
            parent.removeView(contentView);
        }

        if (parentView != null) {
            parentView.addView(outerView, layoutParams);
        }
        mIsShowing = false;
        return true;
    }

    @Override
    public void requestDefaultFocus() {
        if (outerView != null && outerView instanceof IDefaultFocus) {
            focusView = ((IDefaultFocus) outerView).getDefaultFocusView();
            if (focusView != null) {
                ((IDefaultFocus) outerView).getDefaultFocusView().requestFocus();
                return;
            }
        }
        if (focusView != null) {
            focusView.requestFocus();
            return;
        }
        if (outerView != null) {
            outerView.requestFocus();
            return;
        }
        if (contentView != null) {
            contentView.requestFocus();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.e(TAG, "dispatchKeyEvent()" + event.toString());
        return contentView.dispatchKeyEvent(event);
    }


}
