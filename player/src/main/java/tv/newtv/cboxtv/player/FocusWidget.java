package tv.newtv.cboxtv.player;

import android.view.KeyEvent;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player
 * 创建事件:         11:39
 * 创建人:           weihaichao
 * 创建日期:          2018/9/11
 */
public class FocusWidget implements IFocusWidget {
    private IFocusWidget mFocusWidget;
    private List<KeyAction> mKeyCodes;
    private List<Integer> mOverrideKeys;
    private int id = 0;

    public FocusWidget(IFocusWidget focusWidget) {
        mFocusWidget = focusWidget;
        id = 1001;
        if (getRegisterKeyCodes() != null) {
            mKeyCodes = Arrays.asList(getRegisterKeyCodes());
            mOverrideKeys = new ArrayList<>();
            for (KeyAction action : mKeyCodes) {
                mOverrideKeys.add(action.getKeyCode());
            }
        }
    }

    public int getId() {
        return id;
    }

    public boolean isOverride(int keyCode) {
        return mOverrideKeys != null && mOverrideKeys.contains(keyCode);
    }

    /**
     * 判断是不是注册的显示按键事件
     *
     * @param event
     * @return
     */
    public boolean isRegisterKey(KeyEvent event) {
        if (mKeyCodes != null && mKeyCodes.size() > 0) {
            for (KeyAction keyAction : mKeyCodes) {
                if (keyAction.equals(event)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean show(ViewGroup parent) {
        if (mFocusWidget != null)
            return mFocusWidget.show(parent);
        return false;
    }

    @Override
    public KeyAction[] getRegisterKeyCodes() {
        if (mFocusWidget != null)
            return mFocusWidget.getRegisterKeyCodes();
        return null;
    }

    @Override
    public boolean isShowing() {
        return mFocusWidget.isShowing();
    }


    @Override
    public boolean isToggleKey(int keycode) {
        if (mFocusWidget != null)
            return mFocusWidget.isToggleKey(keycode);
        return false;
    }

    @Override
    public boolean onBackPressed() {
        if (mFocusWidget != null)
            return mFocusWidget.onBackPressed();
        return true;
    }

    @Override
    public void requestDefaultFocus() {
        if (mFocusWidget != null)
            mFocusWidget.requestDefaultFocus();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mFocusWidget.dispatchKeyEvent(event);
    }

    @Override
    public void release() {
        if (mFocusWidget != null)
            mFocusWidget.release();
        mFocusWidget = null;
        mKeyCodes = null;
        mOverrideKeys = null;
    }
}
