package tv.newtv.key;

import android.util.Log;
import android.view.KeyEvent;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.key
 * 创建事件:         18:35
 * 创建人:           weihaichao
 * 创建日期:          2018/6/13
 */
public class XunMaKey extends AbstractKey {

    public static final String TAG = "xunma";

    @Override
    public KeyType getKeyType(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_ESCAPE:
                return KeyType.BACK;
            case KeyEvent.KEYCODE_MENU:
                return KeyType.MENU;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                return KeyType.CENTER;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                return KeyType.LEFT;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                return KeyType.RIGHT;
            case KeyEvent.KEYCODE_DPAD_UP:
                return KeyType.UP;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return KeyType.DOWN;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return KeyType.VOLUMN_DOWN;
            case KeyEvent.KEYCODE_VOLUME_UP:
                return KeyType.VOLUMN_UP;
            case KeyEvent.KEYCODE_VOLUME_MUTE:
                return KeyType.VOLUMN_MUTE;
        }
        return KeyType.UNKNOWN;
    }

    @Override
    String getKeyName() {
        return TAG;
    }

}
