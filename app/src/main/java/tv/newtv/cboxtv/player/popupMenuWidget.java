package tv.newtv.cboxtv.player;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import tv.newtv.cboxtv.utils.ScreenUtils;

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

    @Override
    public void release() {
        Log.e(TAG, "release()");
        onBackPressed();
        popupWindow = null;
        contentView = null;
    }

    public popupMenuWidget(Context context, View outView) {
        outerView = outView;
    }

    @Override
    public boolean show(ViewGroup parent, int gravity) {
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
            } else {
                contentView = new RelativeLayout(parent.getContext());
            }
            contentView.setBackgroundColor(Color.parseColor("#60000000"));
            contentView.setClipToPadding(false);
            contentView.setClipChildren(false);
        }

        Log.e(TAG, "show()");
        popupWindow = new PopupWindow();
        popupWindow.setContentView(contentView);
        contentView.addView(outerView, layoutParams);
        popupWindow.setWidth(outerView.getMeasuredWidth());
        popupWindow.setHeight(outerView.getMeasuredHeight());
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(parent, gravity, 0, 0);
        mIsShowing = true;
        return true;
    }

    @Override
    public KeyAction[] getRegisterKeyCodes() {
        return new KeyAction[]{
                new KeyAction(KeyEvent.KEYCODE_MENU, KeyEvent.ACTION_DOWN),
                new KeyAction(KeyEvent.KEYCODE_DPAD_UP, KeyEvent.ACTION_DOWN),
                new KeyAction(KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.ACTION_DOWN),
        };
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

        if(outerView.getParent() == contentView) {
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
        if (focusView != null) {
            focusView.requestFocus();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.e(TAG, "dispatchKeyEvent()" + event.toString());
        return contentView.dispatchKeyEvent(event);
    }


}
