package tv.newtv.cboxtv.player;

import android.view.KeyEvent;
import android.view.ViewGroup;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player
 * 创建事件:         11:37
 * 创建人:           weihaichao
 * 创建日期:          2018/9/11
 */
public interface IFocusWidget {
    boolean show(ViewGroup parent);
    KeyAction[] getRegisterKeyCodes();
    boolean isShowing();
    boolean isToggleKey(int keycode);
    boolean onBackPressed();
    void requestDefaultFocus();
    boolean dispatchKeyEvent(KeyEvent event);
    void release();
}
