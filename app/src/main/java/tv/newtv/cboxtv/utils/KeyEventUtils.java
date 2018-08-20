package tv.newtv.cboxtv.utils;

import android.view.KeyEvent;

/**
 * <pre>
 *   name:Wei JiaQi
 *   time:2018/5/25
 *   desc:
 *   version:1.0
 * </pre>
 */


public class KeyEventUtils {

    /**
     * 全屏下默认不受条件限制的键值
     * @param event 按键事件
     * @return  布尔值
     */
    public static boolean FullScreenAllowKey(KeyEvent event) {
        return event.getKeyCode() != KeyEvent.KEYCODE_BACK
                && event.getKeyCode() != KeyEvent.KEYCODE_ESCAPE
                && event.getKeyCode() != KeyEvent.KEYCODE_VOLUME_UP
                && event.getKeyCode() != KeyEvent.KEYCODE_VOLUME_DOWN
                && event.getKeyCode() != KeyEvent.KEYCODE_VOLUME_MUTE;
    }

    public static boolean getEventAction(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            return false;
        } else {
            return true;
        }
    }

    public static KeyEvent getEventHOME(KeyEvent event) {
        return event;
    }

}