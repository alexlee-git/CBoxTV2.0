package tv.newtv.cboxtv.player;

import android.util.Log;
import android.view.KeyEvent;

import java.util.Locale;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player
 * 创建事件:         12:54
 * 创建人:           weihaichao
 * 创建日期:          2018/9/11
 */
public class KeyAction {
    private static final String TAG = KeyAction.class.getSimpleName();
    private int keyCode;
    private int keyAction;


    public int getKeyAction() {
        return keyAction;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public KeyAction(int code, int action) {
        keyAction = action;
        keyCode = code;
    }

    public boolean equals(KeyEvent event) {
        Log.e(TAG, String.format("%s equals: action=%d keyCode=%d", toString(), event.getAction()
                , event.getKeyCode()));
        return keyAction == event.getAction() && keyCode == event.getKeyCode();
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "keyAction code=%d action=%d", keyCode,
                keyAction);
    }
}
